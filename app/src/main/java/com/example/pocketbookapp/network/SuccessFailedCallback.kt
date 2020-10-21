package com.example.pocketbookapp.network

interface SuccessFailedCallback {
    fun success(response : String)

    fun failed()
}