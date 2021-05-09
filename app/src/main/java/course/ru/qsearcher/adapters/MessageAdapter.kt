package course.ru.qsearcher.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import course.ru.qsearcher.R
import course.ru.qsearcher.model.Message
import java.lang.Exception


class MessageAdapter(context: Context, resource: Int, private var messages: MutableList<Message>) :
    ArrayAdapter<Message>(
        context,
        resource,
        messages
    ) {
    private var activity: Activity = context as Activity

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val viewHolder: ViewHolder
        val inflater: LayoutInflater =
            activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val msg: Message = getItem(position)!!
        val layoutResource: Int
        val viewType: Int = getItemViewType(position)

        layoutResource = if (viewType == 0) {
            R.layout.my_msg_item
        } else {
            R.layout.your_msg_item
        }
        var view = convertView
        if (view != null) {
            viewHolder = view.tag as ViewHolder
        } else {
            view = inflater.inflate(layoutResource, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }
        val isText: Boolean = msg.imageURL == ""
        if (isText) {
            viewHolder.msgText.visibility = View.VISIBLE
            viewHolder.msgPhoto.visibility = View.GONE
            viewHolder.msgText.text = msg.text
        } else {
            viewHolder.msgText.visibility = View.GONE
            viewHolder.msgPhoto.visibility = View.VISIBLE
            try {
                Picasso.get().load(msg.imageURL).noFade()
                    .into(viewHolder.msgPhoto, object : Callback {
                        override fun onSuccess() {}
                        override fun onError(e: Exception) {}
                    })
            } catch (e: Exception) {
            }
        }
        return view!!
    }

    override fun getItemViewType(position: Int): Int {
        var flag = 1
        val msg: Message = messages[position]
        if (msg.isMine) {
            flag = 0
        }
        return flag
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    private class ViewHolder(view: View) {
        var msgPhoto: ImageView = view.findViewById(R.id.msgImage)
        var msgText: TextView = view.findViewById(R.id.msgText)
    }

}