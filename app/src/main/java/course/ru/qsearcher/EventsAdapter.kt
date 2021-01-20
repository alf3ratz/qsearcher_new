package course.ru.qsearcher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import course.ru.qsearcher.databinding.ItemContainerEventBinding
import course.ru.qsearcher.model.Event

class EventsAdapter(events_: List<Event>) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    private var events: List<Event> = events_
    private var layoutInflater: LayoutInflater?=null

    class EventViewHolder(itemLayoutBinding: ItemContainerEventBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {
        private var itemLayoutBinding: ItemContainerEventBinding? = null

        init {
            this.itemLayoutBinding = itemLayoutBinding
        }

        public fun bindEvent(event: Event) {
            itemLayoutBinding?.event = event
            itemLayoutBinding?.executePendingBindings()
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        var eventBinding: ItemContainerEventBinding =
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