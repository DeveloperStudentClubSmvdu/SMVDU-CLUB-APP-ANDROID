# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# General Android rules
-keepattributes SourceFile,LineNumberTable
-keepattributes Signature
-keepattributes *Annotation*, InnerClasses

# Warning suppressions
-dontwarn java.lang.management.**
-dontwarn org.slf4j.**
-dontwarn io.ktor.util.debug.**
-dontwarn io.netty.**
-dontwarn io.ktor.client.engine.cio.**

# Kotlin Serialization
-keep class kotlinx.serialization.json.** { *; }
-keepclassmembers class kotlinx.serialization.json.** { *; }
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <fields>;
}

# Kotlin Coroutines
-keep class kotlinx.coroutines.** { *; }

# Ktor client (used by Supabase)
-keep class io.ktor.** { *; }

# Supabase
-keep class io.github.jan.supabase.** { *; }
-keep class io.github.jan.** { *; }
-keep class io.github.jan.supabase.gotrue.** { *; }
-keep class io.github.jan.supabase.postgrest.** { *; }
-keep class io.github.jan.supabase.postgrest.query.** { *; }
-keep class io.github.jan.supabase.postgrest.result.** { *; }
-keep class io.github.jan.supabase.storage.** { *; }
-keep class io.github.jan.supabase.realtime.** { *; }
-keep class io.github.jan.supabase.functions.** { *; }

# Google Sign-In
-keep class com.google.android.gms.** { *; }
-keep class com.google.api.client.** { *; }
-keep class com.google.android.libraries.identity.googleid.** { *; }
-keep class com.google.api.client.auth.oauth2.** { *; }
-keep class com.google.api.client.googleapis.auth.oauth2.** { *; }
-keep class com.google.android.gms.auth.api.identity.** { *; }
-keep class com.google.android.gms.auth.api.signin.** { *; }
-keep class * extends com.google.android.gms.auth.api.signin.GoogleSignInClient { *; }

# Firebase
-keep class com.google.firebase.** { *; }
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.auth.** { *; }
-keep class com.google.firebase.messaging.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Compose
-keep class androidx.compose.** { *; }

# SLF4J
-keep class org.slf4j.** { *; }

# App-specific models and data classes
-keep class com.akash.smvduclubapp.data.** { *; }
-keep class com.akash.smvduclubapp.model.** { *; }
-keep class com.akash.smvduclubapp.repository.** { *; }
-keep class com.akash.smvduclubapp.data.ChatRoom { *; }
-keepclassmembers class com.akash.smvduclubapp.** {
    <fields>;
}
-keepclassmembers class com.akash.smvduclubapp.** implements android.os.Parcelable { *; }