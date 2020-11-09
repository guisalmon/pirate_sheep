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
                FormField(it.firstName + " " + it.lastName, 247, 302)
            )
            formBitmap.writeTextOnBitmap(density, FormField(it.birthday + "", 247, 349))
            formBitmap.writeTextOnBitmap(density, FormField(it.birthPlace + "", 617, 349))
            formBitmap.writeTextOnBitmap(
                density,
                FormField(it.address + " " + it.code + " " + it.city, 274, 395)
            )
            formBitmap.writeTextOnBitmap(density, FormField(it.place + "", 217, 1380))
            formBitmap.writeTextOnBitmap(density, FormField(it.date + "", 187, 1429))
            formBitmap.writeTextOnBitmap(density, FormField(it.time + "", 549, 1429))
            it.reasonIndexes.map { index -> Reasons.reasonByIndex(index) }.forEach { reason ->
                reason?.let {
                    formBitmap.writeTextOnBitmap(density, FormField("X", reason.x, reason.y, 40))
                }
            }
            formBitmap.let { bitmap ->
                ImageUtils.writeQrCodeToCanvas(qrCodeSmall, bitmap, 908, 1348, density)
            }
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
        PRO(0, 160, 546, R.string.reason_pro, "travail"),
        GROCERIES(1, 160, 651, R.string.reason_groceries, "achats"),
        MEDICAL(2, 160, 756, R.string.reason_medical, "sante"),
        HELP(3, 160, 832, R.string.reason_help, "famille"),
        HANDICAP(4, 160, 927, R.string.reason_help, "handicap"),
        LEISURE(5, 160, 1008, R.string.reason_leisure, "sport_animaux"),
        LEGAL(6, 160, 1139, R.string.reason_legal, "convocation"),
        TIG(7, 160, 1218, R.string.reason_tig, "missions"),
        CHILDREN(8, 160, 1309, R.string.reason_tig, "enfants");

        fun toReadableText(context: Context) = context.getString(this.textRes)

        companion object {
            fun reasonByIndex(index: Int?) = values().firstOrNull { it.index == index }
        }
    }
}
