package com.tps.challenge.features.storefeed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tps.challenge.R
import com.tps.challenge.network.model.StoreResponse

/**
 * A RecyclerView.Adapter to populate the screen with a store feed.
 */
class StoreFeedAdapter(private val onStoreClicked: (String) -> Unit) :
    RecyclerView.Adapter<StoreItemViewHolder>() {

    private val stores = mutableListOf<StoreResponse>()

    fun loadStores(newStores: List<StoreResponse>) {
        stores.clear()
        stores.addAll(newStores)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemViewHolder {
        return StoreItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_store, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StoreItemViewHolder, position: Int) {
        holder.bind(stores[position], onStoreClicked)
    }

    override fun getItemCount(): Int = stores.size
}

/**
 * Holds the view for the Store item.
 */
class StoreItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val nameTextView: TextView = itemView.findViewById(R.id.name)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.description)
    private val etaTextView: TextView = itemView.findViewById(R.id.eta)
    private val coverImageView: ImageView = itemView.findViewById(R.id.storeImage)


    fun bind(storeResponse: StoreResponse, onStoreClicked: (String) -> Unit) {
        nameTextView.text = storeResponse.name
        descriptionTextView.text = storeResponse.description
        etaTextView.text = storeResponse.status

        itemView.setOnClickListener {
            onStoreClicked(storeResponse.id)
        }

        Glide.with(itemView.context)
            .load(storeResponse.coverImgUrl)
            .centerCrop()
            .into(coverImageView)
    }
}
