package org.robnetwork.piratesheep.ui

import android.os.Bundle
import android.util.Log
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.*
import org.robnetwork.piratesheep.model.BaseData

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
        viewModel.registerObserver(this, Observer { updateUI(binding, it) })
    }

    abstract fun updateUI(binding: B, data: D)
}