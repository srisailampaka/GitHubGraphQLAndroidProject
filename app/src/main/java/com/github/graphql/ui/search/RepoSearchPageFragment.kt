package com.github.graphql.ui.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.TextView
import com.github.graphql.R
import com.github.graphql.ui.searchdetail.SearchDetailFragment
import com.github.graphql.util.AppExecutors
import com.github.graphql.util.Constants
import com.github.graphql.util.KeyBoardUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.repo_search_page.view.*

/**
 *Fragment class to show the search result in the list
 */
class RepoSearchPageFragment : Fragment() {

    private lateinit var viewModel: RepoSearchPageViewModel
    private val repoAdapter: ReposAdapter by lazy {
        ReposAdapter(AppExecutors.getInstance(), this::launchItem, this::loadImages)
    }

    private var searchTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable) {
            if (viewModel.searchWord != s.toString()) {
                view?.clear_search?.visibility = if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                viewModel.searchWord = s.toString()
                viewModel.loadSearchItems(s.toString())
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.repo_search_page, container, false)
        viewModel = ViewModelProviders.of(this).get(RepoSearchPageViewModel::class.java)
        setupView(rootView)
        return rootView
    }

    private fun setupView(view: View) {
        view.recycler_view.adapter = repoAdapter
        view.recycler_view.layoutManager = LinearLayoutManager(context)

        view.search_field.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.colored_search_icon, 0)
        view.search_field.setOnTouchListener { _, p1 ->
            if (p1?.action == MotionEvent.ACTION_DOWN) {
                view.search_field.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                view.clear_search.visibility = View.GONE
                if (!view.search_field.text.isNullOrEmpty()) {
                    view.clear_search.visibility = View.VISIBLE
                }
            }
            false
        }
        view.search_field.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard(v)
                viewModel.loadSearchItems(v.text.toString())
                return@OnEditorActionListener true
            }
            false
        })
        view.search_field.addTextChangedListener(searchTextWatcher)
        view.clear_search.setOnClickListener {
            view.clear_search.visibility = View.GONE
            view.search_field.setText("")
        }
        if (!viewModel.searchWord.isEmpty()) {
            view.search_field.setText(viewModel.searchWord)
            view.search_field.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        } else {
            view.clear_search.visibility = View.GONE
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        viewModel.repoSearchItems.observe(this, Observer { repoList ->
            repoAdapter.submitList(repoList)
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            Log.e(TAG, errorMessage?.message)
        })
    }

    private fun loadImages(avatarUrl: String, imageView: ImageView) {
        Glide.with(imageView).load(avatarUrl).apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()).into(imageView)
    }

    private fun launchItem(owner: String, projectName: String) {
        activity?.let { activity ->
            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, SearchDetailFragment.getInstance(owner, projectName), Constants.SEARCH_DETAIL_TAG).addToBackStack(null)
                    .commit()
        }
    }

    private fun hideKeyboard(view: View) {
        activity?.let { KeyBoardUtil.hideKeyboard(it, view) }
    }

    companion object {
        private val TAG = RepoSearchPageFragment::class.java.toString()
    }
}