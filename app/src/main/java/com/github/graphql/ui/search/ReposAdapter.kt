package com.github.graphql.ui.search

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.github.graphql.R
import com.github.graphql.databinding.RepoSearchItemBinding
import com.github.graphql.ui.common.DataBoundListAdapter
import com.github.graphql.util.AppExecutors

/**
 *Adapter class to show the Search result
 */
class ReposAdapter(appExecutors: AppExecutors, private val itemClickCallback: ((String, String) -> Unit)?,
                   private val loadImageCallback: ((String, ImageView) -> Unit)?) : DataBoundListAdapter<ListReposQuery.Edge, RepoSearchItemBinding>(
        appExecutors = appExecutors,
        diffCallback = object : DiffUtil.ItemCallback<ListReposQuery.Edge>() {
            override fun areItemsTheSame(oldItem: ListReposQuery.Edge, newItem: ListReposQuery.Edge): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: ListReposQuery.Edge, newItem: ListReposQuery.Edge): Boolean {
                return oldItem == newItem
            }
        }) {

    override fun createBinding(parent: ViewGroup): RepoSearchItemBinding {
        return DataBindingUtil.inflate<RepoSearchItemBinding>(LayoutInflater.from(parent.context), R.layout.repo_search_item, parent, false)
    }

    override fun bind(binding: RepoSearchItemBinding, item: ListReposQuery.Edge) {
        val node = item.node()
        if (node is ListReposQuery.AsRepository) {
            binding.repoTitle.text = node.name()
            binding.repoDesc.text = node.description()
            binding.repoFork.text = node.forks().totalCount().toString()
            loadImageCallback?.invoke(node.owner().avatarUrl(), binding.imageView)
            binding.root.setOnClickListener { itemClickCallback?.invoke(node.owner().login(), node.name()) }
        }
    }
}