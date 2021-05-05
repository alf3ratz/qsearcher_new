package course.ru.qsearcher.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import course.ru.qsearcher.R
import course.ru.qsearcher.databinding.ItemContainerEventBinding
import course.ru.qsearcher.databinding.UserItemBinding
import course.ru.qsearcher.listeners.OnUserClickListener
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.model.User

class UsersAdapter(users:MutableList<User>,listener: OnUserClickListener) : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {
    private var users: MutableList<User> = users
    private var listener: OnUserClickListener= listener
    private var layoutInflater: LayoutInflater? = null



//    fun setOnCLickListener(listener: OnUserClickListener) {
//        this.listener = listener
//    }

    inner class UsersViewHolder(itemLayoutBinding: UserItemBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {
        private var itemLayoutBinding: UserItemBinding? = null

        init {
            this.itemLayoutBinding = itemLayoutBinding
        }
        fun bindUser(user:User) {
            itemLayoutBinding?.user = user
            itemLayoutBinding?.avatar?.setImageResource(user.avatarMock)
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