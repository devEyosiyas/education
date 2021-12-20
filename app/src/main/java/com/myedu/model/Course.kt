package com.myedu.model


import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Course(
    @SerializedName("_class")
    val classX: String,
    @SerializedName("headline")
    val headline: String,
    @SerializedName("id")
    @PrimaryKey
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
    var favourite: Boolean
) {
    @Ignore
    @SerializedName("curriculum_items")
    val curriculumItems: List<Any>? = null

    @Ignore
    @SerializedName("curriculum_lectures")
    val curriculumLectures: List<Any>? = null

    @Ignore
    @SerializedName("input_features")
    val inputFeatures: Any? = null

    @Ignore
    @SerializedName("instructor_name")
    val instructorName: Any? = null

    @Ignore
    @SerializedName("lecture_search_result")
    val lectureSearchResult: Any? = null

    @Ignore
    @SerializedName("order_in_results")
    val orderInResults: Any? = null

    @Ignore
    @SerializedName("predictive_score")
    val predictiveScore: Any? = null

    @Ignore
    @SerializedName("price_detail")
    val priceDetail: Any? = null

    @Ignore
    @SerializedName("relevancy_score")
    val relevancyScore: Any? = null

    @Ignore
    @SerializedName("visible_instructors")
    val instructors: List<Instructor>? = null
}