package com.example.cobainstagram.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cobainstagram.MainActivity
import com.example.cobainstagram.R
import com.example.cobainstagram.model.Post
import com.example.cobainstagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter(private val mContext: Context, private val mPost : List<Post>) :
    RecyclerView.Adapter<PostViewHolder>() {

    private var firebaseUser : FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.list_item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val myPost = mPost[position]
        Picasso.get().load(myPost.getPostImage()).into(holder.postImage)
        if (myPost.getDescription().equals("")) {
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = myPost.getDescription()
        }
        publisherInfo(holder.profileImage, holder.userName, holder.publisher, myPost.getPublisher())

        //like
        ngeLike(myPost.getPostId(), holder.likeButton) //function
        numberOfLikes(holder.likes, myPost.getPostId()) //function brapa user like

        holder.likeButton.setOnClickListener {
            if (holder.likeButton.tag == "Like") {
                FirebaseDatabase.getInstance().reference
                    .child("Likes").child(myPost.getPostId()).child(firebaseUser!!.uid)
                    .setValue(true)
            } else {
                FirebaseDatabase.getInstance().reference
                    .child("Unlike").child(myPost.getPostId()).child(firebaseUser!!.uid)
                    .removeValue()

                val intent = Intent(mContext, MainActivity::class.java)
                mContext.startActivity(intent)
            }
        }
    }

    private fun numberOfLikes(likes: TextView, postId: String) {
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists())
                    likes.text = snapshot.childrenCount.toString() + "Likes"
            }
        })
    }

    private fun ngeLike(postId: String, likeButton: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val likesRef = FirebaseDatabase.getInstance().reference
            .child("Likes").child(postId)

        likesRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(firebaseUser!!.uid).exists()) {
                    likeButton.setImageResource(R.drawable.heart_clicked)
                    likeButton.tag = "Liked"
                } else {
                    likeButton.setImageResource(R.drawable.heart_not_clicked)
                    likeButton.tag = "Like"
                }

            }
        })
    }

    private fun publisherInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publisher1: String) {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(publisher1)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user?.getImage()).into(profileImage)
                    userName.text = user?.getUsername()
                    publisher.text = user?.getUsername()
                }
            }
        })
    }


}

class PostViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    var profileImage    : CircleImageView = itemView.findViewById(R.id.circleImage)
    var postImage       : ImageView = itemView.findViewById(R.id.image_postHome)
    var likeButton      : ImageView = itemView.findViewById(R.id.btnLikePost)
    var shareButton     : ImageView = itemView.findViewById(R.id.btnSharePost)
    var commentButton   : ImageView = itemView.findViewById(R.id.btnCommentPost)
    var saveButton      : ImageView = itemView.findViewById(R.id.btnSavePost)
    var userName        : TextView = itemView.findViewById(R.id.txt_usernamePost)
    var likes           : TextView = itemView.findViewById(R.id.tvPostLike)
    var publisher       : TextView = itemView.findViewById(R.id.tvPublisherPost)
    var comment         : TextView = itemView.findViewById(R.id.tvCommentPost)
    var description     : TextView = itemView.findViewById(R.id.tvDescriptionPost)
}
