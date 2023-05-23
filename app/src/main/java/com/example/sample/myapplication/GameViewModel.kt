package com.example.sample.myapplication

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private var _count = DATASET_COUNT;
    private var _data = Array<String?>(_count) { "This is element #$it" }
    //private val _dataFragment = Array<Fragment>(2) {ListFragment(); GridFragment() }
    private val _dataFragment = arrayOf(ListFragment(), GridFragment())

    //private var _data = {}
    val count: Int
        get() = _count

    val data: Array<String?>
        get() = _data

    private val _status = MutableLiveData<String>()

    val status: LiveData<String> = _status

    init {
        getMarsPhotos()
    }

    companion object {
        private const val DATASET_COUNT = 12
    }

    private fun getMarsPhotos() {
        _status.value = "Set the Mars API status response here!"
    }
}