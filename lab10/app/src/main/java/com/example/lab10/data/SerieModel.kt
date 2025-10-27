package com.example.lab10.data

import com.google.gson.annotations.SerializedName

data class SerieModel(
    @SerializedName("id")
    var id: Int,
    @SerializedName("name")
    var name: String,
    @SerializedName("release_date")
    var release_date: String,
    // CRUCIAL: Debe ser Float para que Retrofit/GSON lo serialice como número decimal.
    @SerializedName("rating")
    var rating: Float,
    @SerializedName("category")
    var category: String
)