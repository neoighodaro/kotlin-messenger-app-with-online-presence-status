package com.example.messengerapp

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import java.util.*

class ChatRoomAdapter (private var list: ArrayList<MessageModel>)
  : RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.chat_item, parent, false))
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int)
      = holder.bind(list[position])

  override fun getItemCount(): Int = list.size

  fun add(message: MessageModel) {
    list.add(message)
    notifyDataSetChanged()
  }

  inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val messageTextView: TextView = itemView.findViewById(R.id.text)
    private val cardView: CardView = itemView.findViewById(R.id.cardView)

    fun bind(message: MessageModel) = with(itemView) {
      messageTextView.text = message.message
      val params = cardView.layoutParams as RelativeLayout.LayoutParams
      if (message.senderId==Singleton.getInstance().currentUser.id) {
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
      }
    }
  }
}