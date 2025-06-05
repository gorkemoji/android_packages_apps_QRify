package com.gorkemoji.qrify.fragment

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.gorkemoji.qrify.databinding.FragmentResultBottomSheetBinding
import com.gorkemoji.qrify.utils.parseWifiQr
import com.gorkemoji.qrify.utils.suggestWifiConnection
import androidx.core.net.toUri
import com.gorkemoji.qrify.R

class ResultBottomSheetFragment(
    private val resultText: String
) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentResultBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBottomSheetBinding.inflate(inflater, container, false)
        binding.resultCnt.text = resultText

        when {
            resultText.startsWith("http://") || resultText.startsWith("https://") -> {
                binding.imageView.setImageResource(R.drawable.ic_link_24)
                binding.textResult.text = getString(R.string.url_found)

                binding.btnAction.apply {
                    text = getString(R.string.open_in_browser)
                    visibility = View.VISIBLE
                    setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, resultText.toUri())) }
                }
            }

            resultText.startsWith("WIFI:") -> {
                val wifiData = parseWifiQr(resultText)
                if (wifiData != null) {
                    binding.imageView.setImageResource(R.drawable.ic_wifi_24)
                    binding.textResult.text = getString(R.string.wifi_network_found)

                    binding.resultCnt.text = buildString {
                        append(getString(R.string.ssid) + ": ${wifiData.ssid}\n")
                        append(getString(R.string.encryption) + ": ${wifiData.encryption}\n")
                        append(getString(R.string.password) + ": ${wifiData.password}")
                    }

                    binding.btnAction.apply {
                        text = getString(R.string.connect_to_wifi)
                        visibility = View.VISIBLE

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            isEnabled = true
                            setOnClickListener {
                                suggestWifiConnection(requireContext(), wifiData)
                                Toast.makeText(requireContext(), getString(R.string.suggested_wifi), Toast.LENGTH_LONG).show()
                                startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                            }
                        } else isEnabled = false
                    }
                } else {
                    binding.imageView.setImageResource(R.drawable.ic_text_snippet_24)
                    binding.textResult.text = getString(R.string.text_found)
                    binding.btnAction.visibility = View.GONE
                }
            } else -> {
                binding.imageView.setImageResource(R.drawable.ic_text_snippet_24)
                binding.textResult.text = getString(R.string.text_found)
                binding.btnAction.visibility = View.GONE
            }
        }

        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        parentFragmentManager.setFragmentResult("bottom_sheet_dismissed_key", Bundle())
    }
}