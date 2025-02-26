package com.akash.smvduclubapp.data

data class CommunityPost(
    val username: String,
    val postContent: String
)

fun getDummyCommunityPosts(): List<CommunityPost> {
    return listOf(
        CommunityPost("Alice", "Excited to share my latest project on AI! 🚀"),
        CommunityPost("Bob", "Had a great experience at the recent hackathon. Learned so much!"),
        CommunityPost("Charlie", "Looking for teammates for an upcoming coding competition. DM me!"),
        CommunityPost("David", "Exploring Jetpack Compose. Loving the flexibility it offers!"),
        CommunityPost("Emma", "Anyone interested in collaborating on an open-source Android app?")
    )
}