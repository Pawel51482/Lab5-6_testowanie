package com.example.lab56_testowanie

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DATABASE_NAME = "people.db"
private const val DATABASE_VERSION = 1
const val TABLE_PERSON = "person"

class PersonDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val sql = """
            CREATE TABLE $TABLE_PERSON (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                birth_date TEXT NOT NULL,
                phone TEXT NOT NULL,
                email TEXT NOT NULL,
                address TEXT NOT NULL
            );
        """.trimIndent()
        db.execSQL(sql)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PERSON")
        onCreate(db)
    }

    fun insertPerson(person: Person): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("first_name", person.firstName)
            put("last_name", person.lastName)
            put("birth_date", person.birthDate)
            put("phone", person.phone)
            put("email", person.email)
            put("address", person.address)
        }
        return db.insert(TABLE_PERSON, null, values)
    }

    fun getAllPersons(): List<Person> {
        val list = mutableListOf<Person>()
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_PERSON,
            null,
            null,
            null,
            null,
            null,
            "last_name ASC"
        )
        cursor.use {
            while (it.moveToNext()) {
                val id = it.getLong(it.getColumnIndexOrThrow("id"))
                val firstName = it.getString(it.getColumnIndexOrThrow("first_name"))
                val lastName = it.getString(it.getColumnIndexOrThrow("last_name"))
                val birthDate = it.getString(it.getColumnIndexOrThrow("birth_date"))
                val phone = it.getString(it.getColumnIndexOrThrow("phone"))
                val email = it.getString(it.getColumnIndexOrThrow("email"))
                val address = it.getString(it.getColumnIndexOrThrow("address"))

                list.add(
                    Person(
                        id = id,
                        firstName = firstName,
                        lastName = lastName,
                        birthDate = birthDate,
                        phone = phone,
                        email = email,
                        address = address
                    )
                )
            }
        }
        return list
    }

    fun deletePersonById(id: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_PERSON, "id = ?", arrayOf(id.toString()))
    }
}