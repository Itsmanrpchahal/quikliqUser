package com.quikliq.quikliquser.activities


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.quikliq.quikliquser.R
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.quikliq.quikliquser.fragment.*


class HomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {
   private var navigation: BottomNavigationView? = null
    private lateinit var fm: FragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity__home)
        navigation = findViewById(R.id.navigation)
        navigation!!.setOnNavigationItemSelectedListener(this)
        fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.add(R.id.homeFrag, HomeFragment()).commit()
    }


    companion object {
        var tab_position = 0
    }


    override fun onNavigationItemSelected(@NonNull item: MenuItem): Boolean {
        runOnUiThread {
            when (item.itemId) {
                R.id.orders -> {
                    fm = supportFragmentManager
                    fm.popBackStackImmediate()
                    val drink = fm.beginTransaction()
                    drink.replace(R.id.homeFrag, HomeFragment()).commit()
                }

                R.id.profile -> {
                    fm = supportFragmentManager
                    fm.popBackStackImmediate()
                    val drink = fm.beginTransaction()
                    drink.replace(R.id.homeFrag, ProfileFragment()).commit()
                }
            }
        }
        return true
    }
}
