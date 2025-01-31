package com.example.coffeeshop.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.adapter.OrderAdapter
import com.example.coffeeshop.databinding.ActivityOrderViewBinding
import com.example.coffeeshop.viewmodel.MainViewModel

class OrderActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityOrderViewBinding
    private lateinit var adapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inflate the layout using ViewBinding
        binding = ActivityOrderViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        adapter = OrderAdapter(emptyList())
        binding.rvOrders.adapter = adapter

        // Observe orders LiveData from the ViewModel
        mainViewModel.orders.observe(this) { orderList ->
            if (!orderList.isNullOrEmpty()) {
                // Update adapter with the fetched orders
                adapter = OrderAdapter(orderList)
                binding.rvOrders.adapter = adapter
            } else {
                println("No orders found!")
            }
        }

        // Trigger data loading
        mainViewModel.loadOrders()

    }
}
