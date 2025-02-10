package id.daniel.customdatepicker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import id.daniel.customdatepicker.databinding.ActivityMainBinding
import java.util.Calendar
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val minCalendar = Calendar.getInstance()
        minCalendar.set(Calendar.YEAR, 2020)
        minCalendar.set(Calendar.MONTH, 5)
        minCalendar.set(Calendar.DATE, 12)

        val maxCalendar = Calendar.getInstance()
        maxCalendar.add(Calendar.YEAR, 1900)
        maxCalendar.set(Calendar.MONTH, 2)
        maxCalendar.set(Calendar.DATE, 20)

        val currentCalendar = Calendar.getInstance()
        currentCalendar.set(Calendar.YEAR, 2025)
        currentCalendar.set(Calendar.MONTH, 2)
        currentCalendar.set(Calendar.DATE, 30)

        binding.customDatePicker.setMinDate(minCalendar)
        binding.customDatePicker.setMaxDate(maxCalendar)
        binding.customDatePicker.setCurrentDate(currentCalendar)
        binding.customDatePicker.apply()

        binding.customDatePicker.callback = { year, month, day ->

        }
    }
}