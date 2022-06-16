package com.ffi.collectiondetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ffi.R
import com.ffi.allcataloglist.CollectionListAdapter

class CollectionDetailActivity : AppCompatActivity() {

    lateinit var collection: CollectionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_detail)

       /* rvCollection.apply {
            layoutManager = LinearLayoutManager(context)
            collection = CollectionDetailListAdapter(this@CollectionDetailActivity)
            adapter = collection
        }*/
    }
}