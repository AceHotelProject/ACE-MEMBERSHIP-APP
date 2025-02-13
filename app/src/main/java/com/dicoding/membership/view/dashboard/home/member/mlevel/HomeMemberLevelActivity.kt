package com.dicoding.membership.view.dashboard.home.member.mlevel

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.core.data.source.Resource
import com.dicoding.membership.R
import com.dicoding.membership.databinding.ActivityHomeMemberLevelBinding
import com.dicoding.membership.view.dashboard.home.member.mreferral.HomeMemberReferralActivity
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel

@AndroidEntryPoint
class HomeMemberLevelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeMemberLevelBinding
    private val viewModel: HomeMemberLevelViewModel by viewModels()
    private lateinit var adapter: MembershipLevelAdapter
    private var selectedMemberType: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMemberLevelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupViewModel()
        setupClickListeners()
        isButton(false)
    }

    private fun setupRecyclerView() {
        adapter = MembershipLevelAdapter()
        binding.rvMemberships.apply {
            adapter = this@HomeMemberLevelActivity.adapter
            layoutManager = LinearLayoutManager(this@HomeMemberLevelActivity)
        }

        adapter.setOnItemSelectedCallback { membership ->
            selectedMemberType = membership.type
            isButton(true)
        }
    }

    private fun setupViewModel() {
        viewModel.memberships.observe(this) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    showLoading(true)
                }
                is Resource.Success -> {
                    showLoading(false)
                    resource.data?.results?.let { memberships ->
                        adapter.submitList(memberships)
                    }
                }
                is Resource.Error -> {
                    showLoading(false)
                    // Handle error state if needed
                }
                else -> {
                    showLoading(false)
                }
            }
        }
        viewModel.getMemberships()
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            selectedMemberType?.let { type ->
                startActivity(Intent(this, HomeMemberReferralActivity::class.java).apply {
                    putExtra(HomeMemberReferralActivity.SELECTED_PACKAGE, type)
                })
            }
        }
    }

    private fun isButton(boolean: Boolean){
        binding.btnContinue.isEnabled = boolean
    }

    private fun showLoading(boolean: Boolean){
        binding.apply{
            loadingOverlay.visibility = if(boolean) View.VISIBLE else View.GONE
            rvMemberships.visibility = if(!boolean) View.VISIBLE else View.GONE
            btnContinue.visibility = if(!boolean) View.VISIBLE else View.GONE
        }
    }

    companion object {
        const val EXTRA_USER_ID = "extra_user_id"
    }
}