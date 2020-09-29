package io.keepcoding.eh_ho.posts

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.Post
import io.keepcoding.eh_ho.data.PostsRepo
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_topics.fragmentLoadingContainer
import kotlinx.android.synthetic.main.fragment_topics.viewLoading
import java.lang.IllegalArgumentException

class PostsFragment(postId: String = ""): Fragment() {
    val id = postId
    var postInteractionListener: PostsInteractionListener? = null
    var posts: MutableList<Post>? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    private val postAdapter: PostsAdapter by lazy {
        val adapter = PostsAdapter()
        adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is PostsInteractionListener)
            postInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${PostsFragment.PostsInteractionListener::class.java.canonicalName}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return container?.inflate(R.layout.fragment_posts)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postAdapter.setPosts(PostsRepo.posts)

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            loadPosts(postId = this.id)
        }


        listPosts.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listPosts.adapter = postAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_add_post, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.postCreate -> this.postInteractionListener?.onCreatePost()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadPosts(id)
    }

    fun loadPosts (postId: String) {
        context?.let {
            enableLoading()
            PostsRepo
                .getPosts(
                    it.applicationContext,
                    {
                        postAdapter.setPosts(it)
                        topicTitle.text = it[0].topicTitle
                        enableLoading(false)
                        swipeRefreshLayout?.isRefreshing = false
                    },
                    {
                        enableLoading(false)
                        print("error")
                        swipeRefreshLayout?.isRefreshing = false
                    },
                    postId
                )
        }
    }

    private fun enableLoading(enabled: Boolean = true)  {
        if (enabled) {
            fragmentLoadingContainer.visibility = View.INVISIBLE
            viewLoading.visibility = View.VISIBLE
        } else {
            fragmentLoadingContainer.visibility = View.VISIBLE
            viewLoading.visibility = View.INVISIBLE
        }

    }

    interface PostsInteractionListener {
        fun onCreatePost()
    }


}