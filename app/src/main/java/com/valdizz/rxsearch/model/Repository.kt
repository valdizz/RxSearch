package com.valdizz.rxsearch.model

import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

/**
 * Repository class which has single method [getItems] that allows to get list of movies
 * with initial delay of 5 seconds.
 *
 * @author Vlad Kornev
 */
class Repository {

    private val names = arrayListOf("Terminator", "Rambo", "Robocop", "Predator", "Batman")
    private val countries = arrayListOf("Belarus", "USA", "France", "Germany", "Italy")

    fun getItems(): Observable<Movie> {
        return Observable
            .range(1, 20)
            .concatMapSingle {
                Single.just(
                    Movie(
                        names.random() + it,
                        (1990..2019).random(),
                        countries.random()
                    )
                ).delay(5, TimeUnit.SECONDS)
            }
    }
}