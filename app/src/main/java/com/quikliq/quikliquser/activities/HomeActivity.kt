package com.quikliq.quikliquser.activities


import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult
import com.quikliq.quikliquser.R
import com.quikliq.quikliquser.fragment.HomeFragment
import com.quikliq.quikliquser.fragment.ProfileFragment


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

        facebooktoken();
    }

    private fun facebooktoken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(object : OnCompleteListener<InstanceIdResult?> {
                override fun onComplete(task: Task<InstanceIdResult?>) {
                    if (!task.isSuccessful()) {
                        Log.w("tokenfa", "getInstanceId failed", task.getException())
                        return
                    }
                    // Get new Instance ID token
                    val token: String? = task.getResult()?.getToken()
                    Log.d("tokentest",token);
                }
            })
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
