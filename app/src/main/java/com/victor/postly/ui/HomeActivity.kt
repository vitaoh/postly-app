package com.victor.postly.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.victor.postly.adapter.PostAdapter
import com.victor.postly.auth.UserAuth
import com.victor.postly.dao.PostDao
import com.victor.postly.dao.UserDao
import com.victor.postly.databinding.ActivityHomeBinding
import com.victor.postly.utils.Base64Converter

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val auth = UserAuth()
    private val postDao = PostDao()
    private val userDao = UserDao()
    private val converter = Base64Converter()
    private val adapter = PostAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (!auth.isLoggedIn()) {
            goToLogin()
            return
        }

        setupRecycler()
        loadPosts()
        setupListeners()
    }

    override fun onResume() {
        super.onResume()
        loadAvatar()
    }

    private fun setupRecycler() {
        binding.recyclerFeed.apply {
            this.adapter = this@HomeActivity.adapter
            layoutManager = LinearLayoutManager(this@HomeActivity)
        }
    }

    private fun loadAvatar() {
        val uid = auth.getCurrentUid() ?: return
        userDao.getUser(uid) { user ->
            if (!user?.photo.isNullOrEmpty()) {
                val bitmap = converter.stringToBitmap(user!!.photo!!)
                binding.imgAvatar.setImageBitmap(bitmap)
            }
        }
    }

    private fun loadPosts() {
        postDao.getPosts { posts ->
            adapter.updatePosts(posts)
            binding.layoutEmpty.visibility = if (posts.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupListeners() {
        binding.imgAvatar.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        binding.fabNewPost.setOnClickListener {
            val dialog = NewPostDialog()
            dialog.onPostCreated = { loadPosts() }
            dialog.show(supportFragmentManager, "new_post")
        }
    }

    private fun goToLogin() {
        startActivity(
            Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }
}