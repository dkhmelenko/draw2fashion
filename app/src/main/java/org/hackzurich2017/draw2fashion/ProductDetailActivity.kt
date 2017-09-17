package org.hackzurich2017.draw2fashion

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_product_detail.*
import org.hackzurich2017.draw2fashion.draw2fashion.R
import org.hackzurich2017.draw2fashion.fashionwell.Instance


class ProductDetailActivity : AppCompatActivity() {

    var progressDialog: ProgressDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        initToolbar()

        val sku = intent.getStringExtra(PRODUCT_SKU)
        val gson = Gson()
        val product = gson.fromJson<Instance>(sku, Instance::class.java)

        setProductData(product)

        // loadProductDetails(sku)
    }

    private fun loadProductDetails(sku: String) {
        FashwellManager.getDetails(sku)
                .retry(3)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    showProgress(false)

                    setProductData(response)
                    Log.d("Fashion", "success")
                }, { throwable ->
                    showProgress(false)
                    Toast.makeText(this, "Sorry, something went wrong!", Toast.LENGTH_SHORT).show()
                    finish()
                    Log.e("Fashion", "Error", throwable)
                })
    }

    private fun ProductDetailActivity.setProductData(response: ProductDetails) {
        with(response.product) {
            Picasso.with(applicationContext).load(img_url).into(productImage)
            productTitle.text = title
            productPrice.text = price
            //productBrand.text = brand_name
            supportActionBar?.title = brand_name
            buyButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(product_url))
                startActivity(browserIntent)
            }
        }
    }

    private fun ProductDetailActivity.setProductData(product: Instance) {
        Picasso.with(applicationContext).load(product.img_url).into(productImage)
        productTitle.text = product.title
        productPrice.text = product.price
        //productBrand.text = brand_name
        supportActionBar?.title = product.brand_name
        buyButton.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(product.product_url))
            startActivity(browserIntent)
        }
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
