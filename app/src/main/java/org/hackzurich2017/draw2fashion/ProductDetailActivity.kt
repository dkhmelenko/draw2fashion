package org.hackzurich2017.draw2fashion

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.item_product.*
import kotlinx.android.synthetic.main.item_product.view.*
import org.hackzurich2017.draw2fashion.draw2fashion.R
import java.io.File

class ProductDetailActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        initToolbar()

        val sku = intent.getStringExtra(PRODUCT_SKU)
        loadProductDetails(sku)
    }

    private fun loadProductDetails(sku: String) {
        FashwellManager.getDetails(sku)
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    showProgress(false)

                    with(response.product) {
                        Picasso.with(applicationContext).load(img_url).into(productImage)
                        productTitle.text = title
                        productPrice.text = price
                        productBrand.text = brand_name
                    }
                    Log.d("Fashion", "success")
                }, { throwable ->
                    showProgress(false)
                    Toast.makeText(this, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
                    finish()
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
