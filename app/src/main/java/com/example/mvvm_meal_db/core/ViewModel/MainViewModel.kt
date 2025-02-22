package com.example.mvvm_meal_db.core.ViewModel


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mvc_meal_db.core.model.CategoryModel
import com.example.mvc_meal_db.core.model.cache.CategoryDao
import com.example.mvc_meal_db.core.model.server.RetrofitHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainViewModel (private val dao: CategoryDao, var retrofitHelper: RetrofitHelper) : ViewModel() {

    private var _categoryList: MutableLiveData<List<CategoryModel>> = MutableLiveData()
    var categories:LiveData<List<CategoryModel>> = _categoryList

    private var _message: MutableLiveData<String> = MutableLiveData()
    var message :LiveData<String> = _message

    suspend fun getCategories() {
        try {
            val response = retrofitHelper.retrofitService.getCategories()
            val categories = response.categories
            viewModelScope.launch {
                if (categories.isNullOrEmpty()){
                    _message.postValue("Couldn't Fetch Categories")
                } else {
                    val list: List<CategoryModel> = categories
                    _categoryList.postValue(list)
                }
            }
        } catch (e: Exception) {
            _message.postValue("Error Fetching Categories")
            Log.e("===>", "Error Fetching Categories", e)
        }
    }


    suspend fun saveCategories(category: CategoryModel) {
        try {
            val addingResult = dao.addCategory(category)
            if (addingResult > 0) {
                _message.postValue("Added to Favorites")
            } else {
                _message.postValue("Already in Favorites")
            }
            getCategories()
        } catch (e: Exception) {
            _message.postValue("Error Saving Categories")
            Log.e("===>", "Error Saving Categories", e)
        }
    }

    suspend fun fetchFavorites() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val list =  dao.getAllCategories()
                if (list.isNullOrEmpty()) {
                    _message.postValue("No Categories Available")
                    _categoryList.postValue(emptyList())
                } else {
                    withContext(Dispatchers.Main) {
                        _categoryList.postValue(list)
                    }
                }
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("===>", "Error Fetching Favorites", e)
            }
        }
    }


    suspend fun removeCategory(category: CategoryModel) {
        try {
            viewModelScope.launch (Dispatchers.IO){
                val result = dao.removeCategory(category)
                withContext(Dispatchers.Main){
                    if(result >0){
                        _message.postValue("Removed Successfully")
                    }else{
                        _message.postValue("Couldn't Remove")
                    }
                }
                fetchFavorites()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.e("===>", "Error Removing Category", e)
            }
        }
    }
}


class MyFactory(private val dao: CategoryDao, var retrofitHelper: RetrofitHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(dao, retrofitHelper) as T
    }
}

