package com.example.cobainstagram

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.cobainstagram.model.User
import com.example.cobainstagram.page.login.LoginActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageActivity
import kotlinx.android.synthetic.main.activity_edit_prof.*

class EditProfActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var cekInfoProfile = ""
    private var myUrl = ""
    private var imageUri : Uri? = null
    private var storageProfilePicture : StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_prof)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        storageProfilePicture = FirebaseStorage.getInstance().reference.child("Profile Picture")

        buttonLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val pergi = Intent(this@EditProfActivity, LoginActivity::class.java)
            pergi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(pergi)
            finish()
        }
        txt_ganti_dp.setOnClickListener {
            cekInfoProfile = "Clicked"

            CropImage.activity()
                .setAspectRatio(1, 2)
                .start(this@EditProfActivity)
        }

        save_user_info.setOnClickListener {
            if (cekInfoProfile == "Clicked") {
                uploadProfileAndUpdateInfoProfile()

            }else {
                updateInfoUser()
            }
        }
        userInfo()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK
            && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            set_display_prof.setImageURI(imageUri)
        }
    }

    private fun uploadProfileAndUpdateInfoProfile() {

        when {
            TextUtils.isEmpty(edt_nama.text.toString()) -> {
                Toast.makeText(this, "Gaboleh Kosong ya!", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edt_username.text.toString()) -> {
                Toast.makeText(this, "Gaboleh kosong ya!", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edt_bio.text.toString()) -> {
                Toast.makeText(this, "Gaboleh Kosong ya!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val progressDialog = ProgressDialog(this@EditProfActivity)
                progressDialog.setTitle("Update Profile")
                progressDialog.setMessage("Tunggu lagi update")
                progressDialog.show()

                val fireRef = storageProfilePicture?.child(firebaseUser.uid + ".jpg")

                var uploadTask: StorageTask<*>
                uploadTask = fireRef!!.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (task.isSuccessful) {
                        task.exception.let {
                           // throw it!!
                           // progressDialog.dismiss()
                        }
                    }
                    return@Continuation fireRef.downloadUrl
                }).addOnCompleteListener(OnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUrl =  task.result
                        myUrl = downloadUrl.toString()

                        val userRef = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["Fullname"] = edt_nama.text.toString()
                        userMap["Username"] = edt_username.text.toString()
                        userMap["bio"] = edt_bio.text.toString()
                        userMap["image"] = myUrl

                        userRef.child(firebaseUser.uid).updateChildren(userMap)
                        Toast.makeText(this, "Info Sudah di Update", Toast.LENGTH_SHORT).show()

                        val pergi = Intent(this, MainActivity::class.java)
                        startActivity(pergi)
                        finish()
                        progressDialog.dismiss()

                    }
                })
            }

        }
    }

    private fun updateInfoUser(){
        when{
            TextUtils.isEmpty(edt_nama.text.toString()) -> {
                Toast.makeText(this, "Gaboleh Kosong ya!", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edt_username.text.toString()) -> {
                Toast.makeText(this, "Gaboleh kosong ya!", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(edt_bio.text.toString()) -> {
                Toast.makeText(this, "Gaboleh Kosong ya!", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val userRef = FirebaseDatabase.getInstance().reference
                    .child("Users")

                val userMap = HashMap<String, Any>()
                userMap["Fullname"] = edt_nama.text.toString()
                userMap["Username"] = edt_username.text.toString()
                userMap["Bio"] = edt_bio.text.toString()

                userRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Info Updated", Toast.LENGTH_SHORT).show()

                val pergi = Intent(this, MainActivity::class.java)
                startActivity(pergi)
                finish()
            }
        }
    }
    private fun userInfo() {
        val user = FirebaseDatabase.getInstance().getReference()
            .child("Users").child(firebaseUser.uid)

        user.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user= snapshot.getValue<User>(User::class.java)

                    edt_nama.setText(user?.getFullname())
                    edt_username.setText(user?.getUsername())
                    edt_bio.setText(user?.getBio())
                }
            }


        })
    }

}



