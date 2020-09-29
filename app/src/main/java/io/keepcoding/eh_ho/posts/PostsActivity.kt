package io.keepcoding.eh_ho.posts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.keepcoding.eh_ho.R

const val EXTRA_TOPIC_ID = "TOPIC_ID"
const val EXTRA_TOPIC_TITLE = "TOPIC_TITLE"
const val TRANSACTION_CREATE_POST = "create_post"

class PostsActivity : AppCompatActivity(), PostsFragment.PostsInteractionListener, CreatePostFragment.CreatePostInteractionListener {
    var topicID: String = ""
    var topicTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)

        this.topicID = intent.getStringExtra(EXTRA_TOPIC_ID) ?: ""
        this.topicTitle = intent.getStringExtra(EXTRA_TOPIC_TITLE) ?: ""


        supportFragmentManager.beginTransaction()
            .add(R.id.fragmentContainer, PostsFragment(this.topicID))
            .commit()

    }

    override fun onPostCreated() {

        supportFragmentManager.popBackStack()
    }

    override fun onCreatePost() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, CreatePostFragment(topicID, topicTitle))
            .addToBackStack(TRANSACTION_CREATE_POST)
            .commit()
    }
}