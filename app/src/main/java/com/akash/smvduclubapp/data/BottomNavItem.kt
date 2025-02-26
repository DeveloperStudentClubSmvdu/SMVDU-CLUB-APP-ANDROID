package com.akash.smvduclubapp.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.Screen


data class BottomNavItem(val label: String, val icon: Painter, val route: String) {
    companion object {
        @Composable
        fun dummyData(): List<BottomNavItem> {
            return listOf(
                BottomNavItem("Home", painterResource(R.drawable.baseline_home_24), Screen.MainScreen.route),
                BottomNavItem("Clubs", painterResource(R.drawable.baseline_groups_24), Screen.ClubScreen.route),
                BottomNavItem("Events", painterResource(R.drawable.baseline_event_note_24), Screen.EventListScreen.route),
                BottomNavItem("Profile", painterResource(R.drawable.baseline_person_), Screen.ProfileScreen.route)
            )
        }
    }
}

