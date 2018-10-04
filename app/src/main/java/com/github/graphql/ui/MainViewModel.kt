package com.github.graphql.ui

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel


class MainViewModel : ViewModel() {
    var displayBackArrow = MutableLiveData<Boolean>()
}