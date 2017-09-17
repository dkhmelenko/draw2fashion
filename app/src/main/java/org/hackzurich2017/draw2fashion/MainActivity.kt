package org.hackzurich2017.draw2fashion

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
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
import kotlinx.android.synthetic.main.activity_main_alternative.*
import org.hackzurich2017.draw2fashion.draw2fashion.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val CLOUD_VISION_API_KEY = "AIzaSyDsZ_o23WPCcTMLMJYUmjrI31GHwDc-gmg"

    private val CAMERA_REQUEST_CODE = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_alternative)

        // TODO saveButton.setOnClickListener { saveImage() }

        takeImage.setOnClickListener { takePicture() }

        openGalery.setOnClickListener { pickFromGalery() }
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

    private fun takePicture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                Toast.makeText(this, "Enable camera permission first", Toast.LENGTH_SHORT).show()
            } else {

                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE);
            }
        } else {
            doStartTakePicture()
        }

    }

    private fun copyFile(uri: Uri): File {
        var out: OutputStream?

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
        val imageFileName = "JPEG_" + timeStamp + "_";
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        val image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )

        out = FileOutputStream(image);

        val iStream = contentResolver.openInputStream(uri)
        val inputData = getBytes(iStream)

        out.write(inputData);
        out.close()

        currentPhotoPath = image.getAbsolutePath();
        return image
    }

    fun getBytes(inputStream: InputStream): ByteArray {
        val byteBuffer = ByteArrayOutputStream()
        val bufferSize = 1024
        val buffer = ByteArray(bufferSize)

        var len = 0
        while (len != -1) {
            len = inputStream.read(buffer)
            if (len == -1) {
                break
            }
            byteBuffer.write(buffer, 0, len)
        }
        return byteBuffer.toByteArray()
    }

    private fun pickFromGalery() {
        val pickPhoto = Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, 1)//one can be replaced with any action code
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doStartTakePicture()
                } else {
                    Toast.makeText(this, "Enable camera permission first", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun doStartTakePicture() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile();
            } catch (e: IOException) {
                e.printStackTrace()
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this, "com.example.android.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, 0)
            }
        }
    }

    var currentPhotoPath: String? = null

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
        val imageFileName = "JPEG_" + timeStamp + "_";
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        val image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent?) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
        when (requestCode) {
            0 -> if (resultCode == Activity.RESULT_OK) {
                emptyView.visibility = View.GONE
                val f = File(currentPhotoPath)
                imageView.setImageURI(Uri.fromFile(f))

                goToProducts()

            }
            1 -> if (resultCode == Activity.RESULT_OK) {
                emptyView.visibility = View.GONE
                val selectedImage = imageReturnedIntent?.data
                imageView.setImageURI(selectedImage)

                copyFile(selectedImage!!)

                goToProducts()
            }
        }
    }

    private fun goToProducts() {
        val intent = Intent(this, ProductsActivity::class.java)
        intent.putExtra(PRODUCTS_FILE_PATH, currentPhotoPath)
        startActivity(intent)
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

}
