package com.scifi.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scifi.storyapp.databinding.FragmentHomeBinding
import com.scifi.storyapp.view.MainViewModelFactory
import com.scifi.storyapp.view.adapter.StoryAdapter
import com.scifi.storyapp.view.upload.UploadActivity
import com.scifi.storyapp.view.utils.InterfaceUtils
import com.scifi.storyapp.view.welcome.WelcomeActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory.getInstance(requireContext())
    }
    private lateinit var adapter: StoryAdapter
    private var isScrolledByUser: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
        setupActions()
    }

    private fun setupRecyclerView() {
        adapter = StoryAdapter()
        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = this@HomeFragment.adapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    isScrolledByUser = newState != RecyclerView.SCROLL_STATE_IDLE
                }
            })
        }
    }

    private fun setupObservers() {
        viewModel.apply {
            getSession().observe(viewLifecycleOwner) { user ->
                if (!user.isLogin) {
                    startActivity(Intent(requireContext(), WelcomeActivity::class.java))
                    requireActivity().finish()
                }
            }

            stories.observe(viewLifecycleOwner) { stories ->
                adapter.submitData(lifecycle, stories)
                if (!isScrolledByUser) {
                    binding.rvStory.scrollToPosition(0)
                }
                binding.swipeRefreshLayout.isRefreshing = false
            }

            error.observe(viewLifecycleOwner) { errorMessage ->
                binding.swipeRefreshLayout.isRefreshing = false
                InterfaceUtils.showAlert(requireContext(), errorMessage)
            }
        }
    }

    private fun setupActions() {
        binding.apply {
            btnUpload.setOnClickListener {
                startActivity(Intent(requireContext(), UploadActivity::class.java))
            }
            swipeRefreshLayout.setOnRefreshListener {
                adapter.refresh()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


