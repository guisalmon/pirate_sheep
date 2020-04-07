package org.robnetwork.piratesheep.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.robnetwork.piratesheep.R
import org.robnetwork.piratesheep.databinding.FragmentListBinding
import org.robnetwork.piratesheep.databinding.ListItemAttestationBinding
import org.robnetwork.piratesheep.model.ListItemData
import org.robnetwork.piratesheep.model.MainData

class ListFragment : BaseFragment<FragmentListBinding, MainData, MainViewModel>() {
    override val layoutRes = R.layout.fragment_list
    override val viewModelClass = MainViewModel::class.java

    override fun getModelStoreOwner() = (activity as? MainActivity)

    override fun setupUI(binding: FragmentListBinding, context: Context) {
        super.setupUI(binding, context)
        binding.listRecycler.layoutManager = LinearLayoutManager(context)
    }

    override fun updateUI(binding: FragmentListBinding, data: MainData) {
        binding.listRecycler.adapter = ListItemAdapter(data.list)
    }
}

class ListItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val binding: ListItemAttestationBinding? = DataBindingUtil.bind(itemView)

    fun bind(itemData: ListItemData) {
        binding?.let {
            it.itemLabel.text = itemData.fileName
            it.qrcodeView.setImageBitmap(itemData.code)
        }
    }
}

class ListItemAdapter(
    private val data: MutableList<ListItemData>
) : RecyclerView.Adapter<ListItemVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ListItemVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_attestation,
                parent,
                false
            )
        )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ListItemVH, position: Int) = holder.bind(data[position])

}