package com.example.lab56_testowanie

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab56_testowanie.databinding.ActivityDeletePersonBinding

class DeletePersonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeletePersonBinding
    private lateinit var dbHelper: PersonDbHelper

    private lateinit var adapter: PersonDeleteAdapter
    private var allPersons: MutableList<Person> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeletePersonBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Usuń osobę"

        dbHelper = PersonDbHelper(this)

        // Wczytujemy wszystkie osoby z bazy
        allPersons = dbHelper.getAllPersons().toMutableList()

        adapter = PersonDeleteAdapter(allPersons.toMutableList()) { person ->
            showDeleteDialog(person)
        }

        binding.rvDeletePersons.layoutManager = LinearLayoutManager(this)
        binding.rvDeletePersons.adapter = adapter

        // Wyszukiwanie po imieniu, nazwisku, telefonie, mailu, adresie
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: ""
                filterList(query)
            }
        })
    }

    private fun filterList(query: String) {
        if (query.isEmpty()) {
            adapter.updateData(allPersons)
            return
        }

        val lower = query.lowercase()
        val filtered = allPersons.filter { person ->
            person.firstName.lowercase().contains(lower) ||
                    person.lastName.lowercase().contains(lower) ||
                    person.phone.lowercase().contains(lower) ||
                    person.email.lowercase().contains(lower) ||
                    person.address.lowercase().contains(lower)
        }
        adapter.updateData(filtered)
    }

    private fun showDeleteDialog(person: Person) {
        AlertDialog.Builder(this)
            .setTitle("Usuń osobę")
            .setMessage("Na pewno chcesz usunąć:\n${person.firstName} ${person.lastName}?")
            .setPositiveButton("Usuń") { _, _ ->
                deletePerson(person)
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    private fun deletePerson(person: Person) {
        val deleted = dbHelper.deletePersonById(person.id)
        if (deleted > 0) {
            Toast.makeText(this, "Usunięto ${person.firstName} ${person.lastName}", Toast.LENGTH_SHORT).show()
            // Usuwamy z listy
            allPersons.removeAll { it.id == person.id }
            // Aktualizujemy adapter po filtrowaniu
            val currentQuery = binding.etSearch.text.toString().trim()
            filterList(currentQuery)
        } else {
            Toast.makeText(this, "Nie udało się usunąć rekordu", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
