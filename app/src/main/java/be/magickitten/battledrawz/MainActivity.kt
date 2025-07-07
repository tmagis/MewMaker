package be.magickitten.battledrawz

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import be.magickitten.battledrawz.ui.theme.MewMakerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MewMakerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Pass the Modifier with padding and fillMaxSize to Greeting
                    Greeting(modifier = Modifier.padding(innerPadding).fillMaxSize())
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    // Get the current context to access resources and create MediaPlayer
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize(), // Ensure the Box fills the available space
        contentAlignment = Alignment.Center // Center the image within the Box
    ) {
        Image(
            painter = painterResource(id = R.drawable.kitty), // Replace 'my_image' with your actual image file name
            contentDescription = "MewMaker background image", // Provide a meaningful description for accessibility
            contentScale = ContentScale.Crop, // Scale the image to fill the bounds, cropping if necessary
            modifier = Modifier
                .fillMaxSize() // Make the image fill the entire Box
                .clickable {
                    // Create and start MediaPlayer when the image is clicked
                    val mediaPlayer = MediaPlayer.create(context, R.raw.meow_1) // Replace 'my_sound' with your actual sound file name
                    mediaPlayer?.start()

                    // Optional: Release the MediaPlayer resources after playback to prevent memory leaks
                    // This is important for short sounds that don't need continuous playback
                    mediaPlayer?.setOnCompletionListener { mp ->
                        mp.release()
                    }
                }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MewMakerTheme {
        Greeting()
    }
}