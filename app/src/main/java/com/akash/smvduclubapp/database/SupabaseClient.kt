@file:OptIn(SupabaseExperimental::class)
package com.akash.smvduclubapp.database

import android.util.Log
import com.akash.smvduclubapp.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.annotations.SupabaseExperimental
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.github.jan.supabase.createSupabaseClient
import java.util.concurrent.TimeUnit

@SupabaseInternal
val supabase: SupabaseClient = createSupabaseClient(
    supabaseUrl = BuildConfig.SUPABASE_URL,
    supabaseKey = BuildConfig.SUPABASE_KEY
) {
    install(Postgrest)
    install(Realtime)
    install(Auth)
    install(io.github.jan.supabase.storage.Storage)
    httpConfig {
        install(WebSockets)
        engine {
            if (this is OkHttpConfig) {
                config {
                    // Set connection timeouts
                    connectTimeout(30_000, TimeUnit.MILLISECONDS)
                    readTimeout(30_000, TimeUnit.MILLISECONDS)
                    writeTimeout(30_000, TimeUnit.MILLISECONDS)

                    // Enable retry on connection failure
                    retryOnConnectionFailure(true)

                }
            }
        }

    }
}

@OptIn(SupabaseInternal::class)
suspend fun saveUserToSupabase(uid: String, name: String?, email: String?) {
    try {
        val response = supabase.postgrest["users"]
            .insert(
                mapOf(
                    "uid" to uid,
                    "name" to name,
                    "email" to email
                )
            )
        Log.d("Supabase", "User saved to Supabase: $response")
    } catch (e: Exception) {
        Log.e("Supabase", "Error saving user: ${e.message}")
    }
}