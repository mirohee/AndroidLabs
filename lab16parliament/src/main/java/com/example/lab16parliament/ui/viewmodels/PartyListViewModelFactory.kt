package com.example.lab16parliament.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab16parliament.repository.MPRepository

/**
 * Miro Saarinen
 * 21/10/2024
 * Factory for creating a [PartyListViewModel] with a constructor that takes a [MPRepository].
 */
class PartyListViewModelFactory(
    private val repository: MPRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PartyListViewModel::class.java)) {
            return PartyListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}