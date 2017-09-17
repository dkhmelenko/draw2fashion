package org.hackzurich2017.draw2fashion

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.activity_recognized_images.*
import org.hackzurich2017.draw2fashion.draw2fashion.R

class RecognizedImagesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recognized_images)
        initToolbar()

        val images = intent.getStringArrayListExtra("RECOGNIZED_IMAGES")

        recognizedImages.layoutManager = GridLayoutManager(this, 3)
        recognizedImages.itemAnimator = DefaultItemAnimator()
        recognizedImages.adapter = RecognizedImagesAdapter(images)
    }

    private fun initToolbar() {
        val actionBar = getSupportActionBar()

        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
