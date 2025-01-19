package com.dicoding.core.domain.membership.model

data class Membership(
    val id: String,
    val type: String,
    val duration: Int,
    val price: Int,
    val tnc: List<String>
)