package com.akash.smvduclubapp.screen
import android.R.attr.text
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Result
import com.akash.smvduclubapp.getGoogleSignInClient
import com.akash.smvduclubapp.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onSignInSuccess: () -> Unit,

) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by authViewModel.isLoading.observeAsState(false)
    val result by authViewModel.authResult.observeAsState()
    val context = LocalContext.current
    val googleSignInClient = remember { getGoogleSignInClient(context) }
    val user by authViewModel.googleSignInResult.observeAsState()
    var passwordVisible by remember { mutableStateOf(false)}

    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            authViewModel.signInWithGoogle(account.idToken!!)
        } catch (e: Exception) {
            Toast.makeText(context,
                context.getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show()
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),

        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center

    ) {
        Spacer(modifier = Modifier.fillMaxHeight(0.05f))

        Text(
            text = stringResource(id = R.string.login_sign_in),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.theme_color),
            textAlign = TextAlign.Start,
            )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.sign_in_with_your_university_ids),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,

        )

        Spacer(modifier = Modifier.height(35.dp))
        Text(text = stringResource(id = R.string.email), fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.enter_email_here)) },
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 2f
                    val y = size.height - strokeWidth
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent
            )
        )


        Spacer(modifier = Modifier.height(30.dp))
        Text(text = stringResource(id = R.string.password), fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password_here)) },
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 2f
                    val y = size.height - strokeWidth
                    drawLine(
                        color = Color.Gray,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        authViewModel.login(email, password)
                    }
                }
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent
            )
        )


        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(id = R.string.forget_password),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onNavigateToForgotPassword()  }
        )


        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter) // Aligns the Box at the bottom
                    // Adds some spacing from the bottom
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = {
                            if (email.isBlank()) {
                                Toast.makeText(context, context.getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT).show()
                            } else if (password.isBlank()) {
                                Toast.makeText(context,
                                    context.getString(R.string.please_enter_your_password), Toast.LENGTH_SHORT).show()
                            } else {
                                authViewModel.login(email, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.theme_color))
                    ) {
                        Text(text = stringResource(id = R.string.login), color = Color.White)
                    }
                    LaunchedEffect(result) {
                        when (val currentResult = result) {
                            is Result.Success -> {
                                onSignInSuccess()
                            }
                            is Result.Error -> {
                                val errorMessage = when {
                                    currentResult.exception.message?.contains(context.getString(R.string.no_user_record), ignoreCase = true) == true ||
                                            currentResult.exception.message?.contains(
                                                context.getString(
                                                    R.string.user_not_found
                                                ), ignoreCase = true) == true ->
                                        context.getString(R.string.email_is_not_registered)

                                    currentResult.exception.message?.contains(context.getString(R.string.password_is_invalid), ignoreCase = true) == true ||
                                            currentResult.exception.message?.contains(
                                                context.getString(
                                                    R.string.wrong_password
                                                ), ignoreCase = true) == true ||
                                            currentResult.exception.message?.contains(
                                                context.getString(
                                                    R.string.auth_credential_is_incorrect
                                                ), ignoreCase = true) == true ->
                                        context.getString(R.string.incorrect_email_or_password)

                                    currentResult.exception.message?.contains(context.getString(R.string.network), ignoreCase = true) == true ->
                                        context.getString(R.string.network_error_please_check_your_connection)

                                    else -> context.getString(R.string.authentication_failed_please_try_again)
                                }
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            }
                            else -> {}
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(id = R.string.don_t_have_an_account),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            onNavigateToSignUp()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.button2color))
                    ) {
                        Text(
                            text = stringResource(id = R.string.create_account),
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    GoogleSignInButton {
                        signInLauncher.launch(googleSignInClient.signInIntent)
                    }

                    var googleSignInToastShown by remember { mutableStateOf(false) }
                    var isFromLogout by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        if (authViewModel.isUserAuthenticated()) {
                            isFromLogout = true
                        }
                    }
                    LaunchedEffect(user) {
                        if (user != null && !googleSignInToastShown && !isFromLogout) {
                            Toast.makeText(context,
                                context.getString(R.string.google_sign_in_successful), Toast.LENGTH_SHORT).show()
                            googleSignInToastShown = true
                            onSignInSuccess()
                        }
                    }
               }
            }
        }
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(5.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google Icon",
                tint = Color.Unspecified,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.sign_in_with_google), color = Color.Black)
        }
    }
}

