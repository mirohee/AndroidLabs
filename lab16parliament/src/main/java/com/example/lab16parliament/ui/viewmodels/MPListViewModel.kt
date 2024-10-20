package com.example.lab16parliament.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab16parliament.data.MP
import com.example.lab16parliament.repository.MPRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * Miro Saarinen
 * 21/10/2024
 * ViewModel for the MPListScreen.
 */
class MPListViewModel(private val repository: MPRepository, party: String) : ViewModel() {

    private val _mps = MutableStateFlow<List<MP>>(emptyList())
    val mps: StateFlow<List<MP>> = _mps

    init {
        getMPsByParty(party)
    }

    private fun getMPsByParty(party: String) {
        viewModelScope.launch {
            repository.getMPsByParty(party).collect { mpList ->
                _mps.value = mpList
            }
        }
    }

    fun refreshMPs() {
        viewModelScope.launch {
            repository.refreshMPs()
        }
    }

}
