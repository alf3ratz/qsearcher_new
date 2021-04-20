package course.ru.qsearcher.adapters

import android.content.Context
import android.icu.number.NumberFormatter.with
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


class MessageAdapter(context: Context, resource: Int, messages: List<Message>) :
    ArrayAdapter<Message>(
        context,
        resource,
        messages
    ) {

    var layoutInflater: LayoutInflater? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        var view = convertView
        if (view == null) {
            view = layoutInflater?.inflate(R.layout.message_item, parent, false)
        }
        var photoImageView: ImageView = view?.findViewById(R.id.photoImageView)!!
        var textTextView: TextView = view.findViewById(R.id.textTextView)!!
        var nameTextView: TextView = view.findViewById(R.id.nameTextView)!!
        var message: Message = getItem(position)!!
        var isText: Boolean = message.imageURL == null
        if (isText) {
            textTextView.visibility = View.VISIBLE
            photoImageView.visibility = View.GONE
            textTextView.text = message.text.toString()
        }else{
            textTextView.visibility = View.GONE
            photoImageView.visibility = View.VISIBLE
            try {
                Picasso.get().load(message.imageURL).noFade().into(photoImageView, object : Callback {
                    override fun onSuccess() {
                        photoImageView.animate().setDuration(300).alpha(1f).start()
                    }

                    override fun onError(e: Exception) {
                    }
                })
            } catch (e: Exception) {
            }
        }
        nameTextView.text=message.name

        return view
    }
}