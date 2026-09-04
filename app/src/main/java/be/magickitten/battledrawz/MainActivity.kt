package be.magickitten.battledrawz

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.runtime.DisposableEffect
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
import be.magickitten.battledrawz.ui.theme.MewMakerTheme
import kotlin.random.Random

const val angle=5f
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MewMakerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // 1. SoundPool pour les sons courts
    val soundPool = remember {
        android.media.SoundPool.Builder().setMaxStreams(4).build()
    }
    val soundIds = remember {
        listOf(
            soundPool.load(context, R.raw.meow_1, 1),
            soundPool.load(context, R.raw.meow_2, 1),
            soundPool.load(context, R.raw.meow_3, 1),
            soundPool.load(context, R.raw.meow_4, 1)
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            soundPool.release()
        }
    }

    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val translationY = remember { Animatable(0f) }

    val scale = remember { Animatable(1f) } // Initial scale at 100%

    val zoomOutFactor = 0.90f // Zoom arrière à 90% de la taille
    val zoomInFactor = 1.10f  // Zoom avant à 110% de la taille

    fun pulse() {
        scope.launch {
            // Cycle 1
            scale.animateTo(zoomOutFactor, tween(100, easing = LinearOutSlowInEasing))
            scale.animateTo(zoomInFactor, tween(150, easing = LinearOutSlowInEasing))

            // Cycle 2
            scale.animateTo(zoomOutFactor, tween(100, easing = LinearOutSlowInEasing))
            scale.animateTo(1f, tween(150, easing = LinearOutSlowInEasing)) // Return to normal
        }
    }
    // ---------------------------------

    fun shake() {
        scope.launch {
            rotation.animateTo(angle, tween(100, easing = LinearOutSlowInEasing))
            rotation.animateTo(-angle, tween(100, easing = LinearOutSlowInEasing))
            rotation.animateTo(angle, tween(100, easing = LinearOutSlowInEasing))
            rotation.animateTo(0f, tween(100))
        }
    }

    fun bounce() {
        scope.launch {
            translationY.animateTo(-200f, spring(dampingRatio = Spring.DampingRatioLowBouncy, stiffness = Spring.StiffnessLow))
            translationY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow))
        }
    }

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
                .graphicsLayer(
                    rotationZ = rotation.value,
                    translationY = translationY.value,
                    // APPLICATION DE L'ANIMATION DE ZOOM
                    scaleX = scale.value,
                    scaleY = scale.value
                )
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown()
                        val timerJob = withTimeoutOrNull(3000L) {
                            waitForUpOrCancellation()
                        }

                        if (timerJob == null) {
                            // 2. Retour au MediaPlayer pour le son long
                            val eggPlayer = MediaPlayer.create(context, R.raw.meow_egg)
                            eggPlayer?.start()
                            eggPlayer?.setOnCompletionListener { it.release() }

                            scope.launch {
                                rotation.animateTo(
                                    targetValue = 360f,
                                    animationSpec = tween(durationMillis = eggPlayer?.duration ?: 2000, easing = LinearEasing)
                                )
                            }
                            down.consume()
                        } else {
                            // 3. SoundPool pour les clics normaux
                            soundPool.play(soundIds.random(), 1f, 1f, 1, 0, 1f)

                            // SÉLECTION ALÉATOIRE PARMI LES 3 ANIMATIONS
                            val randomAnim = Random.nextInt(3)
                            when (randomAnim) {
                                0 -> shake()
                                1 -> bounce()
                                2 -> pulse() // La nouvelle animation de zoom
                            }
                        }
                    }
                }
        )
    }
}