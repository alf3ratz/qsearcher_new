package course.ru.qsearcher.adapters

import android.app.Activity
import android.content.Context
import android.icu.number.NumberFormatter.with
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.MyMsgItemBinding
import course.ru.qsearcher.model.Message
import org.w3c.dom.Text
import java.lang.Exception


class MessageAdapter(context: Context, resource: Int, messages:MutableList<Message> ) :
    ArrayAdapter<Message>(
        context,
        resource,
        messages
    ) {
    private var messages:MutableList<Message> = messages
    private var activity: Activity = context as Activity



    var layoutInflater: LayoutInflater? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var viewHolder:ViewHolder
        var inflater:LayoutInflater = activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var msg:Message = getItem(position)!!
        var layoutResource:Int = 0
        val viewType:Int = getItemViewType(position)

        layoutResource = if(viewType ==0){
            R.layout.my_msg_item
        }else{
            R.layout.your_msg_item
        }
        var view = convertView
        if(view!=null){
            viewHolder = view.tag as ViewHolder
        }else{
            view = inflater?.inflate(layoutResource, parent, false)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }
        val isText: Boolean = msg.imageURL == ""
        if(isText){
            viewHolder.msgText.visibility = View.VISIBLE
            viewHolder.msgPhoto.visibility = View.GONE
            viewHolder.msgText.text = msg.text
        }else{
            viewHolder.msgText.visibility = View.GONE
            viewHolder.msgPhoto.visibility = View.VISIBLE
            try {
                Picasso.get().load(msg.imageURL).noFade().into(viewHolder.msgPhoto, object : Callback {
                    override fun onSuccess() {
                        // photoImageView.animate().setDuration(300).alpha(1f).start()
                    }

                    override fun onError(e: Exception) {
                    }
                })
            } catch (e: Exception) {
            }
        }
//        if (layoutInflater == null)
//            layoutInflater = LayoutInflater.from(parent.context)
//       // var view = convertView
//        if (view == null) {
//            view = layoutInflater?.inflate(R.layout.message_item, parent, false)
//        }
//        val photoImageView: ImageView = view?.findViewById(R.id.photoImageView)!!
//        val textTextView: TextView = view.findViewById(R.id.textTextView)!!
//        val nameTextView: TextView = view.findViewById(R.id.nameTextView)!!
//        val message: Message = getItem(position)!!
//        val isText: Boolean = message.imageURL == ""
//        if (isText) {
//            textTextView.visibility = View.VISIBLE
//            photoImageView.visibility = View.GONE
//            textTextView.text = message.text
//        }else{
//            textTextView.visibility = View.VISIBLE//View.GONE
//            photoImageView.visibility = View.VISIBLE
//            try {
//                Picasso.get().load(message.imageURL).noFade().into(photoImageView, object : Callback {
//                    override fun onSuccess() {
//                       // photoImageView.animate().setDuration(300).alpha(1f).start()
//                    }
//
//                    override fun onError(e: Exception) {
//                    }
//                })
//            } catch (e: Exception) {
//            }
//        }
//        nameTextView.text=message.name
        return view!!
    }

    override fun getItemViewType(position: Int): Int {
        //return super.getItemViewType(position)
        var flag:Int = 1
        val msg:Message = messages.get(position)
        if(msg.isMine){
            flag = 0
        }
        return flag;
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    private class ViewHolder(view:View){
        var msgPhoto:ImageView = view.findViewById(R.id.msgImage)
        var msgText:TextView = view.findViewById(R.id.msgText)

    }

}