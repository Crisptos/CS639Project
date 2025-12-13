@file:OptIn(ExperimentalMaterial3Api::class)

package com.calogoal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun NewUser(navController: NavController) {
    val context = LocalContext.current

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    val emailFocusRequester = FocusRequester()
    val passwordFocusRequester = FocusRequester()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Welcome to Calogoal!",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
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

            Spacer(modifier = Modifier.height(28.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocusRequester)
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.key == Key.Tab) {
                            passwordFocusRequester.requestFocus()
                            true
                        } else {
                            false
                        }
                    },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                    text = "Get Started",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
