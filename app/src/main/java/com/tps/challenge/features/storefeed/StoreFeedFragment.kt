package com.tps.challenge.features.storefeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tps.challenge.R
import com.tps.challenge.TCApplication
import com.tps.challenge.ViewModelFactory
import com.tps.challenge.core.ui.UiState
import com.tps.challenge.databinding.FragmentStoreFeedBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Displays the list of Stores with its title, description and the cover image to the user.
 */
class StoreFeedFragment : Fragment() {
    companion object {
        const val TAG = "StoreFeedFragment"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<StoreFeedViewModel>

    private val viewModel: StoreFeedViewModel by lazy {
        viewModelFactory.get<StoreFeedViewModel>(
            requireActivity()
        )
    }

    private var _binding: FragmentStoreFeedBinding? = null
    private val binding get() = _binding!!

    private lateinit var storeFeedAdapter: StoreFeedAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        TCApplication.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when(state) {
                    is UiState.Loading -> {
                        binding.swipeContainer.isRefreshing = true
                    }
                    is UiState.Success -> {
                        binding.swipeContainer.isRefreshing = false
                        storeFeedAdapter.loadStores(state.data)
                    }
                    is UiState.Error -> {
                        binding.swipeContainer.isRefreshing = false
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupViews() {
        binding.swipeContainer.isEnabled = true
        binding.swipeContainer.setOnRefreshListener {
            viewModel.processIntent(StoreFeedViewModel.StoreFeedIntent.RefreshStores)
        }

//        storeFeedAdapter = StoreFeedAdapter { }

        binding.storesView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = storeFeedAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
