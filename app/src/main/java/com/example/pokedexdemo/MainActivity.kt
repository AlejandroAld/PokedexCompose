package com.example.pokedexdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokedexApp() // Utiliza el composable PokedexApp en lugar de PokemonListScreen
        }
    }
}

@Composable
fun PokedexApp() {
    // Utiliza un Row para colocar el título "Pokedex"
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
                style = MaterialTheme.typography.headlineLarge, // Puedes ajustar el estilo según tus preferencias
                modifier = Modifier.padding(8.dp)
            )
        }

        // El contenido principal de la aplicación, incluyendo la lista de Pokémon
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            PokemonListScreen()
        }
    }
}


interface PokemonApiService {
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

val pokemonApiService: PokemonApiService = retrofit.create(PokemonApiService::class.java)

@Composable
fun PokemonListScreen() {
    var pokemonList by remember { mutableStateOf(emptyList<PokemonResult>()) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        fetchPokemonList { newList ->
            pokemonList = newList
        }
    }

    Column {
        BasicTextField(
            value = searchText,
            onValueChange = {
                searchText = it
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Handle search or filtering here
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // Muestra 2 columnas
            contentPadding = PaddingValues(16.dp)
        ) {
            items(pokemonList) { pokemon ->
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val imageUrl = getPokemonImageUrl(pokemon)
                    val customPainter = rememberImagePainter(data = imageUrl)
                    Image(
                        painter = customPainter,
                        contentDescription = null, // Deja que Coil maneje la descripción
                        modifier = Modifier.size(120.dp)
                    )
                    Text(text = pokemon.name)
                }
            }
        }
    }
}

private suspend fun fetchPokemonList(onSuccess: (List<PokemonResult>) -> Unit) {
    val response = pokemonApiService.getPokemonList(limit = 100, offset = 0)
    if (response.isSuccessful) {
        onSuccess(response.body()?.results ?: emptyList())
    }
}



private fun getPokemonImageUrl(pokemon: PokemonResult): String {
    val id = pokemon.url.split("/").dropLast(1).last()
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}