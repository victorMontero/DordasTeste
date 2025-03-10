package com.tps.challenge.features.storedetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.tps.challenge.R
import com.tps.challenge.TCApplication
import com.tps.challenge.ViewModelFactory
import com.tps.challenge.core.ui.UiState
import com.tps.challenge.network.model.StoreDetailsResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Displays detailed information about a specific store.
 */
class StoreDetailsFragment : Fragment() {
    companion object {
        const val TAG = "StoreDetailsFragment"
        const val ARG_STORE_ID = "storeId"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<StoreDetailsViewModel>

    private val viewModel: StoreDetailsViewModel by lazy {
        viewModelFactory.get<StoreDetailsViewModel>(requireActivity())
    }

    private lateinit var storeImage: ImageView
    private lateinit var storeName: TextView
    private lateinit var storeDescription: TextView
    private lateinit var storeAddress: TextView
    private lateinit var storePhone: TextView
    private lateinit var deliveryEta: TextView
    private lateinit var deliveryFee: TextView
    private lateinit var tagsChipGroup: ChipGroup
    private lateinit var progressBar: ProgressBar

    private val args: StoreDetailsFragmentArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        TCApplication.getAppComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_store_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeViews(view)

        viewModel.processIntent(StoreDetailsViewModel.StoreDetailsIntent.LoadStoreDetails(args.storeId))
        observeViewModel()
    }

    private fun initializeViews(view: View) {
        storeImage = view.findViewById(R.id.store_image)
        storeName = view.findViewById(R.id.store_name)
        storeDescription = view.findViewById(R.id.store_description)
        storeAddress = view.findViewById(R.id.store_address)
        storePhone = view.findViewById(R.id.store_phone)
        deliveryEta = view.findViewById(R.id.delivery_eta)
        deliveryFee = view.findViewById(R.id.delivery_fee)
        tagsChipGroup = view.findViewById(R.id.tags_chip_group)
        progressBar = view.findViewById(R.id.progress_bar)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        showLoading(true)
                    }

                    is UiState.Success -> {
                        showLoading(false)
                        updateUI(state.data)
                    }

                    is UiState.Error -> {
                        showLoading(false)
                        showError(state.message)
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    private fun updateUI(storeDetails: StoreDetailsResponse) {
        // Populate views with store details
        storeName.text = storeDetails.name
        storeDescription.text = storeDetails.description
        storeAddress.text = storeDetails.address.printableAddress
        storePhone.text = storeDetails.phoneNumber
        deliveryEta.text = storeDetails.deliveryEta

        // Format delivery fee from cents to dollars
        val feeInDollars = storeDetails.deliveryFeeCents / 100.0
        deliveryFee.text = String.format("$%.2f delivery", feeInDollars)

        // Load image
        Glide.with(requireContext())
            .load(storeDetails.coverImgUrl)
            .centerCrop()
            .into(storeImage)

        // Add tags as chips
        tagsChipGroup.removeAllViews()
        storeDetails.tags.forEach { tag ->
            val chip = Chip(requireContext())
            chip.text = tag
            tagsChipGroup.addView(chip)
        }
    }
}