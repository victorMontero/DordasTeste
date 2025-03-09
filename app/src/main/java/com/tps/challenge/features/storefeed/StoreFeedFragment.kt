package com.tps.challenge.features.storefeed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tps.challenge.R
import com.tps.challenge.TCApplication
import com.tps.challenge.ViewModelFactory
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
    private lateinit var storeFeedAdapter: StoreFeedAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        TCApplication.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_store_feed, container, false)
        setupViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is StoreFeedViewModel.UiState.Loading -> {
                        swipeRefreshLayout.isRefreshing = true
                    }

                    is StoreFeedViewModel.UiState.Success -> {
                        swipeRefreshLayout.isRefreshing = false
                        storeFeedAdapter.loadStores(state.stores)
                    }

                    is StoreFeedViewModel.UiState.Error -> {
                        swipeRefreshLayout.isRefreshing = false
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun setupViews(view: View) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_container)
        swipeRefreshLayout.isEnabled = true
        swipeRefreshLayout.setOnRefreshListener {
            viewModel.onEvent(StoreFeedViewModel.Event.RefreshRequested)
        }

        storeFeedAdapter = StoreFeedAdapter { storeId ->
            navigateToStoreDetails(storeId)
        }

        recyclerView = view.findViewById(R.id.stores_view)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = storeFeedAdapter
        }
    }

    private fun navigateToStoreDetails(storeId: String) {
        val action = StoreFeedFragmentDirections.actionStoreFeedToStoreDetails(storeId)
        findNavController().navigate(action)
    }
}