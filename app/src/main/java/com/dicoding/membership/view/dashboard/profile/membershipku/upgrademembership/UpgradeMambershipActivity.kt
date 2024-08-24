package com.dicoding.membership.view.dashboard.profile.membershipku.upgrademembership

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityUpgradeMambershipBinding

class UpgradeMambershipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpgradeMambershipBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = ActivityUpgradeMambershipBinding.inflate(layoutInflater)
         setContentView(binding.root)

    }
}