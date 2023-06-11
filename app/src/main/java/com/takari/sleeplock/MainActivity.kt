package com.takari.sleeplock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.takari.sleeplock.ui.theme.DarkBackground
import com.takari.sleeplock.ui.theme.DarkLight
import com.takari.sleeplock.ui.theme.SleepLockTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SleepLockTheme {
                HomeScreen()
            }
        }
    }
}


@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {

        Box {

            Image(
                painter = painterResource(id = R.drawable.ic_wave),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .size(200.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.good_night),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 50.dp, start = 50.dp)
                    .height(175.dp)
                    .width(150.dp)
            )
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp),
        ) {

            SleepFeature(
                imageID = R.drawable.white_noise_icon,
                title = "White Noise",
                description = "Doze off to mellow sounds",
                onClick = {}
            )

            SleepFeature(
                imageID = R.drawable.sleep_timer_icon,
                title = "Sleep Timer",
                description = "Mute and sleep the device (perfect for videos)",
                onClick = {}
            )
        }
    }
}

@Composable
fun SleepFeature(
    imageID: Int = R.drawable.white_noise_icon,
    title: String = "White Noise",
    description: String = "Doze off to mellow sounds",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(250.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = DarkLight),
        elevation = CardDefaults.cardElevation(16.dp)
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {

            Image(
                painter = painterResource(id = imageID),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 5.dp)
                    .size(100.dp)
            )

            Text(
                text = title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 5.dp, bottom = 10.dp)
            )

            Spacer(
                modifier = Modifier
                    .background(Color.White)
                    .height(1.dp)
                    .width(150.dp)
            )

            Text(
                text = description,
                color = Color.White,
                modifier = Modifier.padding(top = 20.dp, bottom = 15.dp),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SleepLockTheme {
        HomeScreen()
    }
}