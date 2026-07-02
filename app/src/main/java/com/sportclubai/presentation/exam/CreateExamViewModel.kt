package com.sportclubai.presentation.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sportclubai.domain.model.Exam
import com.sportclubai.domain.model.ExamCriteria
import com.sportclubai.domain.repository.AuthRepository
import com.sportclubai.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CreateExamStatus {
    object Idle : CreateExamStatus()
    object Loading : CreateExamStatus()
    object Success : CreateExamStatus()
    data class Error(val message: String) : CreateExamStatus()
}

@HiltViewModel
class CreateExamViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _createStatus = MutableStateFlow<CreateExamStatus>(CreateExamStatus.Idle)
    val createStatus = _createStatus.asStateFlow()

    fun createExam(title: String, sportType: String, targetBelt: String, date: Long, criteria: List<ExamCriteria>) {
        viewModelScope.launch {
            _createStatus.value = CreateExamStatus.Loading
            try {
                val uid = authRepository.getCurrentUserId() ?: throw Exception("Not authenticated")
                val user = authRepository.getUser(uid) ?: throw Exception("User not found")
                
                val exam = Exam(
                    clubId = user.clubId,
                    title = title,
                    sportType = sportType,
                    targetBelt = targetBelt,
                    date = date,
                    criteria = criteria
                )
                
                examRepository.createOrUpdateExam(exam)
                _createStatus.value = CreateExamStatus.Success
            } catch (e: Exception) {
                _createStatus.value = CreateExamStatus.Error(e.message ?: "Failed to create exam")
            }
        }
    }
}
