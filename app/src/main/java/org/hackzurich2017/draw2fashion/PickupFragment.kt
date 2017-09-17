package org.hackzurich2017.draw2fashion

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main_alternative.*
import org.hackzurich2017.draw2fashion.draw2fashion.R
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PickupFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PickupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PickupFragment : Fragment() {

    private val CAMERA_REQUEST_CODE = 1

    private var listener: OnFragmentInteractionListener? = null

    var currentPhotoPath: String? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.activity_main_alternative, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        takeImage.setOnClickListener { takePicture() }

        openGalery.setOnClickListener { pickFromGalery() }
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

    private fun takePicture() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.CAMERA)) {

                Toast.makeText(activity, "Enable camera permission first", Toast.LENGTH_SHORT).show()
            } else {

                ActivityCompat.requestPermissions(activity,
                        arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE);
            }
        } else {
            doStartTakePicture()
        }

    }

    private fun doStartTakePicture() {

        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile();
            } catch (e: IOException) {
                e.printStackTrace()
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(activity, "com.example.android.fileprovider",
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, 0)
            }
        }
    }

    private fun copyFile(uri: Uri): File {
        var out: OutputStream?

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
        val imageFileName = "JPEG_" + timeStamp + "_";
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        val image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        )

        out = FileOutputStream(image);

        val iStream = activity.contentResolver.openInputStream(uri)
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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
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
                    Toast.makeText(activity, "Enable camera permission first", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date());
        val imageFileName = "JPEG_" + timeStamp + "_";
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        val intent = Intent(activity, ProductsActivity::class.java)
        intent.putExtra(PRODUCTS_FILE_PATH, currentPhotoPath)
        startActivity(intent)
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
        fun onPickupAction(uri: Uri)
    }

    companion object {

        fun newInstance(): PickupFragment {
            val fragment = PickupFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
