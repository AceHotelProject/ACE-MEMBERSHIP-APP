package com.dicoding.membership.view.dashboard.member.listeditmember

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.databinding.ActivityListEditMemberBinding
import com.dicoding.membership.databinding.ActivityMainBinding
import com.dicoding.membership.view.dashboard.MainActivity
import com.dicoding.membership.view.dashboard.member.listeditmember.editmember.EditMemberActivity
import com.google.android.play.core.splitinstall.SplitInstallManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListEditMemberActivity : AppCompatActivity() {

    private lateinit var binding: ActivityListEditMemberBinding
    private val viewModel: ListEditMemberViewModel by viewModels()
    private lateinit var membershipAdapter: MembershipAdapter

    private lateinit var splitInstallManager: SplitInstallManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListEditMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupUI()

        viewModel.getMemberships()
    }

    private fun setupRecyclerView() {
        membershipAdapter = MembershipAdapter()
        binding.membershipRv.apply {
            layoutManager = LinearLayoutManager(this@ListEditMemberActivity)
            adapter = membershipAdapter
        }

        membershipAdapter.setOnItemClickCallback { membership ->
            // Handle click if needed
        }
    }

    private fun setupObservers() {
        viewModel.memberships.observe(this) { result ->
            //Log.d("Debug", "Observer triggered with result: $result")  // Add this
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { response ->
                        //Log.d("Debug", "Response received: $response")  // Add this
                        if (response.totalResults == 0) {
                            //Log.d("Debug", "Empty Results confirmed")
                            showEmptyState(true)
                        } else {
                            //Log.d("Debug", "Non-empty Results confirmed: ${response.results.size} items")  // Modified
                            showEmptyState(false)
                            membershipAdapter.submitList(response.results)
                        }
                    } ?: run {
                        //Log.d("Debug", "Response data is null")  // Add this
                        showEmptyState(true)
                    }
                }
                is Resource.Loading -> {
                    //Log.d("Debug", "Loading state")  // Add this
                    showLoading(true)
                }
                is Resource.Error -> {
                    Log.d("Debug", "Error state: ${result.message}")  // Add this
                    showLoading(false)
                    showEmptyState(true)  // Show empty state on error
                    showError(result.message)
                }
                else -> {
                    Log.d("Debug", "Unknown state")  // Add this
                    showLoading(false)
                    showEmptyState(true)  // Show empty state on unknown condition
                    showError("Terjadi kesalahan")
                }
            }
        }
    }

    private fun showEmptyState(boolean: Boolean) {
        binding.apply {
            membershipRv.visibility = if(boolean) View.GONE else View.VISIBLE
            layoutEmptyList.visibility = if(boolean) View.VISIBLE else View.GONE
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.btnClose.visibility = if(isLoading) View.GONE else View.VISIBLE
        binding.detailTitle.visibility = if(isLoading) View.GONE else View.VISIBLE
        binding.constraintLayout2.visibility = if(isLoading) View.GONE else View.VISIBLE
        binding.loadingOverlay.visibility = if(isLoading) View.VISIBLE else View.GONE

    }

    private fun setupUI() {
        binding.btnAdd.setOnClickListener {
            //add Membership
            val intent = Intent(this@ListEditMemberActivity, EditMemberActivity::class.java).apply {
                putExtra("SCREEN_TITLE", "Tambah Membership")
                putExtra("BUTTON_TEXT", "Tambah")
            }
            startActivity(intent)
        }
        binding.btnClose.setOnClickListener {
//            val intent = Intent(this, MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
            finish()
        }
    }

    private fun showError(message: String?) {
        Toast.makeText(this, message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
    }
}