package io.keepcoding.eh_ho.topics

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.Topic
import io.keepcoding.eh_ho.data.TopicsRepo
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_posts.*
import kotlinx.android.synthetic.main.fragment_topics.*
import kotlinx.android.synthetic.main.fragment_topics.fragmentLoadingContainer
import kotlinx.android.synthetic.main.fragment_topics.viewLoading
import kotlinx.android.synthetic.main.view_error.*
import java.lang.IllegalArgumentException


class TopicsFragment : Fragment() {

    var topicsInteractionListener: TopicsInteractionListener? = null
    var swipeRefreshLayout: SwipeRefreshLayout? = null

    private val topicsAdapter: TopicsAdapter by lazy {
        val adapter = TopicsAdapter {
            this.topicsInteractionListener?.onShowPosts(it)
        }
        adapter
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is TopicsInteractionListener)
            topicsInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${TopicsInteractionListener::class.java.canonicalName}")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return container?.inflate(R.layout.fragment_topics)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout = view?.findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout?.setOnRefreshListener {
            loadTopics()
        }

        buttonCreate.setOnClickListener {
            this.topicsInteractionListener?.onCreateTopic()
        }
        topicsAdapter.setTopics(TopicsRepo.topics)


        buttonRetry.setOnClickListener {
            this.topicsInteractionListener?.tryIt()
        }


        listTopics.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        listTopics.adapter = topicsAdapter
        listTopics.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_topics, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> this.topicsInteractionListener?.onLogout()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadTopics()
    }

    private fun loadTopics() {
        context?.let {

            enableLoading()
            TopicsRepo
                .getTopics(it.applicationContext,
                    {
                        enableLoading(false)
                        topicsAdapter.setTopics(it)
                        swipeRefreshLayout?.isRefreshing = false
                    },
                    {
                        enableLoading(false)
                        fragmentErrorContainer.visibility = View.VISIBLE
                        viewError.visibility = View.VISIBLE
                        swipeRefreshLayout?.isRefreshing = false
                    }
                )
        }
    }


    private fun enableLoading(enabled: Boolean = true) {
        if (enabled) {

            fragmentLoadingContainer.visibility = View.INVISIBLE
            viewLoading.visibility = View.VISIBLE
        } else {
            viewLoading.visibility = View.INVISIBLE
            fragmentLoadingContainer.visibility = View.VISIBLE
        }

    }

    override fun onDetach() {
        super.onDetach()
        topicsInteractionListener = null
    }

    interface TopicsInteractionListener {
        fun onCreateTopic()
        fun onLogout()
        fun onShowPosts(topic: Topic)
        fun tryIt()
    }


}