package com.driver.eho.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.driver.eho.R
import com.driver.eho.databinding.ItemAllBookingHistoryBinding
import com.driver.eho.model.Booking.BookingData
import com.driver.eho.utils.Constants.TAG

class BookingHistoryListAdapter(
    private var historyList: List<BookingData>?,
    private val listener: OnBookingClick
) :
    RecyclerView.Adapter<BookingHistoryListAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(val binding: ItemAllBookingHistoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(bookingData: BookingData) {

            /*  val time = bookingData.dropTime ?: ""

              val jsParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
              val date = jsParser.parse(time) ?: ""

              val calendar = Calendar.getInstance()
              calendar.time = date as Date
              calendar.add(Calendar.HOUR_OF_DAY, 5)
              calendar.add(Calendar.MINUTE, 30)

              val javaParser = SimpleDateFormat("hh:mm a", Locale.getDefault())*/


            binding.apply {
                tvTitleHere.text = bookingData.userName
                tvDate.text = bookingData.date
                tvTime.text = ""
                tvAmount.text = bookingData.price.toString()
                tvPickUpLocation.text = bookingData.pickupAddress
                tvDestinationLocation.text = bookingData.dropAddress

                Glide.with(itemView)
                    .load(bookingData.userImage)
                    .error(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .into(ivLogoBH)


                when {
                    bookingData.paymentStatusString.equals("Success") -> {
                        tvStatus.text = "Receipt"
                        tvStatus.background.setTint(Color.BLUE)
                    }
                    bookingData.paymentStatusString.isNullOrEmpty() -> {
                        tvStatus.visibility = View.INVISIBLE
                    }
                    else -> {
                        tvStatus.text = bookingData.paymentStatusString
                        tvStatus.background.setTint(Color.parseColor("#ffadb8"))
                    }
                }

                if (bookingData.paymentStatusString.equals("Success")) {
                    root.setOnClickListener {
                        val position = bindingAdapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            val item = historyList?.get(position)
                            if (item != null) {
                                listener.onClick(bookingData)
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
        val currentItem = historyList?.get(position)

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return historyList!!.size
    }

    fun updateData(payload: List<BookingData>) {
            historyList = payload
            notifyDataSetChanged()
    }

    /* private val differCallback = object : DiffUtil.ItemCallback<BookingData>() {
         override fun areItemsTheSame(oldItem: BookingData, newItem: BookingData): Boolean {
             return true
         }

         override fun areContentsTheSame(oldItem: BookingData, newItem: BookingData): Boolean {
             return true
         }
     }

     val differ = AsyncListDiffer(this, differCallback)*/

    interface OnBookingClick {
        fun onClick(bookingData: BookingData)
    }

}