package com.akash.smvduclubapp.screen.mainscreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.akash.smvduclubapp.R

@Composable
fun ViceChancellorMessageSection(
    navController: NavController,
    vcName: String = stringResource(R.string.vc_name),
    vcMessage: String = "\"It gives me great pride and pleasure to assume the office of the Vice-Chancellor of Shri Mata Vaishno Devi University (SMVDU) which is situated in the spiritual lap of Maa Bhagwati. The University stands true to its motto,”विज्ञानं ब्रम्ह” which means, “GOD (BRAHMA) IS SCIENCE”. The aim at the University is to evolve into a solution-provider and knowledge-spreading center of higher education in the country and thereby usher in a meaningful and a productive engagement with all stake-holders.\n" +
            "\n" +
            "Shri Mata Vaishno Devi University has adopted the National Education Policy 2020 (NEP-2020) in letter and spirit. As a first step towards this, we have introduced multi- and cross-disciplinary courses across engineering, science, social science, and humanities. The curriculum has been designed and updated to meet the challenges of the 21st century, while enriching the students learning experience through student-centric teaching methods. I am happy to share that the university is ranked between 101-150 band in the category of Top Engineering Institutions, by NIRF 2023 of the Ministry of MHRD, Govt. of India. The University has also been graded among the Top 26th in the category of Architecture institutions by NIRF 2023, and has been ranked 151-200 among top universities in University Category & Overall Institutions Category in NIRF 2023. The university is committed to maintain high research standards and has witnessed a huge number of publications in high impact factor journals.\n" +
            "\n" +
            "The university has established itself proudly as a learning seat of academia across the nation, and now it stands firmly amidst the reputed universities and premier institutes. This is really a milestone, and many others are to be achieved in due course. No wonder that many luminaries, such as Presidents, Vice-Presidents and Prime Ministers of India, and other academic figures and leaders of national and international importance have graced Shri Mata Vaishno Devi University with their regular visits. The credit goes to the efforts put up by the well-accomplished faculty members and dedicated support staff.\n" +
            "\n" +
            "On a note of exhortation, I am optimistic that the SMVDU fraternity will continue to produce excellent academic ecosystem comprising of scientists, technocrats, visionary leaders, competent researchers and teachers in the fields of science, engineering, humanities, management and social sciences.\n" +
            "\n" +
            "Jai Mata Di\n" +
            "\n" +
            "Prof. (Dr.) Pragati Kumar Vice Chancellor",
) {

    val truncatedMessage = vcMessage.split("\\s+".toRegex())
        .take(20)
        .joinToString(" ") + "..."

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(
            text = "📝 Voices of Vision",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable{ navController.navigate("vcMessage")},
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.vcimg),
                    contentDescription = "Vice Chancellor Photo",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = vcName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = truncatedMessage,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Justify,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Read more...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable { navController.navigate("vcMessage") }
                    )
                }
            }
        }
    }
}

