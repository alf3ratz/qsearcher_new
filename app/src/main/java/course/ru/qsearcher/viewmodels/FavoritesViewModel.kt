package course.ru.qsearcher.viewmodels

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import course.ru.qsearcher.database.EventsDatabase
import course.ru.qsearcher.model.Event
import io.reactivex.Completable
import io.reactivex.Flowable

class FavoritesViewModel(@NonNull application: Application) : AndroidViewModel(application) {
    private var eventsDatabase: EventsDatabase? = null

    init {
        eventsDatabase = EventsDatabase.getEventsDatabase(application)
    }

    fun loadFavorites():Flowable<List<Event>>{
        return eventsDatabase?.eventsDao()?.getFavorites()!!
    }

    fun removeEventFromFavoritesList(event:Event):Completable{
        return eventsDatabase?.eventsDao()?.removeFromFavourites(event)!!
    }
}