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

    private val CLOUD_VISION_API_KEY = "AIzaSyDsZ_o23WPCcTMLMJYUmjrI31GHwDc-gmg"

    public val CAMERA_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tab_layout)

        // TODO saveButton.setOnClickListener { saveImage() }

        val pagerAdapter = PagerAdapter(getSupportFragmentManager())
        pager.setAdapter(pagerAdapter)
        pager.setOffscreenPageLimit(3)

        tabs.setupWithViewPager(pager)

        // icons TODO
//        tabs.getTabAt(0)?.setIcon(android.R.drawable.ic_)
//        tabs.getTabAt(1)?.setIcon(android.R.drawable.edit_text)
    }

    private fun saveImage() {
        var bitmap = drawingPad.signatureBitmap

        // TODO Implement sending bitmap for image recognition

        //callCloudVision(bitmap)

        val file = savebitmap("test2", bitmap)

        uploadFile(file)

        drawingPad.clear()
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


    private fun callCloudVision(bitmap: Bitmap) {

        // Do the real work in an async task, because we need to use the network anyway
        object : AsyncTask<Any, Void, List<String>>() {
            override fun doInBackground(vararg params: Any): List<String> {
                try {
                    val httpTransport = AndroidHttp.newCompatibleTransport()
                    val jsonFactory = GsonFactory.getDefaultInstance()

                    var requestInitializer = VisionRequestInitializer(CLOUD_VISION_API_KEY)

                    val builder = Vision.Builder(httpTransport, jsonFactory, null)
                    builder.setVisionRequestInitializer(requestInitializer)

                    val vision = builder.build()

                    val batchAnnotateImagesRequest = BatchAnnotateImagesRequest()
                    batchAnnotateImagesRequest.setRequests(object : ArrayList<AnnotateImageRequest>() {
                        init {
                            val annotateImageRequest = AnnotateImageRequest()

                            // Add the image
                            val base64EncodedImage = Image()
                            // Convert the bitmap to a JPEG
                            // Just in case it's a format that Android understands but Cloud Vision
                            val byteArrayOutputStream = ByteArrayOutputStream()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
                            val imageBytes = byteArrayOutputStream.toByteArray()

                            // Base64 encode the JPEG
                            base64EncodedImage.encodeContent(imageBytes)
                            annotateImageRequest.setImage(base64EncodedImage)

                            // add the features we want
                            annotateImageRequest.setFeatures(object : ArrayList<Feature>() {
                                init {
                                    val labelDetection = Feature()
                                    labelDetection.setType("WEB_DETECTION")
                                    labelDetection.setMaxResults(10)
                                    add(labelDetection)
                                }
                            })

                            // Add the list of one thing to the request
                            add(annotateImageRequest)
                        }
                    })

                    val annotateRequest = vision.images().annotate(batchAnnotateImagesRequest)
                    // Due to a bug: requests to Vision API containing large images fail when GZipped.
                    annotateRequest.setDisableGZipContent(true)

                    val response = annotateRequest.execute()
                    return parseResponse(response)

                } catch (e: GoogleJsonResponseException) {
                    Log.d("Fashion", "failed to make API request because " + e.getContent())
                } catch (e: IOException) {
                    Log.d("Fashion", "failed to make API request because of other IOException " + e.message)
                }

                return listOf<String>()
            }

            override fun onPostExecute(result: List<String>) {
                Log.d("Fashion", "Response: ${result}")

            }
        }.execute()
    }

    private fun parseResponse(response: BatchAnnotateImagesResponse): List<String> {
        val resultList = ArrayList<String>()

        val labels = response.responses[0].get("webDetection")
        if (labels is ArrayMap<*, *>) {
            val images = labels.get("visuallySimilarImages")
            if (images is ArrayList<*>) {
                for (label in images) {
                    if (label is ArrayMap<*, *>) {
                        if (label.get("url") is String) {
                            resultList.add(label.get("url") as String)
                        }
                    }
                }
            }
        }

        return resultList
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
