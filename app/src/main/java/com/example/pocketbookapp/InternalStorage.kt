package com.example.pocketbookapp

import android.content.Context
import android.util.Log
import com.example.pocketbookapp.model.Author
import com.example.pocketbookapp.model.Book
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.lang.ref.WeakReference
import java.util.*

class InternalStorage private constructor(context: Context) {
    private val mContext: WeakReference<Context>
    private var bookCache: MutableMap<Int, Book>? = null
    private var authorCache: MutableMap<Int, Author>? = null
    private var favListCache: MutableList<Int>? = null

    fun cacheBook(book: Book) {
        try {
            bookCache!![book.getId()] = book
            writeObject(
                mContext.get(),
                BOOK_CACHE,
                bookCache
            )
        } catch (err: IOException) {
            Log.e(
                TAG,
                "cacheBook: cannot write into storage",
                err
            )
        }
    }

    fun getCachedBookById(id: Int): Book? {
        return if (bookCache!!.containsKey(id)) {
            bookCache!![id]
        } else null
    }

    fun cacheAuthor(author: Author) {
        try {
            authorCache!![author.getId()] = author
            writeObject(
                mContext.get(),
                AUTHOR_CACHE,
                authorCache
            )
        } catch (err: IOException) {
            Log.e(
                TAG,
                "cacheAuthor: cannot write into storage",
                err
            )
        }
    }

    fun getCachedAuthorById(id: Int): Author? {
        return if (authorCache!!.containsKey(id)) {
            authorCache!![id]
        } else null
    }


    fun getFavListCache(): List<Int>? {
        Log.d(TAG, "getFavListCache: $favListCache")
        return favListCache
    }

    fun isFavorite(id: Int): Boolean {
        for (i in favListCache!!) {
            if (i == id) {
                return true
            }
        }
        return false
    }

    fun addToFavList(id: Int) {
        Log.d(TAG, "addToFavList: $id")
        try {
            favListCache!!.add(id)
            writeObject(
                mContext.get(),
                FAV_CACHE,
                favListCache
            )
        } catch (err: IOException) {
            Log.e(
                TAG,
                "addToFavList: cannot write into storage",
                err
            )
        }
    }

    fun removeFromFavList(id: Int) {
        Log.d(TAG, "removeFromFavList: $id")
        try {
            for (i in favListCache!!.indices) {
                if (favListCache!![i] == id) {
                    favListCache!!.removeAt(i)
                    break
                }
            }
            writeObject(
                mContext.get(),
                FAV_CACHE,
                favListCache
            )
        } catch (err: IOException) {
            Log.e(
                TAG,
                "removeFromFavList: cannot write into storage",
                err
            )
        }
        Log.d(TAG, "removeFromFavList: " + favListCache)
    }

    companion object {
        private val TAG = InternalStorage::class.java.name
        private var instance: InternalStorage? = null
        private const val BOOK_CACHE = "BOOK_CACHE"
        private const val AUTHOR_CACHE = "AUTHOR_CACHE"
        private const val FAV_CACHE = "FAV_CACHE"

        fun init(context: Context) {
            instance = InternalStorage(context)
        }

        fun getInstance(): InternalStorage? {
            if (instance == null) {
                throw Error("Internal storage was not initialised")
            }
            return instance
        }

        @Throws(IOException::class)
        private fun writeObject(
            context: Context?,
            fileName: String,
            `object`: Any?
        ) {
            val fos =
                context!!.openFileOutput(fileName, Context.MODE_PRIVATE)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(`object`)
            oos.close()
            fos.close()
        }

        @Throws(IOException::class, ClassNotFoundException::class)
        private fun readObject(
            context: Context?,
            fileName: String
        ): Any {
            val fis = context!!.openFileInput(fileName)
            val ois = ObjectInputStream(fis)
            return ois.readObject()
        }
    }

    init {
        mContext = WeakReference(context)
        try {
            bookCache = readObject(
                mContext.get(),
                BOOK_CACHE
            ) as HashMap<Int, Book>
            authorCache = readObject(
                mContext.get(),
                AUTHOR_CACHE
            ) as HashMap<Int, Author>
        } catch (er: ClassNotFoundException) {
            Log.e(TAG, "InternalStorage: ", er)
        } catch (er: IOException) {
            try {
                bookCache = HashMap<Int, Book>()
                authorCache = HashMap<Int, Author>()
                writeObject(
                    mContext.get(),
                    BOOK_CACHE,
                    bookCache
                )
                writeObject(
                    mContext.get(),
                    AUTHOR_CACHE,
                    authorCache
                )
            } catch (err: IOException) {
                Log.e(
                    TAG,
                    "InternalStorage: cannot write into storage",
                    err
                )
            }
        }
        try {
            favListCache = readObject(
                mContext.get(),
                FAV_CACHE
            ) as ArrayList<Int>
        } catch (er: ClassNotFoundException) {
            Log.e(TAG, "InternalStorage: ", er)
        } catch (er: IOException) {
            try {
                favListCache = ArrayList()
                writeObject(
                    mContext.get(),
                    FAV_CACHE,
                    favListCache
                )
            } catch (err: IOException) {
                Log.e(
                    TAG,
                    "InternalStorage: cannot write into storage",
                    err
                )
            }
        }
    }
}