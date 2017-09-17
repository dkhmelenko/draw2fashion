package org.hackzurich2017.draw2fashion

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_products.*
import org.hackzurich2017.draw2fashion.draw2fashion.R
import org.hackzurich2017.draw2fashion.fashionwell.Instance
import java.io.File


const val PRODUCTS_FILE_PATH = "PRODUCTS_FILE_PATH"
const val PRODUCT_SKU = "PRODUCT_SKU"

class ProductsActivity : AppCompatActivity() {

    val productsDataList = ArrayList<Instance>()
    val productsAdapter = ProductsAdapter(productsDataList, { pos: Int ->
        val intent = Intent(this, ProductDetailActivity::class.java)
        val sku = productsDataList[pos].sku
        intent.putExtra(PRODUCT_SKU, sku)
        startActivity(intent)
    })

    var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)
        initToolbar()

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

                    if (!response?.products?.isNotEmpty()!!) {
                        emptyView.visibility = View.VISIBLE
                    } else {
                        productsDataList.clear()
                        response.products.forEach { product -> appendProducts(product.instances) }
                    }
                    Log.d("Fashion", "success")
                }, { throwable ->
                    emptyView.visibility = View.VISIBLE
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
