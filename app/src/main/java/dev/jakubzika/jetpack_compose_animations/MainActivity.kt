@file:OptIn(ExperimentalAnimationApi::class)

package dev.jakubzika.jetpack_compose_animations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.jakubzika.jetpack_compose_animations.ui.theme.JetpackcomposeanimationsTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackcomposeanimationsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ContentView()
                }
            }
        }
    }
}

@Composable
fun ContentView() {
    val contentSpace: @Composable () -> Unit = { Spacer(modifier = Modifier.height(24.dp)) }
    val data = remember {
        arrayOf(
            "English" to "Hello!",
            "Spanish" to "¡Hola!",
            "French" to "Bonjour!",
            "German" to "Guten Tag!",
            "Mandarin" to "你好",
            "Japanese" to "こんにちは",
            "Arabic" to "مرحباً",
            "Hindi" to "नमस्ते",
            "Swahili" to "Jambo!",
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedTextExample()
        contentSpace()
        AnimatedProgressExample1()
        contentSpace()
        AnimatedProgressExample2()
        contentSpace()
        AnimatedDropdownExample(data)
        contentSpace()
        AnimatedCarouselExample(data)
        contentSpace()
        contentSpace()
        contentSpace()
        contentSpace()
        ChatGPT_SpiralAnimation()
    }
}

/**
 * Example of [AnimatedVisibility]
 */
@Composable
private fun AnimatedTextExample() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        var state by remember { mutableStateOf(false) }
        Button(onClick = { state = state.not() }) { Text(text = "Klikni!") }
        AnimatedVisibility(
            visible = state,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(text = "CN Group - Nalejvárna", style = MaterialTheme.typography.h5)
        }
    }
}

/**
 * Example of [rememberInfiniteTransition], [animateFloat], [rotate]
 */
@Composable
private fun AnimatedProgressExample1() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val rotation = rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 1500,
                    easing = LinearEasing,
                )
            )
        )
        var state by remember { mutableStateOf(true) }
        val transition =
            updateTransition(targetState = state, label = "animated_progress_transition")
        val size by transition.animateDp(
            label = "animated_progress_icon_size",
            transitionSpec = {
                when {
                    true isTransitioningTo false -> spring(
                        stiffness = Spring.StiffnessLow,
                        dampingRatio = Spring.DampingRatioHighBouncy
                    )
                    else -> tween(durationMillis = 200)
                }
            }
        ) { if (it) 56.dp else 72.dp }
        Button(onClick = { state = state.not() }) { Text(text = "Klikni!") }
        transition.Crossfade {
            Icon(
                modifier = Modifier
                    .rotate(if (it) rotation.value else 0f)
                    .size(size),
                imageVector = if (it) Icons.Default.Refresh else Icons.Default.CheckCircle,
                contentDescription = ""
            )
        }
    }
}

/**
 * Example of [animateFloatAsState]
 */
@Composable
private fun AnimatedProgressExample2() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var progress by remember { mutableStateOf(0.5f) }
        val animatedProgress by animateFloatAsState(
            targetValue = progress,
            animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy)
        )
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            progress = animatedProgress
        )
        Row(modifier = Modifier, horizontalArrangement = Arrangement.SpaceEvenly) {
            IconButton(onClick = { if (progress > 0f) progress -= 0.1f else progress = 0f }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
            IconButton(onClick = { if (progress < 1f) progress += 0.1f else progress = 1f }) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}

/**
 * Example of [animateContentSize], [animateFloatAsState], [rotate]
 */
@Composable
private fun AnimatedDropdownExample(data: Array<Pair<String, String>>) {
    var expanded by remember { mutableStateOf(false) }
    val angle = if (expanded) 90f else 0f
    val animatedAngle: Float by animateFloatAsState(
        targetValue = angle,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing
        )
    )
    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colors.primary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(8.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { expanded = expanded.not() }
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Menu", style = MaterialTheme.typography.h6)
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.rotate(animatedAngle)
            )
        }
        if (expanded) {
            data.forEach { pair ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = pair.first)
                    Text(text = pair.second)
                }
            }
        }
    }
}

/**
 * Example of [AnimatedContent], [slideInHorizontally], [slideOutHorizontally]
 */
@Composable
private fun AnimatedCarouselExample(data: Array<Pair<String, String>>) {
    val (currentIndex, setCurrentIndex) = remember { mutableStateOf(0) }

    LaunchedEffect(currentIndex, data.size) {
        launch(Dispatchers.IO) {
            delay(4000)
            when {
                // checks array size during changes
                currentIndex + 1 > data.lastIndex -> setCurrentIndex(0)
                // checks the last item to move cursor on the start again
                currentIndex == data.lastIndex -> setCurrentIndex(0)
                // moves cursor one item ahead
                else -> setCurrentIndex(currentIndex + 1)
            }
        }
    }

    AnimatedContent(
        targetState = currentIndex,
        transitionSpec = {
            slideInHorizontally(initialOffsetX = { it }) + fadeIn() with
                    slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
        }
    ) { selectedMessageIndex ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val currentItem = data[selectedMessageIndex.takeIf { it < data.size } ?: data.lastIndex]
            Text(text = currentItem.first, style = MaterialTheme.typography.h6)
            Text(text = currentItem.second, style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
fun ChatGPT_SpiralAnimation() {
    var rotationAngle by remember { mutableStateOf(0f) }
    val spiralAnimation = rememberInfiniteTransition()
    val spiralAngle by spiralAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 720f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val spiralRadius by spiralAnimation.animateFloat(
        initialValue = 0f,
        targetValue = 200f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    rotationAngle = spiralAngle

    Box(
        modifier = Modifier
            .size(64.dp)
            .background(color = MaterialTheme.colors.background)
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier
                .offset {
                    val x =
                        (spiralRadius * kotlin.math.cos(Math.toRadians(spiralAngle.toDouble()))).toFloat()
                    val y =
                        (spiralRadius * kotlin.math.sin(Math.toRadians(spiralAngle.toDouble()))).toFloat()
                    return@offset IntOffset(x.toInt(), y.toInt())
                }
                .rotate(rotationAngle),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackcomposeanimationsTheme {
        ContentView()
    }
}