package com.example.pocketbookapp.network

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class GoodreadRequest(key:String, context:Context) {
    private val requestQueue:RequestQueue
    private val key:String
    init{
        this.key = key
        requestQueue = Volley.newRequestQueue(context)
    }
    private fun request(url:String, callback:SuccessFailedCallback) {
        Log.d(TAG, "request: " + url)
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response->
                Log.d(TAG, "response : " + response)
                if (callback != null)
                {
                    callback.success(response)
                } }, { error-> if (callback != null)
            {
                callback.failed()
            } })
        requestQueue.add(stringRequest)
    }

    fun getBook(id:Int, callback:SuccessFailedCallback) {
        val url = "https://www.goodreads.com/book/show/" + id + ".xml?key=" + key
        request(url, callback)
    }

    fun getAuthor(id:Int, callback:SuccessFailedCallback) {
        val url = "https://www.goodreads.com/author/show/" + id + "?format=xml&key=" + key
        request(url, callback)
    }

    fun searchBook(query:String, callback:SuccessFailedCallback) {
        val url = "https://www.goodreads.com/search/index.xml?q=" + query + "&key=" + key
        request(url, callback)
    }
    companion object {
        private val TAG = GoodreadRequest::class.java.getName()
    }
}