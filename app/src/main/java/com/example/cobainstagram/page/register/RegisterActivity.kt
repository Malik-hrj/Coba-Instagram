package com.example.cobainstagram.page.register

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.cobainstagram.MainActivity
import com.example.cobainstagram.R
import com.example.cobainstagram.page.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        tosignin_page.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        btn_regist.setOnClickListener {
            createAkun()
        }

    }

    private fun createAkun() {
        val fullName = edt_fullname_regist.text.toString()
        val userName = edt_usname_regist.text.toString()
        val email    = edt_email_regist.text.toString()
        val password = edt_pass_regist.text.toString()

        when {
            TextUtils.isEmpty(fullName) -> Toast.makeText(this, "Fullname is Required",
                Toast.LENGTH_SHORT)
            TextUtils.isEmpty(userName) -> Toast.makeText(this, "userName is Required",
                Toast.LENGTH_SHORT)
            TextUtils.isEmpty(email) -> Toast.makeText(this, "Email is Required",
                Toast.LENGTH_SHORT)
            TextUtils.isEmpty(password) -> Toast.makeText(this, "Password is Required",
                Toast.LENGTH_SHORT)

            else -> {
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Register")
                progressDialog.setMessage("Please Wait...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth : FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful)
                            saveUserInfo(fullName, userName, email, password, progressDialog)
                    }
            }

        }
    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, password: String, progressDialog: ProgressDialog) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName
        userMap["username"] = userName
        userMap["email"] = email
        userMap["bio"] = "Hey"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/coba-instagra.appspot.com/o/Default%20Image%2F74D4F10C-0288-40D4-AEC7-3DA2CF73963E.jpeg?alt=media&token=8ee9ed5e-9b79-4d2d-ac32-73a85562a439"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener {task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Akun Sudah Dibuat", Toast.LENGTH_SHORT).show()

                    val pergi = Intent(this, MainActivity:: class.java)
                    pergi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(pergi)
                    finish()
                }else {
                    val message = task.exception!!.toString()
                    Toast.makeText(this, "Error: $message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }

            }
    }
}