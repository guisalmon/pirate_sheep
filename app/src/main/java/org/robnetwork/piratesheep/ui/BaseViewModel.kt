package org.robnetwork.piratesheep.ui

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import org.robnetwork.piratesheep.model.BaseData

abstract class BaseViewModel<D : BaseData> : ViewModel() {
    protected abstract val data: MutableLiveData<D>

    fun registerObserver(owner: LifecycleOwner, observer: Observer<D>) =
        data.observe(owner, observer)

    open fun update(copy: (D) -> D) {
        data.value?.let {
            data.value = copy(it)
        } ?: Log.e(javaClass.simpleName, "Attempt to update uninitialized data")
    }
}