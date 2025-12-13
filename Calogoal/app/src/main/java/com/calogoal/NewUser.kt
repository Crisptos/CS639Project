package com.calogoal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            /* Logo */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "CaloGoal Logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.95f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Welcome to Calogoal!",
                fontSize = 34.sp,
                fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Let’s get you set up for success.",
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

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
                    .height(56.dp)
                    .padding(bottom = 24.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA500),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 20.sp,
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                )
            }
        }
    }
}
