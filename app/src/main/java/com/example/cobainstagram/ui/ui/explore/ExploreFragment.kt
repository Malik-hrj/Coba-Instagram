package com.example.cobainstagram.ui.ui.explore

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cobainstagram.R
import com.example.cobainstagram.adapter.SearchUserAdapter
import com.example.cobainstagram.model.User
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_explore.*
import kotlinx.android.synthetic.main.fragment_explore.view.*

class ExploreFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userSearchAdapter: SearchUserAdapter? = null
    private var mUser : MutableList<User>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_explore, container, false)

        recyclerView = view.findViewById(R.id.recycler_explore)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = GridLayoutManager(context, 2)


        mUser = ArrayList()
        userSearchAdapter = context.let { it?.let { it1 -> SearchUserAdapter(it, mUser as ArrayList<User>) } }
        recyclerView?.adapter = userSearchAdapter

        view.search_edtText.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (view.search_edtText.toString() == "") {

                }else {
                    recyclerView?.visibility = View.VISIBLE
                    getUSer()
                    searchUser(p0.toString().toLowerCase())
                }
            }
        })
        return view

    }

    private fun searchUser(input: String) {
        val query = FirebaseDatabase.getInstance().getReference()
            .child("Users")
            .orderByChild("fullname")
            .startAt(input).endAt(input + "")

        query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                mUser?.clear()

                for (p0 in snapshot.children){
                    val user = p0.getValue(User::class.java)
                    if (user != null){
                        mUser?.add(user)
                    }

                }
                userSearchAdapter?.notifyDataSetChanged()
            }

        })
    }




    private fun getUSer() {
        val userRed = FirebaseDatabase.getInstance().getReference().child("Users")
        userRed.addValueEventListener(object  : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (view?.search_edtText?.text.toString() == "")
                    mUser?.clear()

                for (snapshot in snapshot.children) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        mUser?.add(user)
                    }
                }
                userSearchAdapter?.notifyDataSetChanged()
            }
        })
    }


}



