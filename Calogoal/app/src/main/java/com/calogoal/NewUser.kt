package com.calogoal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.ListItemDefaults.contentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NewUser(navController: NavController) {
    val context = LocalContext.current

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Calogoal Logo

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )
            // Title and subtitle
            Column {
                Text(
                    text = "Welcome to Calogoal!",
                    fontSize = 38.sp,
                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Let's get you set up for success.",
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 26.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(60.dp))

// Getting Started Button
            Button(
                onClick = {
                    val prefs = context.getSharedPreferences("calogoal_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putBoolean("is_new_user", false).apply()

                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.NewUser.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA500),
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Get Started",
                    fontSize = 20.sp,
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                )
            }
        }
    }
}
