package com.example.pocketbookapp.search

import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketbookapp.InternalStorage
import com.example.pocketbookapp.R
import com.example.pocketbookapp.adapters.BookRecyclerViewAdapter
import com.example.pocketbookapp.model.Book
import com.example.pocketbookapp.network.GoodreadRequest
import java.util.*

class SearchActivity : AppCompatActivity(),
    com.example.pocketbookapp.search.SearchView {
    private val books: MutableList<Book> = ArrayList()
    private var mGoodreadRequest: GoodreadRequest? = null
    private var bookRecyclerViewAdapter: BookRecyclerViewAdapter? = null
    private var bookSearch: SearchView? = null
    private var bookRecyclerView: RecyclerView? = null
    private var loadingIcon: ProgressBar? = null
    private var cache: InternalStorage? = null
    private var searchPresenter: SearchPresenter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        cache = InternalStorage.getInstance()
        mGoodreadRequest = GoodreadRequest(getString(R.string.GR_API_Key), applicationContext)
        searchPresenter = SearchPresenter(this)
        bookRecyclerView = findViewById(R.id.book_recycler_view)
        loadingIcon = findViewById(R.id.loading_icon)

        // for smooth scrolling in recycler view
        bookRecyclerView?.setNestedScrollingEnabled(false)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        bookRecyclerView?.setLayoutManager(layoutManager)
        bookRecyclerViewAdapter = BookRecyclerViewAdapter(books)
        bookRecyclerView?.setAdapter(bookRecyclerViewAdapter)
        bookSearch = findViewById(R.id.book_search)
        bookSearch?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                loadingIcon?.setVisibility(View.VISIBLE)
                bookRecyclerView?.setVisibility(View.GONE)
                bookRecyclerViewAdapter?.clear()
                searchPresenter?.searchQuery(query, mGoodreadRequest!!, cache!!)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        bookSearch?.setOnQueryTextFocusChangeListener(OnFocusChangeListener { v: View?, hasFocus: Boolean ->
            if (!hasFocus) {
                bookSearch?.setIconified(true)
            }
        })
    }

    override fun showBookResult(book: Book) {
        loadingIcon!!.visibility = View.GONE
        bookRecyclerView?.setVisibility(View.VISIBLE)
        bookRecyclerViewAdapter?.add(book)
    }


    override fun showToast(t: String?) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val TAG = SearchActivity::class.java.name
    }
}
