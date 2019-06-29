package com.quikliq.quikliquser.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.dataProcessing.OrderProcessing
import com.quikliq.quikliquser.models.CartProductModel
import com.quikliq.quikliquser.utilities.Utility

class CartListAdapter(
    private val context: Context,
    private var cartproductList: ArrayList<CartProductModel>
) :
    RecyclerView.Adapter<CartListAdapter.ViewHolder>() {
    private var utility: Utility? = null
    private var counter = 0
    private var totalAmt: Int? = 0
    private var order = java.util.ArrayList<OrderProcessing>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_cart_item, parent, false)
        return ViewHolder(v)
    }

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
//            ProductsActivity.menuData!![productModel.id!!] =
//                OrderProcessing(productModel.id!!, "" + counter, productModel.product_name, productModel.price)
//            order.clear()
//            totalAmt = 0
//
//            for (key in ProductsActivity.menuData!!.keys) {
//                println("value:$key")
//                System.out.println("value:" + ProductsActivity.menuData!![key])
//                order.add(ProductsActivity.menuData!![key]!!)
//
//            }
//            for (i in 0 until order.size) {
//                totalAmt = totalAmt!! + (order[i].ItemCount.toInt() * order[i].Price.toInt())
//                Log.e("amount", "===$totalAmt")
//            }
//
//            ProductsActivity.updateItemPrice!!.show(ProductsActivity.menuData!!.size, totalAmt.toString())
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
//                ProductsActivity.menuData!![productModel.id!!] =
//                    OrderProcessing(productModel.id!!, "" + counter, productModel.product_name, productModel.price)
            } else {
//                holder.tv_quantity.visibility = View.INVISIBLE
//                holder.tv_minus.visibility = View.INVISIBLE
//                holder.tv_plus.visibility = View.INVISIBLE
//                holder.iv_add_to_cart.visibility = View.VISIBLE
//                ProductsActivity.menuData!!.remove(productModel.id!!)
            }
//            order.clear()
//            totalAmt = 0
//
//            for (key in ProductsActivity.menuData!!.keys) {
//                println("value:$key")
//                System.out.println("value:" + ProductsActivity.menuData!![key])
//                order.add(ProductsActivity.menuData!![key]!!)
//
//            }
//            for (i in 0 until order.size) {
//                totalAmt = totalAmt!! + (order[i].ItemCount.toInt() * order[i].Price.toInt())
//                Log.e("amount", "===$totalAmt")
//            }
//
//            ProductsActivity.updateItemPrice!!.show(ProductsActivity.menuData!!.size, totalAmt.toString())
//            if (ProductsActivity.menuData!!.isNotEmpty()) {
//                ProductsActivity.showcart!!.show(true)
//            } else {
//                ProductsActivity.showcart!!.show(false)
//            }

        }

        holder.removeIV.setOnClickListener {

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
        val removeIV: ImageView = itemView.findViewById(R.id.removeIV) as ImageView
        val tv_minus: ImageView = itemView.findViewById(R.id.tv_minus) as ImageView
        val tv_quantity: TextView = itemView.findViewById(R.id.tv_quantity) as TextView
    }


}