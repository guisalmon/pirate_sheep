package org.robnetwork.piratesheep.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.FragmentCreationBinding
import org.robnetwork.piratesheep.model.MainData
import java.util.*

class CreationFragment : BaseFragment<FragmentCreationBinding, MainData, MainViewModel>() {
    override val layoutRes: Int = R.layout.fragment_creation
    override val viewModelClass = MainViewModel::class.java

    override fun getModelStoreOwner() = (activity as? MainActivity)

    override fun setupUI(binding: FragmentCreationBinding, context: Context) {
        super.setupUI(binding, context)

        val cal = Calendar.getInstance()

        viewModel.data.value?.let {
            setupBirthday(binding.birthdayEdit, cal, context)
            setupDate(binding.dateEdit, cal, context)
            setupTime(binding.timeEdit, cal, context)
            binding.reasonEdit.setOnClickListener { _ -> showReasons(context, it) }
            binding.firstNameEdit.setText(it.firstName)
            binding.lastNameEdit.setText(it.lastName)
            binding.birthPlaceEdit.setText(it.birthPlace)
            binding.addressEdit.setText(it.address)
            binding.cityEdit.setText(it.city)
            binding.codeEdit.setText(it.code)
            binding.placeEdit.setText(it.place)
            updateUI(binding, it)
        }

        binding.firstNameEdit.doOnTextChanged { t, _, _, _ ->
            viewModel.update {
                it.copy(
                    firstName = t.nullIfEmpty()
                )
            }
        }
        binding.lastNameEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(lastName = t.nullIfEmpty()) } }
        binding.birthPlaceEdit.doOnTextChanged { t, _, _, _ ->
            viewModel.update {
                it.copy(
                    birthPlace = t.nullIfEmpty()
                )
            }
        }
        binding.addressEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(address = t.nullIfEmpty()) } }
        binding.cityEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(city = t.nullIfEmpty()) } }
        binding.codeEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(code = t.nullIfEmpty()) } }
        binding.placeEdit.doOnTextChanged { t, _, _, _ -> viewModel.update { it.copy(place = t.nullIfEmpty()) } }
        binding.qrcodeFab.setOnClickListener {
            viewModel.generateForm(context) { lastDoc ->
                createPdf(lastDoc?.fileName)
            }
        }
    }

    override fun updateUI(binding: FragmentCreationBinding, data: MainData) {
        binding.birthdayEdit.text = data.birthday
        binding.dateEdit.text = data.date
        binding.timeEdit.text = data.time
        binding.qrcodeFab.isClickable = data.isValid()
    }

    private fun setupBirthday(birthdayBtn: Button, cal: Calendar, context: Context) {
        birthdayBtn.setOnClickListener(
            showDatePicker(
                cal,
                context,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.update { it.copy(birthday = dateString(dayOfMonth, month + 1, year)) }
                })
        )
    }

    private fun setupDate(dateBtn: Button, cal: Calendar, context: Context) {
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
                context,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    viewModel.update { it.copy(date = dateString(dayOfMonth, month + 1, year)) }
                })
        )
    }

    private fun setupTime(timeBtn: Button, cal: Calendar, context: Context) {
        viewModel.update {
            it.copy(
                time = timeString(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)),
                timeStamp = timeString(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            )
        }
        timeBtn.setOnClickListener(
            showTimePicker(
                cal,
                context,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    viewModel.update {
                        it.copy(
                            time = timeString(hourOfDay, minute),
                            timeStamp = timeString(hourOfDay, minute)
                        )
                    }
                })
        )
    }

    private fun showReasons(context: Context, values: MainData) {
        AlertDialog.Builder(context)
            .setTitle(R.string.main_reason)
            .setPositiveButton(R.string.positive) { dialog, _ -> dialog.dismiss() }
            .setMultiChoiceItems(
                MainViewModel.Reasons.values().map { context.getString(it.textRes) }.toTypedArray(),
                MainViewModel.Reasons.values().map { values.reason.contains(it.keyword) }.toBooleanArray()
            ) { _, which, isChecked ->
                viewModel.update {
                    val reasons = it.reason
                    val reasonIndexes = it.reasonIndexes
                    MainViewModel.Reasons.values()[which].keyword.let { keyword ->
                        if (isChecked && !reasons.contains(keyword)) {
                            reasons.add(keyword)
                            reasonIndexes.add(which)
                        }
                        if (!isChecked && reasons.contains(keyword)) {
                            reasons.remove(keyword)
                            reasonIndexes.remove(which)
                        }
                    }
                    it.copy(reason = reasons, reasonIndexes = reasonIndexes)
                }
            }
            .create()
            .show()
    }

    private fun showDatePicker(
        cal: Calendar,
        context: Context,
        onDateSetListener: DatePickerDialog.OnDateSetListener
    ) = View.OnClickListener {
        DatePickerDialog(
            context,
            onDateSetListener,
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker(
        cal: Calendar,
        context: Context,
        onTimeSetListener: TimePickerDialog.OnTimeSetListener
    ) = View.OnClickListener {
        TimePickerDialog(
            context,
            onTimeSetListener,
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun MainData.isValid() = !firstName.isNullOrBlank()
            && !lastName.isNullOrBlank()
            && !birthday.isNullOrBlank()
            && !birthPlace.isNullOrBlank()
            && !address.isNullOrBlank()
            && !city.isNullOrBlank()
            && !code.isNullOrBlank()
            && !reason.isNullOrEmpty()
            && !reasonIndexes.isNullOrEmpty()
            && !place.isNullOrBlank()
            && !date.isNullOrBlank()
            && !time.isNullOrBlank()

    private fun CharSequence?.nullIfEmpty() = if (isNullOrEmpty()) null else toString()


    private fun createPdf(docName: String?) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, docName)
        }
        activity?.startActivityForResult(intent, MainActivity.CREATE_FILE)
    }

    private fun openPdf(uri: Uri) =
        startActivityForResult(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setDataAndType(uri, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri)
            }
        }, MainActivity.PICK_PDF_FILE)

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
}