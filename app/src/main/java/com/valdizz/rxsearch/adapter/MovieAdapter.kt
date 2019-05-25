package com.valdizz.rxsearch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.valdizz.rxsearch.R
import com.valdizz.rxsearch.model.Movie
import kotlinx.android.synthetic.main.item_movie.view.*

/**
 * Adapter class for displaying data in [RecyclerView].
 *
 * @author Vlad Kornev
 */
class MovieAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val movies = mutableListOf<Movie>()

    fun addMovie(movie: Movie) {
        movies.add(movie)
        notifyItemChanged(movies.size - 1)
    }

    fun clear() {
        val size = movies.size
        movies.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bindData(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindData(movie: Movie) {
            itemView.tv_name.text = movie.name
            itemView.tv_year.text = itemView.context.getString(R.string.item_year, movie.year)
            itemView.tv_rating.text = movie.country
        }
    }
}