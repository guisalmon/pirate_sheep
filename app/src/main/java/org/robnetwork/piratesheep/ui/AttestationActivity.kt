package org.robnetwork.piratesheep.ui

import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.ActivityAttestationBinding
import org.robnetwork.piratesheep.model.ListItemData

class AttestationActivity :
    BaseActivity<ActivityAttestationBinding, ListItemData, AttestationViewModel>() {
    override val layoutRes = R.layout.activity_attestation
    override val viewModelClass = AttestationViewModel::class.java

    override fun setupUI(binding: ActivityAttestationBinding) {
        super.setupUI(binding)
        intent.getStringExtra(EXTRA_FILENAME)?.let {
            viewModel.loadData(this, it)
        }
    }

    override fun updateUI(binding: ActivityAttestationBinding, data: ListItemData) {
        binding.attestationPage1.setImageBitmap(data.page1)
        binding.attestationPage2.setImageBitmap(data.page2)
        binding.attestationPage1.adjustViewBounds = true
        binding.attestationPage2.adjustViewBounds = true
    }

    companion object {
        const val EXTRA_FILENAME = "extra_filename"
    }
}