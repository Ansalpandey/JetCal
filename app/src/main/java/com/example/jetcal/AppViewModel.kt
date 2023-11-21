package com.example.jetcal

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AppViewModel : ViewModel() {
  private val mFirstNumber: MutableStateFlow<Double?> = MutableStateFlow(null)
  val firstNumber = mFirstNumber.asStateFlow()
  private val mSecondNumber: MutableStateFlow<Double?> = MutableStateFlow(null)
  val secondNumber = mSecondNumber.asStateFlow()

  private val _action: MutableStateFlow<String?> = MutableStateFlow("")
  val mAction = _action.asStateFlow()

  fun setFirstNum(input: Double) {
    mFirstNumber.update { input }
  }

  fun setSecondNum(input: Double) {
    mSecondNumber.update { input }
  }

  fun setAction(action: String) {
    _action.update { action }
  }

  fun resetAll() {
    _action.update { "" }
    mFirstNumber.update { null }
    mSecondNumber.update { null }
  }

  fun getResult(): Double {
    return when (_action.value) {
      "-" -> {
        mFirstNumber.value!! - mSecondNumber.value!!
      }
      "+" -> {
        mFirstNumber.value!! + mSecondNumber.value!!
      }
      "/" -> {
        mFirstNumber.value!! / mSecondNumber.value!!
      }
      "x" -> {
        mFirstNumber.value!! * mSecondNumber.value!!
      }
      else -> {
        0.0
      }
    }
  }
}
