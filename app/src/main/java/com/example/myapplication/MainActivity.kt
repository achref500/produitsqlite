package com.example.myapplication

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

// Data models for User and Product
data class Product(
    val id: Int = 0,
    val name: String,
    val description: String,
    val price: String,
    val imageId: Int
)

data class User(
    val id: Int = 0,
    val username: String,
    val email: String,
    val password: String
)

// Database helper class for products and users
class DatabaseHelper(context: MainActivity) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app_db"
        private const val DATABASE_VERSION = 1

        // Tables and Columns for Products and Users
        const val TABLE_PRODUCTS = "products"
        const val TABLE_USERS = "users"

        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_PRICE = "price"
        const val COLUMN_IMAGE_ID = "image_id"

        const val COLUMN_USERNAME = "username"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create tables
        val createProductsTable = """
            CREATE TABLE $TABLE_PRODUCTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT,
                $COLUMN_DESCRIPTION TEXT,
                $COLUMN_PRICE TEXT,
                $COLUMN_IMAGE_ID INTEGER
            )
        """

        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USERNAME TEXT,
                $COLUMN_EMAIL TEXT,
                $COLUMN_PASSWORD TEXT
            )
        """

        db?.execSQL(createProductsTable)
        db?.execSQL(createUsersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // Insert product into the database
    fun insertProduct(name: String, description: String, price: String, imageId: Int): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_DESCRIPTION, description)
            put(COLUMN_PRICE, price)
            put(COLUMN_IMAGE_ID, imageId)
        }
        return db.insert(TABLE_PRODUCTS, null, contentValues)
    }

    // Get all products from the database
    fun getAllProducts(): List<Product> {
        val productList = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PRODUCTS", null)

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                val price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
                val imageId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_ID))

                productList.add(Product(id, name, description, price, imageId))
            } while (cursor.moveToNext())
        }

        cursor?.close() // Close cursor to avoid memory leaks
        return productList
    }

    // Get user by email and password (for login)
    fun getUserByEmailAndPassword(email: String, password: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, password)
        )

        var user: User? = null
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val username = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME))
            val userEmail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val userPassword = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            user = User(id, username, userEmail, userPassword)
        }

        cursor?.close() // Close cursor to avoid memory leaks
        return user
    }

    // Insert user into the database
    fun insertUser(username: String, email: String, password: String): Long {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
        }
        return db.insert(TABLE_USERS, null, contentValues)
    }

    // Update user's password
    fun updateUserPassword(email: String, newPassword: String): Int {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_PASSWORD, newPassword)
        }
        return db.update(TABLE_USERS, contentValues, "$COLUMN_EMAIL = ?", arrayOf(email))
    }
}

// MainActivity with Login, Signup, and Home screens
class MainActivity : ComponentActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                dbHelper = DatabaseHelper(this)
                AuthScreen()
            }
        }
    }

    // Screens for login, signup, and home
    @Composable
    fun AuthScreen() {
        var screen by remember { mutableStateOf<Screen>(Screen.Login) }

        when (screen) {
            Screen.Login -> LoginScreen(
                onNavigateToSignup = { screen = Screen.Signup },
                onNavigateToHome = { screen = Screen.Home },
                onNavigateToForgotPassword = { screen = Screen.ForgotPassword }
            )
            Screen.Signup -> SignupScreen(onNavigateToLogin = { screen = Screen.Login })
            Screen.ForgotPassword -> ForgotPasswordScreen(onNavigateToLogin = { screen = Screen.Login })
            Screen.Home -> HomeScreen()
        }
    }

    // Enum for different screens
    enum class Screen {
        Login, Signup, ForgotPassword, Home
    }

    // Login screen
    @Composable
    fun LoginScreen(onNavigateToSignup: () -> Unit, onNavigateToHome: () -> Unit, onNavigateToForgotPassword: () -> Unit) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var errorMessage by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Login", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())

            Spacer(modifier = Modifier.height(16.dp))

            Text(errorMessage, color = Color.Red)

            Button(onClick = {
                val user = dbHelper.getUserByEmailAndPassword(email, password)
                if (user != null) {
                    onNavigateToHome()
                } else {
                    errorMessage = "Invalid email or password"
                }
            }) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Forgot Password?", color = Color.Blue, modifier = Modifier.clickable { onNavigateToForgotPassword() })

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Don't have an account? Sign up", color = Color.Blue, modifier = Modifier.clickable { onNavigateToSignup() })
        }
    }

    // Signup screen
    @Composable
    fun SignupScreen(onNavigateToLogin: () -> Unit) {
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Sign Up", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(value = username, onValueChange = { username = it }, label = { Text("Username") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            Spacer(modifier = Modifier.height(8.dp))
            TextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation())

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                dbHelper.insertUser(username, email, password)
                onNavigateToLogin()
            }) {
                Text("Sign Up")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Already have an account? Login", color = Color.Blue, modifier = Modifier.clickable { onNavigateToLogin() })
        }
    }

    // Forgot Password screen
    @Composable
    fun ForgotPasswordScreen(onNavigateToLogin: () -> Unit) {
        var email by remember { mutableStateOf("") }
        var message by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Forgot Password", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(16.dp))

            TextField(value = email, onValueChange = { email = it }, label = { Text("Enter your email") })

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val result = dbHelper.updateUserPassword(email, "newpassword")
                message = if (result > 0) "Password updated successfully" else "Email not found"
            }) {
                Text("Reset Password")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(message, color = Color.Green)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Back to Login", color = Color.Blue, modifier = Modifier.clickable { onNavigateToLogin() })
        }
    }

    // Home screen where products are displayed
    @Composable
    fun HomeScreen() {
        val products = remember { mutableStateOf(dbHelper.getAllProducts()) }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "Products", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(20.dp))

            products.value.forEach { product ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            // Handle product click (navigate to detailed view)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = product.imageId),
                        contentDescription = "Product Image",
                        modifier = Modifier.size(120.dp),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = product.name, style = MaterialTheme.typography.bodyLarge)
                        Text(text = product.price, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                // Example of adding a product to the database
                val newProduct = Product(name = "New Product", description = "Description", price = "$100", imageId = R.drawable.product1)
                dbHelper.insertProduct(newProduct.name, newProduct.description, newProduct.price, newProduct.imageId)
                products.value = dbHelper.getAllProducts() // Update the list
            }) {
                Text("Add New Product")
            }
        }
    }
}
