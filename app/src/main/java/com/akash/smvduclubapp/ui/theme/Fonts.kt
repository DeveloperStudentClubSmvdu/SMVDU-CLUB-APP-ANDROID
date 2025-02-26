package com.akash.smvduclubapp.ui.theme


import androidx.compose.runtime.remember
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.akash.smvduclubapp.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val poppinsFont = GoogleFont("Poppins")

val poppinsFontFamily =
    FontFamily(
        Font(googleFont = poppinsFont, fontProvider = provider, weight = FontWeight.Normal),
        Font(googleFont = poppinsFont, fontProvider = provider, weight = FontWeight.SemiBold),
        Font(googleFont = poppinsFont, fontProvider = provider, weight = FontWeight.Light)
    )

val abeezeeFont = GoogleFont("ABeeZee")

val abeezeeFontFamily = FontFamily(
    Font(googleFont = abeezeeFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = abeezeeFont, fontProvider = provider, weight = FontWeight.Light)
)
val balooTammuduFont = GoogleFont("Baloo Tammudu 2")

val balooTammuduFontFamily = FontFamily(
    Font(googleFont = balooTammuduFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = balooTammuduFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = balooTammuduFont, fontProvider = provider, weight = FontWeight.Light)
)