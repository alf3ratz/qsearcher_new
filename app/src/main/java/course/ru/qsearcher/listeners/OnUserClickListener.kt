package course.ru.qsearcher.listeners

import course.ru.qsearcher.model.User

interface OnUserClickListener {

    //fun onUserCLick(position: Int) {}
    fun onUserCLick(user: User) {}

}