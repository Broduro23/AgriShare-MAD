package com.example.semesterproject.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage  // ← Make sure this is here
import io.github.jan.supabase.storage.storage  // ← ADD THIS LINE

object SupabaseClient {
    private const val SUPABASE_URL = "https://hbwstbcizflqlowwidhf.supabase.co"
    private const val SUPABASE_KEY = "process.env.SUPABASE_KEY"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Storage)
    }

    val storage = client.storage  // Now this should work
}