package com.gorkemoji.qrify.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gorkemoji.qrify.databinding.FragmentResultBottomSheetBinding

class ResultBottomSheetFragment(
    private val resultText: String
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentResultBottomSheetBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResultBottomSheetBinding.inflate(inflater, container, false)

        binding.textResult.text = resultText

        when {
            resultText.startsWith("http://") || resultText.startsWith("https://") -> {
                binding.btnAction.text = "Open in Browser"
                binding.btnAction.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(resultText))
                    startActivity(intent)
                }
            }
            resultText.startsWith("WIFI:") -> {
                binding.btnAction.text = "Connect to Wi-Fi"
                binding.btnAction.setOnClickListener {
                    startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                }
            }
            else -> binding.btnAction.visibility = View.GONE
        }




        return binding.root
    }
}