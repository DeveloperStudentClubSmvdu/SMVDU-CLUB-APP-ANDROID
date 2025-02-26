package com.akash.smvduclubapp.data

import com.akash.smvduclubapp.R



data class Event(
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    val posterResId: Int,
)

fun getDummyEvents(): List<Event> {
    return listOf(
        Event(
            title = "Mastering Git & GitHub",
            description = "Join us for an insightful session on Git fundamentals, GitHub workflows, and best practices for collaboration.",
            date = "15th March",
            time = "6:00 PM",
            R.drawable.event_poster
        ),
        Event("Tech Talk", "Join us for an interactive session on AI and ML trends.", "12th March", "5:00 PM", R.drawable.event_poster),
        Event("Hackathon", "A 24-hour coding challenge for developers.", "25th April", "5:00 PM", R.drawable.event_poster),
        Event("Music Night", "Enjoy an evening of live music.", "15th March", "7:00 PM", R.drawable.event_poster),
        Event("Drama Fest", "A stage play by the Drama Club.", "20th March", "6:30 PM", R.drawable.event_poster),
        Event("Photography Workshop", "Learn the art of photography from experts.", "20th March", "4:00 PM", R.drawable.event_poster),
        Event("Gaming Tournament", "Compete in an exciting eSports tournament.", "5th May", "3:00 PM", R.drawable.event_poster),
        Event("Startup Meetup", "Networking session for budding entrepreneurs.", "10th June", "2:00 PM", R.drawable.event_poster),
        Event("Robotics Exhibition", "Showcasing innovative robotics projects.", "22nd July", "11:00 AM", R.drawable.event_poster),
        Event("Coding Bootcamp", "Learn web development in an intensive workshop.", "18th August", "10:00 AM", R.drawable.event_poster),
        Event("Cultural Fest", "A vibrant celebration of music, dance, and art.", "30th September", "5:00 PM", R.drawable.event_poster)
    )
}
