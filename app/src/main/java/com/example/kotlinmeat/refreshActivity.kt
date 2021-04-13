
import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.kotlinmeat.R

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Declaring a layout (changes are to be made to this)
        // Declaring a textview (which is inside the layout)
        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)
        val textView = findViewById<TextView>(R.id.tv1)

        // Refresh function for the layout
        swipeRefreshLayout.setOnRefreshListener{

            textView.text = "Refreshed"

            // This line is important as it explicitly refreshes only once
            // If "true" it implicitly refreshes forever
            swipeRefreshLayout.isRefreshing = false
        }
    }
}