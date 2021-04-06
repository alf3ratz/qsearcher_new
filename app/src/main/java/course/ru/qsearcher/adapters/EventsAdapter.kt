package course.ru.qsearcher.adapters

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.EventsAdapter.EventViewHolder
import course.ru.qsearcher.databinding.ItemContainerEventBinding
import course.ru.qsearcher.listeners.EventListener
import course.ru.qsearcher.model.Event
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import androidx.navigation.ui.NavigationUI


class EventsAdapter(events_: List<Event>,eventsListener_: EventListener) : RecyclerView.Adapter<EventViewHolder>() {

    private var events: List<Event> = events_
    private var layoutInflater: LayoutInflater?=null
    var eventsListener: EventListener = eventsListener_

    inner class EventViewHolder(itemLayoutBinding: ItemContainerEventBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {
        private var itemLayoutBinding: ItemContainerEventBinding? = null

        init {
            this.itemLayoutBinding = itemLayoutBinding
        }

        public fun bindEvent(event: Event) {
            itemLayoutBinding?.event = event
            itemLayoutBinding?.executePendingBindings()
            Log.i("adapter"," в ивент адаптере1")
            //Toast.makeText(layoutInflater?.context,"в ИвентАдаптере1",Toast.LENGTH_SHORT)
            if(itemLayoutBinding?.root!=null)
                Log.i("adapter"," рут не равен налл")


            itemView.setOnClickListener {
                eventsListener.onEventClicked(event)
                Log.i("adapter"," в ивент адаптере2222")

                //view.navigate(R.id.action_titleFragment_to_gameFragment)

            }
            //itemLayoutBinding?.root?.setOnClickListener (i:View.)


//                    eventsListener.onEventClicked(event)
//                    Log.i("adapter"," в ивент адаптере2222")
//                    Toast.makeText(Application().applicationContext,"в ИвентАдаптере",Toast.LENGTH_SHORT)
                    //                    View.OnClickListener {
                    //                        eventsListener.onEventClicked(event)
                    //                    }



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