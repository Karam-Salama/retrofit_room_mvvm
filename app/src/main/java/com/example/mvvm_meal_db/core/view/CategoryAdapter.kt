package com.example.mvvm_meal_db.core.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvc_meal_db.core.model.CategoryListener
import com.example.mvc_meal_db.core.model.CategoryModel
import com.example.mvvm_meal_db.R
import com.google.android.material.imageview.ShapeableImageView

class CategoryAdapter(
    var categories: List<CategoryModel>,
    var context: Context,
    var listener: CategoryListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    @SuppressLint("ResourceType")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_list_item, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = categories[position]
        holder.categoryTitle.text = currentItem.strCategory
        val description = currentItem.strCategoryDescription
            ?.split(".")
            ?.firstOrNull()
            ?.trim() ?: "No description available"
        holder.categorySubTitle.text = description
        Glide.with(context).load(currentItem.strCategoryThumb).into(holder.categoryImage)

        holder.itemView.setOnClickListener {
            listener.onCategoryClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage = itemView.findViewById<ShapeableImageView>(R.id.itemImage)
        val categoryTitle = itemView.findViewById<TextView>(R.id.itemTitle)
        val categorySubTitle = itemView.findViewById<TextView>(R.id.itemSubTitle)
    }
}
