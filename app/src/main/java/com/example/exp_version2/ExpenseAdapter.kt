import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.exp_version2.R
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    private val context: Context,
    private var expenses: List<Expense>,
    private val onDeleteClick: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    inner class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val tagTextView: TextView = itemView.findViewById(R.id.tagTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(expense: Expense) {
            descriptionTextView.text = expense.title
            tagTextView.text = expense.tag
            dateTextView.text = dateFormat.format(expense.whenText)

            val formattedAmount = when {
                expense.type == "Savings" -> "+₹${expense.amount}"
                expense.type == "Spending" -> "-₹${expense.amount}"
                else -> "₹${expense.amount}"
            }
            amountTextView.text = formattedAmount

            val textColorResId = when {
                expense.type == "Savings" -> R.color.green
                expense.type == "Spending" -> R.color.red
                else -> android.R.color.black
            }
            amountTextView.setTextColor(ContextCompat.getColor(context, textColorResId))

            deleteButton.setOnClickListener {
                onDeleteClick(expense)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        holder.bind(expenses[position])
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    fun updateExpenses(newExpenses: List<Expense>) {
        expenses = newExpenses
        notifyDataSetChanged()
    }
}
