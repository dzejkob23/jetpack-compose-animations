@file:OptIn(ExperimentalAnimationApi::class)

package dev.jakubzika.jetpack_compose_animations

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
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
 * Example of [rememberInfiniteTransition], [animateFloat]
 */
@Composable
private fun AnimatedProgressExample1() {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
        Button(onClick = { state = state.not() }) { Text(text = "Klikni!") }
        Crossfade(targetState = state) {
            Icon(
                modifier = Modifier
                    .rotate(if (it) rotation.value else 0f)
                    .size(56.dp),
                imageVector = if (it) Icons.Default.Refresh else Icons.Default.CheckCircle,
                contentDescription = ""
            )
        }
    }
}

@Composable
private fun AnimatedProgressExample2() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var progress by remember { mutableStateOf(0.5f) }
        val animatedProgress by animateFloatAsState(targetValue = progress)
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
 * Example of [AnimatedContent], [...transition, rotation,]
 */
@Composable
private fun AnimatedDropdownExample(data: Array<Pair<String, String>>) {
    var expanded by remember { mutableStateOf(false) }
    val angle: Float by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
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
                modifier = Modifier.rotate(angle)
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackcomposeanimationsTheme {
        ContentView()
    }
}