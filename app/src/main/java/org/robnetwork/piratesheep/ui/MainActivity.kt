package org.robnetwork.piratesheep.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.ActivityMainBinding
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutRes: Int = R.layout.activity_main

    override fun setupUI(binding: ActivityMainBinding) {
        super.setupUI(binding)
		val cal = Calendar.getInstance()
        binding.mainBirthdayEdit.setOnClickListener(showDatePicker(binding.mainBirthdayEdit, cal))
        binding.mainDateEdit.setDate(
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR)
        )
        binding.mainDateEdit.setOnClickListener(showDatePicker(binding.mainDateEdit, cal))
        binding.mainTimeEdit.setTime(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE))
        binding.mainTimeEdit.setOnClickListener(showTimePicker(binding.mainTimeEdit, cal))
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.reasons_array).toList()
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.mainReasonEdit.adapter = it
        }
    }

    private fun showDatePicker(textToUpdate: TextView, cal: Calendar) = View.OnClickListener {
        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, m, dayOfMonth ->
                val month = m + 1
                textToUpdate.setDate(dayOfMonth, month, year)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(textToUpdate: TextView, cal: Calendar) = View.OnClickListener {
        TimePickerDialog(
            this,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                textToUpdate.text =
                    getString(R.string.time_template, hourOfDay, minute)
            },
            cal.get(Calendar.HOUR),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun TextView.setDate(day: Int, month: Int, year: Int) {
        text = getString(R.string.date_template, day, month, year)
    }

    private fun TextView.setTime(hour: Int, minutes: Int) {
        text = getString(R.string.time_template, hour, minutes)
    }
}
