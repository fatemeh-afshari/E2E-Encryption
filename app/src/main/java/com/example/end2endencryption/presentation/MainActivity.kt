package com.example.end2endencryption.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.end2endencryption.presentation.theme.End2EndEncryptionTheme
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
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
                    Page()
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