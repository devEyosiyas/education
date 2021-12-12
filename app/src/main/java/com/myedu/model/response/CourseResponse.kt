package com.myedu.model.response


import com.google.gson.annotations.SerializedName
import com.myedu.model.Course

data class CourseResponse(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: String,
    @SerializedName("previous")
    val previous: Any,
    @SerializedName("results")
    val courses: List<Course>,
    @SerializedName("search_tracking_id")
    val searchTrackingId: String
)