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
            val data = result.data
            val pokemonAdded = data?.getBooleanExtra("pokemon_added", false) ?: false
            if (pokemonAdded) {
                // Realiza una acción después de agregar el Pokémon
            }
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

        val title = viewModel.title.value
        val description = viewModel.description.value

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
                    viewModel.setTitle("Nuevo título")
                    viewModel.setDescription("Nueva descripción")
                    // Lanza la actividad para agregar un Pokémon
                    addPokemonLauncher.launch(Intent(this@MainActivity, AddPokemonActivity::class.java))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Añadir")
            }


            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                PokemonListScreen()
            }
        }
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

@OptIn(ExperimentalCoilApi::class)
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
            columns = GridCells.Fixed(2),
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
    val response = PokemonApiServicePo.getPokemonList(limit = 100, offset = 0)
    if (response.isSuccessful) {
        onSuccess(response.body()?.results ?: emptyList())
    }
}



private fun getPokemonImageUrl(pokemon: PokemonResult): String {
    val id = pokemon.url.split("/").dropLast(1).last()
    return "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
}