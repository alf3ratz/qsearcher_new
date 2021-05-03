package course.ru.qsearcher.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import course.ru.qsearcher.repositories.SearchEventRepository
import course.ru.qsearcher.responses.EventResponse

class SearchViewModel() : ViewModel() {

    private var searchEventRepository: SearchEventRepository? = null

    init {
        searchEventRepository = SearchEventRepository()
    }

    fun searchEvent(query: String): LiveData<EventResponse> {
        return searchEventRepository?.searchEvent(query)!!
    }

}
