package com.akash.smvduclubapp.screen



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.ui.theme.poppinsFontFamily
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(500)  // Show splash screen for 2 seconds
        onTimeout()  // Navigate to the main screen
    }
    Box(
        modifier = Modifier.fillMaxSize().background(colorResource(id = R.color.spscreen)),
        contentAlignment = Alignment.Center // Center the Column within the Box
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center ) {
            Image(
                painter = painterResource(id = R.drawable.smvdu_logo), contentDescription = "logo",
                Modifier.size(180.dp, 160.dp)
            )
            Spacer(modifier = Modifier.padding(24.dp),)
            Text(
                "SMVDU CLUBS",
                style = TextStyle(fontFamily = poppinsFontFamily, fontSize = 20.sp),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//private fun prevSpScr() {
//    SplashScreen()
//}