package course.ru.qsearcher.listeners

import course.ru.qsearcher.model.Event

interface FavoritesListener {
    fun onEventClicked(event: Event)

    fun removeEventFromFavorites(event: Event, position: Int)
}