package com.driver.eho.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.driver.eho.R
import com.driver.eho.model.WithdrawalListModel

class WithdrawalIListAdapter(var item: Array<WithdrawalListModel>):
    RecyclerView.Adapter<WithdrawalIListAdapter.MyViewHolder>() {

/*
    var item = ArrayList<BookingHistoryListModel>()
*/

    /*fun setTaskListData(bookingHistoryList: ArrayList<BookingHistoryListModel>){
        this.item = bookingHistoryList
    }*/


    class MyViewHolder(view: View): RecyclerView.ViewHolder(view){

        val WithdrawalId = view.findViewById<TextView>(R.id.tvWithdrawalId)
        val WithdrawalStatus = view.findViewById<TextView>(R.id.tvWithdrawalStatus)
        val WithdrawalDate = view.findViewById<TextView>(R.id.tvWithdrawalDate)
        val WithdrawalTime = view.findViewById<TextView>(R.id.tvWithdrawalTime)
        val WithdrawalAmmount = view.findViewById<TextView>(R.id.tvWithdrawalAmmount)
        @SuppressLint("SetTextI18n")
        fun bind(WithdrawalList: WithdrawalListModel){
            WithdrawalId.text = WithdrawalList.WithdrawalId
            WithdrawalStatus.text = WithdrawalList.WithdrawalStatus
            WithdrawalDate.text = WithdrawalList.WithdrawalDate
            WithdrawalTime.text = WithdrawalList.WithdrawalTime
            WithdrawalAmmount.text = WithdrawalList.WithdrawalAmmount

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val  v = LayoutInflater.from(parent.context).inflate(R.layout.item_all_eho_money_liat, parent, false)
        return MyViewHolder(v)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(item[position])

    }

    override fun getItemCount(): Int {
        return item.size
    }

}