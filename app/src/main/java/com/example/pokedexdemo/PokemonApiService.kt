package com.example.pokedexdemo

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PokemonApiService {
    @GET(".")
    suspend fun getPokemon(): Response<List<PokemonApi>>

    @POST(".")
    fun createPokemon(@Body pokemon: Pokemon?): Call<Pokemon?>
}
