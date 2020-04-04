package org.robnetwork.piratesheep.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.MutableLiveData
import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.ActivityMainBinding
import org.robnetwork.piratesheep.model.MainData
import org.robnetwork.piratesheep.utils.ImageUtils
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
            setupReason(binding.reasonEdit, it.reasonIndex)
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
                binding.formView.visibility = View.VISIBLE
                binding.formView.setImageBitmap(viewModel.formBitmap)
            }
        }
        binding.qrcodeOverlay.setOnClickListener {
            binding.qrcodeOverlay.visibility = View.GONE
            binding.qrcodeView.visibility = View.GONE
            binding.formView.visibility = View.GONE
        }
        binding.qrcodeOverlay.visibility = View.GONE
        binding.qrcodeView.visibility = View.GONE
        binding.formView.visibility = View.GONE
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

    private fun setupReason(reasonSpinner: Spinner, reasonIndex: Int?) {
        val reasonsList = Reasons.values().toList()
        ReasonsSpinnerAdapter(
            this,
            android.R.layout.simple_spinner_item,
            reasonsList
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            reasonSpinner.adapter = it
        }

        reasonSpinner.setSelection(if (reasonIndex != null && reasonIndex > -1 && reasonIndex < reasonsList.size) reasonIndex else 0)

        reasonSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) =
                viewModel.update { it.copy(reason = null, reasonIndex = -1) }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) = viewModel.update {
                it.copy(
                    reason = reasonsList[position].toReadableText(this@MainActivity),
                    reasonIndex = position
                )
            }
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
            && reasonIndex != -1
            && place != null
            && date != null
            && time != null
}

class MainViewModel(override val data: MutableLiveData<MainData> = MutableLiveData(MainData())) :
    BaseViewModel<MainData>() {
    var formBitmap: Bitmap? = null

    fun loadData(context: Context, onDataLoadedListener: (MainData) -> Unit) =
        MainData.loadData(context) {
            data.value = it
            ImageUtils.drawableToBitmap(context, R.drawable.attestation_deplacement_empty) { form ->
                formBitmap = form
            }
            onDataLoadedListener(it)
        }

    fun storeData(context: Context) = data.value?.let { MainData.storeData(context, it) }

    fun generateQrCode(context: Context, onQRCodeGenerated: (Bitmap) -> Unit) {
        data.value?.let {
            val density = context.resources.displayMetrics.density
            formBitmap?.writeTextOnBitmap(
                density,
                FormField(it.firstName + " " + it.lastName, 257, 327)
            )
            formBitmap?.writeTextOnBitmap(density, FormField(it.birthday + "", 257, 377))
            formBitmap?.writeTextOnBitmap(density, FormField(it.birthPlace + "", 191, 425))
            formBitmap?.writeTextOnBitmap(density, FormField(it.address + " " + it.city, 280, 478))
            formBitmap?.writeTextOnBitmap(density, FormField(it.place + "", 230, 1285))
            formBitmap?.writeTextOnBitmap(density, FormField(it.date + "", 191, 1337))
            formBitmap?.writeTextOnBitmap(density, FormField(it.time + "", 415, 1337))
            Reasons.reasonByIndex(it.reasonIndex)?.let { reason ->
                formBitmap?.writeTextOnBitmap(density, FormField("X", reason.x, reason.y, 40))
            }
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

    private fun Bitmap.writeTextOnBitmap(density: Float, field: FormField) {
        ImageUtils.writeTextToBitmapAt(field.text, this, field.x, field.y, field.size, density)
    }

    private data class FormField(
        val text: String,
        val x: Int,
        val y: Int,
        val size: Int
    ) {
        constructor(text: String, x: Int, y: Int) : this(text, x, y, 20)
    }
}

private enum class Reasons(val index: Int, val x: Int, val y: Int, @StringRes val textRes: Int) {
    PRO(0, 157, 658, R.string.reason_pro),
    GROCERIES(1, 157, 762, R.string.reason_groceries),
    MEDICAL(2, 157, 847, R.string.reason_medical),
    HELP(3, 157, 923, R.string.reason_help),
    LEISURE(4, 157, 1039, R.string.reason_leisure),
    LEGAL(5, 157, 1135, R.string.reason_legal),
    TIG(6, 157, 1212, R.string.reason_tig);

    fun toReadableText(context: Context) = context.getString(this.textRes)
    companion object {
        fun reasonByIndex(index: Int?) = values().firstOrNull { it.index == index }
    }
}

private class ReasonsSpinnerAdapter(
    context: Context, @LayoutRes layout: Int,
    private val reasons: List<Reasons>
) : ArrayAdapter<Reasons>(context, layout, reasons) {

    override fun getCount() = reasons.size

    override fun getItem(position: Int) = reasons[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view as? TextView)?.text = reasons[position].toReadableText(context)
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        (view as? TextView)?.text = reasons[position].toReadableText(context)
        return view
    }
}
