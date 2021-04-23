package course.ru.qsearcher.model

import course.ru.qsearcher.R
import kotlinx.android.synthetic.main.user_item.view.*

class User(var name:String,var email:String,var id:String,var avatarMock:Int) {
    constructor() : this("","","", R.drawable.ic_person) {

    }
}