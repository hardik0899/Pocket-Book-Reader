package com.example.pocketbookapp.adapters

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pocketbookapp.AuthorViewFragment
import com.example.pocketbookapp.R
import com.example.pocketbookapp.model.Author
import com.example.pocketbookapp.util.CircleTransform
import com.squareup.picasso.Picasso

class AuthorRecyclerViewAdapter(authorList:List<Author>):RecyclerView.Adapter<AuthorRecyclerViewAdapter.AuthorViewHolder>() {
    var authorList: List<Author> = authorList

    override fun onBindViewHolder(holder:AuthorViewHolder, position:Int) {
        val a = authorList.get(position)
        holder.name.text = a.name
        Picasso.get()
            .load(a.img)
            .transform(CircleTransform())
            .into(holder.image)
        holder.row.setOnClickListener { v->
            val i = Intent(v.context, AuthorViewFragment::class.java)
            Log.d(TAG, "onBindViewHolder: onClick" + a.img)
            i.putExtra("author", a)
            v.context.startActivity(i) }
    }
    override fun onCreateViewHolder(parent:ViewGroup, viewType:Int):AuthorViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.author_row_layout, parent, false)
        return AuthorViewHolder(v)
    }
    inner class AuthorViewHolder internal constructor(view:View):RecyclerView.ViewHolder(view) {
        internal val name:TextView
        internal val image:ImageView
        internal val row:LinearLayout
        init{
            name = view.findViewById(R.id.authorName)
            image = view.findViewById(R.id.authorImage)
            row = view.findViewById(R.id.authorRowContainer)
        }
    }
    companion object {
        private val TAG = AuthorRecyclerViewAdapter::class.java.name
    }

    override fun getItemCount(): Int {
        return authorList.size
    }
}