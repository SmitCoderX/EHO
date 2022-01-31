package com.driver.eho.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.driver.eho.databinding.LayoutAccountListBinding
import com.driver.eho.model.AccountList

class BankDetailsListAdapter(private val listener: OnAccountDetailsClick) :
    RecyclerView.Adapter<BankDetailsListAdapter.AccountListViewHolder>() {

    inner class AccountListViewHolder(val binding: LayoutAccountListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: AccountList) {
            binding.apply {
                tvAccountNumber.text = data.accountNumber
                tvIFSC.text = data.ifscCode
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountListViewHolder {
        val binding =
            LayoutAccountListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountListViewHolder, position: Int) {
        val currentItem = differ.currentList[position]

        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<AccountList>() {
        override fun areItemsTheSame(oldItem: AccountList, newItem: AccountList): Boolean {
            return true
        }

        override fun areContentsTheSame(oldItem: AccountList, newItem: AccountList): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    interface OnAccountDetailsClick {
        fun onDeleteClick(data: AccountList)
    }

}