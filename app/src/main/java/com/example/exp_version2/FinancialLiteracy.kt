package com.example.exp_version2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class FinancialLiteracyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.financial_literacy)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Financial Literacy"
        val one: LinearLayout = findViewById(R.id.one)
        one.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.morningstar.in/posts/76598/how-to-improve-financial-literacy.aspx"))
            startActivity(intent)
        }
        val two: LinearLayout = findViewById(R.id.two)
        two.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.investopedia.com/terms/f/financial-literacy.asp"))
            startActivity(intent)
        }
        val three: LinearLayout = findViewById(R.id.three)
        three.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gflec.org/wp-content/uploads/2015/09/Annamaria-Lusardi-Paper.pdf"))
            startActivity(intent)
        }
        val four: LinearLayout = findViewById(R.id.four)
        four.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://corporatefinanceinstitute.com/resources/management/financial-literacy/"))
            startActivity(intent)
        }
        val five: LinearLayout = findViewById(R.id.five)
        five.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.capitalone.com/learn-grow/money-management/financial-literacy/"))
            startActivity(intent)
        }
        val six: LinearLayout = findViewById(R.id.six)
        six.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.greatlakes.edu.in/herald/pdfs/march-2015/Article_3.pdf"))
            startActivity(intent)
        }
        val seven: LinearLayout = findViewById(R.id.seven)
        seven.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://link.springer.com/article/10.1186/s41937-019-0027-5"))
            startActivity(intent)
        }
        val eight: LinearLayout = findViewById(R.id.eight)
        eight.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://papers.ssrn.com/sol3/papers.cfm?abstract_id=923437"))
            startActivity(intent)
        }
        val nine: LinearLayout = findViewById(R.id.nine)
        nine.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.mdpi.com/2227-7102/9/3/238"))
            startActivity(intent)
        }
        val ten: LinearLayout = findViewById(R.id.ten)
        ten.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://journals.sagepub.com/doi/full/10.1177/2047173417719555"))
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
