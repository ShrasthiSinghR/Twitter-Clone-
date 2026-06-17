package com.humblesolutions.twitter

import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore


private val db = Firebase.firestore.collection("tweets")

fun addTweet(username: String, content: String, onDone: () -> Unit) {
    db.add(
        mapOf(
            "username" to username,
            "content" to content,
            "likeCount" to 0,
            "timestamp" to FieldValue.serverTimestamp()   // ✅ FIXED
        )
    ).addOnSuccessListener {
        onDone()
    }
}

fun getTweets(onResult: (List<Tweet>) -> Unit) {
    val db = Firebase.firestore.collection("tweets")

    db.get().addOnSuccessListener { result ->
        val tweets = result.map { doc ->

            val timestamp = doc.getTimestamp("timestamp")?.seconds ?: 0L  // ✅ FIXED

            Tweet(
                id = doc.id,
                username = doc.getString("username") ?: "",
                content = doc.getString("content") ?: "",
                likeCount = doc.getLong("likeCount")?.toInt() ?: 0,
                timestamp = timestamp
            )
        }
        onResult(tweets)
    }
}

fun updateLike(tweetId: String, isLiked: Boolean) {
    val docRef = Firebase.firestore.collection("tweets").document(tweetId)

    docRef.get().addOnSuccessListener { snapshot ->

        val currentLikes = snapshot.getLong("likeCount") ?: 0

        val newLikes = if (isLiked) {
            currentLikes + 1
        } else {
            maxOf(0, currentLikes - 1)
        }

        docRef.update("likeCount", newLikes)
    }
}

fun addComment(tweetId: String, username: String, content: String) {
    Firebase.firestore.collection("tweets")
        .document(tweetId)
        .collection("comments")
        .add(
            mapOf(
                "username" to username,
                "content" to content,
                "timestamp" to FieldValue.serverTimestamp()   // ✅ FIXED
            )
        )
}

fun getComments(tweetId: String, onResult: (List<Comment>) -> Unit) {
    Firebase.firestore.collection("tweets")
        .document(tweetId)
        .collection("comments")
        .get()
        .addOnSuccessListener { result ->
            val comments = result.map {

                val timestamp = it.getTimestamp("timestamp")?.seconds ?: 0L  // ✅ FIXED

                Comment(
                    id = it.id,
                    username = it.getString("username") ?: "",
                    content = it.getString("content") ?: "",
                    timestamp = timestamp
                )
            }
            onResult(comments)
        }
}

fun deleteTweet(tweetId: String) {
    val db = Firebase.firestore.collection("tweets")
    db.document(tweetId).delete()
}