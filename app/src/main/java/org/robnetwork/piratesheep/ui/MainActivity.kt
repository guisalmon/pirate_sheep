package org.robnetwork.piratesheep.ui

import android.app.Activity
import android.content.Intent
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.ActivityMainBinding
import org.robnetwork.piratesheep.model.MainData

class MainActivity : BaseActivity<ActivityMainBinding, MainData, MainViewModel>() {
    override val layoutRes: Int = R.layout.activity_main
    override val viewModelClass = MainViewModel::class.java

    override fun updateUI(data: MainData) {}

    override fun onPause() {
        super.onPause()
        viewModel.storeData(this)
    }

    override fun setupUI(binding: ActivityMainBinding) {
        super.setupUI(binding)
        binding.bottomNavigation.setupWithNavController(findNavController(R.id.nav_host_fragment))
        viewModel.loadData(this, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            resultData?.data?.also {
                when (requestCode) {
                    CREATE_FILE -> {
                        viewModel.saveForm(this, it)
                        //openPdf(it)
                    }
                    PICK_PDF_FILE -> startActivity(Intent().apply {
                        action = Intent.ACTION_VIEW
                        setDataAndType(it, "application/pdf")
                    })
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, resultData)
    }

    companion object {
        const val CREATE_FILE = 1
        const val PICK_PDF_FILE = 2
    }
}