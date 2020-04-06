package org.robnetwork.piratesheep.ui

import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.FragmentCreationBinding
import org.robnetwork.piratesheep.model.MainData

class ListFragment : BaseFragment<FragmentCreationBinding, MainData, MainViewModel>() {
    override val layoutRes = R.layout.fragment_list
    override val viewModelClass = MainViewModel::class.java

    override fun updateUI(data: MainData) {}
}