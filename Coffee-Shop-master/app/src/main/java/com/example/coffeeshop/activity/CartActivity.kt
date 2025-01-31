package com.example.coffeeshop.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coffeeshop.adapter.CartAdapter
import com.example.coffeeshop.databinding.ActivityCartBinding
import com.example.coffeeshop.helper.ChangeNumberItemsListener
import com.example.coffeeshop.helper.ManagmentCart
import com.example.coffeeshop.model.CartItem


class CartActivity : BaseActivity() {

    lateinit var management: ManagmentCart
    private var tax: Double = 0.0
    private val binding: ActivityCartBinding by lazy {
        ActivityCartBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        management = ManagmentCart(this)

        calculateCart()  // To initialize the cart values
        setVariable()
        initCartList()

        // SetOnClickListener for proceedCheckoutBtn
        binding.proceedCheckoutBtn.setOnClickListener {
            val totalPrice = calculateTotalPrice()  // Calculate total price

            // Pass the total price to PlaceOrderActivity
            val intent = Intent(this, PlaceOrderActivity::class.java)
            intent.putExtra("TOTAL_PRICE", totalPrice) // Pass total price
            startActivity(intent)
        }
        binding.proceedCheckoutBtn.setOnClickListener {
            val totalPrice = calculateTotalPrice()

            // Map your cart items to the CartItem class
            val cartDetails = management.getListCart().map {
                CartItem(productName = it.title, quantity = it.numberInCart)
            }

            // Create an intent and pass the Parcelable ArrayList
            val intent = Intent(this, PlaceOrderActivity::class.java)
            intent.putExtra("TOTAL_PRICE", totalPrice)
            intent.putParcelableArrayListExtra("CART_DETAILS", ArrayList(cartDetails)) // Pass Parcelable ArrayList
            startActivity(intent)
        }

    }

    private fun initCartList() {
        with(binding) {
            rvCartView.layoutManager =
                LinearLayoutManager(this@CartActivity, LinearLayoutManager.VERTICAL, false)
            rvCartView.adapter = CartAdapter(
                management.getListCart(),
                this@CartActivity,
                object : ChangeNumberItemsListener {
                    override fun onChanged() {
                        calculateCart()
                    }
                })
        }
    }

    private fun setVariable() {
        binding.ivBack.setOnClickListener { finish() }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateCart() {
        val percentTax = 0.02
        val delivery = 15.0
        tax = Math.round((management.getTotalFee() * percentTax) * 100) / 100.0
        val total = Math.round((management.getTotalFee() + tax + delivery) * 100) / 100
        val itemTotal = Math.round(management.getTotalFee() * 100) / 100

        with(binding) {
            subTotalPriceTxt.text = "$$itemTotal"
            totalTaxPriceTxt.text = "$$tax"
            deliveryPriceTxt.text = "$$delivery"
            totalPriceTxt.text = "$$total"
        }
    }

    // Function to calculate total price
    private fun calculateTotalPrice(): Double {
        val percentTax = 0.02
        val delivery = 15.0
        tax = Math.round((management.getTotalFee() * percentTax) * 100) / 100.0
        val total = Math.round((management.getTotalFee() + tax + delivery) * 100) / 100.0
        return total
    }
}


