package com.valdizz.rxsearch.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.valdizz.rxsearch.R

/**
 * [RxSearchActivity] has a fragment that loads movies.
 *
 * @author Vlad Kornev
 */
class RxSearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rxsearch)
        if (savedInstanceState == null) {
            createJobsFragment()
        }
    }

    private fun createJobsFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragment_container,
                RxSearchFragment.newInstance()
            )
            .commit()
    }
}
