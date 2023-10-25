package com.example.pokedexdemo

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class PokemonViewModel : ViewModel() {
    private val _title = mutableStateOf("")
    val title: MutableState<String> = _title

    private val _description = mutableStateOf("")
    val description: MutableState<String> = _description

    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun setDescription(newDescription: String) {
        _description.value = newDescription
    }
}