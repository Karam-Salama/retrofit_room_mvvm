package com.example.mvvm_meal_db.features.favorites.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mvc_meal_db.core.model.CategoryListener
import com.example.mvc_meal_db.core.model.CategoryModel
import com.example.mvc_meal_db.core.model.cache.CategoryDatabase
import com.example.mvc_meal_db.core.model.server.RetrofitHelper
import com.example.mvvm_meal_db.R
import com.example.mvvm_meal_db.core.ViewModel.MainViewModel
import com.example.mvvm_meal_db.core.ViewModel.MyFactory
import com.example.mvvm_meal_db.core.view.CategoryAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class FavoritesActivity : AppCompatActivity() , CategoryListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites)

        initViews()
        setUpViewModel()

        lifecycleScope.launch {
            mainViewModel.fetchFavorites()
        }

        mainViewModel.categories.observe(this){
            adapter.categories = it
            adapter.notifyDataSetChanged()
        }
        mainViewModel.message.observe(this){
            Snackbar.make(recyclerView, it , Snackbar.LENGTH_SHORT).show()
        }


    }

    private fun initViews() {
        recyclerView = findViewById(R.id.rc_favorite)
        adapter = CategoryAdapter(listOf(), recyclerView.context, this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setUpViewModel() {
        var dao = CategoryDatabase.getDatabase(this).getCategoryDao()
        var myFactory = MyFactory(dao, RetrofitHelper)
        mainViewModel = ViewModelProvider(this, myFactory).get(MainViewModel::class.java)
    }

    override fun onCategoryClick(category: CategoryModel) {
        lifecycleScope.launch {
            mainViewModel.removeCategory(category)
        }
    }
}