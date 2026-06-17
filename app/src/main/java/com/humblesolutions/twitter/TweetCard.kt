package com.humblesolutions.twitter

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.humblesolutions.twitter.ui.theme.TwitterBlue
import com.humblesolutions.twitter.ui.theme.TwitterBorder
import com.humblesolutions.twitter.ui.theme.TwitterRed
import com.humblesolutions.twitter.ui.theme.TwitterSecondaryText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon






@Composable
fun TweetCard(
    tweet: Tweet,
    isLiked: Boolean,
    isHighlighted: Boolean,
    onCommentClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onLikeClick: () -> Unit,

)

{
    val formattedTime = SimpleDateFormat(
        "hh:mm a",
        Locale.getDefault()
    ).format(Date(tweet.timestamp))
    val borderColor = if (isHighlighted)
        TwitterBlue
    else
        TwitterBlue.copy(alpha = 0.4f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isHighlighted) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text("@${tweet.username}", color = TwitterSecondaryText)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = formattedTime,
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.width(8.dp))

                // 🔥 DELETE ICON
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable {
                            onDeleteClick()   // 👈 callback
                        }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))


        Text(tweet.content, color = Color.White)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End   // 🔥 THIS DOES THE MAGIC
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onLikeClick() }
            ) {
                Icon(
                    imageVector = if (isLiked)
                        Icons.Filled.ThumbUp
                    else
                        Icons.Outlined.ThumbUp,
                    contentDescription = "Like",
                    tint = if (isLiked) TwitterBlue else Color.White
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${tweet.likeCount}",
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "💬 Comment",
                    color = TwitterSecondaryText,
                    modifier = Modifier.clickable { onCommentClick() }
                )
            }
        }

        HorizontalDivider(color = TwitterBorder)


    }
}