package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector.MeteoriteSelector
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import java.util.*

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
class MeteoriteAdapter(
        private val meteoriteSelector: MeteoriteSelector,
        private val viewModel: MeteoriteListViewModel,
        differ: MeteoriteDiffCallback
) : PagedListAdapter<Meteorite, ViewHolderMeteorite>(differ) {

    private var selectedMeteorite: Meteorite? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMeteorite {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_meteorite, parent, false)
        val vh = ViewHolderMeteorite(view)

        //On view click use MeteoriteSelector to do execute the proper according the current layout
        view.setOnClickListener {
            vh.meteorite?.let { meteoriteSelector.selectItem(it) }
        }
        return vh
    }

    fun updateListUI(meteorite: Meteorite) {
        if (!Objects.equals(meteorite, selectedMeteorite)) {

            val previousMet = selectedMeteorite
            selectedMeteorite = meteorite

            currentList?.indexOf(previousMet)?.let { notifyItemChanged(it) }
            currentList?.indexOf(meteorite)?.let { notifyItemChanged(it) }
        }
    }

    fun setData(meteorites: PagedList<Meteorite>) {
        if (itemCount != meteorites.size && currentList != meteorites) {
            submitList(null)
            notifyDataSetChanged()
        }
        submitList(meteorites)
    }

    override fun onBindViewHolder(viewHolder: ViewHolderMeteorite, position: Int) {
        getItem(position)?.let { meteorite ->
            viewHolder.onBind(meteorite, selectedMeteorite, viewModel.getLocation())
        }
    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        return if (item != null) {
            (item.id.hashCode() + item.name.hashCode()).toLong()
        } else {
            super.getItemId(position)
        }
    }

}