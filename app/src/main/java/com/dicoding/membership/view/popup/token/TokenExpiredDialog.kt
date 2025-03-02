package com.dicoding.membership.view.popup.token

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.dicoding.membership.R
import com.dicoding.membership.view.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TokenExpiredDialog : DialogFragment() {
    private val tokenExpiredViewModel: TokenExpiredViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            val builder = AlertDialog.Builder(it, R.style.TransparentDialogTheme)
            val inflater = requireActivity().layoutInflater

            val view = inflater.inflate(R.layout.dialog_token_expired, null)
            builder.setView(view)
            builder.setCancelable(false)

            val btnContinue = view.findViewById<Button>(R.id.btn_expired_continue)
            btnContinue.setOnClickListener {
                handleDataDeletion()
            }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun handleDataDeletion() {
        tokenExpiredViewModel.getUser().observe(this) { user ->
            user?.let {
                lifecycleScope.launch {
                    tokenExpiredViewModel.deleteToken()

                    tokenExpiredViewModel.deleteUser(it)
                    tokenExpiredViewModel.deleteAllData()

                    navigateToLogin()
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireActivity(), LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
        dismiss()
        requireActivity().finish()
    }
}