package com.example.lab56_testowanie

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lab56_testowanie.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            startActivity(Intent(this, AddPersonActivity::class.java))
        }

        binding.btnList.setOnClickListener {
            startActivity(Intent(this, ListPersonsActivity::class.java))
        }

        binding.btnDelete.setOnClickListener {
            startActivity(Intent(this, DeletePersonActivity::class.java))
        }

        // Bottom navigation
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_add -> {
                    startActivity(Intent(this, AddPersonActivity::class.java))
                    true
                }
                R.id.nav_list -> {
                    startActivity(Intent(this, ListPersonsActivity::class.java))
                    true
                }
                R.id.nav_delete -> {
                    startActivity(Intent(this, DeletePersonActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
