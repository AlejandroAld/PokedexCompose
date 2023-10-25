package com.example.pokedexdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : ComponentActivity() {
    private val viewModel: PokemonViewModel by viewModels()
    private val addPokemonLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Se ha agregado un nuevo Pokémon, reinicia la actividad principal
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexApp()
        }
    }

    @Composable
    fun PokedexApp() {
        val viewModel: PokemonViewModel = viewModel


        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Pokedex",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Button(
                onClick = {
                    // Lanza la actividad para agregar un Pokémon
                    addPokemonLauncher.launch(Intent(this@MainActivity, AddPokemonActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Añadir")
            }

            PokemonListScreen()
        }
    }

    interface PokemonApiServicePoke {
        @GET("pokemon")
        suspend fun getPokemonList(
            @Query("limit") limit: Int,
            @Query("offset") offset: Int
        ): Response<PokemonListResponse>
    }

    data class PokemonListResponse(
        val results: List<PokemonResult>
    )

    data class PokemonResult(
        val name: String,
        val url: String
    )

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val PokemonApiServicePo: PokemonApiServicePoke = retrofit.create(PokemonApiServicePoke::class.java)

    val retrofit2 = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/api/post/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val pokemonApiService: PokemonApiService = retrofit2.create(PokemonApiService::class.java)

    @OptIn(ExperimentalCoilApi::class)
    @Composable
    fun PokemonListScreen() {
        var pokemonList by remember { mutableStateOf(emptyList<PokemonResult>()) }

        LaunchedEffect(Unit) {
            fetchPokemonList { newList ->
                pokemonList = newList
            }
        }

        var pokemonListApi by remember { mutableStateOf(emptyList<PokemonApi>()) }
        LaunchedEffect(Unit) {
            fetchPokemonListAPI { newList ->
                pokemonListApi = newList
            }
        }

        Column {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(pokemonListApi) { pokemon ->
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = pokemon.title)
                        Text(text = pokemon.content)
                    }
                }
                items(pokemonList) { pokemon ->
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val imageUrl = getPokemonImageUrl(pokemon)
                        val customPainter = rememberImagePainter(data = imageUrl)
                        Image(
                            painter = customPainter,
                            contentDescription = null,
                            modifier = Modifier.size(120.dp)
                        )
                        Text(text = pokemon.name)
                    }
                }
            }
        }
    }

    private suspend fun fetchPokemonList(onSuccess: (List<PokemonResult>) -> Unit) {
        val response = PokemonApiServicePo.getPokemonList(limit = 20, offset = 0)
        if (response.isSuccessful) {
            onSuccess(response.body()?.results ?: emptyList())
        }
    }

    private suspend fun fetchPokemonListAPI(onSuccess: (List<PokemonApi>) -> Unit) {
        val response = pokemonApiService.getPokemon()
        if (response.isSuccessful) {
            val pokemonList = response.body() ?: emptyList()
            onSuccess(pokemonList)
        }
    }


    private fun getPokemonImageUrl(pokemon: PokemonResult): String {
        val id = pokemon.url.split("/").dropLast(1).last()
        return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
    }

}


