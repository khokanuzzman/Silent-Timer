package com.example.silenttimer

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.silenttimer.alarm.SilentModeScheduler
import com.example.silenttimer.data.db.AppDatabase
import com.example.silenttimer.data.repository.SilentPeriodRepository
import com.example.silenttimer.ui.period.SilentPeriodViewModel
import com.example.silenttimer.ui.period.SilentPeriodViewModelFactory
import com.example.silenttimer.ui.screens.period.AddSilentPeriodScreen
import com.example.silenttimer.ui.screens.period.SilentPeriodListScreen
import android.graphics.Color
import android.view.View
import android.view.WindowManager

class MainActivity : ComponentActivity() {
    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val repository = SilentPeriodRepository(db.silentPeriodDao())
        val viewModelFactory = SilentPeriodViewModelFactory(repository)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.WHITE
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        setContent {
            // Request notification permission (Android 13+)
            val requestNotificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
                        Log.d("Permission", "✅ Notification permission granted")
                    } else {
                        Log.w("Permission", "❌ Notification permission denied")
                    }
                }
            )

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            val navController = rememberNavController()
            val viewModel: SilentPeriodViewModel = viewModel(factory = viewModelFactory)

            NavHost(navController, startDestination = "home") {
                composable("home") {
                    SilentPeriodListScreen(
                        viewModel = viewModel,
                        onAddClick = { navController.navigate("add") },
                        onEditClick = { periodId -> navController.navigate("edit/$periodId") }
                    )
                }
                composable("add") {
                    AddSilentPeriodScreen(
                        isEditMode = false,
                        period = null,
                        onSave = {
                            viewModel.addSilentPeriod(it)
                            SilentModeScheduler.scheduleSilentPeriod(applicationContext, it)
                            navController.popBackStack()
                        }
                    )
                }
                composable(
                    route = "edit/{id}",
                    arguments = listOf(navArgument("id") { type = NavType.IntType })
                ) { backStackEntry ->
                    val id = backStackEntry.arguments?.getInt("id") ?: -1
                    val period = viewModel.silentPeriods.value?.find { it.id == id }

                    if (period != null) {
                        AddSilentPeriodScreen(
                            isEditMode = true,
                            period = period,
                            onSave = { updated ->
                                viewModel.updateSilentPeriod(updated)
                                SilentModeScheduler.cancelScheduledAlarms(applicationContext, updated.id)
                                SilentModeScheduler.scheduleSilentPeriod(applicationContext, updated)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
