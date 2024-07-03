import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "ExpenseTrackerDB"

        private const val TABLE_EXPENSES = "expenses"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_TAG = "tag"
        private const val COLUMN_WHEN = "when_date"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = ("CREATE TABLE $TABLE_EXPENSES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_TITLE TEXT," +
                "$COLUMN_AMOUNT REAL," +
                "$COLUMN_TYPE TEXT," +
                "$COLUMN_TAG TEXT," +
                "$COLUMN_WHEN INTEGER)")
        db.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        onCreate(db)
    }

    fun addExpense(expense: Expense): Boolean {
        if (isExpenseExists(expense)) {
            return false
        }

        val values = ContentValues().apply {
            put(COLUMN_TITLE, expense.title)
            put(COLUMN_AMOUNT, expense.amount)
            put(COLUMN_TYPE, expense.type)
            put(COLUMN_TAG, expense.tag)
            put(COLUMN_WHEN, expense.whenText.time)
        }

        val db = this.writableDatabase
        db.insert(TABLE_EXPENSES, null, values)
        db.close()
        return true  // Expense added successfully
    }

    private fun isExpenseExists(expense: Expense): Boolean {
        val db = this.readableDatabase
        val selection = "$COLUMN_TITLE = ? AND $COLUMN_AMOUNT = ? AND $COLUMN_TYPE = ? AND $COLUMN_TAG = ? AND $COLUMN_WHEN = ?"
        val selectionArgs = arrayOf(
            expense.title,
            expense.amount.toString(),
            expense.type,
            expense.tag,
            expense.whenText.time.toString()
        )

        val cursor = db.query(
            TABLE_EXPENSES,
            arrayOf(COLUMN_ID),
            selection,
            selectionArgs,
            null,
            null,
            null
        )

        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun getAllExpenses(): List<Expense> {
        return getExpensesFiltered(null, null)
    }

    fun getExpensesByMonth(month: Int): List<Expense> {
        return getExpensesFiltered(month, null)
    }

    fun getExpensesByYear(year: Int): List<Expense> {
        return getExpensesFiltered(null, year)
    }

    fun getExpensesByMonthAndYear(month: Int, year: Int): List<Expense> {
        return getExpensesFiltered(month, year)
    }

    private fun getExpensesFiltered(month: Int?, year: Int?): List<Expense> {
        val expenses = mutableListOf<Expense>()
        var selection: String? = null
        var selectionArgs: Array<String>? = null

        if (month != null && year != null) {
            selection = "strftime('%m', datetime($COLUMN_WHEN/1000, 'unixepoch')) = ? AND strftime('%Y', datetime($COLUMN_WHEN/1000, 'unixepoch')) = ?"
            selectionArgs = arrayOf(String.format("%02d", month), year.toString())
        } else if (month != null) {
            selection = "strftime('%m', datetime($COLUMN_WHEN/1000, 'unixepoch')) = ?"
            selectionArgs = arrayOf(String.format("%02d", month))
        } else if (year != null) {
            selection = "strftime('%Y', datetime($COLUMN_WHEN/1000, 'unixepoch')) = ?"
            selectionArgs = arrayOf(year.toString())
        }

        val orderBy = "$COLUMN_WHEN DESC"
        val db = this.readableDatabase

        val cursor = db.query(
            TABLE_EXPENSES,
            null,
            selection,
            selectionArgs,
            null,
            null,
            orderBy
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
                val tag = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TAG))
                val whenDate = Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_WHEN)))

                Log.d("DBHelper", "ID: $id, Title: $title, Amount: $amount, Type: $type, Tag: $tag, WhenDate: $whenDate")

                expenses.add(Expense(id, title, amount, type, tag, whenDate))
            } while (cursor.moveToNext())
        }

        cursor.close()
        return expenses
    }

    fun getTotalSavingsByMonthAndYear(month: Int, year: Int): Double {
        val selectQuery = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES WHERE $COLUMN_TYPE = 'Savings' AND strftime('%m', datetime($COLUMN_WHEN/1000, 'unixepoch')) = ? AND strftime('%Y', datetime($COLUMN_WHEN/1000, 'unixepoch')) = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(String.format("%02d", month), year.toString()))
        var total = 0.0

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }

        cursor.close()
        return total
    }

    fun getTotalSpendingsByMonthAndYear(month: Int, year: Int): Double {
        val selectQuery = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES WHERE $COLUMN_TYPE = 'Spending' AND strftime('%m', datetime($COLUMN_WHEN/1000, 'unixepoch')) = ? AND strftime('%Y', datetime($COLUMN_WHEN/1000, 'unixepoch')) = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(String.format("%02d", month), year.toString()))
        var total = 0.0

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }

        cursor.close()
        return total
    }

    fun getTotalSavingsByYear(year: Int): Double {
        val selectQuery = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES WHERE $COLUMN_TYPE = 'Savings' AND strftime('%Y', datetime($COLUMN_WHEN/1000, 'unixepoch')) = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(year.toString()))
        var total = 0.0

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }

        cursor.close()
        return total
    }

    fun getTotalSpendingsByYear(year: Int): Double {
        val selectQuery = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES WHERE $COLUMN_TYPE = 'Spending' AND strftime('%Y', datetime($COLUMN_WHEN/1000, 'unixepoch')) = ?"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(year.toString()))
        var total = 0.0

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }

        cursor.close()
        return total
    }
    fun deleteExpense(expense: Expense) {
        val db = this.writableDatabase
        db.delete(TABLE_EXPENSES, "$COLUMN_ID = ?", arrayOf(expense.id.toString()))
        db.close()
    }

    fun getTotalSavings(): Double {
        val selectQuery = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES WHERE $COLUMN_TYPE = 'Savings'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var total = 0.0

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }

        cursor.close()
        return total
    }

    fun getTotalSpendings(): Double {
        val selectQuery = "SELECT SUM($COLUMN_AMOUNT) FROM $TABLE_EXPENSES WHERE $COLUMN_TYPE = 'Spending'"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        var total = 0.0

        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0)
        }

        cursor.close()
        return total
    }
}
