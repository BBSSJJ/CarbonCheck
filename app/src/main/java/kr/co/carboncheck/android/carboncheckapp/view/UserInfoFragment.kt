package kr.co.carboncheck.android.carboncheckapp.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.co.carboncheck.android.carboncheckapp.R
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentDetailedUsageBinding
import kr.co.carboncheck.android.carboncheckapp.databinding.FragmentUserInfoBinding
import kr.co.carboncheck.android.carboncheckapp.util.UserPreference

class UserInfoFragment : Fragment() {
    private var _binding: FragmentUserInfoBinding? = null
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
        _binding = FragmentUserInfoBinding.inflate(inflater, container, false)
        val logoutButton = binding.logoutButton

        logoutButton.setOnClickListener {
            deletePreferences(requireActivity())
        }

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun deletePreferences(context: Context?) {
        val userPreference = UserPreference().getPreferences(context!!)
        val editor = userPreference!!.edit()
        editor.clear()
        editor.apply()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        startActivity(intent)
    }
}