package com.example.retrodogapp

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.retrodogapp.databinding.ItemDogBinding
import com.squareup.picasso.Picasso

class DogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val binding = ItemDogBinding.bind(view)

    fun bind(imageUrl: String) {
        Picasso.get().load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_menu_report_image)
            .fit().centerCrop().into(binding.ivDog)
    }

    fun recycle() {
        Picasso.get().cancelRequest(binding.ivDog)
        binding.ivDog.setImageDrawable(null)
    }
}
