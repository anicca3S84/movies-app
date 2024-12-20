package com.codework.movies_app.viewmodes

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codework.movies_app.data.ApiResponse
import com.codework.movies_app.data.Episode
import com.codework.movies_app.data.FilmDetail
import com.codework.movies_app.data.Item
import com.codework.movies_app.network.MarsApi
import com.codework.movies_app.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.contracts.Effect

class MediaPlayerViewModel: ViewModel() {
    private val _episodes = MutableStateFlow<Resource<List<Episode>>>(Resource.Unspecified())
    val episodes = _episodes.asStateFlow()

    private val _suggestFilms = MutableStateFlow<Resource<ApiResponse>>(Resource.Loading())
    val suggestFilm = _suggestFilms.asStateFlow()

    private val _filmDetail = MutableStateFlow<Resource<FilmDetail>>(Resource.Loading())
    val filmDetail = _filmDetail.asStateFlow()

    val slug = MutableLiveData<String>()

    init {
        getListSuggest("series")
        slug.observeForever { newSlug ->
            newSlug?.let {
                getListEpisodes(it)
                Log.d("getListEpisodes", it)
                getFilmDetail(it)
            }
        }
    }

    private fun getFilmDetail(slug: String) {
        viewModelScope.launch {
            _filmDetail.emit(Resource.Loading())
        }
        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.getFilmDetail(slug)
                _filmDetail.emit(Resource.Success(response))
            }catch (e: Exception){
                _filmDetail.emit(Resource.Error(e.message.toString()))
            }            }
    }

    private fun getListSuggest(type: String) {
        viewModelScope.launch {
            _suggestFilms.emit(Resource.Loading())
        }

        viewModelScope.launch {
            try {
                val response = MarsApi.retrofitService.getFilmsByType(type)
                _suggestFilms.emit(Resource.Success(response))
            } catch (e: Exception){
                _suggestFilms.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun getListEpisodes(slug: String) {
        viewModelScope.launch {
            _episodes.emit(Resource.Loading())
        }
        viewModelScope.launch {
            try {
                Log.d("ListEpisodes", "Fetching episodes with slug: $slug")
                val response = MarsApi.retrofitService.getLisEpisodesBySlug(slug)

                _episodes.emit(Resource.Success(response))
                Log.d("ListEpisodes",response.toString())
            } catch (e: Exception){
                _episodes.emit(Resource.Error(e.message.toString()))
                Log.d("ListEpisodes",e.message.toString())
            }
        }
    }


}