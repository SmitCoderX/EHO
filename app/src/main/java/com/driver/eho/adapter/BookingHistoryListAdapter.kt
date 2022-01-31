package com.driver.eho.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.driver.eho.R
import com.driver.eho.databinding.ItemAllBookingHistoryBinding
import com.driver.eho.model.Booking.Data
import com.driver.eho.utils.Constants.TAG

class BookingHistoryListAdapter(private val listener: OnBookingClick) :
    RecyclerView.Adapter<BookingHistoryListAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(val binding: ItemAllBookingHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data) {

            /*  val time = data.dropTime ?: ""

              val jsParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
              val date = jsParser.parse(time) ?: ""

              val calendar = Calendar.getInstance()
              calendar.time = date as Date
              calendar.add(Calendar.HOUR_OF_DAY, 5)
              calendar.add(Calendar.MINUTE, 30)

              val javaParser = SimpleDateFormat("hh:mm a", Locale.getDefault())*/


            binding.apply {
                tvTitleHere.text = data.userName
                tvDate.text = data.date
                tvTime.text = ""
                tvAmount.text = data.price.toString()
                tvPickUpLocation.text = data.pickupAddress
                tvDestinationLocation.text = data.dropAddress

                Glide.with(itemView)
                    .load(data.userImage)
                    .error(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .into(ivLogoBH)

                if (data.paymentStatusString.equals("Success")) {
                    tvStatus.text = "Receipt"
                    tvStatus.background.setTint(Color.BLUE)
                } else if (data.paymentStatusString.isNullOrEmpty()) {
                    tvStatus.visibility = View.INVISIBLE
                } else {
                    tvStatus.text = data.paymentStatusString
                    tvStatus.background.setTint(Color.parseColor("#ffadb8"))
                }

                if (data.paymentStatusString.equals("Success")) {
                    root.setOnClickListener {
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val item = differ.currentList[position]
                            if (item != null) {
                                listener.onClick(data)
                            }
                        }
                    }
                } else {
                    Log.d(TAG, "bind: Not Successful")
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding =
            ItemAllBookingHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return true
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnBookingClick {
        fun onClick(data: Data)
    }

}