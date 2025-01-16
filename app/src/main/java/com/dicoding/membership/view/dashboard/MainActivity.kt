package com.dicoding.membership.view.dashboard

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showLongToast
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityMainBinding
import com.dicoding.membership.view.dashboard.admin.addpromo.AdminAddPromoActivity
import com.dicoding.membership.view.dashboard.admin.addpromo.reedempromo.RedeemPromoCodeActivity
import com.dicoding.membership.view.dashboard.floatingvalidasi.ValidasiActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class  MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()

    private lateinit var splitInstallManager: SplitInstallManager

    private var isFabMenuOpen = false

    private lateinit var fabRotateOpenAnim: Animation
    private lateinit var fabRotateCloseAnim: Animation
    private lateinit var fabFromAnim: Animation
    private lateinit var fabCloseAnim: Animation


//    private var fabMenuState: FabMenuState = FabMenuState.COLLAPSED

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splitInstallManager = SplitInstallManagerFactory.create(this)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        validateToken()

        checkUserRole()

        setupBottomNavbar()

        setupFabMenu()

        loadAnimations()

        setFabClickListener()

        checkNotificationPermission()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkUserRole() {
        mainViewModel.getUser().observe(this) { loginDomain ->
//            user.user?.role?.let { handleFab(it) }
            Log.d("MainActivity", "User Data: $loginDomain")
        }
    }

    private fun validateToken() {
        mainViewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
            }
        }
    }

    private fun setupBottomNavbar() {
        val navView: BottomNavigationView = binding.bottomNavbar
        val navViewController = findNavController(R.id.nav_host_fragment)

//        Bottom Navbar LIMIT ONLY 5 MENU
        val appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.homeFragment,
                R.id.mitraFragment,
                R.id.promoFragment,
//                R.id.memberFragment,
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
                R.id.mitraFragment -> {
                    navViewController.navigate(R.id.mitraFragment)
                }
                R.id.promoFragment -> {
                    navViewController.navigate(R.id.promoFragment)
                }
//                R.id.memberFragment -> {
//                    navViewController.navigate(R.id.memberFragment)
//                }
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

    private fun setupFabMenu() {
        binding.fbMenu.setOnClickListener {
            toggleFabMenu()
        }
    }

    private fun loadAnimations() {
        fabRotateOpenAnim = AnimationUtils.loadAnimation(this, R.anim.floating_rotate_open_anim)
        fabRotateCloseAnim = AnimationUtils.loadAnimation(this, R.anim.floating_rotate_close_anim)
        fabFromAnim = AnimationUtils.loadAnimation(this, R.anim.floating_from_bottom_anim)
        fabCloseAnim = AnimationUtils.loadAnimation(this, R.anim.floating_to_bottom_anim)
    }

    private fun toggleFabMenu() {
        if (isFabMenuOpen) {
            binding.fbCoupon.startAnimation(fabCloseAnim)
            binding.fbValidMembership.startAnimation(fabCloseAnim)
            binding.fbAddPromo.startAnimation(fabCloseAnim)

            binding.tvCoupon.startAnimation(fabCloseAnim)
            binding.tvValidMembership.startAnimation(fabCloseAnim)
            binding.tvAddPromo.startAnimation(fabCloseAnim)

            binding.fbMenu.startAnimation(fabRotateCloseAnim)

            binding.lnFab2.visibility = View.INVISIBLE
            binding.lnFab3.visibility = View.INVISIBLE
            binding.lnFab4.visibility = View.INVISIBLE
        } else {
            binding.fbCoupon.startAnimation(fabFromAnim)
            binding.fbValidMembership.startAnimation(fabFromAnim)
            binding.fbAddPromo.startAnimation(fabFromAnim)

            binding.tvCoupon.startAnimation(fabFromAnim)
            binding.tvValidMembership.startAnimation(fabFromAnim)
            binding.tvAddPromo.startAnimation(fabFromAnim)

            binding.fbMenu.startAnimation(fabRotateOpenAnim)

            binding.lnFab2.visibility = View.VISIBLE
            binding.lnFab3.visibility = View.VISIBLE
            binding.lnFab4.visibility = View.VISIBLE
        }

        isFabMenuOpen = !isFabMenuOpen
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

    @SuppressLint("ClickableViewAccessibility")
    private fun setFabClickListener() {
        // Pair FABs with their corresponding TextViews
        val fabTextViewPairs = listOf(
            Pair(binding.fbCoupon, binding.tvCoupon),
            Pair(binding.fbValidMembership, binding.tvValidMembership),
            Pair(binding.fbAddPromo, binding.tvAddPromo),
            Pair(binding.fbMenu, null) // No associated TextView for menu FAB
        )

        fabTextViewPairs.forEach { (fab, textView) ->
            fab.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        // Shrink FAB and TextView
                        view.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).start()
                        textView?.animate()?.scaleX(0.8f)?.scaleY(0.8f)?.setDuration(100)?.start()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        // Restore FAB and TextView to original size
                        view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                        textView?.animate()?.scaleX(1.0f)?.scaleY(1.0f)?.setDuration(100)?.start()
                    }
                }
                false
            }

            // Optional: Add onClickListener for FAB
            fab.setOnClickListener {
                when (fab) {
                    binding.fbCoupon -> {
                        showToast("FAB Coupon clicked!")
                        val intent = Intent(this, RedeemPromoCodeActivity::class.java)
                        startActivity(intent)
                    }
                    binding.fbValidMembership -> {
                        showToast("FAB Valid Membership clicked!")
                        val intent = Intent(this, ValidasiActivity::class.java)
                        startActivity(intent)
                    }
                    binding.fbAddPromo -> {
                        showToast("FAB Add Promo clicked!")
                        val intent = Intent(this, AdminAddPromoActivity::class.java)
                        startActivity(intent)
                    }
                    binding.fbMenu -> {
                        toggleFabMenu() // Keep the existing menu FAB functionality
                    }
                }
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