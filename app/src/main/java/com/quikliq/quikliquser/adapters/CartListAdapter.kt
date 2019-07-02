package com.quikliq.quikliquser.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.activities.CartActivity
import com.quikliq.quikliquser.dataProcessing.OrderProcessing
import com.quikliq.quikliquser.models.CartProductModel
import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.utilities.Utility
import org.json.JSONException
import org.json.JSONObject
import retrofit.RequestsCall
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartListAdapter(
    private val context: Context,
    private var cartproductList: ArrayList<CartProductModel>
) :
    RecyclerView.Adapter<CartListAdapter.ViewHolder>() {
    private var utility: Utility? = null
    private var counter = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_cart_item, parent, false)
        return ViewHolder(v)
    }

    fun updateData(newcartList: ArrayList<CartProductModel>) {
        this.cartproductList = newcartList
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        utility = Utility()
        val productModel = cartproductList[position]
        holder.tv_product_name.text = productModel.productname
        holder.item_price.text = "$" + productModel.productprice
        holder.totalitem_price.text = "$" + productModel.totalprice
        holder.tv_quantity.text = productModel.quantity

        holder.tv_plus.setOnClickListener {
            counter = java.lang.Double.parseDouble(holder.tv_quantity.text.toString()).toInt()
            counter++
            holder.tv_quantity.text = "" + counter
            val scale = ScaleAnimation(
                0f,
                1f,
                0f,
                1f,
                ScaleAnimation.RELATIVE_TO_SELF,
                .5f,
                ScaleAnimation.RELATIVE_TO_SELF,
                .5f
            )
            scale.duration = 300
            scale.interpolator = OvershootInterpolator()
            holder.tv_quantity.startAnimation(scale)
            CartActivity.updateQuantityInterface!!.update(productModel.id!!,  productModel.productprice!!, "" + counter)
        }

        holder.tv_minus.setOnClickListener {
            counter = java.lang.Double.parseDouble(holder.tv_quantity.text.toString()).toInt()
            if (counter > 1) {
                counter--
                holder.tv_quantity.text = "" + counter
                val scale = ScaleAnimation(
                    0f,
                    1f,
                    0f,
                    1f,
                    ScaleAnimation.RELATIVE_TO_SELF,
                    .5f,
                    ScaleAnimation.RELATIVE_TO_SELF,
                    .5f
                )
                scale.duration = 300
                scale.interpolator = OvershootInterpolator()
                holder.tv_quantity.startAnimation(scale)
                CartActivity.updateQuantityInterface!!.update(productModel.id!!,  productModel.productprice!!, "" + counter)
            } else {
                CartActivity.removeItemInterface!!.remove(productModel.id!!)
            }
        }
    }


    override fun getItemCount(): Int {
        return cartproductList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_product_name: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
        val item_price: TextView = itemView.findViewById(R.id.item_price) as TextView
        val totalitem_price: TextView = itemView.findViewById(R.id.totalitem_price) as TextView
        val tv_plus: ImageView = itemView.findViewById(R.id.tv_plus) as ImageView
        val tv_minus: ImageView = itemView.findViewById(R.id.tv_minus) as ImageView
        val tv_quantity: TextView = itemView.findViewById(R.id.tv_quantity) as TextView
    }

}