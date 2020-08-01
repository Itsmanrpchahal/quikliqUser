package com.quikliq.quikliquser.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.activities.OrderHistory
import com.quikliq.quikliquser.activities.OrderSummaryActivity
import com.quikliq.quikliquser.constants.Constant
import com.quikliq.quikliquser.models.HistoryModel
import com.quikliq.quikliquser.utilities.Utility


class HistoryAdapter(
    private val context: Context,
    private var historyModelArraylist: ArrayList<HistoryModel>
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    private var builder: AlertDialog.Builder?= null

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
            Constant.BASE_URL + historymodel.image,
            holder.providerIV
        )
        holder.providernameTV.text = historymodel.providername

        when {
            historymodel.order_status!! == "0" -> {
                holder.statusTV.text = "Pending"
                holder.cancelTV.visibility = View.VISIBLE
            }
            historymodel.order_status!! == "1" -> holder.statusTV.text = "Preparing"
            historymodel.order_status!! == "2" -> holder.statusTV.text = "Ready to Delivery"
            historymodel.order_status!! == "3" -> holder.statusTV.text = "Delivered"
            historymodel.order_status!! == "4" -> holder.statusTV.text = "Reject"
            historymodel.order_status!! == "5" -> holder.statusTV.text = "Canceled by User"
        }
        holder.totalpriceTV.text = "$"+historymodel.totalprice
        holder.allitemsTV.text = historymodel.items!!.joinToString(limit = 20, truncated = "....")
        holder.ordertimeTV.text = historymodel.datetime
        if(historymodel.driver_name != "null"){
            holder.driverTV.visibility = View.VISIBLE
            holder.driverTV.setText("Driver Name : "+historymodel.driver_name)
        }else{
            holder.driverTV.visibility = View.GONE

        }
        holder.parentCart.setOnClickListener {
            context.startActivity(Intent(context, OrderSummaryActivity::class.java).putExtra("order",historyModelArraylist[position]))
        }
        holder.cancelTV.setOnClickListener {
            builder = AlertDialog.Builder(context)
            builder!!.setTitle("Order Cancel")
            builder!!.setMessage("Do you want to cancel this Order?")
            builder!!.setCancelable(false)
                .setPositiveButton("Yes") { dialog, _ ->
                    OrderHistory.cancelOrder!!.cancel(historymodel.orderid!!)
                    dialog.cancel()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    //  Action for 'NO' Button
                    dialog.cancel()

                }
            //Creating dialog box
            val alert = builder!!.create()
            alert.show()
        }
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
        val parentCart: CardView = itemView.findViewById(R.id.parentCart)
        val cancelTV : TextView = itemView.findViewById(R.id.cancelTV)
        val driverTV : TextView = itemView.findViewById(R.id.driverTV)
    }



}