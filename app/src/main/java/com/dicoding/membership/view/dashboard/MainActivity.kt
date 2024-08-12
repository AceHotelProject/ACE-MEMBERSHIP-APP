package com.dicoding.membership.view.dashboard

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showLongToast
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityMainBinding
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var splitInstallManager: SplitInstallManager

//    private var fabMenuState: FabMenuState = FabMenuState.COLLAPSED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splitInstallManager = SplitInstallManagerFactory.create(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateLoginStatus()

        setupBottomNavbar()

        checkNotificationPermission()
    }

    private fun validateLoginStatus() {
        mainViewModel.getLoginStatus().observe(this) { isLoggedIn ->
            if (!isLoggedIn) {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun setupBottomNavbar() {
        val navView: BottomNavigationView = binding.bottomNavbar
        val navViewController = findNavController(R.id.nav_host_fragment)

        val appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.homeFragment,
                R.id.promoFragment,
                R.id.historyFragment,
                R.id.profileFragment,
            )
        ).build()

//        setupActionBarWithNavController(navViewController, appBarConfiguration)
        navView.setupWithNavController(navViewController)

        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    checkAndNavigateToFeature("favorite", R.id.homeFragment, navViewController)
                }
                R.id.promoFragment -> {
                    navViewController.navigate(R.id.promoFragment)
                }
                R.id.historyFragment -> {
                    navViewController.navigate(R.id.historyFragment)
                }
                R.id.profileFragment -> {
                    navViewController.navigate(R.id.profileFragment)
                }
            }
            true
        }
    }

    private fun checkAndNavigateToFeature(moduleName: String, destinationId: Int, navController: NavController) {
        if (splitInstallManager.installedModules.contains(moduleName)) {
            navController.navigate(destinationId)
        } else {
            val request = SplitInstallRequest.newBuilder()
                .addModule(moduleName)
                .build()

            splitInstallManager.startInstall(request)
                .addOnSuccessListener {
                    navController.navigate(destinationId)
                }
                .addOnFailureListener { exception ->
                    showLongToast("Error installing module: $moduleName")
                }
        }
    }

    private fun setupActionBar() {
        supportActionBar?.hide()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showToast("Izin notifikasi diberikan")
            } else {
                showLongToast("Izin tidak diberikan, ini akan mempengaruhi jalannya aplikasi")
            }
        }
}