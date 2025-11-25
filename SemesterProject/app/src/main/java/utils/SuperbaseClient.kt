package com.example.semesterproject.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage  // ← Make sure this is here
import io.github.jan.supabase.storage.storage  // ← ADD THIS LINE

object SupabaseClient {
    private const val SUPABASE_URL = "https://hbwstbcizflqlowwidhf.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imhid3N0YmNpemZscWxvd3dpZGhmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQwMjM3OTEsImV4cCI6MjA3OTU5OTc5MX0.KCZYu7nHMIs9dG2Bn_MgQfMV-STHCjCktz6QaBKEuno"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Storage)
    }

    val storage = client.storage  // Now this should work
}