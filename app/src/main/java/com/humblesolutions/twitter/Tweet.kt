package com.humblesolutions.twitter

data class Tweet(
    val id: String = "",
    val username: String = "",
    val content: String = "",
    val likeCount: Int = 0,

    val timestamp: Long = System.currentTimeMillis()
)