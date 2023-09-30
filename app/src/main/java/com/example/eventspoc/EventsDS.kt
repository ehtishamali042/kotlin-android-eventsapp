package com.example.eventspoc


data class EventInfo(
    val eventId: String,
    var title: String,
    var description: String,
    val imageResource: Int,
    val startDate: String,
    val type: String
)

object EventsDS {
    val events = ArrayList<EventInfo>()

    init {
        dummyEvents()
    }

    fun dummyEvents() {
        var event1 = EventInfo(
            "school-id", "POL School", "This is a pol school",
            com.google.android.material.R.drawable.tooltip_frame_light, "23-09-2023", "Task"
        )
        events.add(event1)
        var event2 = EventInfo(
            "uni-id",
            "Comsats",
            "This is a uni level course",
            R.drawable.ic_launcher_foreground,
            "11-09-2023",
            "OOO"
        )
        events.add(event2)
        var event3 = EventInfo(
            "home-id",
            "Town",
            "This is a town place",
            R.drawable.ic_launcher_foreground,
            "02-09-2023",
            "Event"
        )
        events.add(event3)
    }

    fun createEvent(event: EventInfo) {
        events.add(event)
    }
}