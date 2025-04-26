package com.example.ccpapplication.pages.clients


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun ScheduleVisitPage(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: ClientsViewModel = viewModel()
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()){
        Text("Agendar Visita")
    }
}