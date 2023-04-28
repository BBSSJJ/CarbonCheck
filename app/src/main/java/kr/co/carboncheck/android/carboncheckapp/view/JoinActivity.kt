package kr.co.carboncheck.android.carboncheckapp.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityJoinBinding
import kr.co.carboncheck.android.carboncheckapp.databinding.ActivityLoginBinding

class JoinActivity : AppCompatActivity() {
    private lateinit var binding: ActivityJoinBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJoinBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}