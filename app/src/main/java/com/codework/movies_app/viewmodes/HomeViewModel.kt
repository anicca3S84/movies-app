package com.codework.movies_app.viewmodes

import androidx.lifecycle.ViewModel
import com.codework.movies_app.utils.Resource
import com.google.api.ResourceDescriptor.History
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel: ViewModel() {
    private val _history = MutableStateFlow<Resource<History>>(Resource.Unspecified())
    val history = _history.asStateFlow()
}