package org.hackzurich2017.draw2fashion

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ArrayMap
import com.google.api.services.vision.v1.Vision
import com.google.api.services.vision.v1.VisionRequestInitializer
import com.google.api.services.vision.v1.model.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import org.hackzurich2017.draw2fashion.draw2fashion.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [DrawFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [DrawFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrawFragment : Fragment() {

    private val CLOUD_VISION_API_KEY = "AIzaSyDsZ_o23WPCcTMLMJYUmjrI31GHwDc-gmg"


    var progressDialog: ProgressDialog? = null

    private var listener: OnFragmentInteractionListener? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.activity_main, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton.setOnClickListener { saveImage() }
    }

    private fun saveImage() {
        var bitmap = drawingPad.signatureBitmap

        progressDialog = ProgressDialog.show(activity, "Loading...", "We are trying to recognize your picture")
        callCloudVision(bitmap)

        drawingPad.clear()
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

                progressDialog?.dismiss()

                val intent = Intent(activity, RecognizedImagesActivity::class.java)
                val arrayList = ArrayList(result)
                intent.putStringArrayListExtra("RECOGNIZED_IMAGES", arrayList)
                startActivity(intent)

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


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onDrawAction()
    }

    companion object {
        fun newInstance(): DrawFragment {
            val fragment = DrawFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
