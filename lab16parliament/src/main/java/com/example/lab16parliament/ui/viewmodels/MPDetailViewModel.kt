package com.example.lab16parliament.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab16parliament.data.MP
import com.example.lab16parliament.data.MPComment
import com.example.lab16parliament.data.MPExtras
import com.example.lab16parliament.repository.MPRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Miro Saarinen
 * 21/10/2024
 * ViewModel for the MPDetailScreen.
 */
class MPDetailViewModel(private val repository: MPRepository) : ViewModel() {

    private val _mp = MutableStateFlow<MP?>(null)
    val mp: StateFlow<MP?> = _mp

    private val _comments = MutableStateFlow<List<MPComment>>(emptyList())
    val comments: StateFlow<List<MPComment>> = _comments

    private val _mpExtras = MutableStateFlow<List<MPExtras>>(emptyList())
    val mpExtras: StateFlow<List<MPExtras>> = _mpExtras

    fun getMPById(hetekaId: Int) {
        viewModelScope.launch {
            repository.getMPById(hetekaId).collect {
                _mp.value = it
            }
        }
    }

    fun getMPComments(hetekaId: Int) {
        viewModelScope.launch {
            repository.getMPComments(hetekaId).collect { commentList ->
                _comments.value = commentList
            }
        }
    }

    fun getMpExtras(hetekaId: Int) {
        viewModelScope.launch {
            repository.getMPExtras(hetekaId).collect {
                _mpExtras.value = it
            }
        }
    }

    fun addCommentAndGrade(comment: String, grade: Float, hetekaId: Int) {
        viewModelScope.launch {
            val mpComment = MPComment(hetekaId = hetekaId, comment = comment, grade = grade)
            repository.addCommentAndGrade(mpComment)

            getMPComments(hetekaId)
        }
    }
}
