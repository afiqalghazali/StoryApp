package com.scifi.storyapp.view.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.scifi.storyapp.data.remote.response.ListStoryItem
import com.scifi.storyapp.databinding.ActivityDetailBinding
import com.scifi.storyapp.view.utils.InterfaceUtils


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        setupAction()
        setupData()
    }

    private fun setupAction() {
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupData() {
        val story = intent.getParcelableExtra<ListStoryItem>("Story") as ListStoryItem
        postponeEnterTransition()
        binding.apply {
            tvAuthor.text = story.name
            tvDescription.text = story.description
            tvCreatedAt.text = InterfaceUtils.formatToRelativeTime(story.createdAt.toString())
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .listener(object :
                    RequestListener<Drawable> { //Add listener to load the image first before start the transition
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        startPostponedEnterTransition()
                        return false
                    }
                })
                .into(binding.ivImage)
            val locationText = story.lat?.let { lat ->
                story.lon?.let { lon ->
                    InterfaceUtils.formatLocation(lat, lon, this@DetailActivity)
                }
            }
            tvLocation.text = locationText
            tvLocation.visibility = if (locationText != null) View.VISIBLE else View.GONE
            val nameMargin = tvAuthor.layoutParams as ViewGroup.MarginLayoutParams
            nameMargin.topMargin = if (locationText != null) 0 else 16
        }
    }
}