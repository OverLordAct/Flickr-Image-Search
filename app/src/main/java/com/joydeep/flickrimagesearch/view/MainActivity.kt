package com.joydeep.flickrimagesearch.view

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.joydeep.flickrimagesearch.databinding.ActivityMainBinding
import com.joydeep.flickrimagesearch.viewmodel.MainActivityViewModel
import com.joydeep.flickrimagesearch.viewmodel.TEST
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val LAST_SEARCH_QUERY: String = "LAST_SEARCH_QUERY"
        private const val DEFAULT_QUERY = "dogs"
    }

    private lateinit var binding: ActivityMainBinding
    private val pagingAdapter = ImageAdapter()
    private val adapter2 = ImageRecyclerAdapter(mutableListOf())
    private var searchJob: Job? = null

    private val viewModel by viewModels<MainActivityViewModel>()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TEST, "onCreate called")
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.retryButton.setOnClickListener {
            pagingAdapter.retry()
        }

        initAdapter()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        search(query)
        initSearch(query)

//        initSimpleAdapter()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(LAST_SEARCH_QUERY, binding.searchInput.text.toString().trim())
        super.onSaveInstanceState(outState)
    }

    @ExperimentalCoroutinesApi
    private fun initSimpleAdapter() {
        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchQuery = binding.searchInput.text?.trim().toString()
//                viewModel.getPhotos(searchQuery)
                viewModel.getPhotosFlow(searchQuery)
                binding.searchInput.clearFocus()
//                binding.searchInput.hideKeyboard()
                true
            } else {
                false
            }
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter2
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerView.itemAnimator = DefaultItemAnimator()

//        viewModel.photosLiveData.observe(this) {
//            adapter2.updateData(it)
//        }

        lifecycleScope.launch {
            viewModel.currentSearchResult.collectLatest {
//                adapter2.updateData(list)
                delay(1000)
                Log.d("TESTING", it.toString())
            }
        }

//        viewModel.searchResults.observe(this) {
//            adapter2.updateData(it)
//        }
    }

    private fun initAdapter() {
//        val header = ImageLoadStateAdapter { adapter.retry() }

//        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager =
            GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = pagingAdapter
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = pagingAdapter.withLoadStateFooter(
            footer = ImageLoadStateAdapter { pagingAdapter.retry() }
        )

        binding.recyclerView.apply {
            lifecycleScope.launchWhenStarted {
                viewModel.currentSearchResult.collectLatest {
                    pagingAdapter.submitData(it)
                }
            }
        }

        pagingAdapter.addLoadStateListener { loadState ->
            val isEmptyList =
                loadState.refresh is LoadState.NotLoading && pagingAdapter.itemCount == 0
            showEmptyList(isEmptyList)

//            header.loadState = loadState.mediator?.refresh?.takeIf {
//                it is LoadState.Error && adapter.itemCount > 0
//            } ?: loadState.prepend

            binding.recyclerView.isVisible =
                loadState.source.refresh is LoadState.NotLoading || loadState.mediator?.refresh is LoadState.NotLoading
            binding.retryButton.isVisible =
                loadState.mediator?.refresh is LoadState.Error && pagingAdapter.itemCount == 0

            val errorState = loadState.source.append as? LoadState.Error
                ?: loadState.source.prepend as? LoadState.Error
                ?: loadState.append as? LoadState.Error
                ?: loadState.prepend as? LoadState.Error

            errorState?.let {
                Toast.makeText(this, "Some error happened", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        binding.noDataView.isVisible = show
        binding.recyclerView.isVisible = !show
    }

    private fun search(searchString: String) {
        searchJob?.cancel()
        viewModel.getImages(searchString)
    }

//    override fun onBackPressed() {
//        if (binding.recyclerView.computeVerticalScrollOffset() != 0) {
//            binding.recyclerView.smoothScrollToPosition(0)
//            return
//        }
//        super.onBackPressed()
//    }

    private fun initSearch(query: String) {
        binding.searchInput.setText(query)

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Log.d("TESTING", "Search enter pressed")
                updateRepoListFromInput()
//                binding.searchInput.hideKeyboard()
                true
            } else {
                false
            }
        }

//        binding.searchInput.setOnKeyListener { _, keyCode, event ->
//            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
//                updateRepoListFromInput()
//                binding.searchInput.hideKeyboard()
//                true
//            } else {
//                false
//            }
//        }

        lifecycleScope.launchWhenStarted {
            pagingAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect {
                    binding.recyclerView.scrollToPosition(0)
                }
        }
    }

    private fun updateRepoListFromInput() {
        binding.searchInput.text?.trim()?.let {
            if (it.isNotEmpty()) {
                search(it.toString())
            }
        }
    }
}