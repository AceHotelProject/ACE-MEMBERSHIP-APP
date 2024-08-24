package com.dicoding.membership.core.utils.constants

enum class UserRole(val role: String, val display: String) {
    ADMIN("admin", "Admin"),
    MITRA("mitra", "Mitra"),
    RECEPTIONIST("receptionist", "Receptionist"),
    MEMBER("member", "Member"),
    NONMEMBER("non_member", "Nonmember"),
    UNDEFINED("role", "Role")
}

fun mapToUserRole(role: String): UserRole = when (role) {
    UserRole.ADMIN.role -> {
        UserRole.ADMIN
    }
    UserRole.MITRA.role -> {
        UserRole.MITRA
    }
    UserRole.RECEPTIONIST.role -> {
        UserRole.RECEPTIONIST
    }
    UserRole.MEMBER.role -> {
        UserRole.MEMBER
    }
    UserRole.NONMEMBER.role -> {
        UserRole.NONMEMBER
    }
    else -> {
        UserRole.UNDEFINED
    }
}

fun mapToUserDisplay(role: String): String = when (role) {
    UserRole.ADMIN.role -> {
        UserRole.ADMIN.display
    }
    UserRole.MITRA.role -> {
        UserRole.MITRA.display
    }
    UserRole.RECEPTIONIST.role -> {
        UserRole.RECEPTIONIST.display
    }
    UserRole.MEMBER.role -> {
        UserRole.MEMBER.display
    }
    UserRole.NONMEMBER.role -> {
        UserRole.NONMEMBER.display
    }
    else -> {
        UserRole.UNDEFINED.display
    }
}