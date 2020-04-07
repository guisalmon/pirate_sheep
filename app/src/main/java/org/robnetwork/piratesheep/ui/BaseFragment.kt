package org.robnetwork.piratesheep.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.fragment.NavHostFragment
import org.robnetwork.piratesheep.model.BaseData

abstract class BaseFragment<B : ViewDataBinding, D : BaseData, VM : BaseViewModel<D>> : Fragment() {
    @get:LayoutRes
    protected abstract val layoutRes: Int
    abstract val viewModelClass: Class<VM>

    protected lateinit var viewModel: VM

    protected var binding: B? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = DataBindingUtil.inflate<B>(inflater, layoutRes, container, false)?.apply {
        binding = this
        getModelStoreOwner()?.let { viewModel = ViewModelProvider(it).get(viewModelClass) }
    }?.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.let { binding -> context?.let { setupUI(binding, it) } }
    }

    @CallSuper
    protected open fun setupUI(binding: B, context: Context) {
        viewModel.registerObserver(this, Observer { updateUI(binding, it) })
    }


    protected abstract fun getModelStoreOwner(): ViewModelStoreOwner?

    protected abstract fun updateUI(binding: B, data: D)

    protected fun navigateTo(navId: Int) {
        NavHostFragment.findNavController(this).navigate(navId)
    }
}