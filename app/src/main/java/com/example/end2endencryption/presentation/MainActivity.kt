package com.example.end2endencryption.presentation

import android.graphics.BlurMaskFilter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.LAYER_TYPE_SOFTWARE
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.end2endencryption.presentation.theme.End2EndEncryptionTheme
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat.setLayerType
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.end2endencryption.data.state.DataState
import dagger.hilt.android.AndroidEntryPoint
import javax.crypto.SecretKey
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            End2EndEncryptionTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background) {
                    //Page()

                    Scaffold(backgroundColor = Color.White) {
                        CustomProgressbar(initialValue = 10f, targetValue =50f )
                    }
                }
            }
        }
    }
}


@Composable
fun Page(
    vm: MainViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val secretKeyState by vm.secretKey.observeAsState()
    when (secretKeyState) {
        is DataState.Loading -> Loading()
        is DataState.Success<SecretKey> -> {
            //secretKeyState.data
            Text(text = "Generate Secret Key Successfully")
        }
        is DataState.Error -> {
            Toast.makeText(context,
                (secretKeyState as DataState.Error).exception.message,
                Toast.LENGTH_LONG).show()
        }
    }


}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    End2EndEncryptionTheme {
        Page()
    }
}
@Composable
fun CustomProgressbar(initialValue: Float , targetValue:Float) {

    val progress = remember { Animatable(initialValue/100f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue/100f,
            animationSpec = tween(2000),
        )
    }



    val horizontalGradientBrush = Brush.horizontalGradient(
        tileMode = TileMode.Clamp,
        colors = listOf(
            Color.Green,
            Color.Yellow,
        )
    )
    AndroidView(
        factory = { context ->
            ComposeView(context).apply {
                setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            }
        },
        update = { composeView ->
            composeView.setContent {
                Box(
                    contentAlignment = Alignment.CenterEnd,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                        .background(color = Color.Transparent)

                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color = Color.Gray),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.value)
                            .height(4.dp)
                            .advanceShadow(color = Color.Green)
                            .clip(RoundedCornerShape(16.dp))
                            .background(brush = horizontalGradientBrush)


                    )
                }
            }
        },

    )


}
@Composable
fun Loading() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        CircularProgressIndicator()
    }
}

 fun Modifier.advanceShadow(
    color: Color = Color.Black,
    borderRadius: Dp = 12.dp,
    blurRadius: Dp = 10.dp,
    offsetY: Dp = 0.dp,
    offsetX: Dp = 0.dp,
    spread: Float = 1f,
) = drawBehind {
        this.drawIntoCanvas {
            val paint = Paint()
            val frameworkPaint = paint.asFrameworkPaint()
            val spreadPixel = spread.dp.toPx()
            val leftPixel = (0f - spreadPixel) + offsetX.toPx()
            val topPixel = (0f - spreadPixel) + offsetY.toPx()
            val rightPixel = (this.size.width + spreadPixel)
            val bottomPixel =  (this.size.height + spreadPixel)

            if (blurRadius != 0.dp) {
                /*
                    The feature maskFilter used below to apply the blur effect only works
                    with hardware acceleration disabled.
                 */
                frameworkPaint.maskFilter =
                    (BlurMaskFilter(blurRadius.toPx(), BlurMaskFilter.Blur.NORMAL))
            }

            frameworkPaint.color = color.toArgb()
            it.drawRoundRect(
                left = leftPixel,
                top = topPixel,
                right = rightPixel,
                bottom = bottomPixel,
                radiusX = borderRadius.toPx(),
                radiusY = borderRadius.toPx(),
                paint
            )
        }
    }
