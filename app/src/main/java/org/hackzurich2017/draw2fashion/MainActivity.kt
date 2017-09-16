package org.hackzurich2017.draw2fashion

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.hackzurich2017.draw2fashion.draw2fashion.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        saveButton.setOnClickListener { saveImage() }
    }

    private fun saveImage() {
        var bitmap = drawingPad.signatureBitmap

        // TODO Implement sending bitmap for image recognition

        drawingPad.clear()
    }
}
