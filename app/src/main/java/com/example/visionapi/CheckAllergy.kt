package com.example.visionapi

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.*
import com.example.visionapi.databinding.ActivityCheckAllergyBinding

class CheckAllergy : AppCompatActivity() {
    private lateinit var binding: ActivityCheckAllergyBinding
    private var userDB: UserDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCheckAllergyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 데이터베이스 인스턴스 생성 및 초기화
        userDB = UserDatabase.getInstance(applicationContext)

        userDB?.UserDao()?.let { dao ->
            // 유저 데이터가 없으면 초기화
            if (dao.getUserNum() <= 0) {
                val tmpUser = User(1, "", "", "", "", "")
                dao.insertUser(tmpUser)
            }

            // 기존 알러지 정보 불러오기
            setAllergyBox(dao.getUser(1))
            Log.e("Allergy", "User allergy loaded")
        }

        // 설정해둔 알러지 저장
        binding.BtnSaveInfo.setOnClickListener {
            userDB?.UserDao()?.updateUser(checkAllergyBox())
            Toast.makeText(this, "알러지가 수정되었습니다!", Toast.LENGTH_SHORT).show()
        }

        // 뒤로가기
        binding.BtnGoBackMain.setOnClickListener {
            finish()
        }
    }

    private fun checkAllergyBox(): User {
        val checkBoxes = arrayOf(
            findViewById<CheckBox>(R.id.CbAllergy1),
            findViewById<CheckBox>(R.id.CbAllergy2),
            findViewById<CheckBox>(R.id.CbAllergy3),
            findViewById<CheckBox>(R.id.CbAllergy4),
            findViewById<CheckBox>(R.id.CbAllergy5)
        )

        val allergies = checkBoxes.map { if (it.isChecked) it.text.toString() else "" }

        return User(1, allergies[0], allergies[1], allergies[2], allergies[3], allergies[4])
    }

    private fun setAllergyBox(user: User) {
        val checkBoxes = arrayOf(
            findViewById<CheckBox>(R.id.CbAllergy1),
            findViewById<CheckBox>(R.id.CbAllergy2),
            findViewById<CheckBox>(R.id.CbAllergy3),
            findViewById<CheckBox>(R.id.CbAllergy4),
            findViewById<CheckBox>(R.id.CbAllergy5)
        )

        val allergies = arrayOf(user.al1, user.al2, user.al3, user.al4, user.al5)

        checkBoxes.forEachIndexed { index, checkBox ->
            checkBox?.isChecked = allergies[index].isNotEmpty()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        userDB?.close()  // 데이터베이스 닫기
        userDB = null
    }
}
