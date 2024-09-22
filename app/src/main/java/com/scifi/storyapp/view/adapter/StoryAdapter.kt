package com.scifi.storyapp.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.scifi.storyapp.data.remote.response.ListStoryItem
import com.scifi.storyapp.databinding.ItemStoryBinding
import com.scifi.storyapp.view.detail.DetailActivity
import com.scifi.storyapp.view.utils.InterfaceUtils

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    class ViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStoryItem) {
            binding.apply {
                tvAuthor.text = item.name
                tvDescription.text = item.description
                tvCreatedAt.text = InterfaceUtils.formatToRelativeTime(item.createdAt.toString())
                Glide.with(itemView.context)
                    .load(item.photoUrl)
                    .into(ivImage)

                val locationText = item.lat?.let { lat ->
                    item.lon?.let { lon ->
                        InterfaceUtils.formatLocation(lat, lon, itemView.context)
                    }
                }
                tvLocation.text = locationText
                tvLocation.visibility = if (locationText != null) View.VISIBLE else View.GONE
                val nameMargin = tvAuthor.layoutParams as ViewGroup.MarginLayoutParams
                nameMargin.topMargin = if (locationText != null) 0 else 16
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra("Story", item)

                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.avatar, "avatar"),
                        Pair(binding.ivImage, "image"),
                        Pair(binding.tvAuthor, "author"),
                        Pair(binding.tvDescription, "description"),
                        Pair(binding.tvCreatedAt, "createdAt"),
                        Pair(binding.tvLocation, "location"),
                    )
                itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem,
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}