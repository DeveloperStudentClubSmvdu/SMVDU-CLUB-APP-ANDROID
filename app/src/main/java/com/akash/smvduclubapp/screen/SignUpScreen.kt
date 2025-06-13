package com.akash.smvduclubapp.screen

import android.R.attr.fontWeight
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.MaterialTheme.colorScheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Result
import com.akash.smvduclubapp.viewmodel.AuthViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToMain: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var reenterPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var reenterPasswordVisible by remember { mutableStateOf(false) }
    val authResult by authViewModel.authResult.observeAsState()
    val context = LocalContext.current

    // Regular expression for email validation
    val emailPattern = remember { Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}") }

    LaunchedEffect(authResult) {
        when (authResult) {
            is Result.Success -> {
                // Clear any previous error message
                errorMessage = null
                // Show success toast

                // Clear form fields
                username = ""
                email = ""
                password = ""
                reenterPassword = ""
                onNavigateToMain()
            }
            is Result.Error -> {
                errorMessage = (authResult as Result.Error).exception.message
                Toast.makeText(context, errorMessage ?: context.getString(R.string.an_error_occurred), Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(id = R.string.create_an_account),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 56.dp),
            color = colorResource(id = R.color.theme_color),
        )

        Text(
            text = stringResource(id = R.string.use_your_university_ids),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(id = R.string.name)) },
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 2f
                    val y = size.height - strokeWidth
                    drawLine(
                        color = Color.Gray, // Adjust color if needed
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(id = R.string.email)) },
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val strokeWidth = 2f
                    val y = size.height - strokeWidth
                    drawLine(
                        color = Color.Gray, // Change color if needed
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(stringResource(id = R.string.password)) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = reenterPassword,
            onValueChange = { reenterPassword = it },
            label = { Text(stringResource(id = R.string.re_enter_password)) },
            visualTransformation = if (reenterPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
            trailingIcon = {
                IconButton(onClick = { reenterPasswordVisible = !reenterPasswordVisible }) {
                    Icon(
                        imageVector = if (reenterPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (reenterPasswordVisible) "Hide password" else "Show password"
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
        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Validate all fields before signing up
                when {
                    username.isBlank() -> {
                        errorMessage = context.getString(R.string.please_enter_your_name)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    email.isBlank() -> {
                        errorMessage = context.getString(R.string.please_enter_your_email)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    !emailPattern.matches(email) -> {
                        errorMessage = context.getString(R.string.enter_a_valid_email_address)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    password.isBlank() -> {
                        errorMessage = context.getString(R.string.please_enter_a_password)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    password.length < 7 -> {
                        errorMessage = context.getString(R.string.password_8_characters)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    reenterPassword != password -> {
                        errorMessage = context.getString(R.string.passwords_do_not_match)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        // All validations passed, attempt sign up
                        errorMessage = null
                        // Show loading toast if needed
                        Toast.makeText(context,
                            context.getString(R.string.signing_up_), Toast.LENGTH_SHORT).show()
                        authViewModel.signUp(email, password, username, reenterPassword)
                        // Fields will be cleared by LaunchedEffect when successful
                    }
                }
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
                text = stringResource(id = R.string.sign_up),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        if (showTermsDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                title = { Text(stringResource(R.string.terms_and_conditions)) },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 400.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = stringResource(id = R.string.terms_and_conditions_text),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showTermsDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(id = R.color.theme_color)
                        )
                    ) {
                        Text(stringResource(R.string.accept))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showTermsDialog = false }) {
                        Text(stringResource(R.string.close))
                    }
                }
            )
        }

        TextButton(
            onClick = {
                showTermsDialog = true
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.term_cond),
                color = colorResource(id = R.color.textColor),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        TextButton(
            onClick = {
                onNavigateToLogin()
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.login_here),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}



