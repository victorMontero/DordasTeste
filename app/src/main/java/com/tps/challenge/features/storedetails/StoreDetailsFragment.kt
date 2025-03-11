package com.tps.challenge.features.storedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.tps.challenge.R
import com.tps.challenge.TCApplication
import com.tps.challenge.ViewModelFactory
import com.tps.challenge.core.ui.UiState
import com.tps.challenge.databinding.FragmentStoreDetailsBinding
import com.tps.challenge.network.model.StoreDetailsResponse
import kotlinx.coroutines.launch
import javax.inject.Inject


class StoreDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<StoreDetailsViewModel>

    private val viewModel: StoreDetailsViewModel by lazy {
        viewModelFactory.get<StoreDetailsViewModel>(
            this
        )
    }

    private var _binding: FragmentStoreDetailsBinding? = null
    private val binding get() = requireNotNull(_binding) { "ViewBinding is accessed after onDestroyView()" }

    private val args: StoreDetailsFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        TCApplication.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoreDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        viewModel.processIntent(StoreDetailsViewModel.StoreDetailsIntent.LoadStoreDetails(args.storeId))
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.swipeDetailsContainer.isRefreshing = true
                    }
                    is UiState.Success -> {
                        binding.swipeDetailsContainer.isRefreshing = false
                        updateUi(state.data)
                    }
                    is UiState.Error -> {
                        binding.swipeDetailsContainer.isRefreshing = false
                        Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUi(storeDetails: StoreDetailsResponse) {
        with(binding) {
            swipeDetailsContainer.isEnabled = false
            storeName.text = storeDetails.name
            storeDescription.text = storeDetails.description
            deliveryStatus.text = getString(R.string.delivery_eta_format, storeDetails.deliveryEta)
            deliveryFee.text =
                getString(R.string.delivery_fee, storeDetails.deliveryFeeCents / 100.0)
            phoneNumber.text = getString(R.string.phone_store, storeDetails.phoneNumber)
            address.text = storeDetails.address.printableAddress

            Glide.with(requireContext())
                .load(storeDetails.coverImgUrl)
                .centerCrop()
                .into(coverImageDetails)

            tagsGroup.removeAllViews()
            storeDetails.tags.forEach { tag ->
                val chip = Chip(context)
                    .apply {
                        text = tag
                        isClickable = false
                    }
                tagsGroup.addView(chip)
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}