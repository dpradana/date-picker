package id.daniel.date_picker

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.NumberPicker
import id.daniel.date_picker.databinding.ViewDatePickerBinding
import java.text.DateFormatSymbols
import java.util.Calendar

class DatePickerView : LinearLayout {
    private lateinit var binding: ViewDatePickerBinding

    private var minDateCalendar: Calendar? = null
    private var maxDateCalendar: Calendar? = null
    private var currentDateCalendar: Calendar? = null

    var callback: ((year: Int, month: Int, day: Int) -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributes: AttributeSet) : super(context, attributes) {
        binding = ViewDatePickerBinding.inflate(
            LayoutInflater.from(context),
            this, true
        )
    }

    private fun initView() {
        if (maxDateCalendar == null) {
            setMaxDate(Calendar.getInstance())
        }
        if (minDateCalendar == null) {
            setMinDate(Calendar.getInstance())
        }
        if (currentDateCalendar == null) {
            setCurrentDate(Calendar.getInstance())
        }

        val maxYear = maxDateCalendar!![Calendar.YEAR]
        val maxMonth = maxDateCalendar!![Calendar.MONTH]
        val maxDate = maxDateCalendar!![Calendar.DATE]

        val minYear = minDateCalendar!![Calendar.YEAR]
        val minMonth = minDateCalendar!![Calendar.MONTH]
        val minDate = minDateCalendar!![Calendar.DATE]

        val currYear = currentDateCalendar!![Calendar.YEAR]
        val currMonth = currentDateCalendar!![Calendar.MONTH]
        val currMaxMonth = currentDateCalendar!!.getActualMaximum(Calendar.MONTH)
        val currMaxDate = currentDateCalendar!!.getActualMaximum(Calendar.DATE)
        val currDate = currentDateCalendar!![Calendar.DATE]

        binding.apply {
            year.minValue = minYear
            year.maxValue = maxYear
            year.wrapSelectorWheel = false
            year.value = currYear
            year.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            year.setOnValueChangedListener { picker, oldVal, newVal ->
                setupMinMaxMonth(newVal, maxYear, maxMonth, currMaxMonth, minYear, minMonth)
                updateDays(newVal, month.value, currDate)
                callback?.invoke(year.value, month.value + 1, day.value)
            }

            setupMinMaxMonth(currYear, maxYear, maxMonth, currMaxMonth, minYear, minMonth)
            month.value = currMonth
            month.wrapSelectorWheel = false
            month.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            month.setOnValueChangedListener { picker, oldVal, newVal ->
                updateDays(year.value, newVal, currDate)
                callback?.invoke(year.value, month.value + 1, day.value)
            }
            if (currYear == minYear && currMonth == minMonth) {
                day.minValue = minDate
            } else {
                day.minValue = START_OF_DATE
            }
            if (maxYear == currYear && maxMonth == currMonth) {
                day.maxValue = maxDate
            } else {
                day.maxValue = currMaxDate
            }
            day.value = currDate
            day.wrapSelectorWheel = false
            day.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            day.setOnValueChangedListener { picker, oldVal, newVal ->
                callback?.invoke(year.value, month.value + 1, day.value)
            }
            if (year.value.toString().isNotEmpty()
                && month.value.toString().isNotEmpty()
                && day.value.toString().isNotEmpty()
            ) {
                Handler(Looper.getMainLooper()).postDelayed({
                    callback?.invoke(year.value, month.value + 1, day.value)
                }, DELAY)
            }
        }
    }

    private fun setupMinMaxMonth(
        currYear: Int,
        maxYear: Int,
        maxMonth: Int,
        currMaxMonth: Int,
        minYear: Int,
        minMonth: Int
    ) {
        binding.apply {
            month.displayedValues = DateFormatSymbols().months
            if (maxYear == currYear && minYear == currYear) {
                month.minValue = minMonth
                month.maxValue = maxMonth
            } else if (maxYear == currYear) {
                month.minValue = START_OF_MONTH
                month.maxValue = maxMonth
            } else if (minYear == currYear) {
                month.minValue = minMonth
                month.maxValue = currMaxMonth
            } else {
                month.maxValue = END_OF_MONTH
                month.minValue = START_OF_MONTH
            }
            month.displayedValues =
                DateFormatSymbols().shortMonths.slice(month.minValue..month.maxValue).toTypedArray()
        }
    }

    /**
     * calculate min & max date after year & month date picker is selected
     */
    private fun updateDays(year: Int, month: Int, day: Int) {
        val calendarInstance = Calendar.getInstance()
        calendarInstance.set(year, month + 1, 0)
        val maxdays = calendarInstance[Calendar.DATE]

        val calendar = maxDateCalendar ?: Calendar.getInstance()
        val maxYear = calendar[Calendar.YEAR]
        val maxMonth = calendar[Calendar.MONTH]
        val maxDate = calendar[Calendar.DATE]

        val minCalendar = minDateCalendar ?: Calendar.getInstance()
        val minYear = minCalendar[Calendar.YEAR]
        val minMonth = minCalendar[Calendar.MONTH]
        val minDate = minCalendar[Calendar.DATE]

        if (year == maxYear && month == maxMonth) {
            binding.day.maxValue = maxDate
        } else {
            binding.day.maxValue = maxdays
        }

        if (year == minYear && month == minMonth) {
            binding.day.minValue = minDate
        } else {
            binding.day.minValue = START_OF_DATE
        }

    }

    fun setMinDate(calendar: Calendar?) {
        this.minDateCalendar = calendar
    }

    fun setMaxDate(calendar: Calendar?) {
        this.maxDateCalendar = calendar
    }

    fun setCurrentDate(calendar: Calendar?) {
        this.currentDateCalendar = calendar
    }

    fun apply() {
        initView()
    }

    companion object {
        private const val START_OF_MONTH = 0
        private const val END_OF_MONTH = 11
        private const val START_OF_DATE = 1
        private const val DELAY = 500L
    }
}
