package com.dicoding.core.utils.constants

enum class UserRole(val role: String, val display: String) {
    ADMIN("admin", "Admin"),
    MITRA("mitra", "Mitra"),
    RECEPTIONIST("receptionist", "Receptionist"),
    MEMBER("member", "Member"),
    NONMEMBER("nonmember", "NonMember"),
    USER("user", "User"),
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
    UserRole.USER.role -> {
        UserRole.USER
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
    UserRole.USER.role -> {
        UserRole.USER.display
    }
    else -> {
        UserRole.UNDEFINED.display
    }
}