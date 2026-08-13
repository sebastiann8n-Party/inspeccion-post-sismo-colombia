package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.form.FormScreen
import com.example.ui.form.FormViewModel
import com.example.ui.form.FormViewModelFactory
import com.example.ui.home.HomeScreen
import com.example.ui.home.HomeViewModel
import com.example.ui.home.HomeViewModelFactory
import com.example.ui.map.MapScreen
import com.example.ui.map.MapViewModel
import com.example.ui.map.MapViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavigation()
        }
      }
    }
  }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val application = androidx.compose.ui.platform.LocalContext.current.applicationContext as MyApplication
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(application.repository))
            HomeScreen(
                viewModel = viewModel,
                onNavigateToForm = { id ->
                    if (id != null) {
                        navController.navigate("form?id=$id")
                    } else {
                        navController.navigate("form")
                    }
                },
                onNavigateToMapExplorer = {
                    navController.navigate("map_explorer")
                }
            )
        }
        composable(
            route = "form?id={id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")
            val viewModel: FormViewModel = viewModel(factory = FormViewModelFactory(application.repository))
            
            androidx.compose.runtime.LaunchedEffect(id) {
                if (id != null) {
                    viewModel.loadInspeccion(id)
                }
            }
            
            FormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onComplete = { navController.popBackStack() },
                onOpenMapSelection = {
                    val currentLat = viewModel.uiState.value.latitud
                    val currentLon = viewModel.uiState.value.longitud
                    navController.navigate("map_picker?lat=$currentLat&lon=$currentLon")
                }
            )
        }
        composable(
            route = "map_picker?lat={lat}&lon={lon}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType; defaultValue = "0.0" },
                navArgument("lon") { type = NavType.StringType; defaultValue = "0.0" }
            )
        ) { backStackEntry ->
            val latStr = backStackEntry.arguments?.getString("lat") ?: "0.0"
            val lonStr = backStackEntry.arguments?.getString("lon") ?: "0.0"
            val initialLat = latStr.toDoubleOrNull() ?: 0.0
            val initialLon = lonStr.toDoubleOrNull() ?: 0.0

            val mapViewModel: MapViewModel = viewModel(
                factory = MapViewModelFactory(application.repository, application.barrioRepository)
            )

            // Obtain parent form ViewModel to update location
            val parentEntry = remember(backStackEntry) {
                try {
                    navController.getBackStackEntry("form?id={id}")
                } catch (e: Exception) {
                    null
                }
            }
            val formViewModel: FormViewModel? = parentEntry?.let { entry ->
                viewModel(entry, factory = FormViewModelFactory(application.repository))
            }

            MapScreen(
                viewModel = mapViewModel,
                isSelectionMode = true,
                initialLat = initialLat,
                initialLon = initialLon,
                onBack = { navController.popBackStack() },
                onConfirmLocation = { lat, lon, barrioId, barrioNombre, barrioCodigo, localidad, direccion ->
                    formViewModel?.updateLocationAndBarrio(lat, lon, barrioId, barrioNombre, barrioCodigo, localidad, direccion)
                    navController.popBackStack()
                }
            )
        }
        composable("map_explorer") {
            val mapViewModel: MapViewModel = viewModel(
                factory = MapViewModelFactory(application.repository, application.barrioRepository)
            )
            MapScreen(
                viewModel = mapViewModel,
                isSelectionMode = false,
                onBack = { navController.popBackStack() },
                onSelectInspeccion = { id ->
                    navController.navigate("form?id=$id")
                }
            )
        }
    }
}
