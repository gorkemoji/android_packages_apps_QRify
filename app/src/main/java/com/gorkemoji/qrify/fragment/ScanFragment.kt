package com.gorkemoji.qrify.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gorkemoji.qrify.R
import com.gorkemoji.qrify.databinding.FragmentScanBinding

class ScanFragment : Fragment() {
    private lateinit var binding: FragmentScanBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}