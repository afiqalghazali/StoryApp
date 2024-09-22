package com.scifi.storyapp.view.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.scifi.storyapp.R
import com.scifi.storyapp.data.StoryRepository
import com.scifi.storyapp.data.remote.response.ListStoryItem
import com.scifi.storyapp.di.Injection
import com.scifi.storyapp.view.main.MainViewModel

class StoryRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {
    private var stories: List<ListStoryItem> = emptyList()
    private val storyRepository: StoryRepository = Injection.storyRepository(context)
    private val viewModel: MainViewModel = MainViewModel(storyRepository)

    override fun onCreate() {
        observeViewModel()
        fetchStories()
    }

    override fun onDataSetChanged() {
        fetchStories()
    }

    override fun onDestroy() {
        viewModel.storiesLocation.removeObserver(observer)
    }

    override fun getCount(): Int = stories.size

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_image)
        val story = stories[position]

        story.photoUrl?.let {
            try {
                val bitmap = Glide.with(context)
                    .asBitmap()
                    .load(it)
                    .submit()
                    .get()
                views.setImageViewBitmap(R.id.iv_image, bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true

    private fun observeViewModel() {
        viewModel.storiesLocation.observeForever(observer)
    }

    private val observer = Observer<List<ListStoryItem>> {
        stories = it
        AppWidgetManager.getInstance(context).notifyAppWidgetViewDataChanged(
            AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, StoryWidget::class.java)),
            R.id.stack_view
        )
    }

    private fun fetchStories() {
        if (stories.isEmpty()) {
            viewModel.getStoriesWithLocation()
        }
    }
}


