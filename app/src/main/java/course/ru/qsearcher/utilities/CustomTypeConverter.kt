package course.ru.qsearcher.utilities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import course.ru.qsearcher.model.Event
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList

//@ProvidedTypeConverter
class CustomTypeConverter {
    companion object{
    var gson = Gson()
//        @TypeConverter
//        fun stringToSomeObjectList(data: String?): List<Enroll> {
//            if (data == null) {
//                return Collections.emptyList()
//            }
//            val listType: Type = object : TypeToken<List<Enroll?>?>() {}.type
//            return gson.fromJson<List<Enroll>>(data, listType)
//        }

    @TypeConverter
    @JvmStatic
    fun imageListToString(someObjects: ArrayList<Event.Image>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    @JvmStatic
    fun stringToImageList(str: String?): ArrayList<Event.Image>? {
        if (str == null) {
            return ArrayList()
        }
        val listType: Type = object : TypeToken<ArrayList<Event.Image?>?>() {}.type
        return gson.fromJson<ArrayList<Event.Image>>(str, listType)
    }

    @TypeConverter
    @JvmStatic
    fun stringListToString(someObjects: List<String>?): String? {
        return gson.toJson(someObjects)
    }

    @TypeConverter
    @JvmStatic
    fun stringToStringList(str: String?): ArrayList<String>? {
        if (str == null) {
            return ArrayList()
        }
        val listType: Type = object : TypeToken<ArrayList<String?>?>() {}.type
        return gson.fromJson<ArrayList<String>>(str, listType)
    }
}
}
