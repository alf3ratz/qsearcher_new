package course.ru.qsearcher.database

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.provider.CalendarContract
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import course.ru.qsearcher.dao.EventDao
import course.ru.qsearcher.model.Event


@Database(entities = [Event::class], version = 1, exportSchema = false)
abstract class EventsDatabase() : RoomDatabase() {
    private var eventsDatabase: EventsDatabase? = null

    @Synchronized
    fun getEventsDatabase(context: Context): EventsDatabase {
        if (eventsDatabase == null) {
            eventsDatabase =
                Room.databaseBuilder(context, EventsDatabase::class.java, "events_db").build()
        }
        return eventsDatabase!!
    }

    abstract fun eventsDao(): EventDao;
}