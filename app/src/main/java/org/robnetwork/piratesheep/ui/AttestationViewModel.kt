package org.robnetwork.piratesheep.ui

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.robnetwork.piratesheep.model.ListItemData

class AttestationViewModel(override val data: MutableLiveData<ListItemData> = MutableLiveData()) : BaseViewModel<ListItemData>() {
    fun loadData(context: Context, fileName: String) {
        data.value = ListItemData.listItemData(context, fileName)
    }
}