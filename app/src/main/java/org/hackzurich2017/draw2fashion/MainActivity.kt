package org.hackzurich2017.draw2fashion

import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ArrayMap
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tab_layout.*
import org.hackzurich2017.draw2fashion.draw2fashion.R
import java.io.*
import java.util.*


class MainActivity : AppCompatActivity(), PickupFragment.OnFragmentInteractionListener,
        DrawFragment.OnFragmentInteractionListener {

    override fun onPickupAction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDrawAction() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    public val CAMERA_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tab_layout)

        val pagerAdapter = PagerAdapter(getSupportFragmentManager())
        pager.setAdapter(pagerAdapter)
        pager.setOffscreenPageLimit(3)

        tabs.setupWithViewPager(pager)

        // icons TODO
//        tabs.getTabAt(0)?.setIcon(android.R.drawable.ic_)
//        tabs.getTabAt(1)?.setIcon(android.R.drawable.edit_text)
    }


    private fun getFileAttributes(file: File) {
        FashwellManager.getFileAttributes(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.d("Fashion", "success")
                }, { throwable ->
                    Log.e("Fashion", "Error", throwable)
                })
    }

    private fun uploadFile(file: File) {
        FashwellManager.postFile(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.d("Fashion", "success")
                }, { throwable ->
                    Log.e("Fashion", "Error", throwable)
                })
    }

    private fun savebitmap(filename: String, bitmap: Bitmap): File {
        val filesDir = applicationContext.getFilesDir()
        val imageFile = File(filesDir, filename + ".jpg")

        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap", e)
        }

        return imageFile
    }


    ///// UI UI UI

    private inner class PagerAdapter(fragmentManager: FragmentManager) : StateAdapter(fragmentManager) {

        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment? {
            when (position) {
                0 -> return PickupFragment.newInstance()
                1 -> return DrawFragment.newInstance()
                else -> return null
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            when (position) {
                0 -> return getString(R.string.pickup_fragment)
                1 -> return getString(R.string.draw_fragment)
                else -> return null
            }
        }

    }

}
