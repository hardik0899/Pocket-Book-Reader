package com.example.pocketbookapp.search

import android.util.Log
import com.example.pocketbookapp.InternalStorage
import com.example.pocketbookapp.model.Book
import com.example.pocketbookapp.model.BookBuilder
import com.example.pocketbookapp.network.GoodreadRequest
import com.example.pocketbookapp.network.SuccessFailedCallback
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.StringReader
import java.util.*

internal class SearchPresenter(private val searchView: SearchView) {

    fun searchQuery(
        query: String,
        goodreadRequest: GoodreadRequest,
        cache: InternalStorage
    ) {
        var query = query
        query = query.replace(" ".toRegex(), "+")
        goodreadRequest.searchBook(query, object : SuccessFailedCallback {
            override fun success(response: String) {
                val bookIds = getBookIdsFromSearchResults(response)
                getEachBook(bookIds, goodreadRequest, cache)
            }

            override fun failed() {}
        })
    }

    private fun getEachBook(
        bookIds: List<Int>,
        goodreadRequest: GoodreadRequest,
        cache: InternalStorage
    ) {
        for (i in bookIds.indices) {
            if (cache.getCachedBookById(bookIds[i]) == null) {
                goodreadRequest.getBook(bookIds[i], object : SuccessFailedCallback {
                    override fun success(response: String) {
                        val book: Book = BookBuilder.getBookFromXML(response)
                        cache.cacheBook(book)
                        searchView.showBookResult(book)
                    }

                    override fun failed() {
                        searchView.showToast("some error occurred")
                    }
                })
            } else {
                searchView.showBookResult(cache.getCachedBookById(bookIds[i])!!)
            }
        }
    }

    private fun getBookIdsFromSearchResults(xmlString: String): List<Int> {
        Log.d(TAG, "getBookIdsFromSearchResults: entered")
        val bookList: MutableList<Int> = ArrayList()
        val pullParserFactory: XmlPullParserFactory
        try {
            pullParserFactory = XmlPullParserFactory.newInstance()
            val parser = pullParserFactory.newPullParser()
            val in_s = StringReader(xmlString)
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(in_s)
            var eventType = parser.eventType
            var inBook = false
            loop@ while (eventType != XmlPullParser.END_DOCUMENT) {
                var tagName: String
                when (eventType) {
                    XmlPullParser.START_DOCUMENT -> {
                    }
                    XmlPullParser.START_TAG -> {
                        tagName = parser.name
                        if (tagName == "best_book" || inBook) {
                            inBook = true
                        } else {
                            eventType = parser.next()
                            continue@loop
                        }
                        Log.d(
                            TAG,
                            "getBookIdsFromSearchResults: parser - $tagName"
                        )
                        Log.d(
                            TAG,
                            "getBookIdsFromSearchResults: parser - entered best_book"
                        )
                        if (tagName == "id") {
                            Log.d(
                                TAG,
                                "getBookIdsFromSearchResults: parser - set id"
                            )
                            val id = parser.nextText()
                            bookList.add(id.toInt())
                            inBook = false
                        }
                    }
                }
                eventType = parser.next()
            }
        } catch (e: XmlPullParserException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Log.d(TAG, "getBookIdsFromSearchResults: exit")
        return bookList
    }

    companion object {
        private val TAG = SearchPresenter::class.java.name
    }

}