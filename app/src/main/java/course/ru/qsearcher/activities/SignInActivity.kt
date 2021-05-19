package course.ru.qsearcher.activities

import course.ru.qsearcher.R
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import course.ru.qsearcher.databinding.ActivitySignInBinding
import course.ru.qsearcher.model.User
import kotlinx.android.synthetic.main.activity_sign_in.*
import java.io.ByteArrayOutputStream


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SignInActivity : AppCompatActivity() {
    private var activitySignInBinding: ActivitySignInBinding? = null
    private var auth: FirebaseAuth? = null
    private var database: FirebaseDatabase? = null
    private var usersDbRef: DatabaseReference? = null
    private var loginMode: Boolean = false
    private var usersChildEventListener: ChildEventListener? = null

    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null

    companion object {
        var userName: String = ""
        var currentUser: User = User()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        initialize()
    }

    /**
     * Метод инициализирующий поля доступа к бд и в аутентификации, различные вспомогательные поля и дочерные view страницы регистрации.
     */
    private fun initialize() {
        auth = Firebase.auth
        if (auth?.currentUser != null) {
            database = FirebaseDatabase.getInstance()
            usersDbRef = database?.reference?.child("users")
            usersChildEventListener = object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val user: User = snapshot.getValue(User::class.java)!!
                    if (user.id == FirebaseAuth.getInstance().currentUser.uid) {
                        userName = user.name!!
                        currentUser = user
                    }
                }

                override fun onChildChanged(
                    snapshot: DataSnapshot,
                    previousChildName: String?
                ) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {}
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                override fun onCancelled(error: DatabaseError) {}
            }
            usersDbRef?.addChildEventListener(usersChildEventListener as ChildEventListener)
            startActivity(Intent(applicationContext, MainActivity::class.java))
        }
        database = FirebaseDatabase.getInstance()
        usersDbRef = database?.reference?.child("users")
        activitySignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        loginSignUpButton.setOnClickListener {
            loginMode = false
            loginSignUp(
                emailEditText.text.toString().trim(),
                passwordEditText.text.toString().trim()
            )
        }
        toggleLoginTextView.setOnClickListener {
            loginMode = true
            loginSignUp(
                emailEditText.text.toString().trim(),
                passwordEditText.text.toString().trim()
            )
        }
    }

    /**
     * Метод, описывающий логику регистрации нового пользователя в приложении или входа в него уже зарегистрированного пользователя.
     */
    private fun loginSignUp(email: String, password: String) {
        if (loginMode) {
            when {
                passwordEditText.text.toString().trim() != confirmPasswordEditText.text.toString()
                    .trim() -> {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_LONG).show()
                }
                passwordEditText.text.toString().trim().length < 7 -> {
                    Toast.makeText(
                        this,
                        "Пароль должен содердать минимум 7 символов",
                        Toast.LENGTH_LONG
                    ).show()
                }
                emailEditText.text.toString() == "" -> {
                    Toast.makeText(this, "Введите почту", Toast.LENGTH_LONG).show()
                }
                nameEditText.text.toString().trim() == "" -> {
                    Toast.makeText(this, "Введите имя пользователя", Toast.LENGTH_LONG).show()

                }
                else -> auth?.signInWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            startActivity(
                                Intent(
                                    this,
                                    MainActivity::class.java
                                ).putExtra("userName", nameEditText.text.toString().trim())
                            )
                        } else {
                            Toast.makeText(
                                baseContext, "Такого пользователя на существует",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }

        } else {
            when {
                passwordEditText.text.toString().trim() != confirmPasswordEditText.text.toString()
                    .trim() -> {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_LONG).show()
                }
                passwordEditText.text.toString().trim().length < 7 -> {
                    Toast.makeText(
                        this,
                        "Пароль должен содердать минимум 7 символов",
                        Toast.LENGTH_LONG
                    ).show()
                }
                emailEditText.text.toString() == "" -> {
                    Toast.makeText(this, "Введите почту", Toast.LENGTH_LONG).show()
                }
                nameEditText.text.toString().trim() == "" -> {
                    Toast.makeText(this, "Введите имя пользователя", Toast.LENGTH_LONG).show()

                }
                else -> {
                    auth?.createUserWithEmailAndPassword(email, password)
                        ?.addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth?.currentUser
                                createUser(user)
                                startActivity(
                                    Intent(
                                        this,
                                        MainActivity::class.java
                                    ).putExtra("userName", nameEditText.text.toString().trim())
                                )
                            } else {
                                Toast.makeText(
                                    baseContext, "Произошла ошибка аутентификации",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }
        }
    }

    /**
     * Метод, создающий объект пользователя и добавлябщий его в базу данныъ
     */
    private fun createUser(user: FirebaseUser?) {
        val newUser = User()
        newUser.name = nameEditText.text.toString().trim()
        newUser.email = user!!.email
        newUser.id = user.uid
        when ((0..5).shuffled().last()) {
            0 -> {
                newUser.avatarMock = R.drawable.avatar1
            }
            1 -> {
                newUser.avatarMock = R.drawable.avatar2
            }
            2 -> {
                newUser.avatarMock = R.drawable.avatar3
            }
            3 -> {
                newUser.avatarMock = R.drawable.avatar4
            }
            4 -> {
                newUser.avatarMock = R.drawable.avatar5
            }
            5 -> {
                newUser.avatarMock = R.drawable.avatar6
            }
        }
        newUser.favList = mutableListOf()
        newUser.usersList = mutableListOf()
        newUser.occupation = "-"
        newUser.city = "-"
        newUser.socialNetworkUrl = "-"
        newUser.superId =
            newUser.email!!.replace(".", "").replace("#", "").replace("$", "").replace("[", "")
                .replace("]", "")
        storage = FirebaseStorage.getInstance()
        storageRef = storage?.reference?.child("avatars")
        val imgRef: StorageReference = storageRef?.child(newUser.superId + "avatar")!!
        val bm = BitmapFactory.decodeResource(this.resources, newUser.avatarMock)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask: UploadTask = imgRef.putBytes(data)
//        val urlTask = uploadTask.continueWithTask { task ->
////            if (!task.isSuccessful) {
////                task.exception?.let {
////                    Log.i("avatarSign", "отправил?")
////                    throw it
////                }
////            }
////            imgRef.downloadUrl
////        }.addOnCompleteListener { task ->
////            if (task.isSuccessful) {
////                Log.i("avatarSign", "отправил!")
////                //Log.i("avatarSign","отправил?")
//////                val downloadUri = task.result
//////                val msg: Message = Message()
//////                msg.imageURL = downloadUri.toString()
//////                msg.name = userName!!
//////                msg.sender = auth.currentUser.uid
//////                msg.receiver = receiverUserId
//////                messagesRef?.push()?.setValue(msg)
////            } else {
////                // Handle failures
////                // ...
////            }
        //   }
        usersDbRef?.child(newUser.superId!!)?.setValue(newUser)
        userName = newUser.name!!
        currentUser = newUser
    }

//    fun toggleLoginMode(view: View) {
//        if (loginMode) {
//            loginMode = false
//            loginSignUpButton.text = "зарегистрироваться"
//            toggleLoginTextView.text = "Или войдите"
//        } else {
//            loginMode = true
//            loginSignUpButton.text = "Войдите"
//            toggleLoginTextView.text = "или зарегистрируйтесь"
//        }
//    }
}