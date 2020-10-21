package com.example.pocketbookapp.adapters

import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.text.TextUtils
import android.util.Pair
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketbookapp.BookViewFragment
import com.example.pocketbookapp.R
import com.example.pocketbookapp.model.Book
import com.squareup.picasso.Picasso
import java.util.*

class BookRecyclerViewAdapter(private var values : MutableList<Book>) :
    RecyclerView.Adapter<BookRecyclerViewAdapter.BookViewHolder?>() {

    fun clear() {
        values = ArrayList()
        notifyDataSetChanged()
    }

    fun add(item: Book?) {
        val position = values.size
        if (item != null) {
            values.add(position, item)
        }
        notifyItemInserted(position)
    }

    fun remove(position: Int) {
        values.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BookViewHolder {
        val inflater = LayoutInflater.from(
            parent.context
        )
        val v = inflater.inflate(R.layout.book_row_layout, parent, false)
        return BookViewHolder(v)
    }

    override fun onBindViewHolder(
        holder: BookViewHolder,
        position: Int
    ) {
        val book = values[position]
        holder.txtHeader.text = book.title
        holder.rowContainer.setOnClickListener { v ->
            val i = Intent(v.context, BookViewFragment::class.java)
            i.putExtra("book", book)
            val bookCover =
                Pair.create(
                    holder.bookCover as View,
                    "book_cover_activity_transition"
                )

            // adds activity transition to book image
            val options = ActivityOptions.makeSceneTransitionAnimation(
                v.context as Activity,
                bookCover
            )
            v.context.startActivity(i, options.toBundle())
        }
        val authorNameList: MutableList<String?> =
            ArrayList()
        for (i in book.authors) {
            authorNameList.add(i.name)
        }
        holder.txtFooter.text = TextUtils.join(", ", authorNameList)
        Picasso.get().load(book.imageUrl).into(holder.bookCover)
        try {
            holder.bookRating.rating =
                java.lang.Float.valueOf(java.lang.String.valueOf(book.avgRating))
            holder.ratingCount.text = "(" + book.reviewCount.toString() + ")"
        } catch (ex: NullPointerException) {
            holder.bookRating.visibility = View.GONE
            holder.ratingCount.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return values.size
    }

    inner class BookViewHolder(val layout: View) :
        RecyclerView.ViewHolder(layout) {
        val bookCover: ImageView
        val bookRating: RatingBar
        val ratingCount: TextView
        val txtHeader: TextView
        val txtFooter: TextView
        val rowContainer: RelativeLayout

        init {
            txtHeader = layout.findViewById(R.id.firstLine)
            txtFooter = layout.findViewById(R.id.secondLine)
            bookCover = layout.findViewById(R.id.bookCover)
            rowContainer = layout.findViewById(R.id.row_container)
            bookRating = layout.findViewById(R.id.bookRating)
            ratingCount = layout.findViewById(R.id.ratingCount)
            bookRating.setIsIndicator(true)
            bookRating.max = 5
            bookRating.numStars = 5
            bookRating.stepSize = 0.01.toFloat()
        }
    }

}
