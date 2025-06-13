package com.akash.smvduclubapp.screen

import com.akash.smvduclubapp.viewmodel.PasswordResetViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.smvduclubapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    passwordResetViewModel: PasswordResetViewModel,
    onNavigateBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isResetEmailSent by remember { mutableStateOf(false) }

    val isLoading by passwordResetViewModel.isLoading.observeAsState(false)
    val resetResult by passwordResetViewModel.resetResult.observeAsState()

    val context = LocalContext.current

    // Observe the resetResult and update UI accordingly
    resetResult?.let { result ->
        when (result) {
            is com.akash.smvduclubapp.data.Result.Success -> {
                isResetEmailSent = true
                Toast.makeText(context, "Password reset email sent", Toast.LENGTH_SHORT).show()
            }
            is com.akash.smvduclubapp.data.Result.Error -> {
                val errorMessage = result.exception.message
                Toast.makeText(context, "Failed to send reset email: $errorMessage", Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        // Back button
        IconButton(
            onClick = { onNavigateBack() },
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = colorResource(id = R.color.theme_color)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Forgot Password",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.theme_color),
            textAlign = TextAlign.Start,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Enter your email and we'll send you a link to reset your password",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(text = "Email", fontSize = 15.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter your registered email here") },
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

        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            if (isResetEmailSent) {
                Text(
                    text = "Reset link sent! Check your email inbox",
                    color = Color.Green,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Button(
                onClick = {
                    if (email.isBlank()) {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                    } else {
                        passwordResetViewModel.sendPasswordResetEmail(email)
                        // Don't check resetResult here, let the observer handle it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .height(60.dp)
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp), // Reduced from 50.dp
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.theme_color)), // Changed from Color.White
                enabled = !isLoading
            ) {
                Text(
                    text = if (isLoading) "Sending..." else "Send Reset Link",
                    color = Color.White // This is fine since button background is now colored
                )
            }
        }
    }
}