package com.valdizz.rxsearch.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.valdizz.rxsearch.R
import com.valdizz.rxsearch.adapter.MovieAdapter
import com.valdizz.rxsearch.model.Movie
import com.valdizz.rxsearch.model.Repository
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.ReplaySubject
import kotlinx.android.synthetic.main.fragment_rxsearch.*
import java.util.concurrent.TimeUnit

/**
 * [RxSearchFragment] loads movies into the list and allows to enter a search query
 * and filter the list.
 *
 * @author Vlad Kornev
 */
class RxSearchFragment : Fragment() {

    companion object {
        fun newInstance() = RxSearchFragment()
    }

    private val adapter by lazy {
        MovieAdapter()
    }
    private val replaySubject = ReplaySubject.create<Movie>()
    private lateinit var disposableMovies: Disposable
    private lateinit var disposableSearch: Disposable
    private var queryString = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rxsearch, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initRecyclerView()
        if (savedInstanceState == null) {
            loadMovies()
            filterMovies("")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.search_menu, menu)
        val menuItem = menu.findItem(R.id.action_search)
        val searchView = menuItem.actionView as SearchView

        if (queryString.isNotEmpty()) {
            menuItem.expandActionView()
            searchView.setQuery(queryString, false)
        }
        disposableSearch = getSearchViewObservable(searchView)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposableMovies.dispose()
        disposableSearch.dispose()
    }

    private fun initRecyclerView() {
        rv_movies.layoutManager = LinearLayoutManager(activity)
        rv_movies.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        rv_movies.adapter = adapter
    }

    private fun loadMovies() {
        Repository().getItems()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(replaySubject)
    }

    private fun filterMovies(query: String) {
        if (::disposableMovies.isInitialized) {
            adapter.clear()
            disposableMovies.dispose()
        }
        disposableMovies = replaySubject
            .filter { it.name.toLowerCase().contains(query) }
            .subscribeBy(
                onNext = { adapter.addMovie(it) },
                onError = {
                    Toast.makeText(
                        activity,
                        getString(R.string.error_message, it.localizedMessage),
                        Toast.LENGTH_LONG
                    ).show()
                }
            )
    }

    private fun getSearchViewObservable(searchView: SearchView): Disposable {
        return Observable.create(ObservableOnSubscribe<String> {
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    it.onNext(query!!)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    it.onNext(newText!!)
                    return false
                }
            })
        })
            .debounce(400, TimeUnit.MILLISECONDS)
            .map { text -> text.toLowerCase() }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                queryString = it
                filterMovies(it) }
    }
}