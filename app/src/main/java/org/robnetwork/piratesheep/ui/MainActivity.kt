package org.robnetwork.piratesheep.ui

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.TextView
import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.ActivityMainBinding
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override val layoutRes: Int = R.layout.activity_main

    override fun setupUI(binding: ActivityMainBinding) {
        super.setupUI(binding)
        binding.mainBirthdayEdit.setOnClickListener(showDatePicker(binding.mainBirthdayEdit))
        binding.mainDateEdit.setOnClickListener(showDatePicker(binding.mainDateEdit))
		binding.mainTimeEdit.setOnClickListener(showTimePicker(binding.mainTimeEdit))
    }

	private fun showDatePicker(textToUpdate: TextView) = View.OnClickListener {
		val cal = Calendar.getInstance()
		DatePickerDialog(
			this,
			DatePickerDialog.OnDateSetListener { _, year, m, dayOfMonth ->
				val month = m + 1
				textToUpdate.text =
					getString(R.string.date_template, dayOfMonth, month, year)
			},
			cal.get(Calendar.YEAR),
			cal.get(Calendar.MONTH),
			cal.get(Calendar.DAY_OF_MONTH)
		).show()
	}

	private fun showTimePicker(textToUpdate: TextView) = View.OnClickListener {
		val cal = Calendar.getInstance()
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
}
