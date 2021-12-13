package com.myedu.model.response


import com.google.gson.annotations.SerializedName
import com.myedu.model.Instructor

data class CourseDetailResponse(
    @SerializedName("_class")
    val classX: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image_125_H")
    val image125H: String,
    @SerializedName("image_240x135")
    val image240x135: String,
    @SerializedName("image_480x270")
    val image480x270: String,
    @SerializedName("is_paid")
    val isPaid: Boolean,
    @SerializedName("is_practice_test_course")
    val isPracticeTestCourse: Boolean,
    @SerializedName("price")
    val price: String,
    @SerializedName("price_detail")
    val priceDetail: Any,
    @SerializedName("price_serve_tracking_id")
    val priceServeTrackingId: String,
    @SerializedName("published_title")
    val publishedTitle: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("tracking_id")
    val trackingId: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("visible_instructors")
    val instructors: List<Instructor>
)