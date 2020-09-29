package io.keepcoding.eh_ho.data

import android.annotation.SuppressLint
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class Topic(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val date: Date = Date(),
    val posts: Int = 0,
    val views: Int = 0
) {

    companion object {
        fun parseTopicsList(response: JSONObject): List<Topic> {
            val objectList = response.getJSONObject("topic_list")
                .getJSONArray("topics")


            val topics = mutableListOf<Topic>()

            for (i in 0 until objectList.length()) {
                val parsedTopic = parseTopic(objectList.getJSONObject(i))
                topics.add(parsedTopic)
            }
            return topics
        }

        fun parseTopic(jsonObject: JSONObject): Topic {
            val date = jsonObject.getString("created_at")
                .replace("Z", "+0000")

            val dateFormat = SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val dateFormated = dateFormat.parse(date) ?: Date()

            return Topic(
                id = jsonObject.getInt("id").toString(),
                title = jsonObject.getString("title").toString(),
                date = dateFormated,
                posts = jsonObject.getInt("posts_count"),
                views = jsonObject.getInt("views")
            )
        }
    }

    val MINUTE_MILLIS = 1000L * 60
    val HOURS_MILLIS = MINUTE_MILLIS * 60
    val DAYS_MILLIS = HOURS_MILLIS * 24
    val MONTH_MILLIS = DAYS_MILLIS * 30
    val YEARS_MILLIS = MONTH_MILLIS * 12

    data class TimeOffset(val amount: Int, val unit: Int)

    fun getTimeOffset(dateToCompare: Date = Date()): TimeOffset {

        val current = dateToCompare.time
        val diff = current - this.date.time

        val years = diff / YEARS_MILLIS
        if (years > 0) return TimeOffset(years.toInt(), Calendar.YEAR)

        val months = diff / MONTH_MILLIS
        if (months > 0) return TimeOffset(months.toInt(), Calendar.MONTH)

        val days = diff / DAYS_MILLIS
        if (days > 0) return TimeOffset(days.toInt(), Calendar.DAY_OF_MONTH)

        val hours = diff / HOURS_MILLIS
        if (hours > 0) return TimeOffset(hours.toInt(), Calendar.HOUR)

        val minutes = diff / MINUTE_MILLIS
        if (minutes > 0) return TimeOffset(minutes.toInt(), Calendar.MINUTE)

        return TimeOffset(0, Calendar.MINUTE)
    }
}

data class Post(
    val topicTitle: String = "",
    val id: String = "",
    val username: String = "",
    val date: String = "",
    val content: String = ""
) {
    companion object {
        fun parsePostList(response: JSONObject): List<Post> {
            val objectList = response.getJSONObject("post_stream")
                .getJSONArray("posts")
            val posts = mutableListOf<Post>()

            for (i in 0 until objectList.length()) {
                val parsedPost = parsePost(objectList.getJSONObject(i), response)
                posts.add(parsedPost)
            }
            return posts

        }

        @SuppressLint("SimpleDateFormat")
        fun parsePost(jsonObject: JSONObject, response: JSONObject): Post {
            val date = jsonObject.getString("created_at")
                .replace("Z", "+0000")

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
            val dateFormatted = dateFormat.parse(date) ?: Date()
            val dateFormatString = SimpleDateFormat("d MMM, yyyy")
            val dateFormattedString = dateFormatString.format(dateFormatted)



            return Post(
                topicTitle = response.getString("title"),
                id = jsonObject.getInt("id").toString(),
                username = jsonObject.getString("username"),
                date = dateFormattedString,
                content = jsonObject.getString("cooked")
            )
        }
    }
}