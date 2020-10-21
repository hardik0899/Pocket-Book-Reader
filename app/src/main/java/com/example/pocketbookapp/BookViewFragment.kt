package com.example.pocketbookapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketbookapp.adapters.AuthorRecyclerViewAdapter
import com.example.pocketbookapp.model.Book
import com.squareup.picasso.Picasso
import java.util.*

class BookViewFragment : AppCompatActivity(), CompoundButton.OnCheckedChangeListener {
    private var book: Book? = null
    private var txtHeader: TextView? = null
    private var txtFooter: TextView? = null
    private var bookCover: ImageView? = null
    private var bookRating: RatingBar? = null
    private var ratingCount: TextView? = null
    private var webDescription: TextView? = null
    private var descriptionToggle: ToggleButton? = null
    private var authorToggle: ToggleButton? = null
    private var infoToggle: ToggleButton? = null
    private var isbn: TextView? = null
    private var totalPages: TextView? = null
    private var moreInfo: LinearLayout? = null
    private var url: TextView? = null
    private var cache: InternalStorage? = null
    private var authorRecyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_view)
        cache = InternalStorage.getInstance()
        book = intent.getSerializableExtra("book") as Book
        val authorList = book!!.authors
        authorRecyclerView = findViewById(R.id.recycler_view)
        val ca = AuthorRecyclerViewAdapter(authorList)
        authorRecyclerView?.setAdapter(ca)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL
        authorRecyclerView?.setLayoutManager(llm)
        authorRecyclerView?.setNestedScrollingEnabled(false)
        webDescription = findViewById(R.id.webDescription)
        webDescription?.setText(Html.fromHtml(book!!.description))
        title = book!!.title
        txtHeader = findViewById(R.id.firstLine)
        txtFooter = findViewById(R.id.secondLine)
        bookCover = findViewById(R.id.bookCover)
        bookRating = findViewById(R.id.bookRating)
        ratingCount = findViewById(R.id.ratingCount)
        descriptionToggle = findViewById(R.id.descriptionToggle)
        authorToggle = findViewById(R.id.authorToggle)
        infoToggle = findViewById(R.id.infoToggle)
        isbn = findViewById(R.id.isbn)
        totalPages = findViewById(R.id.totalPages)
        moreInfo = findViewById(R.id.moreInfo)
        url = findViewById(R.id.url)
        bookCover = findViewById(R.id.bookCover)
        Picasso.get().load(book!!.imageUrl).into(bookCover)
        val authorNameList: MutableList<String?> =
            ArrayList()
        for (i in book!!.authors) {
            authorNameList.add(i.name)
        }
        txtHeader?.setText(book!!.title)
        txtFooter?.setText(TextUtils.join(", ", authorNameList))
        bookRating?.setRating(java.lang.Float.valueOf(java.lang.String.valueOf(book!!.avgRating)))
        ratingCount?.setText("(" + book!!.reviewCount.toString() + ")")
        isbn?.setText(java.lang.String.valueOf(book!!.isbn))
        totalPages?.setText(java.lang.String.valueOf(book!!.totalPages))
        try {
            if (book!!.url.length > 55) {
                url?.setText(book!!.url.substring(35, 57) + "...")
            } else {
                url?.setText(book!!.url.substring(35))
            }
        } catch (er: ArrayIndexOutOfBoundsException) {
            Log.e(TAG, "onCreate: book.getUrl()", er)
            url?.setText(book!!.url)
        }
        descriptionToggle?.setOnCheckedChangeListener(this)
        authorToggle?.setOnCheckedChangeListener(this)
        infoToggle?.setOnCheckedChangeListener(this)
        url?.setOnClickListener(View.OnClickListener { v: View? ->
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(book!!.url))
            startActivity(browserIntent)
        })
        val fav: ToggleButton = findViewById(R.id.favorite_toggle)
        fav.setOnCheckedChangeListener(this)
        if (cache!!.isFavorite(book!!.id)) {
            fav.isChecked = true
            fav.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_stars_gold_14px,
                0,
                0,
                0
            )
        }
    }

    override fun onCheckedChanged(
        buttonView: CompoundButton,
        isChecked: Boolean
    ) {
        if (isChecked) {
            buttonView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_keyboard_arrow_down_24px,
                0,
                0,
                0
            )
            if (buttonView.id == R.id.descriptionToggle) {
                webDescription!!.visibility = View.VISIBLE
            } else if (buttonView.id == R.id.infoToggle) {
                moreInfo!!.visibility = View.VISIBLE
            } else if (buttonView.id == R.id.authorToggle) {
                authorRecyclerView!!.visibility = View.VISIBLE
            } else if (buttonView.id == R.id.favorite_toggle) {
                buttonView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_stars_gold_14px,
                    0,
                    0,
                    0
                )
                if (!cache!!.isFavorite(book!!.id)) {
                    cache!!.addToFavList(book!!.id)
                }
            }
        } else {
            buttonView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_baseline_keyboard_arrow_right_24px,
                0,
                0,
                0
            )
            if (buttonView.id == R.id.descriptionToggle) {
                webDescription!!.visibility = View.GONE
            } else if (buttonView.id == R.id.infoToggle) {
                moreInfo!!.visibility = View.GONE
            } else if (buttonView.id == R.id.authorToggle) {
                authorRecyclerView!!.visibility = View.GONE
            } else if (buttonView.id == R.id.favorite_toggle) {
                buttonView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_stars_grey_14px,
                    0,
                    0,
                    0
                )
                cache!!.removeFromFavList(book!!.id)
            }
        }
    }

    companion object {
        private val TAG = BookViewFragment::class.java.name
    }
}

