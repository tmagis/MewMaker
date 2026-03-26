package be.magickitten.battledrawz

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
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
    val sounds = listOf(R.raw.meow_1, R.raw.meow_2, R.raw.meow_3, R.raw.meow_4)
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
                .pointerInput(Unit) {
                    awaitEachGesture {
                        // 1. Wait for the initial touch down
                        val down = awaitFirstDown()

                        // 2. Start a timer. If it finishes without being cancelled, trigger egg.
                        val timerJob = withTimeoutOrNull(3000L) {
                            // This block will keep running as long as the finger is held
                            waitForUpOrCancellation()
                        }

                        if (timerJob == null) {
                            // timerJob is null if the timeout (3000ms) was reached!
                            // --- TRIGGER EASTER EGG HERE ---
                            val eggPlayer = MediaPlayer.create(context, R.raw.meow_egg)
                            eggPlayer?.start()
                            eggPlayer?.setOnCompletionListener { it.release() }

                            // Consume the event so it doesn't trigger a normal click later
                            down.consume()
                        } else {
                            // The user lifted their finger BEFORE 3000ms
                            // --- TRIGGER REGULAR CLICK HERE ---
                            val mediaPlayer = MediaPlayer.create(context, sounds.random())
                            mediaPlayer?.start()
                            mediaPlayer?.setOnCompletionListener { it.release() }
                        }
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