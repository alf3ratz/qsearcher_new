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
import io.reactivex.Completable

class MostPopularEventsViewModel(@NonNull application: Application):AndroidViewModel(application) {

    private lateinit var mostPopularEventsRepository: MostPopularEventsRepository
    private var eventsDatabase:EventsDatabase?=null

    init{
        mostPopularEventsRepository = MostPopularEventsRepository()
        eventsDatabase = EventsDatabase.getEventsDatabase(application)//(application)
    }
    fun getMostPopularEvents(page:Int):LiveData<EventResponse>{
        return mostPopularEventsRepository.getMostPopularEvents(page)
    }
    fun addToFavorites(event:Event): Completable {
        return eventsDatabase?.eventsDao()?.addToFavorites(event)!!
    }

}