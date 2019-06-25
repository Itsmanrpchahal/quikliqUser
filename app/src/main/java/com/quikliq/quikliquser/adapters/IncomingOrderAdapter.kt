package com.quikliq.quikliquser.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.activities.ProductsActivity


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
//        if(position==20-1){
//            holder.view.visibility = View.INVISIBLE
//        }
        holder.parentRL.setOnClickListener {
            context.startActivity(Intent(context,ProductsActivity::class.java))
        }
    }


    override fun getItemCount(): Int {
        return 20
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parentRL: RelativeLayout = itemView.findViewById(R.id.parentRL) as RelativeLayout
//        val view: View = itemView.findViewById(R.id.view) as View
//        val comentsTV: TextView = itemView.findViewById(R.id.comentsTV) as TextView
    }


}
