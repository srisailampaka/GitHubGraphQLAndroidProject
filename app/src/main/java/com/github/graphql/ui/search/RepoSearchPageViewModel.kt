package com.github.graphql.ui.search

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.graphql.api.DataRepository

/**
 *View Model class for the Search result page
 */
class RepoSearchPageViewModel : ViewModel() {

    private val dataRepository = DataRepository()
    var searchWord: String = ""
    var repoSearchItems = MutableLiveData<List<ListReposQuery.Edge>?>()
    var error = MutableLiveData<Error?>()


    fun loadSearchItems(searchQuery: String) {
        dataRepository.getReposList(searchQuery) { result ->
            if (result.first != null) {
                repoSearchItems.postValue(result.first)
            } else {
                error.postValue(result.second)
            }

        }
    }


}