package com.dicoding.membership.view.dashboard

interface ScrollStateListener {
    fun onScrollStateChanged(
        isScrollingDown: Boolean,
        isAtBottom: Boolean,
        isNearTop: Boolean,
        isScrollingUp: Boolean
    )
}