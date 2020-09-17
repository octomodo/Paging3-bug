package com.example.paging3bug.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.android.codelabs.paging.ui.ReposAdapter
import com.example.android.codelabs.paging.ui.ReposLoadStateAdapter
import com.example.android.codelabs.paging.ui.SearchRepositoriesViewModel
import com.example.paging3bug.Injection
import com.example.paging3bug.databinding.FragmentHomeBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: SearchRepositoriesViewModel
    private var searchJob: Job? = null
    private val adapter = ReposAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // get the view model
        viewModel = ViewModelProvider(this, Injection.provideViewModelFactory())
            .get(SearchRepositoriesViewModel::class.java)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.list.addItemDecoration(decoration)

        initAdapter()
        search(DEFAULT_QUERY)
    }

    private fun initAdapter() {
        binding.list.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ReposLoadStateAdapter { adapter.retry() },
            footer = ReposLoadStateAdapter { adapter.retry() }
        )
    }

    private fun search(query: String) {
        searchJob?.cancel()
        searchJob = lifecycleScope.launch {
            viewModel.searchRepo(query).collectLatest {
                adapter.submitData(it)
            }
        }
    }

    companion object {
        private const val DEFAULT_QUERY = "Android"
        val TAG = HomeFragment::class.java.simpleName
    }
}