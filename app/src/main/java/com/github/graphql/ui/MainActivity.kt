package com.github.graphql.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.github.graphql.R
import com.github.graphql.ui.search.RepoSearchPageFragment
import com.github.graphql.util.Constants

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.main_container, RepoSearchPageFragment(), Constants.SEARCH_PAGE_TAG).commit()
        supportActionBar?.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_material)
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        subscribeToViewModel()
    }

    private fun subscribeToViewModel() {
        mainViewModel.displayBackArrow.observe(this, Observer { showbackArrow ->
            showbackArrow?.let {
                supportActionBar?.setDisplayHomeAsUpEnabled(showbackArrow)
            }

        })
    }

}
