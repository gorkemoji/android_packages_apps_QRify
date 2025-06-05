package com.gorkemoji.qrify.fragment

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.gorkemoji.qrify.R
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanFragment : Fragment(), ZXingScannerView.ResultHandler {
    private lateinit var scannerView: ZXingScannerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        scannerView = ZXingScannerView(requireContext())
        parentFragmentManager.setFragmentResultListener("bottom_sheet_dismissed_key", viewLifecycleOwner) { key, bundle ->

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                scannerView.setResultHandler(this)
                scannerView.startCamera()
            }
        }
        return scannerView
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            scannerView.setResultHandler(this)
            scannerView.startCamera()
        } else ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.CAMERA), 101)
    }

    override fun onPause() {
        super.onPause()
        scannerView.stopCamera()
    }

    override fun handleResult(p0: Result?) {
        p0?.text?.let { content ->
            val bottomSheet = ResultBottomSheetFragment(content)
            bottomSheet.show(parentFragmentManager, "ScanResult")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            scannerView.setResultHandler(this)
            scannerView.startCamera()
        } else Toast.makeText(context, getString(R.string.permissions_failed), Toast.LENGTH_SHORT).show()
    }
}