package com.example.pocketbookapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketbookapp.adapters.BookRecyclerViewAdapter
import com.example.pocketbookapp.model.Book
import com.example.pocketbookapp.model.BookBuilder
import com.example.pocketbookapp.network.GoodreadRequest
import com.example.pocketbookapp.network.SuccessFailedCallback
import com.example.pocketbookapp.search.SearchActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class LandingPageFragment : AppCompatActivity() {
    private val INTERNET_PERMISSION = 1
    private var mGoodreadRequest: GoodreadRequest? = null
    private var cache: InternalStorage? = null
    private var bookRecyclerViewAdapter: BookRecyclerViewAdapter? = null
    private var bookRecyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing_page)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        InternalStorage.init(applicationContext)
        cache = InternalStorage.getInstance()
        bookRecyclerView = findViewById(R.id.recyclerViewLandingPage)

        // for smooth scrolling in recycler view
        bookRecyclerView?.setNestedScrollingEnabled(false)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        bookRecyclerView?.setLayoutManager(layoutManager)


//        bookRecyclerViewAdapter = new BookRecyclerViewAdapter(new ArrayList<>());
//        bookRecyclerView.setAdapter(bookRecyclerViewAdapter);
        requestInternetPermission()
        mGoodreadRequest = GoodreadRequest(getString(R.string.GR_API_Key), this)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener({ view ->
            startActivity(
                Intent(
                    applicationContext,
                    SearchActivity::class.java
                )
            )
        })
    }

    override fun onResume() {
        loadFavBooks()
        super.onResume()
    }

    private fun loadFavBooks() {
        val favBookIds = cache!!.getFavListCache()
        Log.d(TAG, "loadFavBooks: loading fav books")
        bookRecyclerViewAdapter = BookRecyclerViewAdapter(ArrayList())
        for (i in favBookIds!!.indices) {
            Log.d(
                TAG,
                "loadFavBooks: fav book" + favBookIds[i]
            )
            if (cache!!.getCachedBookById(favBookIds[i]) == null) {
                mGoodreadRequest?.getBook(favBookIds[i], object : SuccessFailedCallback {
                    override fun success(response: String) {
                        val book: Book = BookBuilder.getBookFromXML(response)
                        cache!!.cacheBook(book)
                        bookRecyclerViewAdapter?.add(book)
                    }

                    override fun failed() {
                        Toast.makeText(
                            applicationContext,
                            "some error occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                bookRecyclerViewAdapter?.add(cache?.getCachedBookById(favBookIds[i])!!)
            }
        }
        bookRecyclerView?.setAdapter(bookRecyclerViewAdapter)
        bookRecyclerView?.invalidate()
    }

    private fun requestInternetPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            )
            !== PackageManager.PERMISSION_GRANTED
        ) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.INTERNET
                )
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.INTERNET),
                    INTERNET_PERMISSION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            INTERNET_PERMISSION -> {
                if (grantResults.size <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "Internet Access denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private val TAG = LandingPageFragment::class.java.simpleName
    }
}

