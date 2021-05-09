package course.ru.qsearcher.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ItemContainerEventBinding
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.listeners.FavoritesListener


class FavoritesAdapter(events_: List<Event>, favoritesListener_: FavoritesListener) :
    RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    private var events: List<Event> = events_
    private var layoutInflater: LayoutInflater? = null
    var favoritesListener: FavoritesListener = favoritesListener_

    inner class FavoritesViewHolder(itemLayoutBinding: ItemContainerEventBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {
        private var itemLayoutBinding: ItemContainerEventBinding? = null

        init {
            this.itemLayoutBinding = itemLayoutBinding
        }

        fun bindEvent(event: Event) {
            itemLayoutBinding?.event = event
            itemLayoutBinding?.executePendingBindings()
            if (itemLayoutBinding?.root != null)
                itemView.setOnClickListener {
                    favoritesListener.onEventClicked(event)
                }
            itemLayoutBinding?.imageDelete?.setOnClickListener {
                favoritesListener.removeEventFromFavorites(
                    event, adapterPosition
                )
            }
            itemLayoutBinding?.imageDelete?.visibility = View.VISIBLE
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        val eventBinding: ItemContainerEventBinding =
            DataBindingUtil.inflate(layoutInflater!!, R.layout.item_container_event, parent, false)
        return FavoritesViewHolder(eventBinding)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        holder.bindEvent(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }
}