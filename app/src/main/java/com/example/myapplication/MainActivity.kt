package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Permet l'affichage en plein écran
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FourEqualColorBlocks(
                        modifier = Modifier.padding(innerPadding) // Ajout du padding interne
                    )
                }
            }
        }
    }
}

@Composable
fun FourEqualColorBlocks(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        // Créez une ligne pour les deux premiers blocs
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Bloc rouge
            Surface(
                modifier = Modifier
                    .weight(1f) // Occupe un quart de l'écran
                    .fillMaxHeight(),
                color = Color.Red
            ) {}

            // Bloc vert
            Surface(
                modifier = Modifier
                    .weight(1f) // Occupe un quart de l'écran
                    .fillMaxHeight(),
                color = Color.Green
            ) {}
        }

        // Créez une autre ligne pour les deux derniers blocs
        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // Bloc bleu
            Surface(
                modifier = Modifier
                    .weight(1f) // Occupe un quart de l'écran
                    .fillMaxHeight(),
                color = Color.Blue
            ) {}

            // Bloc jaune
            Surface(
                modifier = Modifier
                    .weight(1f) // Occupe un quart de l'écran
                    .fillMaxHeight(),
                color = Color.Yellow
            ) {}
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFourEqualColorBlocks() {
    MyApplicationTheme {
        FourEqualColorBlocks()
    }
}
