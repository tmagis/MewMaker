package be.magickitten.battledrawz

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import be.magickitten.battledrawz.ui.theme.MewMakerTheme

const val angle=5f
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MewMakerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Pass the Modifier with padding and fillMaxSize to Greeting
                    Greeting(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val sounds = listOf(R.raw.meow_1, R.raw.meow_2, R.raw.meow_3, R.raw.meow_4)

    // 1. Create a CoroutineScope that we can use inside the gesture block
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.kitty),
            contentDescription = "MewMaker background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(rotationZ = rotation.value)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown()

                        // 2. Use 'scope.launch' instead of just 'launch'


                        val timerJob = withTimeoutOrNull(3000L) {
                            waitForUpOrCancellation()
                        }


                        val eggPlayer = MediaPlayer.create(context, R.raw.meow_egg)
                        if (timerJob == null) {
                            scope.launch {
                                rotation.animateTo(
                                    targetValue = 360f,
                                    animationSpec = tween(
                                        durationMillis = eggPlayer.duration,
                                        easing = LinearEasing
                                    )
                                )
                            }
                            eggPlayer?.start()
                            eggPlayer?.setOnCompletionListener { it.release() }
                            down.consume()
                        } else {
                            val mediaPlayer = MediaPlayer.create(context, sounds.random())
                            mediaPlayer?.start()
                            mediaPlayer?.setOnCompletionListener { it.release() }
                            scope.launch {
                                rotation.animateTo(angle, tween(100, easing = LinearOutSlowInEasing))
                                rotation.animateTo(-angle, tween(100, easing = LinearOutSlowInEasing))
                                rotation.animateTo(angle, tween(100, easing = LinearOutSlowInEasing))
                                rotation.animateTo(0f, tween(100)) // Return to center
                            }
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