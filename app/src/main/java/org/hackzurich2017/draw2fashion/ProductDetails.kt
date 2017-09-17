package org.hackzurich2017.draw2fashion


data class ProductDetails(
        val product: Details
)

data class Details(
        val sku: String, // 12096232
        val product_url: String, // https://www.farfetch.com/de/shopping/women/rag-bone-alicia-cropped-t-shirt-item-12096232.aspx
        val title: String, // Rag & Bone "Alicia" Cropped-T-Shirt
        val price: String, // 71.75 €
        val img_url: String, // https://media.fashwell.com/shop/instance/12564624.jpg
        val brand_name: String, // rag & bone
        val msrp: String// 110.38 €
)