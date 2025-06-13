package com.akash.smvduclubapp.screen.mainscreen

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.akash.smvduclubapp.R
import com.akash.smvduclubapp.data.Club
import com.akash.smvduclubapp.data.ClubCategory
import com.akash.smvduclubapp.data.fetchClubCategories
import com.akash.smvduclubapp.data.fetchClubs
import com.akash.smvduclubapp.data.fetchClubsByCategory
import com.akash.smvduclubapp.screen.ClubItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityHeadScreen(navController: NavController, categoryId: Long) {
    val scrollState = rememberScrollState()
    val isScrolling = remember { derivedStateOf { scrollState.value > 0 } }

    var category by remember { mutableStateOf<ClubCategory?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(categoryId) {
        scope.launch {
            try {
                val categories = fetchClubCategories()
                category = categories.find { it.id == categoryId }
            } catch (e: Exception) {
                // Handle error
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.height(0.dp)) {
                AnimatedVisibility(visible = !isScrolling.value) {
                    TopAppBar(
                        title = { Text(text = category?.name ?: stringResource(R.string.club_category), fontSize = 20.sp) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                                    contentDescription = stringResource(R.string.back)
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (category != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                val imageSize by animateDpAsState(targetValue = if (isScrolling.value) 70.dp else 120.dp)
                val textSize by animateFloatAsState(targetValue = if (isScrolling.value) 16f else 20f)

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        if (!category?.logo.isNullOrEmpty()) {
                            AsyncImage(
                                model = category?.logo,
                                contentDescription = stringResource(R.string.category_logo),
                                modifier = Modifier
                                    .size(imageSize)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Face,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(imageSize)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = category?.name ?: stringResource(R.string.club_category),
                            fontSize = textSize.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = category?.description ?: stringResource(R.string.no_description_available),
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Text(
                    text = "📝 Message from Head",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 20.dp, bottom = 2.dp)
                )

                if (!category?.head_name.isNullOrEmpty() || !category?.head_message.isNullOrEmpty()) {
                    var isMessageExpanded by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { isMessageExpanded = !isMessageExpanded },
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            if (isMessageExpanded) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (!category?.head_image.isNullOrEmpty()) {
                                            AsyncImage(
                                                model = category?.head_image,
                                                contentDescription = stringResource(R.string.head_photo),
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(RoundedCornerShape(8.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Face,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                                    .padding(12.dp),
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = category?.head_name ?: stringResource(R.string.head_name),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )

                                            Text(
                                                text = category?.head_designation ?: stringResource(
                                                    R.string.head_designation
                                                ),
                                                fontSize = 14.sp,
                                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = category?.head_message ?: stringResource(R.string.no_message_available),
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Justify,
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (!category?.head_image.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = category?.head_image,
                                            contentDescription = stringResource(R.string.head_photo),
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Face,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(100.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                                .padding(16.dp),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = category?.head_name ?: stringResource(R.string.head_name),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )

                                        Text(
                                            text = category?.head_designation ?: stringResource(R.string.head_designation),
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        val headMessage = category?.head_message ?: stringResource(R.string.no_message_available)
                                        val truncatedHeadMessage = headMessage.split("\\s+".toRegex())
                                            .take(10)
                                            .joinToString(" ") + "..."

                                        Column {
                                            Text(
                                                text = truncatedHeadMessage,
                                                fontSize = 14.sp,
                                                textAlign = TextAlign.Justify
                                            )

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Text(
                                                text = stringResource(R.string.read_more),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.align(Alignment.End)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ClubsList(categoryId = categoryId, navController = navController)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.category_not_found), fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun ClubsList(categoryId: Long, navController: NavController) {
    var clubs by remember { mutableStateOf<List<Club>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Variable to check if clubs exist
    val clubsExist = clubs != null && clubs!!.isNotEmpty()

    LaunchedEffect(categoryId) {
        try {
            isLoading = true
            clubs = fetchClubsByCategory(categoryId)
            isLoading = false
        } catch (e: Exception) {
            error = e.message
            isLoading = false
        }
    }

    // Only show clubs section if loading or if clubs exist
    if (isLoading || clubsExist || error != null) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.clubs),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 20.dp, top = 8.dp, bottom = 8.dp)
            )

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Text(
                        text = "Error loading clubs: $error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                clubs.isNullOrEmpty() -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.no_clubs_found_for_this_category),
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    clubs?.forEach { club ->
                        ClubItem(club, navController)
                    }
                }
            }
        }
    }
}

@Composable
fun ClubItem(club: Club, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable {
                navController.navigate("clubDetails/${club.clubId}")
            },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!club.club_logo.isNullOrEmpty()) {
                AsyncImage(
                    model = club.club_logo,
                    contentDescription = stringResource(R.string.club_logo),
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Groups,
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = club.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                   // color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = club.description?.take(100)?.let { if (it.length == 100) "$it..." else it }
                        ?: "No description available",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}