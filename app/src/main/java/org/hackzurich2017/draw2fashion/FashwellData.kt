package org.hackzurich2017.draw2fashion

import com.google.gson.annotations.SerializedName


data class FashwellData(@SerializedName("id") val id: Long,
                        @SerializedName("name") val name: String,
                        @SerializedName("image") val image: String,
                        @SerializedName("description") val description: String)
