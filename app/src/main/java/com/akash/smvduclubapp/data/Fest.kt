package com.akash.smvduclubapp.data

import androidx.compose.foundation.layout.Row
import com.akash.smvduclubapp.R
data class Fest(
    val title: String,
    val date: String,
    val description: String,
    val logoResId: Int
)

fun getDummyUniversityFests(): List<Fest> {
    return listOf(
        Fest("Ekatva", "28 Feb - 2 Feb", "Ekatva is an annual tech fest that showcases cutting-edge technology, innovation, and coding challenges, attracting participants from various institutions.",
            R.drawable.gdsc_dp),
        Fest("Resurgence", "18 Sept - 22 Sept", "Resurgence is an annual cultural fest featuring music, dance, drama, and art competitions, celebrating diverse traditions and creative talents.",
            R.drawable.gdsc_dp)
    )
}