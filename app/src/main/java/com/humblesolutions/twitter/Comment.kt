package com.humblesolutions.twitter

data class Comment(
    val id: String = "",
    val username: String = "",
    val content: String = "",
    val timestamp: Long = 0L
)