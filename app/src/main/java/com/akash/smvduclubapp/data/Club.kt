package com.akash.smvduclubapp.data

import com.akash.smvduclubapp.R

data class Club(
    val name: String,
    val description: String,
    val logoResId: Int,

)
{
    companion object {
        fun getDummyClubs(): List<Club> {
            return listOf(
                Club("Code Club", "A community for coding enthusiasts.", R.drawable.gdsc_dp),
                Club("AET Club", "A club for automation and embedded technology.", R.drawable.gdsc_dp),
                Club("AI Club", "A hub for AI and machine learning enthusiasts.", R.drawable.gdsc_dp),
                Club("GDSC", "Google Developer Student Club for tech enthusiasts.", R.drawable.gdsc_dp),
                Club("Tarang", "The Electronics and Communication Engineering Club.", R.drawable.gdsc_dp),
                Club("SAE Club", "A society for automotive engineering enthusiasts.", R.drawable.gdsc_dp),
                Club("Prishthabhoomi", "A club dedicated to environmental sustainability.", R.drawable.gdsc_dp),
                Club("Pratibimb", "The Social Media and PR Club.", R.drawable.gdsc_dp),
                Club("Mosaic", "The design community for creative minds.", R.drawable.gdsc_dp),
                Club("Discipline Community", "A community focused on student discipline and conduct.", R.drawable.gdsc_dp),
                Club("Aayojan", "The event planning and management club.", R.drawable.gdsc_dp),
                Club("Yoga Club", "A community promoting physical and mental well-being.", R.drawable.gdsc_dp),
                Club("Vyom", "The Astronomy Club for space and celestial exploration.", R.drawable.gdsc_dp),
                Club("Aalap", "The Music Club for singers and instrumentalists.", R.drawable.gdsc_dp),
                Club("Shabdharth", "The literary club for writers and poets.", R.drawable.gdsc_dp),
                Club("Zenith", "A club for students aiming for excellence in various fields.", R.drawable.gdsc_dp),
                Club("Kala Kshetra", "The Arts Club for painting and creativity.", R.drawable.gdsc_dp),
                Club("Atelier", "The Multimedia Club for digital media enthusiasts.", R.drawable.gdsc_dp),
                Club("Yuva Tourism Club", "The event management and tourism club.", R.drawable.gdsc_dp)
            )
        }
    }
}
