package com.akash.smvduclubapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Result
import com.akash.smvduclubapp.viewmodel.AuthViewModel


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
    val authResult by authViewModel.authResult.observeAsState()

    LaunchedEffect(authResult) {
        if (authResult is Result.Success) {
            onNavigateToMain() // Navigate to MainScreen when signup is successful
        } else if (authResult is Result.Error) {
            errorMessage = (authResult as Result.Error).exception.message
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),

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
            color = Color.Black,
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
            visualTransformation = PasswordVisualTransformation(),
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
            value = reenterPassword,
            onValueChange = { reenterPassword = it },
            label = { Text(stringResource(id = R.string.re_enter_password)) },
            visualTransformation = PasswordVisualTransformation(),
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
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                errorBorderColor = Color.Transparent
            )
        )
        Spacer(modifier = Modifier.height(32.dp))


        Button(
            onClick = {
                authViewModel.signUp(email,password,username,reenterPassword)

                username = ""
                email = ""
                password = ""
                reenterPassword = ""


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
        TextButton(
            onClick = {  },
            modifier = Modifier.padding(top = 8.dp)

        ) {
            Text(
                text = stringResource(id = R.string.term_cond),
                color = colorResource(id = R.color.textColor),
                textAlign = TextAlign.Center, // Center the text horizontally
                modifier = Modifier.fillMaxWidth() // Take up the full width of the parent
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
                color = colorResource(id = R.color.theme_color),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center, // Center the text horizontally
                modifier = Modifier.fillMaxWidth() // Take up the full width of the parent
            )
        }
    }
}




