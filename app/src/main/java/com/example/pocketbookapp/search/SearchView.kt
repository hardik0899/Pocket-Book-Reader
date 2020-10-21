package com.example.pocketbookapp.search

import com.example.pocketbookapp.model.Book
import com.example.pocketbookapp.util.Toastable

interface SearchView : Toastable{

    fun showBookResult(book : Book)
}