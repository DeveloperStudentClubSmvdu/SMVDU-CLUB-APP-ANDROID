package com.akash.smvduclubapp.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.EventRegistration
import com.akash.smvduclubapp.data.registerForEvent
import kotlinx.coroutines.launch
import java.util.UUID


@Composable
fun EventRegistrationForm(navController: NavController, eventTitle: String?, eventId: String?) {
    var name by remember { mutableStateOf("") }
    var entryNo by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var isAlreadyRegistered by remember { mutableStateOf(false)}
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val toastHostState = remember { SnackbarHostState() }
    // Combined validation check


    val isEmailValid = remember(email) {
        email.isNotBlank() && email.endsWith("@smvdu.ac.in")
    }

    // Combined validation check
    val isFormValid = remember(name, entryNo, email, phone, department, isEmailValid, isAlreadyRegistered) {
        name.isNotBlank() &&
                entryNo.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                department.isNotBlank() &&
                isEmailValid &&
                !isAlreadyRegistered
    }

    // Validation function that will only be called when the submit button is clicked
    fun validateForm(): Boolean {
        if (email.isNotBlank() && !email.endsWith("@smvdu.ac.in")) {
            Toast.makeText(context, "Enter university email", Toast.LENGTH_SHORT).show()
            return false
        }
        return isFormValid
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp) // Space for the bottom button
        ) {
            // Header with gradient background (similar to EventDetailScreen)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorResource(id = R.color.theme_color),
                                Color(0xFF005F9E)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = stringResource(R.string.registration_form),
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f).padding(end = 36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Registration Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp)
                    .offset(y = (-40).dp) // Move it upward to overlay on the header
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f)
                        .shadow(10.dp, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = colorResource(id = R.color.clubcardcolor)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(25.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = "Register for",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = eventTitle ?: stringResource(id = R.string.event),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.scrim,
                                textAlign = TextAlign.Center
                            )

                        }
                    }
                }
            }

            // Form Fields
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 5.dp).imePadding()
            ) {
                item {
                    // Name Field
                    ModernTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = stringResource(id = R.string.name),
                        leadingIcon = R.drawable.baseline_person_
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Entry Number Field
                    ModernTextField(
                        value = entryNo,
                        onValueChange = { entryNo = it },
                        label = stringResource(id = R.string.entry_number),
                        leadingIcon = R.drawable.baseline_numbers_24
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email Field
                    ModernTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = stringResource(id = R.string.email),
                        leadingIcon = R.drawable.baseline_email_24,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Phone Field
                    ModernTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = stringResource(id = R.string.phone),
                        leadingIcon = R.drawable.baseline_phone_24,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Department Field
                    ModernTextField(
                        value = department,
                        onValueChange = { department = it },
                        label = stringResource(id = R.string.department),
                        leadingIcon = R.drawable.baseline_school_24,
                    )

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // Submit Button fixed at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    if (validateForm()) {
                        scope.launch {
                            isLoading = true

                            // Create registration object
                            val registration = EventRegistration(
                                id = UUID.randomUUID().toString(),
                                event_id = eventId ?: "",
                                event_name = eventTitle ?: "",
                                name = name,
                                entry_no = entryNo,
                                email = email,
                                phone = phone,
                                department = department
                            )

                            // Submit registration
                            val result = registerForEvent(registration)

                            isLoading = false

                            if (result.isSuccess) {
                                isAlreadyRegistered = true
                                Toast.makeText(context, "Registration successful", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Registration failed: ${result.exceptionOrNull()?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.theme_color),
                    disabledContainerColor = Color(0xFF007BFF).copy(alpha = 0.6f)
                ),
                enabled = isFormValid && !isLoading
            ) {
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = if (isAlreadyRegistered)
                            stringResource(id = R.string.already_registered)
                        else stringResource(id = R.string.submit_registration),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: Int? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    ) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Leading Icon if provided
            leadingIcon?.let {
                Icon(
                    painter = painterResource(id = it),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 12.dp)
                )
            }

            // Text field with underline style
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                singleLine = true,
                keyboardOptions = keyboardOptions,
                modifier = Modifier
                    .fillMaxWidth()

                    .drawBehind {
                        val strokeWidth = 1.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = Color.Gray.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = Color.Transparent,
                )
            )
        }
    }
}