package com.dicoding.membership.view.dashboard.history

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dicoding.membership.view.dashboard.history.member.HistoryMemberFragment
import com.dicoding.membership.view.dashboard.history.poin.HistoryTransferPointFragment
import com.dicoding.membership.view.dashboard.history.promo.HistoryPromoFragment

class HistoryPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HistoryPromoFragment()
            1 -> HistoryTransferPointFragment()
            2 -> HistoryMemberFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }
}