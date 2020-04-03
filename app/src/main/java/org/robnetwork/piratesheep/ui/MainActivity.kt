package org.robnetwork.piratesheep.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.ActivityMainBinding
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding, MainData, MainViewModel>() {

    override val layoutRes: Int = R.layout.activity_main
    override val viewModelClass = MainViewModel::class.java

    override fun setupUI(binding: ActivityMainBinding) {
        super.setupUI(binding)
        val cal = Calendar.getInstance()

        binding.mainBirthdayEdit.setOnClickListener(
            showDatePicker(
                cal,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.updateBirthday(dateString(dayOfMonth, month + 1, year))
                })
        )
        viewModel.updateDate(
            dateString(
                cal.get(Calendar.DAY_OF_MONTH),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.YEAR)
            )
        )
        binding.mainDateEdit.setOnClickListener(
            showDatePicker(
                cal,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.updateDate(dateString(dayOfMonth, month + 1, year))
                })
        )
        viewModel.updateTime(timeString(cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE)))
        binding.mainTimeEdit.setOnClickListener(
            showTimePicker(
                cal,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    viewModel.updateTime(timeString(hourOfDay, minute))
                })
        )
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.reasons_array).toList()
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.mainReasonEdit.adapter = it
        }
    }

    override fun updateUI(data: MainData) {
        binding?.let {
            it.mainBirthdayEdit.text = data.birthday
            it.mainDateEdit.text = data.date
            it.mainTimeEdit.text = data.time
        }
    }

    private fun showDatePicker(
        cal: Calendar,
        onDateSetListener: DatePickerDialog.OnDateSetListener
    ) = View.OnClickListener {
        DatePickerDialog(
            this,
            onDateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(
        cal: Calendar,
        onTimeSetListener: TimePickerDialog.OnTimeSetListener
    ) = View.OnClickListener {
        TimePickerDialog(
            this,
            onTimeSetListener,
            cal.get(Calendar.HOUR),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun dateString(day: Int, month: Int, year: Int) =
        getString(R.string.date_template, day, month, year)

    private fun timeString(hour: Int, minutes: Int) =
        getString(R.string.time_template, hour, minutes)
}

data class MainData(
    val firstName: String? = null,
    val lastName: String? = null,
    val birthday: String? = null,
    val birthPlace: String? = null,
    val address: String? = null,
    val city: String? = null,
    val reason: String? = null,
    val place: String? = null,
    val date: String? = null,
    val time: String? = null
) : BaseData

class MainViewModel : BaseViewModel<MainData>() {
    override val data = MutableLiveData(MainData())

    fun updateBirthday(birthday: String) = data.value?.let {
        data.value = it.copy(birthday = birthday)
    }

    fun updateDate(date: String) = data.value?.let {
        data.value = it.copy(date = date)
    }

    fun updateTime(time: String) = data.value?.let {
        data.value = it.copy(time = time)
    }
}
