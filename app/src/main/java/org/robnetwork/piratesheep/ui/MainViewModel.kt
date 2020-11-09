package org.robnetwork.piratesheep.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.model.ListItemData
import org.robnetwork.piratesheep.model.MainData
import org.robnetwork.piratesheep.utils.ImageUtils
import org.robnetwork.piratesheep.utils.PdfUtils
import org.robnetwork.piratesheep.utils.QRGeneratorUtils
import java.io.FileOutputStream

class MainViewModel(public override val data: MutableLiveData<MainData> = MutableLiveData(MainData())) :
    BaseViewModel<MainData>() {
    private var pdf: PdfDocument? = null

    fun loadData(context: Context, onDataLoadedListener: ((MainData) -> Unit)?) =
        MainData.loadData(context) { loadedData ->
            data.value = loadedData
            onDataLoadedListener?.let { it(loadedData) }
        }

    fun storeData(context: Context) = data.value?.let { MainData.storeData(context, it) }

    fun generateForm(context: Context, onFormGenerated: (ListItemData?) -> Unit) {

        data.value?.let {
            val formBitmap: Bitmap =
                ImageUtils.drawableToBitmap(context, R.drawable.attestation_deplacement_empty)
            val formBitmap2: Bitmap =
                Bitmap.createBitmap(formBitmap.width, formBitmap.height, Bitmap.Config.ARGB_8888)
            val density = context.resources.displayMetrics.density
            val qrCodeSmall = generateQrCode(context, it, density)
            val qrCodeBig = generateQrCode(context, it, density * 3)

            formBitmap.writeTextOnBitmap(
                density,
                FormField(it.firstName + " " + it.lastName, 257, 327)
            )
            formBitmap.writeTextOnBitmap(density, FormField(it.birthday + "", 257, 377))
            formBitmap.writeTextOnBitmap(density, FormField(it.birthPlace + "", 191, 425))
            formBitmap.writeTextOnBitmap(
                density,
                FormField(it.address + " " + it.code + " " + it.city, 280, 478)
            )
            formBitmap.writeTextOnBitmap(density, FormField(it.place + "", 230, 1285))
            formBitmap.writeTextOnBitmap(density, FormField(it.date + "", 191, 1337))
            formBitmap.writeTextOnBitmap(density, FormField(it.time + "", 415, 1337))
            it.reasonIndexes.map { index -> Reasons.reasonByIndex(index) }.forEach { reason ->
                reason?.let {
                    formBitmap.writeTextOnBitmap(density, FormField("X", reason.x, reason.y, 40))
                }
            }
            formBitmap.let { bitmap ->
                ImageUtils.writeQrCodeToCanvas(qrCodeSmall, bitmap, 873, 1211, density)
            }
            formBitmap.writeTextOnBitmap(
                density,
                FormField(context.getString(R.string.timestamp_1), 1077, 1441, 14, false)
            )
            formBitmap.writeTextOnBitmap(
                density,
                FormField(
                    context.getString(R.string.timestamp_2, it.date, it.time),
                    1077,
                    1455,
                    14,
                    false
                )
            )
            formBitmap2.let { bitmap ->
                ImageUtils.setBackgroundWhite(bitmap)
                ImageUtils.writeQrCodeToCanvas(qrCodeBig, bitmap, 70, 70, density)
            }
            pdf = PdfUtils.generatePdf(
                Pair(
                    ImageUtils.resizeBitmapToScreen(context, formBitmap),
                    ImageUtils.resizeBitmapToScreen(context, formBitmap2)
                )
            )
            val lastItem = ListItemData(
                formBitmap,
                formBitmap2,
                qrCodeSmall,
                "attestation_${it.date?.replace('/', '_')}_${it.time}.pdf"
            )
            data.value = it.copy(lastItem = lastItem)
            onFormGenerated(lastItem)
        }
    }

    fun saveForm(context: Context, uri: Uri) {
        context.contentResolver.openFileDescriptor(uri, "rw")?.let { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).let { fos ->
                pdf?.let {
                    it.writeTo(fos)
                    it.close()
                }
                fos.close()
            }
            data.value?.let {
                it.lastItem?.let { lastItem ->
                    it.list.add(lastItem)
                    data.value = it.copy(pathSet = it.pathSet.apply { add(lastItem.fileName) })
                }
            }
        }
    }

    fun deleteForms(context: Context) {
        data.value?.let { mainData ->
            MainData.deleteSelectionFromCache(context, mainData)
            val lastItem = mainData.lastItem.takeUnless { mainData.selectionToDelete.contains(it) }
            val list = mainData.list.filterNot { mainData.selectionToDelete.contains(it) }
            val pathSet = list.map { it.fileName }.toMutableSet()
            data.value = mainData.copy(pathSet = pathSet, lastItem = lastItem, list = list.toMutableList(), deleteMode = false, selectionToDelete = mutableListOf())
        }
    }

    private fun generateQrCode(context: Context, data: MainData, density: Float) =
        QRGeneratorUtils.generateQRCode(
            context.getString(
                R.string.qr_code_template,
                data.date,
                data.timeStamp,
                data.lastName,
                data.firstName,
                data.birthday,
                data.birthPlace,
                data.address,
                data.code,
                data.city,
                data.date,
                data.time,
                data.reason.let { reasons ->
                    var reasonsText = ""
                    reasons.forEachIndexed { index, reason -> reasonsText = "$reasonsText${if (index == 0) reason else ", $reason" }"}
                    return@let reasonsText
                }
            ), 233, density
        )

    private fun Bitmap.writeTextOnBitmap(density: Float, field: FormField) {
        ImageUtils.writeTextToBitmapAt(
            field.text,
            this,
            field.x,
            field.y,
            field.size,
            density,
            field.isLeftAligned
        )
    }

    private data class FormField(
        val text: String,
        val x: Int,
        val y: Int,
        val size: Int = 20,
        val isLeftAligned: Boolean = true
    )

    @Suppress("unused")
    enum class Reasons(
        val index: Int,
        val x: Int,
        val y: Int, @StringRes val textRes: Int,
        val keyword: String
    ) {
        PRO(0, 157, 658, R.string.reason_pro, "travail"),
        GROCERIES(1, 157, 762, R.string.reason_groceries, "courses"),
        MEDICAL(2, 157, 847, R.string.reason_medical, "sante"),
        HELP(3, 157, 923, R.string.reason_help, "famille"),
        LEISURE(4, 157, 1039, R.string.reason_leisure, "sport"),
        LEGAL(5, 157, 1135, R.string.reason_legal, "judiciaire"),
        TIG(6, 157, 1212, R.string.reason_tig, "missions");

        fun toReadableText(context: Context) = context.getString(this.textRes)

        companion object {
            fun reasonByIndex(index: Int?) = values().firstOrNull { it.index == index }
        }
    }
}
