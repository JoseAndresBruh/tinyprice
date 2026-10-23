package com.joseandres.tinyprice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class Success(val offers: List<Offer>) : UiState()
    data class Error(val message: String) : UiState()
}

class TinyPriceViewModel : ViewModel() {
    private val repository = OffersRepository()
    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val anonKey = "sb_publishable_LIvEAfxFvFSOAxKXzs008w_-pvbEASj"

    fun search(query: String) {
        if (query.isBlank()) return
        _uiState.update { UiState.Loading }

        // Lo ejecutamos en un hilo secundario para no congelar la pantalla
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val results = repository.searchOffers(query, anonKey)
                _uiState.update { UiState.Success(results) }
            } catch (e: Exception) {
                _uiState.update { UiState.Error("Error: ${e.message}") }
            }
        }
    }
}