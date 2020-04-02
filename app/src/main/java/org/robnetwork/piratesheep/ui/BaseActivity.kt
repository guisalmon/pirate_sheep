package org.robnetwork.piratesheep.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity() {
    @get:LayoutRes
    abstract val layoutRes: Int

    protected var binding: B? = null
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<B>(this, layoutRes)?.apply { setupUI(this) }
    }

    @CallSuper
    protected open fun setupUI(binding: B) {
        this.binding = binding
    }
}