package org.hackzurich2017.draw2fashion.cloudvision

import com.google.gson.annotations.SerializedName


data class WebDetection(
        @SerializedName("visuallySimilarImages") val visuallySimilarImages: List<ImageUrl>) {
}