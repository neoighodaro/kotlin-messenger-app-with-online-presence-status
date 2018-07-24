package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.User
import com.pusher.client.util.HttpAuthorizer
import kotlinx.android.synthetic.main.activity_contact_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ContactListActivity : AppCompatActivity(),
    ContactRecyclerAdapter.UserClickListener {

  private val mAdapter = ContactRecyclerAdapter(ArrayList(), this)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_contact_list)
    setupRecyclerView()
    fetchUsers()
    subscribeToChannel()
  }


  private fun setupRecyclerView() {
    with(recyclerViewUserList) {
      layoutManager = LinearLayoutManager(this@ContactListActivity)
      adapter = mAdapter
    }
  }


  private fun fetchUsers() {
    RetrofitInstance.retrofit.getUsers().enqueue(object : Callback<List<UserModel>> {
      override fun onFailure(call: Call<List<UserModel>>?, t: Throwable?) {

      }

      override fun onResponse(call: Call<List<UserModel>>?, response: Response<List<UserModel>>?) {

        for (user in response!!.body()!!) {
          if (user.id != Singleton.getInstance().currentUser.id) {
            mAdapter.add(user)
          }
        }
      }

    })
  }


  private fun subscribeToChannel() {

    val authorizer = HttpAuthorizer("http://10.0.2.2:5000/pusher/auth/presence")
    val options = PusherOptions().setAuthorizer(authorizer)
    options.setCluster("PUSHER_APP_CLUSTER")

    val pusher = Pusher("PUSHER_APP_KEY", options)
    pusher.connect()

    pusher.subscribePresence("presence-channel",
        object : PresenceChannelEventListener {
          override fun onUsersInformationReceived(p0: String?, users: MutableSet<User>?) {
            for (user in users!!) {
              if (user.id!=Singleton.getInstance().currentUser.id){
                runOnUiThread {
                  mAdapter.showUserOnline(user.toUserModel())
                }
              }
            }

          }

          override fun onEvent(p0: String?, p1: String?, p2: String?) {

          }

          override fun onAuthenticationFailure(p0: String?, p1: Exception?) {
            Log.e("Pusher", p1!!.message)
          }

          override fun onSubscriptionSucceeded(p0: String?) {
            Log.i("Pusher", "Subscription succeeded")
          }

          override fun userSubscribed(channelName: String, user: User) {
            runOnUiThread {
              mAdapter.showUserOnline(user.toUserModel())
            }
          }

          override fun userUnsubscribed(channelName: String, user: User) {
            runOnUiThread {
              mAdapter.showUserOffline(user.toUserModel())
            }
          }
        })
  }


  override fun onUserClicked(user: UserModel) {
    val intent = Intent(this,ChatRoom::class.java)
    intent.putExtra(ChatRoom.EXTRA_ID,user.id)
    intent.putExtra(ChatRoom.EXTRA_NAME,user.name)
    intent.putExtra(ChatRoom.EXTRA_COUNT,user.count)
    startActivity(intent)
  }


}
