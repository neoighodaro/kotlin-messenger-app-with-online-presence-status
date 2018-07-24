package com.example.messengerapp

class Singleton {
  companion object {
    private val ourInstance = Singleton()
    fun getInstance(): Singleton {
      return ourInstance
    }
  }
  lateinit var currentUser: UserModel
}