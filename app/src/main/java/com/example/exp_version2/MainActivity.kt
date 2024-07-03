package com.example.exp_version2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Application

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val expenseButton: Button = findViewById(R.id.expenseTracker)
        expenseButton.setOnClickListener {
            val intent = Intent(this, ExpenseTracker::class.java)
            startActivity(intent)
        }
        val financeButton: Button = findViewById(R.id.financeLiteracy)
        financeButton.setOnClickListener {
            val intent = Intent(this, FinancialLiteracyActivity::class.java)
            startActivity(intent)
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val expenseTrackerButton: Button = findViewById(R.id.expenseTracker)
        val financeLiteracyButton: Button = findViewById(R.id.financeLiteracy)

        expenseTrackerButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)

        financeLiteracyButton.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)

    }
}