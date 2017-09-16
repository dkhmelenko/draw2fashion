package org.hackzurich2017.draw2fashion

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_product.view.*
import org.hackzurich2017.draw2fashion.draw2fashion.R
import org.hackzurich2017.draw2fashion.fashionwell.Instance

class ProductsAdapter(private val products: List<Instance>) :
        RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(products[position])
    }

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {

        fun bind(product: Instance) {
            with(product) {
                Picasso.with(itemView.context).load(img_url).into(itemView.itemImage)
                itemView.itemTitle.text = title
                itemView.itemPrice.text = price
                itemView.itemBrand.text = brand_name
                itemView.seller.text = "Buy at ${shop_name}"
            }
        }
    }

}