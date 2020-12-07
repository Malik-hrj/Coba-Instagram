package com.example.cobainstagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cobainstagram.adapter.CommentAdapter
import com.example.cobainstagram.model.Comment
import com.example.cobainstagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : AppCompatActivity() {

    private var postId = ""
    private var publisherId = ""
    private var firebaseUser : FirebaseUser? = null
    private var commentAdapter : CommentAdapter? = null
    private var commentListData : MutableList<Comment>? = null
    private var recyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        recyclerView = findViewById(R.id.recycler_comment)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView?.layoutManager = linearLayoutManager

        commentListData = ArrayList()
        commentAdapter = CommentAdapter(this, commentListData as ArrayList<Comment>)
        recyclerView?.adapter = commentAdapter

        userInfo()
        readComment()
        getPostImage()
        txt_post_comment.setOnClickListener {
            if (edt_add_comment.text.toString() == "") {
                Toast.makeText(this, "tolong di isi", Toast.LENGTH_SHORT).show()
            } else {
                addComment()
            }
        }
    }

    private fun getPostImage() {
        val postRef = FirebaseDatabase.getInstance().reference
            .child("Posts").child(postId).child("postimage")
        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val image = snapshot.value.toString()

                    Picasso.get().load(image).into(image_post_comment)
                }


            }
        })
    }

    private fun readComment() {
        val comentRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        comentRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    commentListData!!.clear()

                    for (s in snapshot.children) {
                        val comment = s.getValue(Comment :: class.java)

                        commentListData!!.add(comment!!)
                    }
                    commentAdapter!!.notifyDataSetChanged()

                }
            }
        })

    }

    private fun addComment() {
        val commentsRef = FirebaseDatabase.getInstance().reference
            .child("Comments").child(postId)

        val commentsMap = HashMap<String, Any>()
        commentsMap["comment"] = edt_add_comment.text.toString()
        commentsMap["publisher"] = firebaseUser!!.uid

        commentsRef.push().setValue(commentsMap)
        edt_add_comment.text.clear()

    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)

                    Picasso.get().load(user!!.getImage()).into(profile_image_content)
                }
            }
        })

    }
}