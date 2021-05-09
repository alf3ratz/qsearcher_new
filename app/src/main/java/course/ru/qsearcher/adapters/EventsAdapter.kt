package course.ru.qsearcher.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.EventsAdapter.EventViewHolder
import course.ru.qsearcher.databinding.ItemContainerEventBinding
import course.ru.qsearcher.listeners.EventListener
import course.ru.qsearcher.model.Event



class EventsAdapter(events_: List<Event>, eventsListener_: EventListener) :
    RecyclerView.Adapter<EventViewHolder>() {

    private var events: List<Event> = events_
    private var layoutInflater: LayoutInflater? = null
    var eventsListener: EventListener = eventsListener_

    inner class EventViewHolder(itemLayoutBinding: ItemContainerEventBinding) :
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
                    eventsListener.onEventClicked(event)
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        val eventBinding: ItemContainerEventBinding =
            DataBindingUtil.inflate(layoutInflater!!, R.layout.item_container_event, parent, false)
        return EventViewHolder(eventBinding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bindEvent(events[position])
    }

    override fun getItemCount(): Int {
        return events.size
    }
}