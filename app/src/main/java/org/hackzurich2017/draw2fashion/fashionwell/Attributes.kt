package org.hackzurich2017.draw2fashion.fashionwell


data class AttributesResponse(
        val attributes: List<Attribute>,
        val general: General
)

data class General(
        val fashion: Double
)

data class Attribute(
        val caption: String,
        val x: Double,
        val width: Double,
        val y: Double,
        val attributes: Map<String, Map<String, Any>>,
        val height: Double
)

data class ProductAttributes(val attribute: Map<String, Any>)