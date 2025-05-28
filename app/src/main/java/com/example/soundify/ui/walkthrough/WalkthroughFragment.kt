package com.example.soundify.ui.walkthrough

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.soundify.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class WalkthroughFragment : Fragment() {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var createAccountButton: Button
    private lateinit var loginPromptText: TextView

    private val imageList = listOf(
        R.drawable.views_images_ratio,  // Temporary image
        R.drawable.views_images_ratio,  // Temporary image
        R.drawable.views_images_ratio   // Temporary image
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_walkthrough, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)
        createAccountButton = view.findViewById(R.id.createAccountButton)
        loginPromptText = view.findViewById(R.id.loginPromptText)

        val adapter = ImagePagerAdapter(imageList)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        createAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_walkthrough_to_signup)
        }

        setupLoginPrompt()
    }

    private fun setupLoginPrompt() {
        val haveAccountText = "Already have an account? "
        val loginText = "Log in"
        val fullText = haveAccountText + loginText

        val spannableString = SpannableString(fullText)

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_walkthrough_to_login)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
                ds.color = ContextCompat.getColor(requireContext(), R.color.primaryBase)
                ds.isFakeBoldText = true
            }
        }

        val loginStartIndex = fullText.indexOf(loginText)
        val loginEndIndex = loginStartIndex + loginText.length

        spannableString.setSpan(
            clickableSpan,
            loginStartIndex,
            loginEndIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        loginPromptText.text = spannableString
        loginPromptText.movementMethod = LinkMovementMethod.getInstance()
        loginPromptText.highlightColor = Color.TRANSPARENT
    }
}

class ImagePagerAdapter(private val images: List<Int>) : 
    androidx.recyclerview.widget.RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.carouselImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount() = images.size
} 