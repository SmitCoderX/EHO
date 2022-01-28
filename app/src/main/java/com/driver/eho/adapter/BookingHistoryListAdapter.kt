package com.driver.eho.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.driver.eho.R
import com.driver.eho.model.BookingHistoryListModel

class BookingHistoryListAdapter(var item: Array<BookingHistoryListModel>):
    RecyclerView.Adapter<BookingHistoryListAdapter.MyViewHolder>() {

/*
    var item = ArrayList<BookingHistoryListModel>()
*/

    /*fun setTaskListData(bookingHistoryList: ArrayList<BookingHistoryListModel>){
        this.item = bookingHistoryList
    }*/


    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        val tvTitleHere = view.findViewById<TextView>(R.id.tvTitleHere)
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)
        val tvAmount = view.findViewById<TextView>(R.id.tvAmount)
        val tvPickUpLocation = view.findViewById<TextView>(R.id.tvPickUpLocation)
        val tvDestinationLocation = view.findViewById<TextView>(R.id.tvDestinationLocation)
        @SuppressLint("SetTextI18n")
        fun bind(bookingHistoryList: BookingHistoryListModel){

            tvTitleHere.text = bookingHistoryList.title
            tvDate.text = bookingHistoryList.date
            tvTime.text = bookingHistoryList.time
            tvAmount.text = bookingHistoryList.ammount
            tvPickUpLocation.text = bookingHistoryList.pickLocation
            tvDestinationLocation.text = bookingHistoryList.destinationLocation


         /*
            tasktxtTime.text = taskList.dueFormate
            //    tasktxtCountMessage.text = taskList.taskRepeatStatus*/


          /*  // task progressbar without progress bar
            val childParam1 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            childParam1.weight = taskList.percentageOfPending!!.toFloat()
            tasktxtPending.layoutParams = childParam1

            val childParam2 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            childParam2.weight = taskList.percentageOfSkip!!.toFloat()
            tasktxtSkipped.layoutParams = childParam2

            val childParam3 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            childParam3.weight = taskList.persentageOfAccept!!.toFloat()
            tasktxtAccept.layoutParams = childParam3

            val childParam4 = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT)
            childParam4.weight = taskList.persentageOfPerformed!!.toFloat()
            tasktxtPerform.layoutParams = childParam4
            val isFrom: String? = null

            // task progressbar text
            if (Math.round(taskList.percentageOfPending.toFloat()) == 100) {
                tasktxtStatus.text = "Pending"
            }
            if (Math.round(taskList.percentageOfSkip.toFloat()) == 100) {
                tasktxtStatus.text = "Skipped"
            }
            if (Math.round(taskList.persentageOfAccept.toFloat()) == 100) {
                tasktxtStatus.text = "Accepted"
            }
            if (Math.round(taskList.persentageOfPerformed.toFloat()) == 100) {
                tasktxtStatus.text = "Perform"
            }

            // CompletedTaskFilter and change color
            tasktxtDocCount.text = taskList.daysLeft.toString()
            if (isFrom == "CompletedTaskFilter") {
                if (taskList.createdByUserId!! == sharedPreferences.getString(ApiConstant.USER_ID, "")!!.length
                ) {
                    if (taskList.creatorPerformDate != null) {
                        tasktxtTime.text = taskList.creatorPerformDate as CharSequence?
                    }
                } else {
                    if (taskList.userPerformedDate != null) {
                        tasktxtTime.text = taskList.userPerformedDate
                    }
                }
            } else {
                tasktxtTime.text = taskList.dueFormate
            }
            tasktxtDocCount.setTextColor(Color.parseColor(taskList.daysColorCode))


            val url = taskList.userImage

            Glide.with(tasklistivUser).load(url).placeholder(R.drawable.ic_profile)
                .error(R.drawable.default_thumb).fallback(R.drawable.ic_profile)
                .into(tasklistivUser)
         */
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val  v = LayoutInflater.from(parent.context).inflate(R.layout.item_all_booking_history, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(item[position])

    }

    override fun getItemCount(): Int {
        return item.size
    }

}