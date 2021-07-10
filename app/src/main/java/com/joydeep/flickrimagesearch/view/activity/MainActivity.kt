package com.joydeep.flickrimagesearch.view.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.joydeep.flickrimagesearch.databinding.ActivityMainBinding
import com.joydeep.flickrimagesearch.utils.ConnectivityWatcher
import com.joydeep.flickrimagesearch.utils.hideKeyBoard
import com.joydeep.flickrimagesearch.view.adapter.ImageLoadStateAdapter
import com.joydeep.flickrimagesearch.view.adapter.ImagePagingAdapter
import com.joydeep.flickrimagesearch.viewmodel.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val pagingAdapter = ImagePagingAdapter()

    private val viewModel by viewModels<MainActivityViewModel>()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ConnectivityWatcher(this).observe(this) {
            if (it == false) {
                Snackbar.make(binding.root, "No Internet Available", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.retryButton.setOnClickListener {
            pagingAdapter.retry()
        }

        initAdapter()
        initTextInput()
    }

    private fun initAdapter() {
        binding.recyclerView.apply {
            layoutManager =
                GridLayoutManager(this@MainActivity, 2, GridLayoutManager.VERTICAL, false)
            adapter = pagingAdapter
            itemAnimator = DefaultItemAnimator()
            adapter = pagingAdapter.withLoadStateFooter(
                footer = ImageLoadStateAdapter { pagingAdapter.retry() }
            )

            lifecycleScope.launchWhenStarted {
                viewModel.currentSearchResult.collectLatest {
                    pagingAdapter.submitData(it)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            pagingAdapter.loadStateFlow
                .distinctUntilChangedBy { it.refresh }
                .filter { it.refresh is LoadState.NotLoading }
                .collect {
                    if (viewModel.scrollToTop) {
                        binding.recyclerView.scrollToPosition(0)
                        viewModel.scrollToTop = false
                    }
                }
        }

        pagingAdapter.addLoadStateListener { loadState ->
            val showPlaceholder =
                loadState.refresh is LoadState.NotLoading && pagingAdapter.itemCount == 0
            showPlaceholder(showPlaceholder)

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

    private fun showPlaceholder(show: Boolean) {
        binding.noDataView.isVisible = show
        binding.recyclerView.isVisible = !show
    }

    override fun onBackPressed() {
        if (binding.recyclerView.computeVerticalScrollOffset() != 0) {
            binding.recyclerView.smoothScrollToPosition(0)
            return
        }
        super.onBackPressed()
    }

    private fun initTextInput() {
        viewModel.currentQueryString.observe(this) {
            binding.searchInput.setText(it ?: "")
        }

        binding.searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchInput.hideKeyBoard(this)
                searchWithQuery()
                true
            } else {
                false
            }
        }
    }

    private fun searchWithQuery() {
        binding.searchInput.text?.trim()?.let {
            if (it.isNotEmpty()) {
                viewModel.getImages(it.toString())
            }
        }
    }
}