package com.example.soundify.ui.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.soundify.R
import com.example.soundify.databinding.FragmentMyApplicationBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MyApplicationFragment : Fragment() {
    private var _binding: FragmentMyApplicationBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyApplicationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser ?: return

        setupNavigationDrawer()
        setupUserDetails()
        setupLogout()
    }

    private fun setupLogout() {
        binding.logoutButton.setOnClickListener {
            auth.signOut()
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_my_application_to_login)
        }
    }

    private fun setupNavigationDrawer() {
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView

        // Setup the hamburger icon to open/close drawer
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    findNavController().navigate(R.id.nav_home)
                }
                R.id.nav_gallery -> {
                    findNavController().navigate(R.id.nav_gallery)
                }
                R.id.nav_slideshow -> {
                    findNavController().navigate(R.id.nav_slideshow)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Update navigation header with user details
        val headerView = navigationView.getHeaderView(0)
        headerView.findViewById<android.widget.TextView>(R.id.nav_header_name).text = 
            currentUser.displayName ?: "User"
        headerView.findViewById<android.widget.TextView>(R.id.nav_header_email).text = 
            currentUser.email
    }

    private fun setupUserDetails() {
        // Set user details in the main content
        binding.userName.text = currentUser.displayName ?: "User"
        binding.userEmail.text = currentUser.email

        // Make the user card clickable for object detection
        binding.userCard.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_myApplication_to_objectDetection)
            } catch (e: Exception) {
                Log.e("MyApplicationFragment", "Navigation failed: ${e.message}")
            }
        }

        // TODO: Set up RecyclerView with user's applications
        // For now, we'll just show a placeholder
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 