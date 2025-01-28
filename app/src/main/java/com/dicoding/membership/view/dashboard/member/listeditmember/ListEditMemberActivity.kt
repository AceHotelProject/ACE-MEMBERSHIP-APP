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
import com.dicoding.membership.view.dialog.GlobalTwoButtonDialog
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

        membershipAdapter.setOnEditClickCallback { membership ->
            // Handle edit action
            val intent = Intent(this@ListEditMemberActivity, EditMemberActivity::class.java).apply {
                putExtra("SCREEN_TITLE", "Edit Membership")
                putExtra("BUTTON_TEXT", "Simpan")
                putExtra("MEMBERSHIP_ID", membership.id)
            }
            startActivity(intent)
        }

        membershipAdapter.setOnDeleteClickCallback { membership ->
            // Show delete confirmation dialog
            val dialog = GlobalTwoButtonDialog().apply {
                setDialogTitle("Hapus Membership")
                setDialogMessage("Apakah anda yakin ingin menghapus membership ini?")
                setOnYesClickListener {
                    // Call delete function in your ViewModel
                    viewModel.deleteMembership(membership.id!!)
                }
                setOnNoClickListener {
                    // Dialog will automatically dismiss
                }
            }
            dialog.show(supportFragmentManager, "delete_dialog")
        }
    }

    private fun setupObservers() {
        // Main observer for list data
        viewModel.memberships.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    showLoading(false)
                    result.data?.let { response ->
                        if (response.totalResults == 0) {
                            showEmptyState(true)
                        } else {
                            showEmptyState(false)// Clear the current list
                            membershipAdapter.submitList(response.results)  // Submit new list
                        }
                    } ?: run {
                        showEmptyState(true)
                    }
                }
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Error -> {
                    showLoading(false)
                    showEmptyState(true)
                    showError(result.message)
                }
                else -> {
                    showLoading(false)
                    showEmptyState(true)
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
        binding.layoutEmptyList.visibility = View.GONE //regardless of the condition
        binding.membershipRv.visibility = if(isLoading) View.GONE else View.VISIBLE
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
        binding.fabAddMembership.setOnClickListener {
            //add Membership via FAB
            val intent = Intent(this@ListEditMemberActivity, EditMemberActivity::class.java).apply {
                putExtra("SCREEN_TITLE", "Tambah Membership")
                putExtra("BUTTON_TEXT", "Tambah")
            }
            startActivity(intent)
        }
    }

    private fun showError(message: String?) {
        Toast.makeText(this, message ?: "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
    }
}