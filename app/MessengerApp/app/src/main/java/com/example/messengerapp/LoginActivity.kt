package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    if(PreferenceManager.getDefaultSharedPreferences(this).contains("USER")){
      loginFunction(PreferenceManager.getDefaultSharedPreferences(this).getString("USER",""))
    }

    loginButton.setOnClickListener {
      if (editTextUsername.text.isNotEmpty()) {

        var pref = PreferenceManager.getDefaultSharedPreferences(this).edit()
        pref.putString("USER",editTextUsername.text.toString())
        pref.apply()


        loginFunction(editTextUsername.text.toString())
      }
    }
  }

  private fun loginFunction(name:String) {
    val jsonObject = JSONObject()
    jsonObject.put("name",name)
    val jsonBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
        jsonObject.toString())

    RetrofitInstance.retrofit.login(jsonBody).enqueue(object:Callback<UserModel> {
      override fun onFailure(call: Call<UserModel>?, t: Throwable?) {
        Log.i("LoginActivity",t!!.localizedMessage)
      }

      override fun onResponse(call: Call<UserModel>?, response: Response<UserModel>?) {
        if (response!!.code()==200 ){
          Singleton.getInstance().currentUser = response.body()!!
          startActivity(Intent(this@LoginActivity,ContactListActivity::class.java))
          finish()
        }
      }
    })
  }
}
