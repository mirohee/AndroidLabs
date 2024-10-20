package com.example.lab16parliament.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lab16parliament.repository.MPRepository
import com.example.lab16parliament.ui.viewmodels.PartyListViewModel
import com.example.lab16parliament.ui.viewmodels.PartyListViewModelFactory


/**
 * Miro Saarinen
 * 21/10/2024
 * Screen for displaying the list of parties.
 */
@Composable
fun PartyListScreen(
    navController: NavHostController,
    repository: MPRepository,
) {
    val viewModel: PartyListViewModel = viewModel(
        factory = PartyListViewModelFactory(repository)
    )

    val parties by viewModel.partyList.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Parties", modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(parties, key = { it }) { party ->
                PartyListItem(party, navController)
            }
        }
    }
}

@Composable
fun PartyListItem(party: String, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                navController.navigate("party_members/$party")
            }
            .padding(16.dp)
    ) {
        Text(
            text = party, modifier = Modifier.padding(8.dp)
        )
    }
}

