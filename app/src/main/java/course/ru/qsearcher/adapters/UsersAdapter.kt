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
import course.ru.qsearcher.databinding.UserItemBinding
import course.ru.qsearcher.listeners.OnUserClickListener
import course.ru.qsearcher.model.User
import java.lang.Exception

class UsersAdapter(
    private var users: MutableList<User>,
    private var listener: OnUserClickListener
) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {
    private var layoutInflater: LayoutInflater? = null
    private var storage: FirebaseStorage? = null
    private var storageRef: StorageReference? = null


    inner class UsersViewHolder(itemLayoutBinding: UserItemBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {
        private var itemLayoutBinding: UserItemBinding? = null

        init {
            this.itemLayoutBinding = itemLayoutBinding
            storage = FirebaseStorage.getInstance()
            storageRef = storage?.reference?.child("avatars")
        }

        fun bindUser(user: User) {
            itemLayoutBinding?.user = user
            storageRef?.child(user.superId + "avatar")?.downloadUrl?.addOnSuccessListener {
                Picasso.get().load(it).noFade().into(itemLayoutBinding?.avatar!!, object :
                    Callback {
                    override fun onSuccess() {
                    }

                    override fun onError(e: Exception) {
                    }
                })
            }?.addOnFailureListener {

            }
            itemLayoutBinding?.executePendingBindings()
            if (itemLayoutBinding?.root != null)
                itemView.setOnClickListener {
                    listener.onUserClick(user)
                }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersViewHolder {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.context)
        val usersBinding: UserItemBinding =
            DataBindingUtil.inflate(layoutInflater!!, R.layout.user_item, parent, false)
        return UsersViewHolder(usersBinding)
    }

    override fun onBindViewHolder(holder: UsersAdapter.UsersViewHolder, position: Int) {
        holder.bindUser(users[position])
    }

    override fun getItemCount(): Int {
        return users.size
    }
}