package com.example.coffeeshop.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coffeeshop.databinding.ItemOrderBinding
import com.example.coffeeshop.model.Order
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance("https://coffeeshop1721-default-rtdb.firebaseio.com/").reference

        fun bind(order: Order) {
            // Display the date
            binding.tvOrderDate.text = "Order Date: ${order.orderDate}"

            // Display the address
            binding.tvAddress.text = "Address: ${order.address}"

            // Display the total price with proper formatting
            binding.tvTotalPrice.text = "Total: $${String.format("%.2f", order.totalPrice)}"

            // Display the product names joined by commas
            val productsList = order.productNames.filterNotNull().joinToString(", ")
            binding.tvProductDetails.text = "Products: $productsList"

            fetchOrderStatus(order)
            // Display the order status
        }

        private fun fetchOrderStatus(order: Order) {
            if (order.orderId.isEmpty()) {
                binding.tvOrderStatus.text = "Status: ${order.status}"
                return
            }

            databaseRef.child("Orders").child(order.orderId).child("status")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val status = snapshot.getValue(String::class.java) ?: order.status
                        binding.tvOrderStatus.text = "Status: $status"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        binding.tvOrderStatus.text = "Status: Error"
                    }
                })
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