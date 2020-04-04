package org.robnetwork.piratesheep.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.ActivityMainBinding
import org.robnetwork.piratesheep.model.MainData
import org.robnetwork.piratesheep.utils.QRGeneratorUtil
import java.util.*

class MainActivity : BaseActivity<ActivityMainBinding, MainData, MainViewModel>() {

    override val layoutRes: Int = R.layout.activity_main
    override val viewModelClass = MainViewModel::class.java

    override fun setupUI(binding: ActivityMainBinding) {
        super.setupUI(binding)

        val cal = Calendar.getInstance()

        viewModel.loadData(this) {
            setupBirthday(binding.birthdayEdit, cal)
            setupDate(binding.dateEdit, cal)
            setupTime(binding.timeEdit, cal)
            setupReason(binding.reasonEdit, it.reason)
            binding.firstNameEdit.setText(it.firstName)
            binding.lastNameEdit.setText(it.lastName)
            binding.birthPlaceEdit.setText(it.birthPlace)
            binding.addressEdit.setText(it.address)
            binding.cityEdit.setText(it.city)
            binding.placeEdit.setText(it.place)
        }

        binding.firstNameEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(firstName = t.nullIfEmpty()) } }
        binding.lastNameEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(lastName = t.nullIfEmpty()) } }
        binding.birthPlaceEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(birthPlace = t.nullIfEmpty()) } }
        binding.addressEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(address = t.nullIfEmpty()) } }
        binding.cityEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(city = t.nullIfEmpty()) } }
        binding.placeEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(place = t.nullIfEmpty()) } }
        binding.qrcodeFab.setOnClickListener {
            viewModel.generateQrCode(this) {
                binding.qrcodeOverlay.visibility = View.VISIBLE
                binding.qrcodeView.visibility = View.VISIBLE
                binding.qrcodeView.setImageBitmap(it)
            }
        }
        binding.qrcodeOverlay.setOnClickListener {
            binding.qrcodeOverlay.visibility = View.GONE
            binding.qrcodeView.visibility = View.GONE
        }
        binding.qrcodeOverlay.visibility = View.GONE
        binding.qrcodeView.visibility = View.GONE
    }

    override fun updateUI(data: MainData) {
        binding?.let {
            it.birthdayEdit.text = data.birthday
            it.dateEdit.text = data.date
            it.timeEdit.text = data.time
            it.qrcodeFab.isClickable = data.isValid()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.storeData(this)
    }

    private fun setupBirthday(birthdayBtn: Button, cal: Calendar) {
        birthdayBtn.setOnClickListener(
            showDatePicker(
                cal,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.update { it.copy(birthday = dateString(dayOfMonth, month + 1, year)) }
                })
        )
    }

    private fun setupDate(dateBtn: Button, cal: Calendar) {
        viewModel.update {
            it.copy(
                date = dateString(
                    cal.get(Calendar.DAY_OF_MONTH),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.YEAR)
                )
            )
        }
        dateBtn.setOnClickListener(
            showDatePicker(
                cal,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.update { it.copy(date = dateString(dayOfMonth, month + 1, year)) }
                })
        )
    }

    private fun setupTime(timeBtn: Button, cal: Calendar) {
        viewModel.update {
            it.copy(time = timeString(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)))
        }
        timeBtn.setOnClickListener(
            showTimePicker(
                cal,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    viewModel.update { it.copy(time = timeString(hourOfDay, minute)) }
                })
        )
    }

    private fun setupReason(reasonSpinner: Spinner, reason: String?) {
        val reasonsList = resources.getStringArray(R.array.reasons_array).toList()
        ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            reasonsList
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            reasonSpinner.adapter = it
        }

        reasonSpinner.setSelection(reasonsList.indexOf(reason).let { if (it == -1) 0 else it })

        reasonSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) =
                viewModel.update { it.copy(reason = null) }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) = viewModel.update { it.copy(reason = reasonsList[position]) }
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
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun dateString(day: Int, month: Int, year: Int) = getString(
        R.string.date_template,
        day.numberTo2DigitString(),
        month.numberTo2DigitString(),
        year.toString()
    )

    private fun timeString(hour: Int, minutes: Int) = getString(
        R.string.time_template,
        hour.numberTo2DigitString(),
        minutes.numberTo2DigitString()
    )

    private fun Int.numberTo2DigitString() = if (this < 10) "0$this" else toString()

    private fun CharSequence?.nullIfEmpty() = if (isNullOrEmpty()) null else toString()

    private fun MainData.isValid() = firstName != null
            && lastName != null
            && birthday != null
            && birthPlace != null
            && address != null
            && city != null
            && reason != null
            && place != null
            && date != null
            && time != null
}

class MainViewModel(override val data: MutableLiveData<MainData> = MutableLiveData(MainData())) :
    BaseViewModel<MainData>() {

    fun loadData(context: Context, onDataLoadedListener: (MainData) -> Unit) =
        MainData.loadData(context) {
            data.value = it
            onDataLoadedListener(it)
        }

    fun storeData(context: Context) = data.value?.let { MainData.storeData(context, it) }

    fun generateQrCode(context: Context, onQRCodeGenerated: (Bitmap) -> Unit) {
        data.value?.let {
            QRGeneratorUtil.generateQRCode(
                context.getString(
                    R.string.qr_code_template,
                    it.date,
                    it.time,
                    it.firstName,
                    it.lastName,
                    it.birthday,
                    it.birthPlace,
                    it.address,
                    it.city,
                    it.date,
                    it.time,
                    it.reason
                ), onQRCodeGenerated
            )
        }
    }
}
