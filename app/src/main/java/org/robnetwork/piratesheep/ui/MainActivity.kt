package org.robnetwork.piratesheep.ui

import android.os.Bundle
import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>() {

	override val layoutRes: Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

	override fun setupUI(binding: ActivityMainBinding) {
		super.setupUI(binding)
		binding.toolbar.toolbarTitle.text = "niklapolis"
	}
}
