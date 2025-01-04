package com.dicoding.membership.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.dicoding.membership.R
import com.dicoding.membership.databinding.DialogGlobalTwoButtonBinding

class GlobalTwoButtonDialog : DialogFragment() {
    private var onYesClickListener: (() -> Unit)? = null
    private var onNoClickListener: (() -> Unit)? = null
    private var dialogTitle: String = "Gunakan Promo?"
    private var dialogMessage: String = "Lorem ipsum dolor sit amet consectetur. Tellus eget sed feugiat faucibus lectus vitae."

    fun setOnYesClickListener(listener: () -> Unit) {
        onYesClickListener = listener
    }

    fun setOnNoClickListener(listener: () -> Unit) {
        onNoClickListener = listener
    }

    fun setDialogTitle(title: String) {
        dialogTitle = title
    }

    fun setDialogMessage(message: String) {
        dialogMessage = message
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DialogGlobalTwoButtonBinding.inflate(inflater, container, false)

        // Set background dialog menjadi transparan
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set dialog title and message
        binding.tvTitle.text = dialogTitle
        binding.tvDescription.text = dialogMessage

        binding.buttonYes.setOnClickListener {
            onYesClickListener?.invoke()
            dismiss()
        }

        binding.buttonNo.setOnClickListener {
            onNoClickListener?.invoke()
            dismiss()
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val params = attributes
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT

            // Tambahkan margin (contoh: margin horizontal 50dp)
            val margin = resources.getDimensionPixelSize(R.dimen.dialog_margin) // Definisikan dimensi di res
            params.horizontalMargin = margin.toFloat() / resources.displayMetrics.widthPixels

            attributes = params

            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

}