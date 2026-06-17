package com.humblesolutions.twitter

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.humblesolutions.twitter.ui.theme.TwitterBlack
import com.humblesolutions.twitter.ui.theme.TwitterBlue
import com.humblesolutions.twitter.ui.theme.TwitterBorder
import com.humblesolutions.twitter.ui.theme.TwitterSecondaryText
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.airbnb.lottie.compose.*
@Composable
fun MainScreen() {
    var isLoadingScreen by remember { mutableStateOf(false) }
    var selectedTweetId by rememberSaveable { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }
    var tweets by remember { mutableStateOf(listOf<Tweet>()) }
    var newTweet by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var likedTweetIds by remember { mutableStateOf(setOf<String>()) }
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    var highlightedTweetId by remember { mutableStateOf<String?>(null) }
    var lastPostedTime by remember { mutableStateOf<Long?>(null) }

    val scope = rememberCoroutineScope()
    fun loadTweets() {
        isLoading = true
        getTweets { data ->
            tweets = data
            isLoading = false
        }

    }
    if (isLoadingScreen) {

        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.paper_plane)
        )

        val progress by animateLottieCompositionAsState(
            composition = composition,
            iterations = 1   // 🔥 play once fully
        )

        LaunchedEffect(progress) {
            if (progress == 1f) {
                delay(300)   // small delay
                isLoadingScreen = false
                isLoggedIn = true
                loadTweets()
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TwitterBlack),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome aboard,@$username 🚀",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        return   // 🔥 VERY IMPORTANT (stops rest of UI)
    }

    if (!isLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(TwitterBlack)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        )
        {
            Text("𝕏", color = Color.White, style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Enter username") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = TwitterBlue,
                    focusedLabelColor = TwitterBlue
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    isLoadingScreen = true
                },
                enabled = username.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = TwitterBlue)
            ) {
                Text("Get Started")
            }
        }


    } else {
        Scaffold(
            containerColor = TwitterBlack,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)

            }
        ) { paddingValues ->
            LaunchedEffect(showSnackbar) {
                if (showSnackbar) {
                    snackbarHostState.showSnackbar("Tweet posted successfully 🚀")
                    showSnackbar = false
                }
            }

            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                Column{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        "Sign Out",
                        color = TwitterBlue,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .border(
                                width = 1.dp,
                                color = TwitterBlue,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                isLoggedIn = false
                                username = ""
                                tweets = emptyList()
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    Button(
                        onClick = {
                            loadTweets()
                        },
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = TwitterBlue),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Refresh")
                    }
                }
                HorizontalDivider(color = TwitterBorder)

                // Tweet input area
                Column(modifier = Modifier.padding(16.dp)) {

                    OutlinedTextField(
                        value = newTweet,
                        onValueChange = { newTweet = it },
                        placeholder = { Text("What's happening?", color = TwitterSecondaryText) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = TwitterBlue
                        )
                    )

                    Button(
                        onClick = {
                            val tweetText = newTweet
                            newTweet = ""

                            val currentTime = System.currentTimeMillis()
                            lastPostedTime = currentTime   // ✅ store time

                            scope.launch {
                                addTweet(
                                    username = username,
                                    content = tweetText,
                                    onDone = {
                                        loadTweets()   // 🔥 instant refresh after success
                                    }
                                )

                                snackbarHostState.showSnackbar("Tweet posted successfully 🚀")
                            }
                        },
                        enabled = newTweet.trim().isNotBlank(),
                        modifier = Modifier.align(Alignment.End),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = TwitterBlue)
                    ) {
                        Text("Tweet")
                    }

                    if (isLoading == true) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = TwitterBlue)
                        }
                    } else {
                        val sortedTweets = tweets.sortedByDescending { it.timestamp }
                        LazyColumn {
                            items(sortedTweets) { tweet ->
                                TweetCard(
                                    tweet = tweet,
                                    isLiked = likedTweetIds.contains(tweet.id),
                                    isHighlighted = false,
                                    onLikeClick = {
                                        val alreadyLiked = likedTweetIds.contains(tweet.id)

                                        likedTweetIds =
                                            if (alreadyLiked) likedTweetIds - tweet.id
                                            else likedTweetIds + tweet.id

                                        updateLike(tweet.id, !alreadyLiked)
                                        loadTweets()
                                    },
                                    onCommentClick = {
                                        selectedTweetId = null        // 🔥 reset first
                                        selectedTweetId = tweet.id    // 🔥 then set
                                    },
                                    onDeleteClick = {
                                        deleteTweet(tweet.id)
                                        loadTweets()
                                    }
                                )
                            }
                        }
                    }



                }}
                    if (selectedTweetId != null) {
                        CommentSection(
                            tweetId = selectedTweetId!!,
                            username = username,
                            onClose = { selectedTweetId = null }

                        )
                        loadTweets()
                    }

                }






        }
    }
}



