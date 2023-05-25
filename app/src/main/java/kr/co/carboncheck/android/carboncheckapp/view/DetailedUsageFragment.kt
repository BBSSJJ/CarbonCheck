package kr.co.carboncheck.android.carboncheckapp.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val registerHomeServerButton = binding.registerHomeServerButton
        val joinHomeServerButton = binding.joinHomeServerButton
        val registerFaceButton = binding.registerFaceButton

        // TODO: 이미 가입된 홈서버가 있으면 가입안되도록 수정하자.
        registerHomeServerButton.setOnClickListener {
            val intent = Intent(activity, QrcodeScanActivity::class.java)
            intent.putExtra("ACTION", "REGISTER_HOMESERVER")
            startActivity(intent)
        }

        joinHomeServerButton.setOnClickListener {
            val intent = Intent(activity, QrcodeScanActivity::class.java)
            intent.putExtra("ACTION", "JOIN_HOMESERVER")
            startActivity(intent)
        }

        registerFaceButton.setOnClickListener {

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}