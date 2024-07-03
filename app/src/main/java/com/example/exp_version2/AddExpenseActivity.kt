package com.example.exp_version2

import DBHelper
import Expense
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.*

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var titleInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var typeSpinner: Spinner
    private lateinit var tagInput: EditText
    private lateinit var whenInput: TextView
    private lateinit var addButton: Button
    private lateinit var dbHelper: DBHelper

    private var selectedDate: Date? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        dbHelper = DBHelper(this)

        titleInput = findViewById(R.id.titleInput)
        amountInput = findViewById(R.id.amountInput)
        typeSpinner = findViewById(R.id.typeSpinner)
        tagInput = findViewById(R.id.tagInput)
        whenInput = findViewById(R.id.whenInput)
        addButton = findViewById(R.id.addButton)

        whenInput.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedCalendar = Calendar.getInstance()
                    selectedCalendar.set(year, month, dayOfMonth)
                    selectedDate = selectedCalendar.time
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    whenInput.text = dateFormat.format(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        addButton.setOnClickListener {
            val title = titleInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull()
            val type = typeSpinner.selectedItem.toString()
            val tag = tagInput.text.toString()

            if (title.isNotBlank() && amount != null && selectedDate != null) {
                val expense = Expense(
                    id = 0L,
                    title = title,
                    amount = amount,
                    type = type,
                    tag = tag,
                    whenText = selectedDate!!
                )
                dbHelper.addExpense(expense)

                val resultIntent = Intent()
                resultIntent.putExtra("expense", expense)
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() // Handle the back button
        return true
    }
}
