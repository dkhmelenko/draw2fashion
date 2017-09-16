package org.hackzurich2017.draw2fashion

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_products.*
import org.hackzurich2017.draw2fashion.draw2fashion.R
import org.hackzurich2017.draw2fashion.fashionwell.Instance
import org.hackzurich2017.draw2fashion.fashionwell.Product
import java.io.File


const val PRODUCTS_FILE_PATH = "PRODUCTS_FILE_PATH"

class ProductsActivity : Activity() {

    val productsDataList = ArrayList<Instance>()
    val productsAdapter = ProductsAdapter(productsDataList)

    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        productsList.layoutManager = LinearLayoutManager(applicationContext)
        productsList.itemAnimator = DefaultItemAnimator()
        productsList.adapter = productsAdapter

        val filePath = intent.getStringExtra(PRODUCTS_FILE_PATH)
        val f = File(filePath)

        showProgress(true)
        uploadFile(f)
    }

    private fun setProducts(products: List<Instance>) {
        productsDataList.clear()
        productsDataList.addAll(products)

        productsAdapter.notifyDataSetChanged()
    }

    private fun appendProducts(products: List<Instance>) {
        productsDataList.addAll(products)

        productsAdapter.notifyDataSetChanged()
    }

    private fun uploadFile(file: File) {
        FashwellManager.postFile(file)
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    showProgress(false)
                    productsDataList.clear()
                    response?.products?.forEach { product -> appendProducts(product.instances) }
                    Log.d("Fashion", "success")
                }, { throwable ->
                    showProgress(false)
                    Toast.makeText(this, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
                    Log.e("Fashion", "Error", throwable)
                })
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            progressDialog = ProgressDialog.show(this, "Loading...", "")
        } else {
            progressDialog?.dismiss()
        }
    }

}
