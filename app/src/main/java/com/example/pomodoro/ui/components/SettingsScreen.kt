package com.example.pomodoro.ui.components

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material.*
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.pomodoro.R
import com.example.pomodoro.ui.theme.StatusColors

/**
 * Background options for the app
 */
data class BackgroundOption(
    val id: Int,
    val name: String,
    val drawableRes: Int?,
    val solidColor: Color? = null
)

val backgroundOptions = listOf(
    BackgroundOption(0, "Dark", null, Color(0xFF0D0D0D)),
    BackgroundOption(1, "Purple", null, Color(0xFF1A0A2E)),
    BackgroundOption(2, "Blue", null, Color(0xFF0A1A2E)),
    BackgroundOption(3, "Image 1", R.drawable.lofi_bg_1, null),
    BackgroundOption(4, "Image 2", R.drawable.lofi_bg_2, null),
    BackgroundOption(5, "Image 3", R.drawable.lofi_bg_3, null),
    BackgroundOption(6, "Image 4", R.drawable.lofi_bg_4, null)
)

/**
 * Settings screen for background selection
 */
@Composable
fun SettingsScreen(
    selectedBackgroundIndex: Int,
    customBackgroundUri: String?,
    powerSaveMode: Boolean,
    onBackgroundSelected: (Int) -> Unit,
    onPickCustomBackground: () -> Unit,
    onTogglePowerSave: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScalingLazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(
            top = 32.dp,
            bottom = 32.dp
        )
    ) {
        // Power Save Mode
        item {
            PowerSaveToggle(
                isEnabled = powerSaveMode,
                onToggle = onTogglePowerSave
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            Text(
                text = "Background",
                fontSize = 16.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        
        // Colors row
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                backgroundOptions.take(3).forEachIndexed { index, option ->
                    ColorCircle(
                        color = option.solidColor ?: Color.Black,
                        isSelected = selectedBackgroundIndex == index,
                        onClick = { onBackgroundSelected(index) }
                    )
                }
            }
        }
        
        // Images row
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                backgroundOptions.drop(3).forEachIndexed { index, option ->
                    val actualIndex = index + 3
                    ImageCircle(
                        drawableRes = option.drawableRes!!,
                        isSelected = selectedBackgroundIndex == actualIndex,
                        onClick = { onBackgroundSelected(actualIndex) }
                    )
                }
            }
        }
        
        // Custom background
        item {
            Text(
                text = "Custom",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
            )
        }
        
        item {
            CustomBackgroundPicker(
                customUri = customBackgroundUri,
                isSelected = selectedBackgroundIndex == -1 && customBackgroundUri != null,
                onClick = onPickCustomBackground
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        item {
            CompactChip(
                onClick = onBack,
                label = { Text("Back", fontSize = 12.sp) },
                icon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_arrow_back),
                        contentDescription = "Back",
                        modifier = Modifier.size(16.dp)
                    )
                },
                colors = ChipDefaults.secondaryChipColors()
            )
        }
    }
}

@Composable
private fun ColorCircle(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) StatusColors.Focus else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

@Composable
private fun ImageCircle(
    drawableRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) StatusColors.Focus else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun CustomBackgroundPicker(
    customUri: String?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = if (isSelected) StatusColors.Focus else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (customUri != null) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(Uri.parse(customUri))
                    .crossfade(true)
                    .build(),
                contentDescription = "Custom background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text = "+",
                fontSize = 24.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun getBackgroundModifier(selectedIndex: Int): Modifier {
    val option = backgroundOptions.getOrElse(selectedIndex) { backgroundOptions[0] }
    
    return if (option.solidColor != null) {
        Modifier.background(option.solidColor)
    } else {
        Modifier
    }
}

/**
 * Power Save Mode toggle
 */
@Composable
private fun PowerSaveToggle(
    isEnabled: Boolean,
    onToggle: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Power Mode",
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        CompactChip(
            onClick = onToggle,
            label = { 
                Text(
                    text = if (isEnabled) "ON" else "OFF",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ) 
            },
            colors = if (isEnabled) {
                ChipDefaults.primaryChipColors(
                    backgroundColor = Color(0xFF4CAF50)
                )
            } else {
                ChipDefaults.secondaryChipColors()
            }
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = if (isEnabled) "Saves battery" else "Full features",
            fontSize = 10.sp,
            color = Color.White.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
