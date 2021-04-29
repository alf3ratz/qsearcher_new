package course.ru.qsearcher.activities

import android.content.Intent
import android.net.Uri
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import course.ru.qsearcher.R
import course.ru.qsearcher.adapters.MessageAdapter
import course.ru.qsearcher.databinding.ActivityChatBinding
import course.ru.qsearcher.databinding.ActivityFavoritesBinding
import course.ru.qsearcher.model.Message
import course.ru.qsearcher.model.User
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_main.*

class ChatActivity : AppCompatActivity() {
    private val RC_IMAGE: Int = 1

    private var messageAdapter: MessageAdapter? = null
    private var database: FirebaseDatabase? = null
    private var messagesRef: DatabaseReference? = null
    private var usersRef: DatabaseReference? = null
    private var usersChildEventListener: ChildEventListener? = null
    private var userName: String? = null

    private var activityChatBinding: ActivityChatBinding? = null

    private var messagesChildEventListener: ChildEventListener? = null
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null

    private lateinit var auth: FirebaseAuth

    // Айди юзера-полчателя, то есть того, с кем мы ведем диалог. Нужен чтобы достать из базы все данные о пользователе.
    private lateinit var receiverUserId: String
    // Имя пользователя, с которым ведется диалог. Отображается вверху экрана.
    private lateinit var receiverUserName:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityChatBinding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        auth = FirebaseAuth.getInstance()
        val intent: Intent = intent
        if (intent != null) {
            userName = intent.getStringExtra("userName")
            receiverUserId = intent.getStringExtra("receiverId")!!
            receiverUserName = intent.getStringExtra("receiverUserName")!!
        } else {
            userName = "Default_User"
            receiverUserId = intent.getStringExtra("receiverId")!!
        }
        // Задаем значение надписи вверху страницы.
        title = receiverUserId


        database = Firebase.database
        storage = FirebaseStorage.getInstance()
        storageRef = storage?.reference?.child("chat_images")
        messagesRef = database!!.reference.child("message")
        usersRef = database!!.reference.child("users")

        usersChildEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val user: User = snapshot.getValue(User::class.java)!!
                if (user.id == FirebaseAuth.getInstance().currentUser.uid) {
                    userName = user.name
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        }
        usersRef?.addChildEventListener(usersChildEventListener as ChildEventListener)
//        messagesRef!!.setValue("Hello, World!")
        //userName = SignInActivity.userName

        var lst: MutableList<Message> = mutableListOf()
        messageAdapter = MessageAdapter(this, R.layout.message_item, lst)
        messageListView?.adapter = messageAdapter
        progressBar?.visibility = ProgressBar.INVISIBLE;
        messageEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                sendMessageButton.isEnabled = false;
                if (p0.toString().trim().isNotEmpty())
                    sendMessageButton.isEnabled = true
            }

            override fun afterTextChanged(p0: Editable?) {}

        })
        Log.i("db", "дошел")
        messageEdit?.filters = arrayOf(InputFilter.LengthFilter(500))
        sendPhotoButton?.setOnClickListener {
            var intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
            startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), RC_IMAGE)
        }
        imageBack.setOnClickListener { onBackPressed() }
        sendMessageButton.setOnClickListener {

            val msg: Message = Message(
                messageEdit.text.toString(),
                userName!!,
                "",
                auth.currentUser.uid,
                receiverUserId,
                true
            )

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
                if (msg.sender == auth.currentUser.uid && msg.receiver == receiverUserId) {
                    msg.isMine = true
                    messageAdapter?.add(msg)
                    Log.i("db", msg.text)
                } else if (msg.receiver == auth.currentUser.uid && msg.sender == receiverUserId) {
                    msg.isMine = false
                    messageAdapter?.add(msg)
                    Log.i("db", msg.text)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        }
        messagesRef?.addChildEventListener(messagesChildEventListener as ChildEventListener)
        imageSignOut?.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(applicationContext, SignInActivity::class.java))
        }
        activityChatBinding?.imageUsers?.setOnClickListener {
            startActivity(Intent(applicationContext, UsersActivity::class.java))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_IMAGE && resultCode == RESULT_OK) {
            var selectedImage: Uri = data?.data!!
            var imgRef: StorageReference = storageRef?.child(selectedImage.lastPathSegment!!)!!
            //selectedImage.lastPathSegment?.let { storageRef?.child(it) }!!
            var uploadTask: UploadTask = imgRef.putFile(selectedImage)
            uploadTask = imgRef.putFile(selectedImage)
            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                imgRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val msg: Message = Message()
                    msg.imageURL = downloadUri.toString()
                    msg.name = userName!!
                    msg.sender = auth.currentUser.uid
                    msg.receiver = receiverUserId
                    messagesRef?.push()?.setValue(msg)
                } else {
                    // Handle failures
                    // ...
                }
            }
        }

    }
}