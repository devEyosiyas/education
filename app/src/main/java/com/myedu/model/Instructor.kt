package com.myedu.model


import com.google.gson.annotations.SerializedName

data class Instructor(
    @SerializedName("_class")
    val classX: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("image_100x100")
    val image100x100: String,
    @SerializedName("image_50x50")
    val image50x50: String,
    @SerializedName("initials")
    val initials: String,
    @SerializedName("job_title")
    val jobTitle: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String
)