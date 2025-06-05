package com.gorkemoji.qrify.fragment

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.gorkemoji.qrify.R
import com.gorkemoji.qrify.databinding.FragmentCreateBinding
import androidx.core.graphics.set
import androidx.core.graphics.createBitmap

class CreateFragment : Fragment() {
    private lateinit var binding: FragmentCreateBinding
    private var selectedQrType: String = "text"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateBinding.inflate(inflater, container, false)

        binding.editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        binding.createButton.isEnabled = false

        binding.editText.doAfterTextChanged { editable ->
            val input = editable?.toString()?.trim().orEmpty()

            binding.createButton.isEnabled = when (selectedQrType) {
                "url" -> isValidUrl(input)
                "phone" -> isValidPhone(input)
                "email" -> isValidEmail(input)
                "text" -> input.isNotEmpty()
                else -> false
            }
        }

        binding.chipGroupQrType.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isEmpty()) binding.textChip.isChecked = true
            else {
                when (checkedIds[0]) {
                    R.id.textChip -> {
                        selectedQrType = "text"
                        binding.editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    }
                    R.id.urlChip -> {
                        selectedQrType = "url"
                        binding.editText.inputType = InputType.TYPE_TEXT_VARIATION_URI
                    }
                    R.id.phoneChip -> {
                        selectedQrType = "phone"
                        binding.editText.inputType = InputType.TYPE_CLASS_PHONE
                    }
                    R.id.emailChip -> {
                        selectedQrType = "email"
                        binding.editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                    }
                }
                binding.editText.text?.clear()
                binding.createButton.isEnabled = false
            }
        }

        binding.createButton.setOnClickListener {
            val inputContent = binding.editText.text.toString().trim()
            val qrCodeData: String
            var isValidInput = true

            when (selectedQrType) {
                "text" -> qrCodeData = inputContent
                "url" -> {
                    qrCodeData = if (!inputContent.startsWith("http://") && !inputContent.startsWith("https://"))
                        "https://$inputContent"
                    else inputContent

                    if (!isValidUrl(inputContent)) {
                        binding.createButton.isEnabled = false
                        isValidInput = false
                    }
                }
                "phone" -> {
                    qrCodeData = "tel:$inputContent"
                    if (!isValidPhone(inputContent)) {
                        binding.createButton.isEnabled = false
                        isValidInput = false
                    }
                }
                "email" -> {
                    qrCodeData = "mailto:$inputContent"
                    if (!isValidEmail(inputContent)) {
                        binding.createButton.isEnabled = false
                        isValidInput = false
                    }
                }
                else -> qrCodeData = inputContent
            }

            if (isValidInput) {
                val qrBitmap = generateQrCodeBitmap(qrCodeData)
                val bottomSheet = ResultBottomSheetFragment(qrCodeData, qrBitmap)
                bottomSheet.show(parentFragmentManager, "CreateResult")
            }
        }

        return binding.root
    }

    private fun getThemeColor(attrRes: Int): Int {
        val typedValue = TypedValue()
        val theme = requireContext().theme
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }

    private fun generateQrCodeBitmap(data: String): Bitmap {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = createBitmap(width, height)
        val qrColor = getThemeColor(android.R.attr.colorPrimary)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (bitMatrix.get(x, y)) bitmap[x, y] = qrColor
                else bitmap[x, y] = Color.TRANSPARENT
            }
        }
        return bitmap
    }

    private fun isValidUrl(input: String): Boolean {
        val urlToValidate = if (!input.startsWith("http://") && !input.startsWith("https://")) "https://$input"
        else input
        return android.util.Patterns.WEB_URL.matcher(urlToValidate).matches()
    }

    private fun isValidPhone(input: String): Boolean {
        return android.util.Patterns.PHONE.matcher(input).matches() && input.isNotEmpty()
    }

    private fun isValidEmail(input: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches() && input.isNotEmpty()
    }
}