package com.example.lab16parliament.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab16parliament.repository.MPRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Miro Saarinen
 * 21/10/2024
 * ViewModel for the PartyListScreen.
 */
class PartyListViewModel(
    repository: MPRepository
) : ViewModel() {

    val partyList: StateFlow<List<String>> = repository.getParties()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

}