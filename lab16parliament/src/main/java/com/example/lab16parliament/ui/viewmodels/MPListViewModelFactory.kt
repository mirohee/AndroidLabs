package com.example.lab16parliament.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.lab16parliament.repository.MPRepository


/**
 * Miro Saarinen
 * 21/10/2024
 * Factory for creating a [MPListViewModel] with a constructor that takes a [MPRepository] and a party.
 */
class MPListViewModelFactory(
    private val repository: MPRepository,
    private val party: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MPListViewModel::class.java)) {
            return MPListViewModel(repository, party) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

