package com.example.pokedexdemo

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class PokemonViewModel : ViewModel() {
    private val _title = mutableStateOf("")
    val title: MutableState<String> = _title

    private val _description = mutableStateOf("")
    val description: MutableState<String> = _description

    // Lista de Pokémon
    private val _pokemonList = mutableStateOf<List<MainActivity.PokemonResult>>(emptyList())
    val pokemonList: State<List<MainActivity.PokemonResult>> = _pokemonList


    fun setTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun setDescription(newDescription: String) {
        _description.value = newDescription
    }

}
