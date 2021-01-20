package course.ru.qsearcher.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import course.ru.qsearcher.model.Event
import course.ru.qsearcher.repositories.MostPopularEventsRepository
import course.ru.qsearcher.responses.EventResponse

class MostPopularEventsViewModel():ViewModel() {

    private lateinit var mostPopularEventsRepository: MostPopularEventsRepository

    init{
        mostPopularEventsRepository = MostPopularEventsRepository()
    }

    fun getMostPopularEvents(page:Int):LiveData<EventResponse>{
        return mostPopularEventsRepository.getMostPopularEvents(page)
    }

}