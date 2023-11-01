package com.tezov.medium.adr.textfield_focus

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tezov.medium.adr.textfield_focus.ui.theme.Textfield_focusTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Textfield_focusTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    FocusExample()
                }
            }
        }
    }
}

private const val MAX_LOGIN_LENGTH = 8
private const val MAX_PASSWORD_LENGTH = 4

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FocusExample() {
    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top
    ) {
        val focusDispatcher = rememberFocusDispatcher()

        val login = remember { mutableStateOf("") }
        val loginId = remember { focusDispatcher.createId() }

        val password = remember { mutableStateOf("") }
        val passwordId = remember { focusDispatcher.createId(autoShowKeyboard = false) }

        fun onLoginChange(newValue: String) {
            if (newValue.length <= MAX_LOGIN_LENGTH) {
                login.value = newValue
            }
            if (login.value.length >= MAX_LOGIN_LENGTH) {
                if (password.value.length < MAX_PASSWORD_LENGTH) {
                    passwordId.requestFocus()
                } else {
                    focusDispatcher.requestClearFocus()
                }
            }
        }

        fun onPasswordChange(newValue: String) {
            passwordId.requestFocus()
            if (password.value.length < MAX_PASSWORD_LENGTH) {
                password.value = newValue
            }
            if (password.value.length >= MAX_PASSWORD_LENGTH) {
                if (login.value.length < MAX_LOGIN_LENGTH) {
                    loginId.requestFocus()
                } else {
                    focusDispatcher.requestClearFocus()
                }
            }

        }

        LaunchedEffect(Unit) { loginId.requestFocus() }

        @Composable
        fun LoginInput() {
            TextField(modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 8.dp)
                .fillMaxWidth()
                .focusId(loginId),
                textStyle = TextStyle(
                    fontSize = 24.sp
                ),
                label = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Login")
                        Text(text = "${login.value.length} / $MAX_LOGIN_LENGTH")
                    }
                },
                value = login.value,
                onValueChange = {
                    onLoginChange(newValue = it)
                })
        }
        LoginInput()

        @Composable
        fun PasswordInput() {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(
                    LocalTextInputService provides null
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(1.0f)
                            .focusId(loginId),
                        textStyle = TextStyle(
                            fontSize = 24.sp
                        ),
                        label = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Password")
                                Text(text = "${password.value.length} / $MAX_PASSWORD_LENGTH")
                            }

                        },
                        value = password.value,
                        onValueChange = { password.value = it },
                        readOnly = true,
                        colors = TextFieldDefaults.textFieldColors(
                            cursorColor = Color.Transparent
                        )
                    )
                }
                Button(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = {
                        passwordId.requestFocus()
                        password.value = ""
                    }) {
                    Text(
                        text = "X",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }

        }
        PasswordInput()

        @Composable
        fun PasswordKeyBoard() {
            @Composable
            fun Bouton(
                value: String,
            ) {
                Button(
                     colors = ButtonDefaults.buttonColors(
                        containerColor = if(passwordId.hasFocus) Color.Blue else Color.LightGray
                    ),
                    onClick = {
                        onPasswordChange(newValue = password.value + value)
                    }) {
                    Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            (0..1).forEach { up ->
                Row(
                    modifier = Modifier
                        .align(CenterHorizontally)
                        .padding(top = 12.dp),
                ) {
                    (0..4).forEach { down ->
                        Bouton(value = "${(5 * up) + down}")
                    }
                }
            }
        }
        PasswordKeyBoard()

        fun isLoginValid() = login.value.length == MAX_LOGIN_LENGTH && password.value.length == MAX_PASSWORD_LENGTH

        val context = LocalContext.current
        Button(
            modifier = Modifier
                .align(CenterHorizontally)
                .padding(top = 32.dp),
            enabled = isLoginValid(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Magenta
            ),
            onClick = {
                Toast.makeText(context, "Yeah!!!!!", Toast.LENGTH_LONG).show()
            }) {
            Text(text = if (isLoginValid()) "Rock'n Roll" else "-----", fontSize = 38.sp)
        }


    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Textfield_focusTheme {
        FocusExample()
    }
}