package course.ru.qsearcher.viewmodels

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import course.ru.qsearcher.database.EventsDatabase
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.repositories.EventsRepository
import course.ru.qsearcher.responses.EventResponse
import io.reactivex.Completable
import io.reactivex.Flowable

class EventsViewModel(@NonNull application: Application) : AndroidViewModel(application) {

    private var eventsRepository: EventsRepository =
        EventsRepository()
    private var eventsDatabase: EventsDatabase? = null

    init {
        eventsDatabase = EventsDatabase.getEventsDatabase(application)
    }

    fun getMostPopularEvents(page: Int): LiveData<EventResponse> {
        return eventsRepository.getMostPopularEvents(page)
    }

//    fun getEventsById(id: Int): LiveData<SingleEventResponse> {
//        return eventsRepository.getEventsById(id)
//    }

    fun eventsWithSelectedCategories(categories: String): LiveData<EventResponse> {
        return eventsRepository.eventsWithSelectedCategories(categories)
    }

    fun addToFavorites(event: Event): Completable {
        return eventsDatabase?.eventsDao()?.addToFavorites(event)!!
    }


    fun getEventFromFavorites(eventId: String): Flowable<Event> {
        return eventsDatabase?.eventsDao()?.getEventFromFavorites(eventId)!!
    }

    fun removeEventFromFavorites(event: Event): Completable {
        return eventsDatabase?.eventsDao()?.removeFromFavourites(event)!!
    }
}