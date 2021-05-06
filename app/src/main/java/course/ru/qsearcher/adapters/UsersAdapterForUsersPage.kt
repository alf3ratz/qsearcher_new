package course.ru.qsearcher.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import course.ru.qsearcher.R
import course.ru.qsearcher.activities.SignInActivity
import course.ru.qsearcher.databinding.ItemContainerEventBinding
import course.ru.qsearcher.databinding.UserItemAtUserspageBinding
import course.ru.qsearcher.databinding.UserItemBinding
import course.ru.qsearcher.listeners.OnUserClickListener
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.model.User
import java.lang.Exception

class UsersAdapterForUsersPage(users:MutableList<User>,listener: OnUserClickListener) : RecyclerView.Adapter<UsersAdapterForUsersPage.UsersViewHolder>() {
    private var users: MutableList<User> = users
    private var listener: OnUserClickListener= listener
    private var layoutInflater: LayoutInflater? = null
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null



//    fun setOnCLickListener(listener: OnUserClickListener) {
//        this.listener = listener
//    }

    inner class UsersViewHolder(itemLayoutBinding:UserItemAtUserspageBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {
        private var itemLayoutBinding: UserItemAtUserspageBinding? = null

        init {
            this.itemLayoutBinding = itemLayoutBinding
            storage = FirebaseStorage.getInstance()
            storageRef = storage?.reference?.child("avatars")
        }
        fun bindUser(user:User) {
            itemLayoutBinding?.user = user
            storageRef?.child(user.superId + "avatar")?.downloadUrl?.addOnSuccessListener {
                Picasso.get().load(it).noFade().into(itemLayoutBinding?.avatar!!, object :
                    Callback {
                    override fun onSuccess() {
                        //itemLayoutBinding?.avatar!!.animate().setDuration(300).alpha(1f).start()
                    }

                    override fun onError(e: Exception) {
                    }
                })
                //activitySettingsBinding.userImage.setImageURI(uri)
            }?.addOnFailureListener {
                Log.i(
                    "usersAdapter",
                    "Не получилось загрузить аватар для " + user.superId
                )
            }
            //itemLayoutBinding?.avatar?.setImageResource(user.avatarMock)
            itemLayoutBinding?.executePendingBindings()
            if(itemLayoutBinding?.root!=null)
                Log.i("adapter"," рут не равен налл")
            itemView.setOnClickListener {
                listener.onUserCLick(user)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersViewHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        val usersBinding: UserItemAtUserspageBinding =
            DataBindingUtil.inflate(layoutInflater!!, R.layout.user_item_at_userspage, parent, false)
        return UsersViewHolder(usersBinding)
    }

    override fun onBindViewHolder(holder: UsersAdapterForUsersPage.UsersViewHolder, position: Int) {
        holder.bindUser(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }
}