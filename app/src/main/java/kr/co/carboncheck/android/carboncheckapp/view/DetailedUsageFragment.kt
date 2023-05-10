package kr.co.carboncheck.android.carboncheckapp.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.zxing.integration.android.IntentIntegrator
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityMainBinding
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentDetailedUsageBinding

class DetailedUsageFragment : Fragment() {
    private var _binding: FragmentDetailedUsageBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailedUsageBinding.inflate(inflater, container, false)
        val moduleRegisterButton = binding.moduleRegisterButton
        val plugRegisterButton = binding.plugRegisterButton

        moduleRegisterButton.setOnClickListener {
            val intent = Intent(activity, QrcodeScanActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}