package org.hackzurich2017.draw2fashion

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_image.view.*
import kotlinx.android.synthetic.main.item_product.view.*
import org.hackzurich2017.draw2fashion.draw2fashion.R


class RecognizedImagesAdapter(private val images: List<String>) :
        RecyclerView.Adapter<RecognizedImagesAdapter.ViewHolder>() {

    override fun onBindViewHolder(holder: RecognizedImagesAdapter.ViewHolder?, position: Int) {
        holder?.bind(images[position])
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecognizedImagesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_image, parent, false)
        return RecognizedImagesAdapter.ViewHolder(view)
    }

    class ViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {

        fun bind(image: String) {
            Picasso.with(itemView.context).load(image).into(itemView.recImage)
        }
    }
}