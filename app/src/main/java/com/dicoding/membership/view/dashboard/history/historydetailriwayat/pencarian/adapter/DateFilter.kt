package com.dicoding.membership.view.dashboard.history.historydetailriwayat.pencarian.adapter

sealed class DateFilter(val displayName: String) {
    object TODAY : DateFilter("Hari ini")
    object THIS_MONTH : DateFilter("Bulan ini")
    object THIS_YEAR : DateFilter("Tahun ini")
}

sealed class MembershipStatus(val displayName: String) {
    object ACTIVE : MembershipStatus("Active")
    object EXPIRED : MembershipStatus("Expired")
}
