package course.ru.qsearcher.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ActivitySignInBinding
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {
    private var activitySignInBinding: ActivitySignInBinding? = null
    private var auth: FirebaseAuth? = null
    private var loginMode: Boolean = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth
        if (auth?.currentUser != null)
            startActivity(Intent(applicationContext, MainActivity::class.java))
        activitySignInBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in)
        loginSignUpButton.setOnClickListener {
            Log.i("login","voshel")
            loginSignUp(
                emailEditText.text.toString().trim(),
                passwordEditText.text.toString().trim()
            )
        }
    }

    private fun loginSignUp(email: String, password: String) {
        if (loginMode) {
            auth?.signInWithEmailAndPassword(email, password)
                ?.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("auth", "createUserWithEmail:success")
                        val user = auth?.currentUser
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        //updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("auth", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        // updateUI(null)
                    }
                }
        } else {
            Log.i("login","v create")
            when {
                passwordEditText.text.toString().trim() != confirmPasswordEditText.text.toString()
                    .trim() -> {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                }
                passwordEditText.text.toString().trim().length<7 -> {
                    Toast.makeText(this, "Пароль должен содердать минимум 7 символов", Toast.LENGTH_SHORT).show()
                }
                emailEditText.text.toString()==""->{
                    Toast.makeText(this, "Введите почту", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.i("login","sozdaet")
                    Log.i("login",email+" _ "+password)
                    auth?.createUserWithEmailAndPassword(email, password)
                        ?.addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("auth", "createUserWithEmail:success")
                                val user = auth?.currentUser
                                createUser(user)
                                startActivity(Intent(this, MainActivity::class.java))
                                //updateUI(user)
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i("authq", "createUserWithEmail:failure", task.exception)
                                Toast.makeText(
                                    baseContext, "Authentication failed.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // updateUI(null)
                            }
                        }
                }
            }
        }
    }

    private fun createUser(user: FirebaseUser?) {
       var  newUser:User
    }

    fun toggleLoginMode(view: View) {
        if (loginMode) {
            loginMode = false
            loginSignUpButton.text = "зарегистрироваться"
            toggleLoginTextView.text = "Или войдите"
        } else {
            loginMode = true
            loginSignUpButton.text = "Войдите"
            toggleLoginTextView.text = "или зарегистрируйтесь"
        }

    }
}