package com.example.pokedexdemo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Callback


class AddPokemonActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AddPokemonContent()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddPokemonContent() {
        val ctx = LocalContext.current
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var response = ""


        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopAppBar(
                title = { Text(text = "Añadir Pokémon") },
                navigationIcon = {
                    IconButton(
                        onClick = { finish() }
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Manejar la acción de "Listo" según sea necesario
                        }
                    ),
                    placeholder = { Text(text = "Nombre del Pokemon") },
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Manejar la acción de "Listo" según sea necesario
                        }
                    ),
                    placeholder = { Text(text = "Añade una descripción del Pokemon") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        // Realiza una acción después de agregar el Pokémon
                        postDataUsingRetrofit(ctx, title, description, result = response)
                        finish()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Añadir")
                }

//                Spacer(modifier = Modifier.height(20.dp))
//
//                Text(
//                    text = response,
//                    color = Color.Black,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold, modifier = Modifier
//                        .padding(10.dp)
//                        .fillMaxWidth(),
//                    textAlign = TextAlign.Center
//                )


            }
        }
    }
}

private fun postDataUsingRetrofit(ctx: Context, title: String, description: String, result: String) {
    // Obtén la instancia de Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8000/api/post/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val pokemonApiService: PokemonApiService = retrofit.create(PokemonApiService::class.java)
    // passing data from our text fields to our model class.
    val dataModel = Pokemon(title, description)
    // calling a method to create an update and passing our model class.
    val call: Call<Pokemon?>? = pokemonApiService.createPokemon(dataModel)
    // on below line we are executing our method.
    call!!.enqueue(object : Callback<Pokemon?> {
        override fun onResponse(call: Call<Pokemon?>, response: Response<Pokemon?>) {
            // this method is called when we get response from our api.
            Toast.makeText(ctx, "Data posted to API", Toast.LENGTH_SHORT).show()

            // we are getting a response from our body and
            // passing it to our model class.
//            val model: Pokemon? = response.body()
            // on below line we are getting our data from model class
            // and adding it to our string.
//            result =
//                "Response Code : " + response.code() + "\n" + "User Name : " + model!!.title + "\n" + "Job : " + model!!.content
        }

        override fun onFailure(call: Call<Pokemon?>, t: Throwable)  {
            // we get error response from API.
            Toast.makeText(ctx, "Fail to post data", Toast.LENGTH_SHORT).show()
//            result = "Error found is : " + t.message
        }
    })}


