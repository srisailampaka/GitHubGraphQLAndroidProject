package com.github.graphql.ui.searchdetail

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.graphql.R
import com.github.graphql.ui.MainViewModel
import kotlinx.android.synthetic.main.search_detail.view.*

/**
 * This Fragment class for to show the search details
 */
class SearchDetailFragment : Fragment() {

    private lateinit var viewModel: SearchDetailViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.search_detail, container, false)
        viewModel = ViewModelProviders.of(this).get(SearchDetailViewModel::class.java)
        val repoOwner = arguments?.getString(REPO_OWNER)
        val projectName = arguments?.getString(PROJECT_NAME)
        if (repoOwner != null && projectName != null) {
            viewModel.showSearchDetail(repoOwner, projectName)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        mainViewModel.displayBackArrow.postValue(true)
        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        viewModel.repoDetail.observe(this, Observer { repositoryDetail ->
            repositoryDetail?.repository()?.let { repoDetail ->
                view?.title?.text = repoDetail.name()
                view?.description?.text = repoDetail.description()
                view?.forks?.text = resources.getString(R.string.forks, repoDetail.forks().totalCount().toString())
                view?.watchers?.text = resources.getString(R.string.watchers, repoDetail.watchers().totalCount().toString())
                view?.pull_requests?.text = resources.getString(R.string.pull_request, repoDetail.pullRequests().totalCount().toString())
            }
        })

        viewModel.error.observe(this, Observer { errorMessage ->
            Log.e(TAG, errorMessage?.message)
        })
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.displayBackArrow.postValue(false)
    }

    companion object {
        val TAG = SearchDetailFragment::class.java.toString()
        private const val REPO_OWNER = "owner"
        private const val PROJECT_NAME = "projectName"

        fun getInstance(owner: String, projectName: String): SearchDetailFragment {
            val bundle = Bundle()
            bundle.putString(REPO_OWNER, owner)
            bundle.putString(PROJECT_NAME, projectName)
            val fragment = SearchDetailFragment()
            fragment.arguments = bundle
            return fragment
        }
    }
}