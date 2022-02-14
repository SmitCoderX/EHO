package com.driver.eho.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.driver.eho.databinding.LayoutNotificationBinding
import com.driver.eho.model.Notification.NotificationData

class NotificationAdapter(var list: List<NotificationData>) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(val binding: LayoutNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(notificationData: NotificationData) {
            binding.apply {
                tvTitleNotification.text = notificationData.title
                tvDateNotification.text = notificationData.date
                tvDescNotification.text = notificationData.description
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            LayoutNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val currentItem = list[position]

        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(payload: List<NotificationData>) {
        list = payload
        notifyDataSetChanged()
    }

}