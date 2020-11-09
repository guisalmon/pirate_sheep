package org.robnetwork.piratesheep.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.preference.PreferenceManager
import java.lang.Exception

interface BaseData

data class MainData(
    val firstName: String? = null,
    val lastName: String? = null,
    val birthday: String? = null,
    val birthPlace: String? = null,
    val address: String? = null,
    val city: String? = null,
    val code: String? = null,
    val reason: MutableSet<String> = mutableSetOf(),
    val reasonIndexes: MutableSet<Int> = mutableSetOf(),
    val place: String? = null,
    val date: String? = null,
    val time: String? = null,
    val pathSet: MutableSet<String> = mutableSetOf(),
    val timeStamp: String? = null,
    val lastItem: ListItemData? = null,
    val list: MutableList<ListItemData> = mutableListOf(),
    val selectionToDelete: MutableList<ListItemData> = mutableListOf(),
    val deleteMode: Boolean = false
) : BaseData {
    companion object {
        private const val FIRSTNAME: String = "firstName"
        private const val LASTNAME: String = "lastName"
        private const val BIRTHDAY: String = "birthday"
        private const val BIRTH_PLACE: String = "birthPlace"
        private const val ADDRESS: String = "address"
        private const val CITY: String = "city"
        private const val CODE: String = "code"
        private const val REASON: String = "reasons"
        private const val REASON_INDEX: String = "reasonIndexes"
        private const val PLACE: String = "place"
        private const val PATH_SET: String = "paths"

        fun storeData(context: Context, data: MainData) {
            data.list.filterNot { it.stored }.forEach {
                saveBitmap(context, it.page1, ListItemData.page1FileName(it.fileName))
                saveBitmap(context, it.page2, ListItemData.page2FileName(it.fileName))
                saveBitmap(context, it.code, ListItemData.qrCodeFileName(it.fileName))
                data.list[data.list.indexOf(it)] = it.apply { stored = true }
            }
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext).edit()
                .putString(FIRSTNAME, data.firstName)
                .putString(LASTNAME, data.lastName)
                .putString(BIRTHDAY, data.birthday)
                .putString(BIRTH_PLACE, data.birthPlace)
                .putString(ADDRESS, data.address)
                .putString(CITY, data.city)
                .putString(CODE, data.code)
                .putStringSet(REASON, data.reason)
                .putStringSet(REASON_INDEX, data.reasonIndexes.map { it.toString() }.toMutableSet())
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
                    it.getStringSet(REASON, mutableSetOf())?.toMutableSet() ?: mutableSetOf(),
                    it.getStringSet(REASON_INDEX, mutableSetOf())?.map { index -> index.toInt() }?.toMutableSet() ?: mutableSetOf(),
                    it.getString(PLACE, null),
                    null,
                    null,
                    it.getStringSet(PATH_SET, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
                ).apply {
                    this.list.clear()
                    this.list.addAll(pathSet.mapNotNull { fileName ->
                        ListItemData.listItemData(context, fileName)
                    }.toMutableList())
                }
            }.apply { onDataLoadedListener(this) }
        }

        fun deleteSelectionFromCache(context: Context, data: MainData) {
            data.selectionToDelete.forEach { deleteFromCache(context, it) }
        }

        private fun deleteFromCache(context: Context, listItemData: ListItemData) {
            deleteBitmap(context, ListItemData.page1FileName(listItemData.fileName))
            deleteBitmap(context, ListItemData.page2FileName(listItemData.fileName))
            deleteBitmap(context, ListItemData.qrCodeFileName(listItemData.fileName))
        }

        private fun saveBitmap(context: Context, bitmap: Bitmap, fileName: String) =
            context.openFileOutput(fileName, Context.MODE_PRIVATE).let{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it)
                it.flush()
                it.close()
            }

        private fun deleteBitmap(context: Context, fileName: String) = context.deleteFile(fileName)
    }
}

data class ListItemData (
    val page1: Bitmap,
    val page2: Bitmap,
    val code: Bitmap,
    val fileName: String
): BaseData {
    var toDelete = false
    var stored = false
    companion object {
        fun page1FileName(fileName: String) = fileName.replace(".pdf", "_page1")
        fun page2FileName(fileName: String) = fileName.replace(".pdf", "_page2")
        fun qrCodeFileName(fileName: String) = fileName.replace(".pdf", "_qrCode")

        fun listItemData(context: Context, fileName: String) = try {
            listItemData(
                loadBitmap(context, page1FileName(fileName)),
                loadBitmap(context, page2FileName(fileName)),
                loadBitmap(context, qrCodeFileName(fileName)),
                fileName
            )
        } catch (e: Exception) {
            null
        }

        private fun listItemData(page1: Bitmap?, page2: Bitmap?, code: Bitmap?, fileName: String?) =
            if (page1 != null && page2 != null && code != null && fileName != null)
                ListItemData(page1, page2, code, fileName).apply { stored = true }
            else null

        private fun loadBitmap(context: Context, fileName: String) =
            BitmapFactory.decodeStream(context.openFileInput(fileName))
    }
}
