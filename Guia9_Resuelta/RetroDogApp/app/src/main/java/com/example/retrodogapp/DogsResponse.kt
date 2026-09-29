package com.example.retrodogapp

import com.google.gson.annotations.SerializedName

data class DogsResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("message") val images: List<String?>? = null
)
