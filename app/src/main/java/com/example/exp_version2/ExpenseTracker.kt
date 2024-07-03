    package com.example.exp_version2

    import DBHelper
    import Expense
    import ExpenseAdapter
    import android.content.Intent
    import android.os.Bundle
    import android.view.View
    import android.widget.AdapterView
    import android.widget.ArrayAdapter
    import android.widget.Button
    import android.widget.Spinner
    import android.widget.TextView
    import androidx.appcompat.app.AppCompatActivity
    import androidx.appcompat.widget.Toolbar
    import androidx.core.content.ContextCompat
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import java.util.*


    class ExpenseTracker : AppCompatActivity() {
        private lateinit var expenseRecyclerView: RecyclerView
        private lateinit var balanceTextView: TextView
        private lateinit var totalSavingsTextView: TextView
        private lateinit var totalSpendingsTextView: TextView
        private lateinit var addExpenseButton: Button
        private lateinit var monthSpinner: Spinner
        private lateinit var yearSpinner: Spinner
        private lateinit var monthlyExpenseTextView: TextView
        private lateinit var yearlyExpenseTextView: TextView
        private lateinit var expenseAdapter: ExpenseAdapter
        private val dbHelper = DBHelper(this)

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.expense_tracker)

            val toolbar: Toolbar = findViewById(R.id.toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)

            expenseRecyclerView = findViewById(R.id.expenseRecyclerView)
            balanceTextView = findViewById(R.id.balanceTextView)
            totalSavingsTextView = findViewById(R.id.totalSavingsTextView)
            totalSpendingsTextView = findViewById(R.id.totalSpendingsTextView)
            addExpenseButton = findViewById(R.id.fab_add)
            monthSpinner = findViewById(R.id.monthSpinner)
            yearSpinner = findViewById(R.id.yearSpinner)
            monthlyExpenseTextView = findViewById(R.id.monthlyExpenseTextView)
            yearlyExpenseTextView = findViewById(R.id.yearlyExpenseTextView)

            expenseAdapter = ExpenseAdapter(this, ArrayList()) { expense ->
                dbHelper.deleteExpense(expense)
                updateUI()
            }
            expenseRecyclerView.adapter = expenseAdapter
            expenseRecyclerView.layoutManager = LinearLayoutManager(this)

            addExpenseButton.setOnClickListener {
                val intent = Intent(this, AddExpenseActivity::class.java)
                startActivityForResult(intent, ADD_EXPENSE_REQUEST_CODE)
            }

            setUpSpinners()

            monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    updateUI()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }

            yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    updateUI()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }

        private fun setUpSpinners() {
            val months = resources.getStringArray(R.array.months_array)
            val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
            monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            monthSpinner.adapter = monthAdapter

            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            val years = mutableListOf(getString(R.string.all))
            for (year in currentYear downTo currentYear - 10) {
                years.add(year.toString())
            }
            val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, years)
            yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            yearSpinner.adapter = yearAdapter

            monthSpinner.setSelection(0)
            yearSpinner.setSelection(0)
        }

        override fun onResume() {
            super.onResume()
            updateUI()
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == ADD_EXPENSE_REQUEST_CODE && resultCode == RESULT_OK) {
                data?.getParcelableExtra<Expense>("expense")?.let { expense ->
                    dbHelper.addExpense(expense)
                    updateUI()
                }
            }
        }

        private fun updateUI() {
            val selectedMonth = monthSpinner.selectedItemPosition
            val selectedYear = yearSpinner.selectedItem as String

            val filteredExpenses = when {
                selectedMonth == 0 && selectedYear == getString(R.string.all) -> {
                    dbHelper.getAllExpenses()
                }
                selectedMonth == 0 -> {
                    dbHelper.getExpensesByYear(selectedYear.toInt())
                }
                selectedYear == getString(R.string.all) -> {
                    dbHelper.getExpensesByMonth(selectedMonth)
                }
                else -> {
                    dbHelper.getExpensesByMonthAndYear(selectedMonth, selectedYear.toInt())
                }
            }

            val totalSavings = dbHelper.getTotalSavings()
            val totalSpendings = dbHelper.getTotalSpendings()
            val balance = totalSavings - totalSpendings

            val selectedYearInt = selectedYear.toIntOrNull() ?: Calendar.getInstance().get(Calendar.YEAR)
            val selectedMonthInt = if (selectedMonth == 0) null else selectedMonth

            val monthlySavings = selectedMonthInt?.let { dbHelper.getTotalSavingsByMonthAndYear(it, selectedYearInt) } ?: 0.0
            val monthlySpendings = selectedMonthInt?.let { dbHelper.getTotalSpendingsByMonthAndYear(it, selectedYearInt) } ?: 0.0
            val monthlyBalance = monthlySavings - monthlySpendings

            val yearlySavings = dbHelper.getTotalSavingsByYear(selectedYearInt)
            val yearlySpendings = dbHelper.getTotalSpendingsByYear(selectedYearInt)
            val yearlyBalance = yearlySavings - yearlySpendings

            expenseAdapter.updateExpenses(filteredExpenses)
            totalSavingsTextView.text = getString(R.string.total_income, totalSavings)
            totalSpendingsTextView.text = getString(R.string.total_expense, totalSpendings)
            balanceTextView.text = balance.toString()

            monthlyExpenseTextView.text = getString(R.string.monthly_balance, monthlyBalance)
            yearlyExpenseTextView.text = getString(R.string.yearly_balance, yearlyBalance)

            val colorResId = if (balance >= 0) R.color.green else R.color.red
            balanceTextView.setTextColor(ContextCompat.getColor(this, colorResId))
        }


        companion object {
            private const val ADD_EXPENSE_REQUEST_CODE = 1
        }
    }
