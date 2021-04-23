package course.ru.qsearcher.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.MessageAdapter
import course.ru.qsearcher.databinding.ActivityChatBinding
import course.ru.qsearcher.databinding.ActivityFavoritesBinding
import course.ru.qsearcher.model.Message
import course.ru.qsearcher.model.User
import kotlinx.android.synthetic.main.activity_chat.*

class ChatActivity : AppCompatActivity() {

    private var messageAdapter: MessageAdapter? = null
    private var database: FirebaseDatabase? = null
    private var messagesRef: DatabaseReference? = null
    private var usersRef: DatabaseReference? = null
    private var usersChildEventListener:ChildEventListener?=null
    private var userName: String? = null

    private var activityChatBinding: ActivityChatBinding? = null

    private var messagesChildEventListener: ChildEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        database = Firebase.database
        messagesRef = database!!.reference.child("message")
        usersRef = database!!.reference.child("users")
        usersChildEventListener = object:ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var user:User = snapshot.getValue(User::class.java)!!
                if(user.id == FirebaseAuth.getInstance().currentUser.uid){
                    userName = user.name
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                //TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                //TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                //TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }

        }
        usersRef?.addChildEventListener(usersChildEventListener as ChildEventListener)
//        messagesRef!!.setValue("Hello, World!")

        userName = SignInActivity.userName

        var lst: List<Message> = mutableListOf()
        messageAdapter = MessageAdapter(this, R.layout.message_item, lst)
        messageListView?.adapter = messageAdapter
        progressBar?.visibility = ProgressBar.INVISIBLE;
        messageEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sendMessageButton.isEnabled = false;
                if (p0.toString().trim().isNotEmpty())
                    sendMessageButton.isEnabled = true

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        Log.i("db", "дошел")
        messageEdit?.filters = arrayOf(InputFilter.LengthFilter(500))
        sendPhotoButton?.setOnClickListener { messageEdit?.setText("") }
        imageBack.setOnClickListener { onBackPressed() }
        sendMessageButton.setOnClickListener {

            var msg: Message = Message(messageEdit.text.toString(), userName!!, "")
//                msg.text = messageEdit.text.toString()
//                msg.name = userName as String
//                msg.imageURL = null.toString()
            messagesRef!!.push().setValue(msg)
            messageEdit?.setText(" ")
            Log.i("db", "должен был отправить")

        }
        messagesChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val msg: Message = snapshot.getValue(Message::class.java)!!
                messageAdapter?.add(msg)
                Log.i("db", msg.text)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        }
        messagesRef?.addChildEventListener(messagesChildEventListener as ChildEventListener)
        imageSignOut?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, SignInActivity::class.java))
        }
    }
}