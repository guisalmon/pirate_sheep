package org.robnetwork.piratesheep.ui

import android.content.Context
import android.content.Intent
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
        binding.listRecycler.adapter = ListItemAdapter({ item ->
            startActivity(Intent(context, AttestationActivity::class.java).apply {
                putExtra(AttestationActivity.EXTRA_FILENAME, item.fileName)
            })
        }, { deleteMode ->
            viewModel.data.value?.let {
                viewModel.data.value = it.copy(deleteMode = deleteMode)
            }
        }, { selectionToDelete ->
            viewModel.update { it.copy(selectionToDelete = selectionToDelete) }
        })
    }

    override fun updateUI(binding: FragmentListBinding, data: MainData) {
        (binding.listRecycler.adapter as? ListItemAdapter)?.update(data)
    }
}

class ListItemAdapter(
    private val onItemClick: (ListItemData) -> Unit,
    private val onDeleteModeChanged: (Boolean) -> Unit,
    private val onItemCheckChanged: (MutableList<ListItemData>) -> Unit
) : RecyclerView.Adapter<ListItemAdapter.ListItemVH>() {
    private val data: MutableList<ListItemData> = mutableListOf()
    private var selectionMode: Boolean = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ListItemVH(
            LayoutInflater.from(parent.context).inflate(
                R.layout.list_item_attestation,
                parent,
                false
            )
        )

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ListItemVH, position: Int) {
        holder.bind(data[position], selectionMode, onItemClick, {
            onDeleteModeChanged(!selectionMode)
        }, { checkedItem, isChecked ->
            data.indexOf(checkedItem).let { index ->
                data[index].toDelete = isChecked
            }
            onItemCheckChanged(data.filter { it.toDelete }.toMutableList())
        })
    }

    fun update(mainData: MainData) {
        data.clear()
        data.addAll(mainData.list)
        selectionMode = mainData.deleteMode
        notifyDataSetChanged()
    }

    inner class ListItemVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ListItemAttestationBinding? = DataBindingUtil.bind(itemView)

        fun bind(
            item: ListItemData,
            selectionMode: Boolean,
            onItemClick: (ListItemData) -> Unit,
            onItemLongClick: (ListItemData) -> Unit,
            onItemSelected: (ListItemData, Boolean) -> Unit
        ) {
            binding?.let {
                it.itemCheckbox.setOnCheckedChangeListener(null)
                it.itemLabel.text = item.fileName
                it.qrcodeView.setImageBitmap(item.code)
                if (selectionMode) it.itemCheckbox.isChecked = item.toDelete
                it.itemCheckbox.visibility = if (selectionMode) View.VISIBLE else View.GONE
                it.itemContainer.setOnClickListener { onItemClick(item) }
                it.itemContainer.setOnLongClickListener { onItemLongClick(item).let { true } }
                it.itemCheckbox.setOnCheckedChangeListener { _, isChecked ->
                    onItemSelected(item, isChecked)
                }
            }
        }
    }
}