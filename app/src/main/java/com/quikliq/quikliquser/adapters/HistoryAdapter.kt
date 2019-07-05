package com.quikliq.quikliquser.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.models.HistoryModel
import com.quikliq.quikliquser.utilities.Utility


class HistoryAdapter(
    private val context: Context,
    private var historyModelArraylist: ArrayList<HistoryModel>
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private var utility: Utility? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_order_item, parent, false)
        return ViewHolder(v)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        utility = Utility()
        val historymodel = historyModelArraylist[position]
        utility!!.loadImageWithLoader(
            context,
            "https://professionaler.com/weed/api/" + historymodel.image,
            holder.providerIV
        )
        holder.providernameTV.text = historymodel.providername
        if(historymodel.order_status!! == "0"){
            holder.statusTV.text = "Pending"
        }
        holder.totalpriceTV.text = "$"+historymodel.totalprice
        holder.allitemsTV.text = historymodel.items!!.joinToString(limit = 5, truncated = "...!")
    }


    override fun getItemCount(): Int {
        return historyModelArraylist.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val allitemsTV: TextView = itemView.findViewById(R.id.allitemsTV) as TextView
        val totalpriceTV: TextView = itemView.findViewById(R.id.totalpriceTV) as TextView
        val ordertimeTV: TextView = itemView.findViewById(R.id.ordertimeTV) as TextView
        val providerIV: ImageView = itemView.findViewById(R.id.providerIV) as ImageView
        val providernameTV: TextView = itemView.findViewById(R.id.providernameTV) as TextView
        val statusTV: TextView = itemView.findViewById(R.id.statusTV) as TextView
    }

}