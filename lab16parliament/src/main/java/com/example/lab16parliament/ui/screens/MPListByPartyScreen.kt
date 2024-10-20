package com.example.lab16parliament.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.lab16parliament.data.MP
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lab16parliament.repository.MPRepository
import com.example.lab16parliament.ui.viewmodels.MPListViewModel
import com.example.lab16parliament.ui.viewmodels.MPListViewModelFactory


/**
 * Miro Saarinen
 * 21/10/2024
 * Screen for displaying the list of MPs by party.
 */
@Composable
fun MPListByPartyScreen(
    navController: NavHostController,
    repository: MPRepository,
    party: String
) {

    val viewModel: MPListViewModel = viewModel(
        factory = MPListViewModelFactory(repository, party)
    )

    LaunchedEffect(Unit) {
        viewModel.refreshMPs()  // Refresh MPs when the screen is displayed
    }

    val mps by viewModel.mps.collectAsState()

    LazyColumn {
        items(mps, key = { it.hetekaId }) { mp ->
            MPListItem(mp, navController)
        }
    }
}

@Composable
fun MPListItem(mp: MP, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("mp_detail/${mp.hetekaId}")
            }
            .padding(16.dp)
    ) {
        Column {
            Text(text = "${mp.firstname} ${mp.lastname}")
            Text(text = "Party: ${mp.party}")
        }
    }
}

