package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                // Create and set up the navigation controller
                val navController = rememberNavController()
                AppNavigation(navController)
            }
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    // Navigation setup with routes
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("signup") { SignupScreen(navController) }
        composable("forgotPassword") { ForgotPasswordScreen(navController) }
    }
}

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = email,
            onValueChange = { email = it; emailError = false },
            label = { Text("Email") },
            isError = emailError
        )
        if (emailError) {
            Text("Invalid email address", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = { password = it; passwordError = false },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = passwordError
        )
        if (passwordError) {
            Text("Password cannot be empty", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            emailError = email.isBlank()
            passwordError = password.isBlank()

            if (!emailError && !passwordError) {
                // Handle login logic here
            }
        }) {
            Text(text = "Login")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("forgotPassword") }) {
            Text("Forgot Password?")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("signup") }) {
            Text("Don't have an account? Sign Up")
        }
    }
}

@Composable
fun SignupScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Sign Up", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))

        TextField(value = firstName, onValueChange = { firstName = it }, label = { Text("First Name") })
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = lastName, onValueChange = { lastName = it }, label = { Text("Last Name") })
        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = email, onValueChange = {
            email = it
            emailError = false
        }, label = { Text("Email") }, isError = emailError)
        if (emailError) {
            Text("Invalid email address", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(value = password, onValueChange = {
            password = it
            passwordError = false
        }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), isError = passwordError)
        if (passwordError) {
            Text("Password cannot be empty", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            emailError = email.isBlank()
            passwordError = password.isBlank()

            if (!emailError && !passwordError) {
                // Handle sign-up logic here
            }
        }) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login")
        }
    }
}

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Forgot Password", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(20.dp))

        TextField(value = email, onValueChange = {
            email = it
            emailError = false
        }, label = { Text("Email") }, isError = emailError)
        if (emailError) {
            Text("Invalid email address", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            if (email.isBlank()) {
                emailError = true
            } else {
                // Handle reset password logic here
            }
        }) {
            Text(text = "Reset Password")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { navController.navigate("login") }) {
            Text("Back to Login")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        val navController = rememberNavController()
        AppNavigation(navController)
    }
}
