package com.quikliq.quikliquser.adapters


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.activities.ProductsActivity
import com.quikliq.quikliquser.activities.ProductsActivity.Companion.menuData
import com.quikliq.quikliquser.dataProcessing.OrderProcessing
import com.quikliq.quikliquser.interfaces.ShowCart
import com.quikliq.quikliquser.models.ProductsModel
import com.quikliq.quikliquser.utilities.Utility


class ProductsAdapter(
    private val context: Context,
    private var productList: ArrayList<ProductsModel>
) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {
    private var utility: Utility? = null
    private var counter = 0
    private var totalAmt: Int? = 0
    private var order = java.util.ArrayList<OrderProcessing>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_sample, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        utility = Utility()
        val productModel = productList[position]
        holder.tv_product_name.text = productModel.product_name
        holder.item_price.text = "$" + productModel.price
        utility!!.loadImageWithLoader(
            context,
            "http://freechatup.website/weed/api/" + productModel.image,
            holder.productImage
        )

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
            menuData!![productModel.id!!] =
                OrderProcessing(productModel.id!!, "" + counter, productModel.product_name, productModel.price)
            order.clear()
            totalAmt = 0

            for (key in menuData!!.keys) {
                println("value:$key")
                System.out.println("value:" + menuData!![key])
                order.add(menuData!![key]!!)

            }
            for (i in 0 until  order.size) {
                totalAmt = totalAmt!! + (order[i].ItemCount.toInt() * order[i].Price.toInt())
                Log.e("amount", "===$totalAmt")
            }

            ProductsActivity.updateItemPrice!!.show(menuData!!.size,totalAmt.toString())
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
                menuData!![productModel.id!!] = OrderProcessing(productModel.id!!, "" + counter, productModel.product_name, productModel.price)
            } else {
                holder.tv_quantity.visibility = View.INVISIBLE
                holder.tv_minus.visibility = View.INVISIBLE
                holder.tv_plus.visibility = View.INVISIBLE
                holder.iv_add_to_cart.visibility = View.VISIBLE
                menuData!!.remove(productModel.id!!)
            }
            order.clear()
            totalAmt = 0

            for (key in menuData!!.keys) {
                println("value:$key")
                System.out.println("value:" + menuData!![key])
                order.add(menuData!![key]!!)

            }
            for (i in 0 until  order.size) {
                totalAmt = totalAmt!! + (order[i].ItemCount.toInt() * order[i].Price.toInt())
                Log.e("amount", "===$totalAmt")
            }

            ProductsActivity.updateItemPrice!!.show(menuData!!.size,totalAmt.toString())
            if(menuData!!.isNotEmpty()){
                ProductsActivity.showcart!!.show(true)
            }else{
                ProductsActivity.showcart!!.show(false)
            }

        }

        holder.iv_add_to_cart.setOnClickListener {
            menuData!![productModel.id!!] = OrderProcessing(productModel.id!!, "1", productModel.product_name, productModel.price)
            holder.iv_add_to_cart.visibility = View.INVISIBLE
            holder.tv_plus.visibility = View.VISIBLE
            holder.tv_minus.visibility = View.VISIBLE
            holder.tv_quantity.visibility = View.VISIBLE
            ProductsActivity.showcart!!.show(true)
            totalAmt = 0
            order.clear()
            for (key in menuData!!.keys) {
                println("value:$key")
                System.out.println("value:" + menuData!![key])
                order.add(menuData!![key]!!)

            }
            for (i in 0 until  order.size) {
                totalAmt = totalAmt!! + (order[i].ItemCount.toInt() * order[i].Price.toInt())
                Log.e("amount", "===$totalAmt")
            }

            ProductsActivity.updateItemPrice!!.show(menuData!!.size,totalAmt.toString())
        }
    }


    override fun getItemCount(): Int {
        return productList.size
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tv_product_name: TextView = itemView.findViewById(R.id.tv_product_name) as TextView
        val item_price: TextView = itemView.findViewById(R.id.item_price) as TextView
        val tv_plus: ImageView = itemView.findViewById(R.id.tv_plus) as ImageView
        val tv_minus: ImageView = itemView.findViewById(R.id.tv_minus) as ImageView
        val tv_quantity: TextView = itemView.findViewById(R.id.tv_quantity) as TextView
        val productImage: ImageView = itemView.findViewById(R.id.productImage) as ImageView
        val iv_add_to_cart: TextView = itemView.findViewById(R.id.iv_add_to_cart) as TextView
    }


}
