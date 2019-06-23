package com.quikliq.quikliquser.adapters

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quikliq.quikliquser.R


class IncomingOrderAdapter(
    private val context: Context
) :
    RecyclerView.Adapter<IncomingOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.incoming_order_adapter, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }


    override fun getItemCount(): Int {
        return 20
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val nameTV: TextView = itemView.findViewById(R.id.nameTV) as TextView
//        val likesTV: TextView = itemView.findViewById(R.id.likesTV) as TextView
//        val comentsTV: TextView = itemView.findViewById(R.id.comentsTV) as TextView
    }


}
