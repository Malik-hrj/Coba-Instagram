package com.example.cobainstagram.ui.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cobainstagram.EditProfActivity
import com.example.cobainstagram.R
import com.example.cobainstagram.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_edit_prof.*
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.android.synthetic.main.fragment_account.view.*
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cobainstagram.adapter.ProfilePostAdapter
import com.example.cobainstagram.model.Post

class AccountFragment : Fragment() {

    private var recycler : RecyclerView? = null
    private var feedAdapter : ProfilePostAdapter? = null
    private var postGridList : MutableList<Post>? = null
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val viewProfile = inflater.inflate(R.layout.fragment_account, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        recycler = viewProfile.findViewById(R.id.rv_profile)
        recycler!!.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(context, 3)
        layoutManager.reverseLayout = true
        recycler!!.layoutManager = layoutManager

        postGridList = ArrayList()
        feedAdapter = context!!.let { ProfilePostAdapter(it, postGridList as ArrayList<Post>) }
        recycler!!.adapter = feedAdapter

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref !=null){
            this.profileId = pref.getString("profileId", "none")!!
        }
        if (profileId == firebaseUser.uid) {
            view?.buttom_profile1?.text = "Edit Profile"
        } else if (profileId != firebaseUser.uid){
            cekFollowandFollowingButtonStatus()
        }
        viewProfile.buttom_profile1.setOnClickListener {
            startActivity(Intent(context, EditProfActivity::class.java))
        }
        getFollowers()
        getFollowing()
        userInfo()
        myPost()
        return viewProfile

    }

    private fun myPost() {
        val firebase = FirebaseDatabase.getInstance().reference.child("posts")
        firebase.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    (postGridList as ArrayList<Post>).clear()

                    for (s in snapshot.children) {
                        val post = s.getValue(Post::class.java)
                        if (post!!.getPublisher() == profileId) {
                            (postGridList as ArrayList<Post>).add(post)
                        }
                        postGridList!!.reverse()
                        feedAdapter!!.notifyDataSetChanged()

                    }
                }
            }
        })
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference
            .child("Users").child(profileId)

        usersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)

                    view?.profile_username?.text = user?.getUsername()
                    view?.txt_fullname?.text = user?.getFullname()
                    view?.txt_bio?.text = user?.getBio()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }



        })
    }

    private fun getFollowing() {
        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")
        followingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    view?.txt_following?.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    view?.txt_followers?.text = snapshot.childrenCount.toString()
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun cekFollowandFollowingButtonStatus() {
        val followingRef = firebaseUser.uid.let { it ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it.toString())
                .child("Following")
        }
        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        view?.buttom_profile1?.text = "Following"
                    } else {
                        view?.buttom_profile1?.text = "Follow"
                    }
                }


            })
        }

    }

    override fun onStop() {
        super.onStop()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

}