package kr.co.carboncheck.android.carboncheckapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentDetailedUsageBinding
import kr.co.carboncheck.android.carboncheckapp.dto.RegisterFaceRequest
import kr.co.carboncheck.android.carboncheckapp.dto.RegisterFaceResponse
import kr.co.carboncheck.android.carboncheckapp.network.RetrofitClient
import kr.co.carboncheck.android.carboncheckapp.util.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val registerPlugButton = binding.registerPlugButton

        // TODO: 이미 가입된 홈서버가 있으면 가입안되도록 수정하자.


        registerPlugButton.setOnClickListener {
            val intent = Intent(activity, QrcodeScanActivity::class.java)
            intent.putExtra("ACTION", "REGISTER_PLUG")
            startActivity(intent)
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}