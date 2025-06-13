package com.akash.smvduclubapp.screen.mainscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.akash.smvduclubapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VCMessageScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    val isScrolling = remember { derivedStateOf { scrollState.value > 10 } }

    val vcName = stringResource(R.string.vc_name)
    val vcMessage = "     It gives me great pride and pleasure to assume the office of the Vice-Chancellor of Shri Mata Vaishno Devi University (SMVDU) which is situated in the spiritual lap of Maa Bhagwati. The University stands true to its motto,\"विज्ञानं ब्रम्ह\" which means, \"GOD (BRAHMA) IS SCIENCE\". The aim at the University is to evolve into a solution-provider and knowledge-spreading center of higher education in the country and thereby usher in a meaningful and a productive engagement with all stake-holders.\n\nShri Mata Vaishno Devi University has adopted the National Education Policy 2020 (NEP-2020) in letter and spirit. As a first step towards this, we have introduced multi- and cross-disciplinary courses across engineering, science, social science, and humanities. The curriculum has been designed and updated to meet the challenges of the 21st century, while enriching the students learning experience through student-centric teaching methods. I am happy to share that the university is ranked between 101-150 band in the category of Top Engineering Institutions, by NIRF 2023 of the Ministry of MHRD, Govt. of India. The University has also been graded among the Top 26th in the category of Architecture institutions by NIRF 2023, and has been ranked 151-200 among top universities in University Category & Overall Institutions Category in NIRF 2023. The university is committed to maintain high research standards and has witnessed a huge number of publications in high impact factor journals.\n\nThe university has established itself proudly as a learning seat of academia across the nation, and now it stands firmly amidst the reputed universities and premier institutes. This is really a milestone, and many others are to be achieved in due course. No wonder that many luminaries, such as Presidents, Vice-Presidents and Prime Ministers of India, and other academic figures and leaders of national and international importance have graced Shri Mata Vaishno Devi University with their regular visits. The credit goes to the efforts put up by the well-accomplished faculty members and dedicated support staff.\n\nOn a note of exhortation, I am optimistic that the SMVDU fraternity will continue to produce excellent academic ecosystem comprising of scientists, technocrats, visionary leaders, competent researchers and teachers in the fields of science, engineering, humanities, management and social sciences.\n\nJai Mata Di\n\nProf. (Dr.) Pragati Kumar\nVice Chancellor"

    Scaffold(
        topBar = {
            Box(modifier = Modifier.height(0.dp)) {
                AnimatedVisibility(visible = !isScrolling.value) {
                    TopAppBar(
                        title = { Text(text = "Vice Chancellor's Message", fontSize = 20.sp) },
                        navigationIcon = {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    painter = painterResource(R.drawable.baseline_arrow_back_24),
                                    contentDescription = "Back"
                                )
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            val imageSize by animateDpAsState(targetValue = if (isScrolling.value) 70.dp else 120.dp)
            val textSize by animateFloatAsState(targetValue = if (isScrolling.value) 18f else 28f)

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
                    Image(
                        painter = painterResource(id = R.drawable.vcimg),
                        contentDescription = "Vice Chancellor Photo",
                        modifier = Modifier
                            .size(imageSize)
                            .clip(CircleShape),
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = vcName,
                        fontSize = textSize.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),

                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "❝",  // Opening quotation mark
                        fontSize = 60.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        modifier = Modifier
                            .padding(top = 0.dp, start = 0.dp)
                    )
                    Text(
                        text = vcMessage,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify,
                        modifier = Modifier.padding(start = 24.dp, top = 32.dp),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }
    }
}