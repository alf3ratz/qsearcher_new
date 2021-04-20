package course.ru.qsearcher.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.MessageAdapter
import course.ru.qsearcher.databinding.ActivityChatBinding
import course.ru.qsearcher.databinding.ActivityFavoritesBinding
import course.ru.qsearcher.model.Message
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private var messageAdapter: MessageAdapter? = null
    private var database: FirebaseDatabase? = null
    private var messagesRef: DatabaseReference?=null
    private var usersRef: DatabaseReference?=null
    private var userName: String? = null
    private var activityChatBinding: ActivityChatBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        database = Firebase.database
        messagesRef = database!!.reference.child("message")
        messagesRef!!.setValue("Hello, World!")

        userName = "Default User"

        var lst: List<Message> = mutableListOf()
        messageAdapter = MessageAdapter(this, R.layout.message_item, lst)
        messageListView?.adapter = messageAdapter
        progressBar?.visibility = ProgressBar.INVISIBLE;
        messageEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sendMessageButton.isEnabled = false;
                if (p0.toString().trim().isNotEmpty())
                    sendMessageButton.isEnabled = true

            }

            override fun afterTextChanged(p0: Editable?) {
                TODO("Not yet implemented")
            }

        })
        messageEdit?.filters = arrayOf(InputFilter.LengthFilter(500))
        sendPhotoButton?.setOnClickListener { View.OnClickListener { messageEdit?.setText("") } }
        imageBack.setOnClickListener { onBackPressed() }
        sendMessageButton.setOnClickListener{
            View.OnClickListener {
                var msg: Message = Message()
                msg.text = messageEdit.text.toString()
                msg.name = userName as String
                msg.imageURL= null.toString()
                messagesRef!!.push().setValue(msg)
                messageEdit?.setText("")
            }
        }

    }
}