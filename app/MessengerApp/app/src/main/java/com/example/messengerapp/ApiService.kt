package com.example.messengerapp

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

  @POST("/login")
  fun login(@Body body:RequestBody): Call<UserModel>

  @POST("/send-message")
  fun sendMessage(@Body body:RequestBody): Call<String>

  @GET("/users")
  fun getUsers(): Call<List<UserModel>>

}