package org.robnetwork.piratesheep.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity<B : ViewDataBinding, D : BaseData, VM : BaseViewModel<D>> :
    AppCompatActivity() {
    @get:LayoutRes
    abstract val layoutRes: Int
    abstract val viewModelClass: Class<VM>

    protected lateinit var viewModel: VM

    protected var binding: B? = null
        private set

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(viewModelClass)
        DataBindingUtil.setContentView<B>(this, layoutRes)?.apply { setupUI(this) }
    }

    @CallSuper
    protected open fun setupUI(binding: B) {
        this.binding = binding
        viewModel.data.observe(
            this,
            Observer {
                updateUI(it)
            }
        )
    }

    abstract fun updateUI(data: D)
}

interface BaseData

abstract class BaseViewModel<D : BaseData> : ViewModel() {
    abstract val data: MutableLiveData<D>
}