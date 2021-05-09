package course.ru.qsearcher.listeners

import course.ru.qsearcher.model.Event

interface EventListener {
    fun onEventClicked(event: Event)
}