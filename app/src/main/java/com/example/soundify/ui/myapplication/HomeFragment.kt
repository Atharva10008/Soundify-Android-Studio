package com.example.soundify.ui.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.soundify.R

class HomeFragment : Fragment() {
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        
        // Set up object detection button click
        val cardView = root.findViewById<CardView>(R.id.objectDetectionButton)
        cardView.setOnClickListener {
            Log.d(TAG, "Object Detection card clicked")
            try {
                findNavController().navigate(R.id.action_nav_home_to_objectDetectionFragment)
            } catch (e: Exception) {
                Log.e(TAG, "Navigation error: ${e.message}")
            }
        }
        
        return root
    }
} 