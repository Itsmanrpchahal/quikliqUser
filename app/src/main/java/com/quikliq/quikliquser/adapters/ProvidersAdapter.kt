package com.quikliq.quikliquser.adapters

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.activities.ProductsActivity
import com.quikliq.quikliquser.models.ProviderModel
import com.quikliq.quikliquser.utilities.Utility


class ProvidersAdapter(
    private val context: Activity,
    private var providersList: ArrayList<ProviderModel>
) :
    RecyclerView.Adapter<ProvidersAdapter.ViewHolder>() {
private var utility: Utility? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_provider, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        utility = Utility()
        val providerModel = providersList[position]
        holder.shop_name_TV.text = providerModel.name
        utility!!.loadImageWithLoader(context,providerModel.picture,holder.food_IV)
        holder.distance_TV.text = providerModel.distance
        if(providerModel.Status == 1){
            holder.status_tv.setTextColor(ContextCompat.getColor(context,R.color.green_open))
            holder.status_tv.text = "OPEN"
        }else{
            holder.status_tv.setTextColor(ContextCompat.getColor(context,R.color.red_close))
            holder.status_tv.text = "CLOSED"
        }

        holder.time_TV.text = providerModel.opentime+" - "+providerModel.closetime

        holder.ratingTV.text = ""+providerModel.rating+".0"
        holder.parentRL.setOnClickListener {
            if (providerModel.Status == 1) {
                context.startActivity(
                    Intent(context, ProductsActivity::class.java).putExtra(
                        "provider",
                        providersList[position]
                    )
                )
            }else{
                val snackbar = Snackbar
                    .make(context.findViewById(android.R.id.content), "Currently Provider is Closed", Snackbar.LENGTH_SHORT).setAction(""
                    ) { }
                snackbar.show()

            }
        }
    }

    override fun getItemCount(): Int {
        return providersList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val parentRL: RelativeLayout = itemView.findViewById(R.id.parentRL) as RelativeLayout
        val shop_name_TV: TextView = itemView.findViewById(R.id.shop_name_TV) as TextView
        val status_tv: TextView = itemView.findViewById(R.id.status_tv) as TextView
        val distance_TV: TextView = itemView.findViewById(R.id.distance_TV) as TextView
        val time_TV: TextView = itemView.findViewById(R.id.time_TV) as TextView
        val ratingTV: TextView = itemView.findViewById(R.id.ratingTV) as TextView
        val food_IV : ImageView = itemView.findViewById(R.id.food_IV) as ImageView
    }


}
