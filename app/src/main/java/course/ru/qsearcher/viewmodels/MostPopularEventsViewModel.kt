package course.ru.qsearcher.viewmodels

import android.app.Application
import androidx.annotation.NonNull
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import course.ru.qsearcher.database.EventsDatabase
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.repositories.MostPopularEventsRepository
import course.ru.qsearcher.responses.EventResponse
import course.ru.qsearcher.responses.SingleEventResponse
import io.reactivex.Completable
import io.reactivex.Flowable
import retrofit2.Call
import retrofit2.http.Query

class MostPopularEventsViewModel(@NonNull application: Application):AndroidViewModel(application) {

    private var mostPopularEventsRepository: MostPopularEventsRepository =
        MostPopularEventsRepository()
    private var eventsDatabase:EventsDatabase?=null

    init{
        eventsDatabase = EventsDatabase.getEventsDatabase(application)
    }
    fun getMostPopularEvents(page:Int):LiveData<EventResponse>{
        return mostPopularEventsRepository.getMostPopularEvents(page)
    }
    fun getEventsById(id:Int):LiveData<SingleEventResponse>{
        return mostPopularEventsRepository.getEventsById(id)
    }
    fun eventsWithSelectedCategories(categories:String): LiveData<EventResponse>{
        return mostPopularEventsRepository.eventsWithSelectedCategories(categories)
    }
    fun addToFavorites(event:Event): Completable {
        return eventsDatabase?.eventsDao()?.addToFavorites(event)!!
    }


    fun getEventFromFavorites(eventId:String): Flowable<Event>{
        return eventsDatabase?.eventsDao()?.getEventFromFavorites(eventId)!!
    }

    fun removeEventFromFavorites(event:Event):Completable{
        return eventsDatabase?.eventsDao()?.removeFromFavourites(event)!!
    }
}