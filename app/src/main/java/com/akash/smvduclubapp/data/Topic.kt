package com.akash.smvduclubapp.data

import androidx.compose.ui.graphics.Color

data class Topic(
    val name: String,
    val description: String,
    val color: Color
)

// Function to return the list of topics
fun getTopics(): List<Topic> {
    return listOf(
        Topic("Git & GitHub", "A version control system used for managing code repositories.", Color(0xFFFF3B30)),
        Topic("Hacktober Fest", "An annual open-source event that encourages participation in GitHub projects.", Color(0xFFFF9500)),
        Topic("Command Line Prompts", "A powerful interface for interacting with your computer using text commands.", Color(0xFF007AFF)),
        Topic("Open Source Contribution", "Learn how to contribute to open-source projects and improve software.", Color(0xFF4CAF50)),
        Topic("Version Control", "A system that records changes to a file or set of files over time.", Color(0xFF673AB7))
    )
}