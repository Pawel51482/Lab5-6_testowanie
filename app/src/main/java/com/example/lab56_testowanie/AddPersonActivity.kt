package com.example.lab56_testowanie

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lab56_testowanie.databinding.ActivityAddPersonBinding
import java.util.Calendar
import android.util.Patterns

// Pierwsza litera duża
private fun String.capitalizeFirstLetter(): String {
    if (this.isEmpty()) return this
    return this.lowercase().replaceFirstChar { it.uppercase() }
}

class AddPersonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddPersonBinding
    private lateinit var dbHelper: PersonDbHelper
    private var isUpdatingPhone = false
    private var isUpdatingPostal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Format telefonu 123 456 789 oraz max 9 cyfr
        binding.etPhone.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                if (isUpdatingPhone) return

                val digits = s.toString().filter { it.isDigit() }
                val limited = digits.take(9) //max 9 cyfr

                val formatted = StringBuilder()
                for (i in limited.indices) {
                    formatted.append(limited[i])
                    if (i == 2 || i == 5) {
                        if (i != limited.lastIndex) {
                            formatted.append(' ')
                        }
                    }
                }

                isUpdatingPhone = true
                binding.etPhone.setText(formatted.toString())
                binding.etPhone.setSelection(binding.etPhone.text.length)
                isUpdatingPhone = false
            }
        })

        // format kodu pocztowego 12-345 oraz max 5 cyfr
        binding.etPostalCode.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {
                if (isUpdatingPostal) return

                val digits = s.toString().filter { it.isDigit() }
                val limited = digits.take(5)  // max 5 cyfr

                val formatted = StringBuilder()
                for (i in limited.indices) {
                    formatted.append(limited[i])
                    if (i == 1 && limited.length > 2) {
                        formatted.append('-')   // po 2 cyfrze
                    }
                }

                isUpdatingPostal = true
                binding.etPostalCode.setText(formatted.toString())
                binding.etPostalCode.setSelection(binding.etPostalCode.text.length)
                isUpdatingPostal = false
            }
        })

        dbHelper = PersonDbHelper(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Dodaj osobę"

        binding.etBirthDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            savePerson()
        }
    }

    private fun showDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            val dd = d.toString().padStart(2, '0')
            val mm = (m + 1).toString().padStart(2, '0')
            binding.etBirthDate.setText("$dd-$mm-$y")
        }, year, month, day).show()
    }

    private fun savePerson() {
        val firstName = binding.etFirstName.text.toString().trim().capitalizeFirstLetter()
        val lastName = binding.etLastName.text.toString().trim().capitalizeFirstLetter()
        val birthDate = binding.etBirthDate.text.toString().trim()
        val phoneInput = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        val city = binding.etCity.text.toString().trim().capitalizeFirstLetter()
        val postalCode = binding.etPostalCode.text.toString().trim()
        val street = binding.etStreet.text.toString().trim().capitalizeFirstLetter()
        val houseNumber = binding.etHouseNumber.text.toString().trim()

        // Czyścimy stare błędy
        binding.etFirstName.error = null
        binding.etLastName.error = null
        binding.etBirthDate.error = null
        binding.etPhone.error = null
        binding.etEmail.error = null
        binding.etCity.error = null
        binding.etPostalCode.error = null
        binding.etStreet.error = null
        binding.etHouseNumber.error = null

        var isValid = true

        // Wymagane pola
        if (firstName.isEmpty()) {
            binding.etFirstName.error = "Podaj imię"
            isValid = false
        }
        if (lastName.isEmpty()) {
            binding.etLastName.error = "Podaj nazwisko"
            isValid = false
        }
        if (birthDate.isEmpty()) {
            binding.etBirthDate.error = "Podaj datę urodzenia"
            isValid = false
        }

        // Email
        if (email.isEmpty()) {
            binding.etEmail.error = "Podaj adres e-mail"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Nieprawidłowy adres e-mail"
            isValid = false
        }

        // Telefon
        var formattedPhone = phoneInput
        val digitsOnly = phoneInput.replace(" ", "")

        if (digitsOnly.length != 9 || !digitsOnly.all { it.isDigit() }) {
            binding.etPhone.error = "Telefon musi mieć 9 cyfr"
            isValid = false
        } else {
            formattedPhone = "${digitsOnly.substring(0, 3)} " +
                    "${digitsOnly.substring(3, 6)} " +
                    digitsOnly.substring(6, 9)
            binding.etPhone.setText(formattedPhone)
        }

        // Adres
        val postalRegex = Regex("\\d{2}-\\d{3}")
        val houseRegex = Regex("\\d+[A-Za-z]?")

        if (city.isEmpty()) {
            binding.etCity.error = "Podaj miasto"
            isValid = false
        }

        if (!postalRegex.matches(postalCode)) {
            binding.etPostalCode.error = "Kod w formacie 12-345"
            isValid = false
        }

        if (street.isEmpty()) {
            binding.etStreet.error = "Podaj ulicę"
            isValid = false
        }

        if (!houseRegex.matches(houseNumber)) {
            binding.etHouseNumber.error = "Nieprawidłowy numer domu"
            isValid = false
        }

        if (!isValid) {
            Toast.makeText(this, "Popraw zaznaczone pola", Toast.LENGTH_SHORT).show()
            return
        }

        // pełny adres do bazy
        val fullAddress = "$city $postalCode $street $houseNumber"

        val person = Person(
            firstName = firstName,
            lastName = lastName,
            birthDate = birthDate,
            phone = formattedPhone,
            email = email,
            address = fullAddress
        )

        val id = dbHelper.insertPerson(person)
        if (id > 0) {
            Toast.makeText(this, "Dodano $firstName $lastName", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Błąd zapisu", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
