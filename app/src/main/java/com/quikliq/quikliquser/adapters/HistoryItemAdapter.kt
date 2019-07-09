package com.quikliq.quikliquser.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quikliq.quikliquser.R


class HistoryItemAdapter(
    private var cartproductList: ArrayList<String>?,
    private var quantity: ArrayList<String>?,
    private var price: ArrayList<String>?
) :
    RecyclerView.Adapter<HistoryItemAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_history_item, parent, false)
        return ViewHolder(v)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
     //   Log.d("quantity",quantity!!.toString())
        holder.item.text = quantity!![position]+" × "+cartproductList!![position]
        holder.item_price.text = "$" + price!![position]
        holder.totalitem_price.text = "$" + (quantity!![position].toInt()*price!![position].toInt())
    }


    override fun getItemCount(): Int {
        return cartproductList!!.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val item: TextView = itemView.findViewById(R.id.item) as TextView
        val item_price: TextView = itemView.findViewById(R.id.item_price) as TextView
        val totalitem_price: TextView = itemView.findViewById(R.id.totalitem_price) as TextView
    }

}