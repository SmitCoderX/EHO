package com.driver.eho.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.driver.eho.databinding.ItemAllEhoMoneyLiatBinding
import com.driver.eho.model.Withdraw.WithdrawData

class WithdrawlHistoryListAdapter :
    RecyclerView.Adapter<WithdrawlHistoryListAdapter.WithdrawlHistoryViewHolder>() {

    inner class WithdrawlHistoryViewHolder(val binding: ItemAllEhoMoneyLiatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: WithdrawData) {

            binding.apply {
                tvWithdrawalId.text = data.transactionId
                tvWithdrawalTime.text = data.time
                tvWithdrawalDate.text = data.date
                tvWithdrawalAmmount.text = data.amount.toString()

                if (data.status.equals("1")) {
                    tvWithdrawalStatus.text = "Success"
                    tvWithdrawalStatus.setTextColor(Color.GREEN)
                } else {
                    tvWithdrawalStatus.text = "Failed"
                    tvWithdrawalStatus.setTextColor(Color.RED)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WithdrawlHistoryViewHolder {
        val binding =
            ItemAllEhoMoneyLiatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WithdrawlHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WithdrawlHistoryViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<WithdrawData>() {
        override fun areItemsTheSame(oldItem: WithdrawData, newItem: WithdrawData): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: WithdrawData, newItem: WithdrawData): Boolean {
            return true
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
/*

    interface OnWithdrawlClick {
        fun onClick(data: WithdrawData)
    }
*/

}