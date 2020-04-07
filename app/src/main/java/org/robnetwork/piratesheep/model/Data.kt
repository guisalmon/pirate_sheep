package org.robnetwork.piratesheep.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.preference.PreferenceManager

interface BaseData

data class MainData(
    val firstName: String? = null,
    val lastName: String? = null,
    val birthday: String? = null,
    val birthPlace: String? = null,
    val address: String? = null,
    val city: String? = null,
    val code: String? = null,
    val reason: String? = null,
    val reasonIndex: Int = -1,
    val place: String? = null,
    val date: String? = null,
    val time: String? = null,
    val pathSet: MutableSet<String> = mutableSetOf(),
    val timeStamp: String? = null,
    val lastItem: ListItemData? = null,
    val list: MutableList<ListItemData> = mutableListOf()
) : BaseData {
    companion object {
        private const val FIRSTNAME: String = "firstName"
        private const val LASTNAME: String = "lastName"
        private const val BIRTHDAY: String = "birthday"
        private const val BIRTH_PLACE: String = "birthPlace"
        private const val ADDRESS: String = "address"
        private const val CITY: String = "city"
        private const val CODE: String = "code"
        private const val REASON: String = "reason"
        private const val REASON_INDEX: String = "reasonIndex"
        private const val PLACE: String = "place"
        private const val PATH_SET: String = "paths"

        fun storeData(context: Context, data: MainData) {
            data.lastItem?.let {
                saveBitmap(context, it.page1, ListItemData.page1FileName(it.fileName))
                saveBitmap(context, it.page2, ListItemData.page2FileName(it.fileName))
                saveBitmap(context, it.code, ListItemData.qrCodeFileName(it.fileName))
            }
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext).edit()
                .putString(FIRSTNAME, data.firstName)
                .putString(LASTNAME, data.lastName)
                .putString(BIRTHDAY, data.birthday)
                .putString(BIRTH_PLACE, data.birthPlace)
                .putString(ADDRESS, data.address)
                .putString(CITY, data.city)
                .putString(CODE, data.code)
                .putString(REASON, data.reason)
                .putInt(REASON_INDEX, data.reasonIndex)
                .putString(PLACE, data.place)
                .putStringSet(PATH_SET, data.pathSet)
                .apply()
        }

        fun loadData(context: Context, onDataLoadedListener: (MainData) -> Unit) {
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext).let {
                MainData(
                    it.getString(FIRSTNAME, null),
                    it.getString(LASTNAME, null),
                    it.getString(BIRTHDAY, null),
                    it.getString(BIRTH_PLACE, null),
                    it.getString(ADDRESS, null),
                    it.getString(CITY, null),
                    it.getString(CODE, null),
                    it.getString(REASON, null),
                    it.getInt(REASON_INDEX, -1),
                    it.getString(PLACE, null),
                    null,
                    null,
                    it.getStringSet(PATH_SET, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                ).apply {
                    this.list.clear()
                    this.list.addAll(pathSet.map { fileName ->
                        val page1 = loadBitmap(context, ListItemData.page1FileName(fileName))
                        ListItemData.listItemData(
                            page1,
                            loadBitmap(context, ListItemData.page2FileName(fileName)),
                            loadBitmap(context, ListItemData.qrCodeFileName(fileName)),
                            fileName
                        )
                    }.filterNotNull().toMutableList())
                }
            }.apply { onDataLoadedListener(this) }
        }

        private fun loadBitmap(context: Context, fileName: String) =
            BitmapFactory.decodeStream(context.openFileInput(fileName))

        private fun saveBitmap(context: Context, bitmap: Bitmap, fileName: String) =
            context.openFileOutput(fileName, Context.MODE_PRIVATE).let{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
                it.flush()
                it.close()
            }
    }
}

data class ListItemData(
    val page1: Bitmap,
    val page2: Bitmap,
    val code: Bitmap,
    val fileName: String
) {
    companion object {
        fun page1FileName(fileName: String) = fileName.replace(".pdf", "_page1")
        fun page2FileName(fileName: String) = fileName.replace(".pdf", "_page2")
        fun qrCodeFileName(fileName: String) = fileName.replace(".pdf", "_qrCode")

        fun listItemData(page1: Bitmap?, page2: Bitmap?, code: Bitmap?, fileName: String?) =
            if (page1 != null && page2 != null && code != null && fileName != null)
                ListItemData(page1, page2, code, fileName)
            else null
    }
}
