package course.ru.qsearcher.utilities

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import course.ru.qsearcher.model.Event
import java.lang.reflect.Type
import kotlin.collections.ArrayList


class CustomTypeConverter {
    companion object {
        private var gson = Gson()

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

        @TypeConverter
        @JvmStatic
        fun doubleListToString(someObjects: List<Double>?): String? {
            return gson.toJson(someObjects)
        }

        @TypeConverter
        @JvmStatic
        fun stringToDoubleList(str: String?): ArrayList<Double>? {
            if (str == null) {
                return ArrayList()
            }
            val listType: Type = object : TypeToken<ArrayList<Double?>?>() {}.type
            return gson.fromJson<ArrayList<Double>>(str, listType)
        }

        @TypeConverter
        @JvmStatic
        fun placeToString(someObjects: Event.Place?): String? {
            return gson.toJson(someObjects)
        }

        @TypeConverter
        @JvmStatic
        fun stringToPlace(str: String?): Event.Place? {
            if (str == null) {
                return Event.Place()
            }
            val listType: Type = object : TypeToken<Event.Place?>() {}.type
            return gson.fromJson<Event.Place>(str, listType)
        }

        @TypeConverter
        @JvmStatic
        fun coordsToString(someObjects: Event.Place.Coords?): String? {
            return gson.toJson(someObjects)
        }

        @TypeConverter
        @JvmStatic
        fun stringToCoords(str: String?): Event.Place.Coords? {
            if (str == null) {
                return Event.Place.Coords()
            }
            val listType: Type = object : TypeToken<Event.Place.Coords?>() {}.type
            return gson.fromJson<Event.Place.Coords>(str, listType)
        }

    }
}
