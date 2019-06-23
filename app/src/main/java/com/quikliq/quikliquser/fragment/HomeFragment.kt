package com.quikliq.quikliquser.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.adapters.IncomingOrderAdapter


class HomeFragment : Fragment() {
    private var layout_manager: RecyclerView.LayoutManager? = null
    private var rvHome: RecyclerView? = null
    private var toolbar_title : TextView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        layout_manager = LinearLayoutManager(activity,  RecyclerView.VERTICAL, false)
        rvHome = view.findViewById(R.id.rvHome)
        rvHome!!.layoutManager = layout_manager
        rvHome!!.itemAnimator = DefaultItemAnimator()
        rvHome!!.adapter = IncomingOrderAdapter(activity!!)
        toolbar_title = view.findViewById(R.id.toolbar_title)
        toolbar_title!!.text = "Orders"

        return view
    }


    companion object {
        var TAG = HomeFragment::class.java.simpleName
        @JvmStatic
        fun newInstance(): HomeFragment {
            val fragment = HomeFragment()
            return fragment
        }
    }

}
