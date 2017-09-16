package org.hackzurich2017.draw2fashion.cloudvision

import com.google.gson.annotations.SerializedName

data class CloudVisionData(@SerializedName("webDetection") val webDetection: WebDetection) {

}
