package com.codework.movies_app.viewmodes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.data.ApiResponse
import com.codework.movies_app.data.Item
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class ItemViewModel : ViewModel() {
    private val _itemResponse = MutableLiveData<ApiResponse>()
    val itemResponse : LiveData<ApiResponse> get() = _itemResponse
    private val _searchResponse = MutableLiveData<List<Item>>()
    val searchResponse : LiveData<List<Item>> get() = _searchResponse
    private val _seriesResponse = MutableLiveData<ApiResponse>()
    val seriesResponse : LiveData<ApiResponse> get() = _seriesResponse
    private var isDataLoaded = false
    private val _categoriesResponse = MutableLiveData<ApiResponse>()
    val categoriesResponse : LiveData<ApiResponse> get() = _categoriesResponse
    fun getNewFilms(
        page: Int,
        pageSize: Int) {
        viewModelScope.launch {
            try {
                if(!isDataLoaded) {
                    val response = MarsApi.retrofitService.getNewFilms(page, pageSize)
                    _itemResponse.value = response
                    Log.d("getNewFilms", response.items.toString())
//                    isDataLoaded = true
                    Log.d("isDataLoaded", isDataLoaded.toString())
                }

            } catch (e: Exception) {
                Log.d("getNewFilms", e.message.toString())
            }
        }
    }
    fun searchItems(query: String) {
        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.getSearchFilms(query)
                _searchResponse.value = response
                Log.d("getSearchFilms", response.toString())
            } catch (e: Exception) {
                Log.d("getSearchFilms", e.message.toString())
            }
        }
    }
    fun getSeries(type: String) {
        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.getFilmsByType(type)
                _seriesResponse.value = response
                Log.d("getSeriesFilms", response.toString())
            } catch (e: Exception) {
                Log.d("getSeriesFilms", e.message.toString())
            }
        }
    }
    fun getCategories(category: String) {
        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.getFilmsByCategory(category)
                _categoriesResponse.value = response
                Log.d("getCategoriesFilms", response.toString())
            } catch( e: Exception) {
                Log.d("getCategoriesFilms", e.message.toString())
            }
        }
    }
    fun isDataLoaded(): Boolean {
        return isDataLoaded
    }
    fun changeData() {
        isDataLoaded = !isDataLoaded
    }
}