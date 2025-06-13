package com.akash.smvduclubapp

sealed class Screen(val route:String){
    object LoginScreen:Screen("loginscreen")
    object SignupScreen:Screen("signupscreen")
    object MainScreen:Screen("mainscreen")
    object SplashScreen:Screen("splashscreen")
    object ForgotPasswordScreen:Screen("forgotpasswordscreen")
    object EventDetailScreen:Screen("eventscreen")
    object ClubScreen:Screen("clubscreen")
    object NotificationScreen:Screen("notificationscreen")
    object ProfileScreen:Screen("profilescreen")
    object HomeScreen:Screen("homescreen")
    object ClubDetailScreen:Screen("clubdetailscreen")
    object EventListScreen:Screen("eventlistscreen")
    object FestDetailScreen:Screen("festdetailscreen")
    object EventRegistrationForm:Screen("eventregistrationform")
    object VCMessageScreen:Screen("vcmessagescreen")
    object ActivityHeadScreen:Screen("activityheadscreen")
    object ChatScreen:Screen("chatscreen")
    object MyClubScreen:Screen("myclubscreen")
    object MyEventScreen:Screen("myeventscreen")
}