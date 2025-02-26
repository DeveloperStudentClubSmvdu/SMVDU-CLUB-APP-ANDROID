package com.akash.smvduclubapp.data

data class NotificationData(
    val title: String,
    val description: String,
    val date: String,
    val time: String,
    var isUnread : Boolean
)