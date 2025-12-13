package com.calogoal

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.calogoal.models.accounts.User
import com.calogoal.viewmodels.LoginViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {

    val user = viewModel.currentUser.collectAsState().value
    val loginStatus = viewModel.loginStatus.collectAsState().value

    LaunchedEffect(loginStatus.success) {
        if (loginStatus.success) {
            navController.navigate(Screen.Profile.route) {
                popUpTo(Screen.NewUser.route) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

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
                painter = painterResource(id = R.drawable.ic_launcher),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(240.dp)
                    .padding(bottom = 16.dp)
            )
            // Title and subtitle
            Column {
                Text(
                    text = "Welcome!",
                    fontSize = 38.sp,
                    fontWeight = MaterialTheme.typography.headlineLarge.fontWeight,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                EmailField(user.email, {viewModel.updateEmail(it)})
                PasswordField(user.password, {viewModel.updatePassword(it)})
                if (loginStatus.error != null) {
                    Text(
                        text = loginStatus.error,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            val scope = rememberCoroutineScope()
            Button(
                onClick = {
                    scope.launch {
                        viewModel.login(user.email, user.password)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.extraLarge,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA500),
                    contentColor = Color.White
                ),
                enabled = !loginStatus.loading || !loginStatus.success
            ) {
                Text(
                    "Login",
                    fontSize = 20.sp,
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight
                )
            }
        }
    }
}

@Composable
fun EmailField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text("Email") }
    )
}

@Composable
fun PasswordField(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation()
    )
}

