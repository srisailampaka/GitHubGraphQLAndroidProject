package com.github.graphql.ui.searchdetail

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.graphql.api.DataRepository

/**
 * This View Model class for search details Model
 */

class SearchDetailViewModel : ViewModel() {
    private var dataRepository = DataRepository()
    var repoDetail = MutableLiveData<SearchDetailQuery.Data>()
    var error = MutableLiveData<Error>()

    fun showSearchDetail(owner: String, projectName: String) {
        dataRepository.getSearchDetail(owner, projectName) { result ->
            if (result.first != null) {
                repoDetail.postValue(result.first)
            } else {
                error.postValue(result.second)
            }
        }
    }
}