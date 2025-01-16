package com.dicoding.core.domain.membership.model

data class Membership(
    val id: String,
    val name: String,
    val periode: Int,
    val price: Int,
    val tnc: List<String>,
    val discount: Int
)