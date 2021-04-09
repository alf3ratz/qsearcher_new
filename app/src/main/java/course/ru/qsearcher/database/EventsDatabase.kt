package course.ru.qsearcher.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import course.ru.qsearcher.dao.EventDao
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.utilities.CustomTypeConverter


@Database(entities = arrayOf(Event::class), version = 1)
@TypeConverters(CustomTypeConverter::class)
abstract class EventsDatabase() : RoomDatabase() {

    companion object {
        private var eventsDatabase: EventsDatabase? = null
        @Synchronized
        fun getEventsDatabase(context: Context): EventsDatabase {
            val converter :CustomTypeConverter = course.ru.qsearcher.utilities.CustomTypeConverter()
            if (eventsDatabase == null) {
                eventsDatabase =
                    Room.databaseBuilder(context, EventsDatabase::class.java, "events_db")
                        //.addTypeConverter(converter)
                        .build()
            }
            return eventsDatabase as EventsDatabase
        }
    }

    abstract fun eventsDao(): EventDao;

}

