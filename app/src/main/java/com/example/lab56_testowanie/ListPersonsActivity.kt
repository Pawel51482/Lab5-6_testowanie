package com.example.lab56_testowanie

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab56_testowanie.databinding.ActivityListPersonsBinding

class ListPersonsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListPersonsBinding
    private lateinit var dbHelper: PersonDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListPersonsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Lista osób"

        dbHelper = PersonDbHelper(this)

        val list = dbHelper.getAllPersons()

        binding.rvPersons.layoutManager = LinearLayoutManager(this)
        binding.rvPersons.adapter = PersonAdapter(list)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
