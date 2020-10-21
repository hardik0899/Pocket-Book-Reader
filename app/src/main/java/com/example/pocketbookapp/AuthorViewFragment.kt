package com.example.pocketbookapp

import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketbookapp.adapters.BookRecyclerViewAdapter
import com.example.pocketbookapp.model.Author
import com.example.pocketbookapp.model.AuthorBuilder
import com.example.pocketbookapp.model.Book
import com.example.pocketbookapp.model.BookBuilder
import com.example.pocketbookapp.network.GoodreadRequest
import com.example.pocketbookapp.network.SuccessFailedCallback
import com.example.pocketbookapp.util.CircleTransform
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_author_view.*
import java.util.*

class AuthorViewFragment : AppCompatActivity(),
    CompoundButton.OnCheckedChangeListener {
    private var author: Author? = null
    private var authorImage: ImageView? = null
    private var authorName: TextView? = null
    private var webAbout: TextView? = null
    private val books: MutableList<Book> = ArrayList()
    private var authorRecyclerView: RecyclerView? = null
    private var bookRecyclerViewAdapter: BookRecyclerViewAdapter? = null
    private var mGoodreadRequest: GoodreadRequest? = null
    private var cache: InternalStorage? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_author_view)
        cache = InternalStorage.getInstance()
        authorName = findViewById(R.id.firstLine)
        authorImage = findViewById(R.id.authorImage)
        authorRecyclerView = findViewById(R.id.recycler_view)

        // for smooth scrolling in recycler view
        authorRecyclerView?.setNestedScrollingEnabled(false)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        authorRecyclerView?.setLayoutManager(layoutManager)
        bookRecyclerViewAdapter = BookRecyclerViewAdapter(books)
        authorRecyclerView?.setAdapter(bookRecyclerViewAdapter)
        webAbout = findViewById(R.id.webAbout)
        val aboutToggle: ToggleButton = findViewById(R.id.aboutToggle)
        val bookToggle: ToggleButton = findViewById(R.id.bookToggle)
        aboutToggle.setOnCheckedChangeListener(this)
        bookToggle.setOnCheckedChangeListener(this)
        mGoodreadRequest = GoodreadRequest(getString(R.string.GR_API_Key), applicationContext)
        author = intent.getSerializableExtra("author") as Author
        if (cache!!.getCachedAuthorById(author!!.getId()) == null) {
            mGoodreadRequest?.getAuthor(author!!.getId(), object : SuccessFailedCallback {
                override fun success(response: String) {
                    author = AuthorBuilder.getAboutDetails(response, author)
                    cache!!.cacheAuthor(author!!)
                    updateDetails(author)
                }

                override fun failed() {
                    Toast.makeText(
                        applicationContext,
                        "something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            author = cache!!.getCachedAuthorById(author!!.getId())
            updateDetails(author)
        }
    }

    private fun updateDetails(author: Author?) {
        authorName?.setText(author?.getName())
        Log.d(TAG, "updateDetails: " + author?.getImg())
        Picasso
            .get()
            .load(author?.getImg())
            .transform(CircleTransform())
            .into(authorImage)
        webAbout!!.text = Html.fromHtml(author?.getAbout())
        val bookIds: List<Int> = author!!.getBookIds()
        for (i in 0 until Math.min(6, bookIds.size)) {
            if (cache!!.getCachedBookById(bookIds[i]) == null) {
                mGoodreadRequest?.getBook(bookIds[i], object : SuccessFailedCallback {
                    override fun success(response: String) {
                        val book: Book = BookBuilder.getBookFromXML(response)
                        cache!!.cacheBook(book)
                        bookRecyclerViewAdapter?.add(book)
                    }

                    override fun failed() {
                        Log.e(
                            TAG,
                            "failed: getting book from id"
                        )
                    }
                })
            } else {
                cache!!.getCachedBookById(bookIds[i])?.let { bookRecyclerViewAdapter?.add(it) }
            }
        }
        content.visibility = View.VISIBLE
        loading_icon.visibility = View.GONE
    }

    override fun onCheckedChanged(
        buttonView: CompoundButton,
        isChecked: Boolean
    ) {
        if (isChecked) {
            if (buttonView.id == R.id.descriptionToggle) {
                buttonView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_keyboard_arrow_down_24px,
                    0,
                    0,
                    0
                )
                webAbout!!.visibility = View.VISIBLE
            } else if (buttonView.id == R.id.authorToggle) {
                buttonView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_keyboard_arrow_down_24px,
                    0,
                    0,
                    0
                )
                authorRecyclerView?.setVisibility(View.VISIBLE)
            }
        } else {
            if (buttonView.id == R.id.descriptionToggle) {
                buttonView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_keyboard_arrow_right_24px,
                    0,
                    0,
                    0
                )
                webAbout!!.visibility = View.GONE
            } else if (buttonView.id == R.id.authorToggle) {
                buttonView.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_baseline_keyboard_arrow_right_24px,
                    0,
                    0,
                    0
                )
                authorRecyclerView?.setVisibility(View.GONE)
            }
        }
    }

    companion object {
        private val TAG = AuthorViewFragment::class.java.name
    }
}

