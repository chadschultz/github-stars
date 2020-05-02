package com.example.githubstars.data

import android.net.Uri

data class GitHubRepo(
    val id: String,
    val name: String,
    val description: String,
    val url: Uri?,
    val starCount: Int
)
