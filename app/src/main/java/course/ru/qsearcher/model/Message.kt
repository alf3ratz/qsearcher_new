package course.ru.qsearcher.model

class Message(
    var text: String,
    var name: String,
    var imageURL: String,
    var sender: String,
    var receiver: String,
    var isMine:Boolean
) {
    //var imageURL:String = image
    constructor() : this("", "", "", "", "",true) {}
}