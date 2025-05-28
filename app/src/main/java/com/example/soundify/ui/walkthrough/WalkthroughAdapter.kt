package com.example.soundify.ui.walkthrough

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soundify.R

class WalkthroughAdapter : RecyclerView.Adapter<WalkthroughAdapter.WalkthroughViewHolder>() {

    private val slides = listOf(
        Slide(R.drawable.oiz, R.string.slide1_title, R.string.slide1_description),
        Slide(R.drawable.oip, R.string.slide2_title, R.string.slide2_description),
        Slide(R.drawable.untitled_design_1, R.string.slide3_title, R.string.slide3_description)
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkthroughViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_walkthrough, parent, false)
        return WalkthroughViewHolder(view)
    }

    override fun onBindViewHolder(holder: WalkthroughViewHolder, position: Int) {
        val slide = slides[position]
        holder.bind(slide)
    }

    override fun getItemCount() = slides.size

    class WalkthroughViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.slideImage)
        private val titleView: TextView = itemView.findViewById(R.id.slideTitle)
        private val descriptionView: TextView = itemView.findViewById(R.id.slideDescription)

        fun bind(slide: Slide) {
            imageView.setImageResource(slide.imageResId)
            titleView.setText(slide.titleResId)
            descriptionView.setText(slide.descriptionResId)
        }
    }

    data class Slide(
        val imageResId: Int,
        val titleResId: Int,
        val descriptionResId: Int
    )
} 