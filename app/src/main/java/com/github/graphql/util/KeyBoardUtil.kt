package com.github.graphql.util


import android.app.Activity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object KeyBoardUtil {

    fun hideKeyboard(activity: Activity, view: View?) {
        if (view != null) {
            val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
