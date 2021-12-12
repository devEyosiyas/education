package com.myedu.event

import com.myedu.BuildConfig
import com.myedu.model.response.CourseResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ServerRequest {
    @Headers(
        "Accept: application/json, text/plain, */*",
        "Authorization: " + BuildConfig.Authorization,
        "Content-Type: application/json;charset=utf-8"
    )
    @GET("api-2.0/courses/")
    fun getCourses(
        @Query(value = "page") page: Int,
        @Query(value = "page_size") pageSize: Int
    ): Call<CourseResponse?>
}