package org.hackzurich2017.draw2fashion.fashionwell


data class ProductsResponse(
        val products: List<Product>
)

data class Product(
        val category: String,
        val instances: List<Instance>
)

data class Instance(
        val sku: String,
        val title: String,
        val price: String,
        val brand_name: String,
        val shop_name: String,
        val product_url: String,
        val image_id: Int,
        val img_url: String,
        val msrp: String
)