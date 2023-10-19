package net.engawapg.app.nestedscrollsamples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import net.engawapg.app.nestedscrollsamples.ui.theme.NestedScrollSamplesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NestedScrollSamplesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "root") {
                        composable("root") {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceEvenly,
                            ) {
                                Button(
                                    onClick = {navController.navigate("collapse_top_bar_sample")}
                                ) { Text("Collapse Top Bar Sample") }
                                Button(
                                    onClick = {navController.navigate("connection_sample1")}
                                ) { Text("Connection Sample1") }
                                Button(
                                    onClick = {navController.navigate("connection_sample2")}
                                ) { Text("Connection Sample2") }
                                Button(
                                    onClick = {navController.navigate("dispatcher_sample1")}
                                ) { Text("Dispatcher Sample1") }
                                Button(
                                    onClick = {navController.navigate("dispatcher_sample2")}
                                ) { Text("Dispatcher Sample 2") }
                            }
                        }
                        composable("collapse_top_bar_sample") {
                            CollapseTopBarSample()
                        }
                        composable("connection_sample1") {
                            ConnectionSample1()
                        }
                        composable("connection_sample2") {
                            ConnectionSample2()
                        }
                        composable("dispatcher_sample1") {
                            DispatcherSample1()
                        }
                        composable("dispatcher_sample2") {
                            DispatcherSample2()
                        }
                    }
                }
            }
        }
    }
}
