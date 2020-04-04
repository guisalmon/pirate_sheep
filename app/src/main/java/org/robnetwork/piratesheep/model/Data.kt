package org.robnetwork.piratesheep.model

import android.content.Context
import androidx.preference.PreferenceManager

interface BaseData

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
) : BaseData {
    companion object {
        private const val FIRSTNAME: String = "firstName"
        private const val LASTNAME: String = "lastName"
        private const val BIRTHDAY: String = "birthday"
        private const val BIRTH_PLACE: String = "birthPlace"
        private const val ADDRESS: String = "address"
        private const val CITY: String = "city"
        private const val REASON: String = "reason"
        private const val PLACE: String = "place"

        fun storeData(context: Context, data: MainData) =
            PreferenceManager.getDefaultSharedPreferences(context.applicationContext).edit()
                .putString(FIRSTNAME, data.firstName)
                .putString(LASTNAME, data.lastName)
                .putString(BIRTHDAY, data.birthday)
                .putString(BIRTH_PLACE, data.birthPlace)
                .putString(ADDRESS, data.address)
                .putString(CITY, data.city)
                .putString(REASON, data.reason)
                .putString(PLACE, data.place)
                .apply()

        fun loadData(context: Context, onDataLoadedListener: (MainData) -> Unit) {
            onDataLoadedListener(PreferenceManager.getDefaultSharedPreferences(context.applicationContext).let {
                MainData(
                    it.getString(FIRSTNAME, null),
                    it.getString(LASTNAME, null),
                    it.getString(BIRTHDAY, null),
                    it.getString(BIRTH_PLACE, null),
                    it.getString(ADDRESS, null),
                    it.getString(CITY, null),
                    it.getString(REASON, null),
                    it.getString(PLACE, null),
                    null,
                    null
                )
            })
        }
    }
}