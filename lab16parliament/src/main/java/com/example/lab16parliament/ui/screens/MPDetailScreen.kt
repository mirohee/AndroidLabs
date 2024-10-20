package com.example.lab16parliament.ui.screens

import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.lab16parliament.repository.MPRepository
import com.example.lab16parliament.ui.MPDetailsImage
import com.example.lab16parliament.ui.viewmodels.MPDetailViewModel
import com.example.lab16parliament.ui.viewmodels.MPDetailViewModelFactory


/**
 * Miro Saarinen
 * 21/10/2024
 * Screen for displaying the details of a single MP.
 */
@Composable
fun MPDetailScreen(
    hetekaId: Int,
    navController: NavHostController,
    repository: MPRepository
) {

    val viewModel: MPDetailViewModel = viewModel(
        factory = MPDetailViewModelFactory(repository)
    )

    val mp by viewModel.mp.collectAsState()
    val mpExtras by viewModel.mpExtras.collectAsState() // Collect extras
    val comments by viewModel.comments.collectAsState(emptyList())

    // Fetch MP data, comments, and extras when hetekaId changes
    LaunchedEffect(hetekaId) {
        viewModel.getMPById(hetekaId)
        viewModel.getMPComments(hetekaId)
        viewModel.getMpExtras(hetekaId)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Display MP image and information

        MPDetailsImage(mp?.pictureUrl)

        mp?.let {
            Text(text = "Name: ${it.firstname} ${it.lastname}")
            Text(text = "Party: ${it.party}")
            Text(text = "Seat Number: ${it.seatNumber}")
            Text(text = "Minister: ${if (it.minister == true) "Yes" else "No"}")
        }

        mpExtras.firstOrNull()?.let { extras ->
            Text(text = "Born Year: ${extras.bornYear ?: "N/A"}")
            Text(text = "Constituency: ${extras.constituency ?: "N/A"}")
            Text(text = "Twitter: ${extras.twitter ?: "N/A"}")
        }

        // Comment and grade form
        CommentAndGradeForm(viewModel, hetekaId)

        // Display comments
        Text(text = "Comments:", modifier = Modifier.padding(top = 16.dp))
        LazyColumn(modifier = Modifier.fillMaxHeight()) {
            items(comments) { comment ->
                Text(text = "${comment.comment} - Grade: ${comment.grade}", modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}


@Composable
fun CommentAndGradeForm(viewModel: MPDetailViewModel, mpId: Int) {
    var comment by remember { mutableStateOf("") }
    var grade by remember { mutableFloatStateOf(0f) }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        TextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Add Comment") },
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Slider for grade
        Slider(
            value = grade,
            onValueChange = { grade = it },
            valueRange = 0f..5f,
            steps = 5,
            modifier = Modifier.padding(bottom = 8.dp)
        )


        Button(onClick = {
            if (comment.isNotBlank() && grade > 0 && grade <= 5) {
                viewModel.addCommentAndGrade(comment, grade, mpId)
                comment = "" // Clear the comment field
            }
        }) {
            Text("Submit")
        }
    }
}
