package com.example.coffeeshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.databinding.ItemOrderBinding
import com.example.coffeeshop.model.Order

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            // Display only the required fields: address, totalPrice, and product details
            binding.tvAddress.text = "Address: ${order.address}"
            binding.tvTotalPrice.text = "Total: $${order.totalPrice}"

            // Display the product names joined by commas
            binding.tvProductDetails.text = "Products: ${order.productNames.joinToString(", ")}" // Assuming a TextView with id 'tvProductDetails' in the layout
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size
}