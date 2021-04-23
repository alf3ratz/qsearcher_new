package course.ru.qsearcher.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import course.ru.qsearcher.databinding.UserItemBinding
import course.ru.qsearcher.model.User

class UsersAdapter : RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {
    private lateinit var users: MutableList<User>


    inner class UsersViewHolder(itemLayoutBinding: UserItemBinding) :
        RecyclerView.ViewHolder(itemLayoutBinding.root) {


    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersAdapter.UsersViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: UsersAdapter.UsersViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }
}