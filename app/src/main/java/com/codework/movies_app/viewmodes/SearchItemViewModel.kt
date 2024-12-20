package com.codework.movies_app.viewmodes

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.data.ApiResponse
import com.codework.movies_app.data.Item
import com.codework.movies_app.network.MarsApi
import kotlinx.coroutines.launch

class SearchItemViewModel : ViewModel(){
    private val _itemResponse = MutableLiveData<List<Item>>()
    val itemResponse : LiveData<List<Item>> get() = _itemResponse
    fun getSearchFilms(query: String) {
        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.getSearchFilms(query)
                _itemResponse.value = response
                Log.d("getSearchFilms: ", response.toString())
            } catch (e: Exception) {
                Log.d("getSearchFilms: ", e.message.toString())
            }
        }
    }
}