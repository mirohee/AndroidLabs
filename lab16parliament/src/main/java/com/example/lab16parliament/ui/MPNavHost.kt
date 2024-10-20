package com.example.lab16parliament.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lab16parliament.repository.MPRepository
import com.example.lab16parliament.ui.screens.MPDetailScreen
import com.example.lab16parliament.ui.screens.MPListByPartyScreen
import com.example.lab16parliament.ui.screens.PartyListScreen


/**
 * Miro Saarinen
 * 21/10/2024
 * NavHost for the app used to navigate between different screens.
 */
@Composable
fun MPNavHost(
    navController: NavHostController = rememberNavController(),
    repository: MPRepository,
    startDestination: String = "party_list"
) {
    NavHost(navController, startDestination) {
        composable("party_list") {
            PartyListScreen(navController = navController, repository = repository)
        }
        composable("party_members/{party}") { backStackEntry ->
            val party = backStackEntry.arguments?.getString("party") ?: ""
            MPListByPartyScreen(party = party, navController = navController, repository = repository)
        }
        composable("mp_detail/{hetekaId}") { backStackEntry ->
            val mpIdString = backStackEntry.arguments?.getString("hetekaId") ?: return@composable
            val mpId = mpIdString.toIntOrNull()
            if (mpId != null) {
                MPDetailScreen(mpId, navController, repository)
            } else {
                Log.e("MPNavHost", "Invalid mpId: $mpIdString")
            }
        }
    }
}


