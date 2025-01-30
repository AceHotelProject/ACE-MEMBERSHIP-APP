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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.dicoding.core.data.source.Resource
import com.dicoding.core.utils.constants.UserRole
import com.dicoding.core.utils.constants.mapToUserRole
import com.dicoding.membership.R
import com.dicoding.membership.core.utils.showLongToast
import com.dicoding.membership.core.utils.showToast
import com.dicoding.membership.databinding.ActivityMainBinding
import com.dicoding.membership.view.dashboard.floatingcoupon.reedemcoupon.RedeemCouponCodeActivity
import com.dicoding.membership.view.dashboard.floatingpromo.StaffAddPromoActivity
import com.dicoding.membership.view.dashboard.floatingvalidasi.ValidasiActivity
import com.dicoding.membership.view.popup.token.TokenExpiredDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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

        setupInitialNavigation()

        setupFabMenu()

        loadAnimations()

        setFabClickListener()

        checkNotificationPermission()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupInitialNavigation() {
        mainViewModel.getUser().observe(this) { loginDomain ->
            val userRole = mapToUserRole(loginDomain.user.role)

            //            Testing
            val mockUserRole = UserRole.MEMBER
            //            3C8F61

            // Setup navigation graph
            val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController
            val navGraph = navController.navInflater.inflate(R.navigation.main_navigation)


            // Set start destination berdasarkan role
            navGraph.setStartDestination(
                when (mockUserRole) { // Ganti dengan userRole untuk production
                    UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> R.id.mitraFragment
                    UserRole.MEMBER, UserRole.NONMEMBER -> R.id.homeFragment
                    else -> R.id.homeFragment
                }
            )

            setupBottomNavbar(mockUserRole)
            setupFabVisibility(mockUserRole)

//            Use This For Real
//            setupBottomNavbar(userRole)
//
//            // Setup FAB visibility berdasarkan role
//            setupFabVisibility(userRole)

            // Set graph ke nav controller
            navController.graph = navGraph

            Log.d("MainActivity", "Navigation setup complete for role: ${mockUserRole.display}")
        }
    }

    private fun setupFabVisibility(userRole: UserRole) {
        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                binding.lnFab1.visibility = View.VISIBLE
                binding.promoBanner.visibility = View.GONE
                binding.bottomNavbar.visibility = View.VISIBLE
            }
            UserRole.MEMBER, UserRole.NONMEMBER -> {
                binding.lnFab1.visibility = View.GONE
                binding.bottomNavbar.visibility = View.VISIBLE

                // Observe inactive promos
                lifecycleScope.launch {
                    mainViewModel.getProposalPromos().observe(this@MainActivity) { result ->
                        when (result) {
                            is Resource.Success -> {
                                result.data?.results?.let { promos ->
                                    val inactivePromos = promos.filter { it.status == "active" }
                                    val inactiveCount = inactivePromos.size

                                    binding.promoBanner.apply {
                                        if (inactiveCount > 0) {
                                            visibility = View.VISIBLE
                                            binding.tvTotalPromo.apply {
                                                text = String.format("%d Kupon", inactiveCount)
                                                visibility = if (inactiveCount > 0) View.VISIBLE else View.GONE
                                            }
                                            animate()
                                                .alpha(1f)
                                                .setDuration(100)
                                                .start()
                                        } else {
                                            animate()
                                                .alpha(0f)
                                                .setDuration(100)
                                                .withEndAction {
                                                    visibility = View.GONE
                                                }
                                                .start()
                                        }
                                    }
                                }
                            }
                            is Resource.Error -> {
                                binding.promoBanner.visibility = View.GONE
                                Log.e("MainActivity", "Error getting promos: ${result.message}")
                            }
                            else -> {
                                // Handle other states if needed
                            }
                        }
                    }
                }
            }
            else -> {
                binding.lnFab1.visibility = View.GONE
                binding.fbValidMembership.visibility = View.GONE
                binding.bottomNavbar.visibility = View.GONE
            }
        }
    }

    private fun validateToken() {
        // Validasi token
        mainViewModel.getRefreshToken().observe(this) { token ->
            if (token.isEmpty() || token == "") {
                TokenExpiredDialog().show(supportFragmentManager, "Token Expired Dialog")
                return@observe
            }
        }
    }

    private fun setupBottomNavbar(userRole: UserRole) {
        val navView: BottomNavigationView = binding.bottomNavbar
        val navViewController = findNavController(R.id.nav_host_fragment)

        Log.d("BottomNav", "Setting up bottom nav for role: ${userRole.display}")

        binding.bottomNavbar.menu.clear()

        when (userRole) {
            UserRole.ADMIN, UserRole.MITRA, UserRole.RECEPTIONIST -> {
                navView.visibility = View.VISIBLE  // Pastikan visibility diset
                navView.inflateMenu(R.menu.staff_bottom_nav_menu)
                setupStaffNavigation(navView, navViewController)
                Log.d("BottomNav", "Inflated staff menu")
            }
            UserRole.MEMBER, UserRole.NONMEMBER -> {
                navView.visibility = View.VISIBLE  // Pastikan visibility diset
                navView.inflateMenu(R.menu.customer_bottom_nav_menu)
                setupCustomerNavigation(navView, navViewController)
                Log.d("BottomNav", "Inflated customer menu")
            }
            else -> {
                navView.visibility = View.VISIBLE  // Pastikan visibility diset
                navView.inflateMenu(R.menu.customer_bottom_nav_menu)
                setupCustomerNavigation(navView, navViewController)
                Log.d("BottomNav", "Inflated default menu")
            }
        }
    }

    private fun setupNavigationListener(navView: BottomNavigationView, navController: NavController) {
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
//                Dicoding Test
//                R.id.homeFragment -> {
//                    checkAndNavigateToFeature("favorite", R.id.homeFragment, navController)
//                }
                R.id.homeFragment -> {
                    navController.navigate(R.id.homeFragment)
                }
                R.id.mitraFragment -> {
                    navController.navigate(R.id.mitraFragment)
                }
                R.id.promoFragment -> {
                    navController.navigate(R.id.promoFragment)
                }
                R.id.memberFragment -> {
                    navController.navigate(R.id.memberFragment)
                }
                R.id.historyFragment -> {
                    navController.navigate(R.id.historyFragment)
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment)
                }
            }
            true
        }
    }

    private fun setupStaffNavigation(navView: BottomNavigationView, navController: NavController) {
        val appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.mitraFragment,
                R.id.promoFragment,
                R.id.memberFragment,
                R.id.historyFragment,
                R.id.profileFragment,
            )
        ).build()

        navView.setupWithNavController(navController)
        setupNavigationListener(navView, navController)
    }

    private fun setupCustomerNavigation(navView: BottomNavigationView, navController: NavController) {
        val appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.homeFragment,
                R.id.promoFragment,
                R.id.historyFragment,
                R.id.profileFragment,
            )
        ).build()

        navView.setupWithNavController(navController)
        setupNavigationListener(navView, navController)
    }

    private fun setupFabMenu() {
        binding.fbMenu.setOnClickListener {
            toggleFabMenu()
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navbar)

        // Menunggu hingga tampilan diukur
        bottomNavigationView.viewTreeObserver.addOnGlobalLayoutListener {
            val height = bottomNavigationView.measuredHeight
            Log.d("BottomNavHeight", "Tinggi BottomNavigationView: $height dp")
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

//    Dicoding Tester Feature Module
//    private fun checkAndNavigateToFeature(moduleName: String, destinationId: Int, navController: NavController) {
//        if (splitInstallManager.installedModules.contains(moduleName)) {
//            navController.navigate(destinationId)
//        } else {
//            val request = SplitInstallRequest.newBuilder()
//                .addModule(moduleName)
//                .build()
//
//            splitInstallManager.startInstall(request)
//                .addOnSuccessListener {
//                    navController.navigate(destinationId)
//                }
//                .addOnFailureListener {
//                    showLongToast("Error installing module: $moduleName")
//                }
//        }
//    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setFabClickListener() {
        val fabTextViewPairs = listOf(
            Pair(binding.fbCoupon, binding.tvCoupon),
            Pair(binding.fbValidMembership, binding.tvValidMembership),
            Pair(binding.fbAddPromo, binding.tvAddPromo),
            Pair(binding.fbMenu, null)
        )

        fabTextViewPairs.forEach { (fab, textView) ->
            fab.setOnTouchListener { view, motionEvent ->
                when (motionEvent.action) {
                    MotionEvent.ACTION_DOWN -> {
                        view.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).start()
                        textView?.animate()?.scaleX(0.8f)?.scaleY(0.8f)?.setDuration(100)?.start()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                        textView?.animate()?.scaleX(1.0f)?.scaleY(1.0f)?.setDuration(100)?.start()
                    }
                }
                false
            }

            fab.setOnClickListener {
                when (fab) {
                    binding.fbCoupon -> {
                        showToast("FAB Coupon clicked!")
                        val intent = Intent(this, RedeemCouponCodeActivity::class.java)
                        startActivity(intent)
                    }
                    binding.fbValidMembership -> {
                        showToast("FAB Valid Membership clicked!")
                        val intent = Intent(this, ValidasiActivity::class.java)
                        startActivity(intent)
                    }
                    binding.fbAddPromo -> {
                        showToast("FAB Add Promo clicked!")
                        val intent = Intent(this, StaffAddPromoActivity::class.java).apply {
                            putExtra(StaffAddPromoActivity.EXTRA_IS_EDIT, false)
                        }
                        startActivity(intent)
                    }
                    binding.fbMenu -> {
                        toggleFabMenu()
                    }
                }
            }
        }
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