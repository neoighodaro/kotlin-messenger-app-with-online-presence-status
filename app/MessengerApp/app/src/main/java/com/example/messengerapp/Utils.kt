package com.example.messengerapp


import com.pusher.client.channel.User
import org.json.JSONObject

fun User.toUserModel():UserModel{
  val jsonObject = JSONObject(this.info)
  val name = jsonObject.getString("name")
  val numb = jsonObject.getInt("count")
  return UserModel(this.id,name,numb)
}