
package com.example.coffeeshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.coffeeshop.model.CategoryModel
import com.example.coffeeshop.model.Order
import com.example.coffeeshop.model.ItemsModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainViewModel : ViewModel() {
    private val firebaseDatabase = FirebaseDatabase.getInstance()


    private val _category = MutableLiveData<MutableList<CategoryModel>>()
    private val _popular = MutableLiveData<MutableList<ItemsModel>>()
    private val _offer = MutableLiveData<MutableList<ItemsModel>>()

    val category: LiveData<MutableList<CategoryModel>> = _category
    val popular: LiveData<MutableList<ItemsModel>> = _popular
    val offer: LiveData<MutableList<ItemsModel>> = _offer

    fun loadCategory() {
        val ref = firebaseDatabase.getReference("Category")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<CategoryModel>()

                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(CategoryModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _category.value = lists
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun loadPopular() {
        val ref = firebaseDatabase.getReference("Items")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()

                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _popular.value = lists
            }


            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
    private val _orders = MutableLiveData<MutableList<Order>>()
    val orders: LiveData<MutableList<Order>> = _orders



        fun loadOrders() {
            val ref = firebaseDatabase.getReference("Orders")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lists = mutableListOf<Order>()
                    for (childSnapshot in snapshot.children) {
                        val order = childSnapshot.getValue(Order::class.java)
                        if (order != null) {
                            lists.add(order)
                        } else {
                            println("Error: Failed to map snapshot to Order class.")
                        }
                    }
                    if (lists.isEmpty()) {
                        println("No orders found in the database.")
                    }
                    _orders.value = lists
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Firebase Error: ${error.message}")
                }
            })
        }



    fun loadOffer() {
        val ref = firebaseDatabase.getReference("Offers")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<ItemsModel>()

                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(ItemsModel::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                _offer.value = lists
            }


            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}
