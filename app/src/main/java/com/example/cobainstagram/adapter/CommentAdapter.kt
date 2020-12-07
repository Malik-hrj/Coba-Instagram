package com.example.cobainstagram.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cobainstagram.R
import com.example.cobainstagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter(private val mContext: Context, private val mComment: ArrayList<com.example.cobainstagram.model.Comment>) :
    RecyclerView.Adapter<ComentViewHolder>() {
    private var firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_layout_comment, parent, false)
        return ComentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComment.size
    }

    override fun onBindViewHolder(holder: ComentViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val myComment = mComment[position]
        holder.komentarnya.text = myComment.getComment()

        getInfoUserKomentar(holder.gambarProfile, holder.usernameKomentar, myComment.getPublisher())
    }

    private fun getInfoUserKomentar(gambarProfile: CircleImageView, usernameKomentar: TextView, publisher: Any) {
        val userRef = FirebaseDatabase.getInstance().reference
            .child("users").child(publisher.toString())

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User :: class.java)
                    Picasso.get().load(user!!.getImage()).into(gambarProfile)
                    usernameKomentar.text = user.getUsername()
                }
            }
        })

    }

}

class ComentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var gambarProfile : CircleImageView = itemView.findViewById(R.id.gambar_list_komentar)
    var usernameKomentar : TextView = itemView.findViewById(R.id.username_profile_comment)
    var komentarnya : TextView = itemView.findViewById(R.id.commets_list)

}
