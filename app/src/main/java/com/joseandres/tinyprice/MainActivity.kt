package com.joseandres.tinyprice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joseandres.tinyprice.ui.theme.TinypriceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TinypriceTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    TinyPriceApp()
                }
            }
        }
    }
}

@Composable
fun TinyPriceApp(viewModel: TinyPriceViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var query by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("¿Qué producto buscas?") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { viewModel.search(query) },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Buscar Ofertas")
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is UiState.Idle -> Text("Escribe un producto y presiona buscar.")
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            is UiState.Success -> {
                LazyColumn {
                    items(state.offers) { offer ->
                        OfferCard(offer)
                    }
                }
            }
            is UiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun OfferCard(offer: Offer) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = offer.store, style = MaterialTheme.typography.labelMedium)
            Text(text = offer.productName, style = MaterialTheme.typography.titleMedium)
            Text(text = offer.price, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Text(text = offer.summary, style = MaterialTheme.typography.bodyMedium)
        }
    }
}