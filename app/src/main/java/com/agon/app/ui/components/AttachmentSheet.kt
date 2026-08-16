package com.agon.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.ui.theme.SaloneAtlanticBlue
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold

data class AttachmentOption(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val actionType: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentBottomSheet(
    onDismiss: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf(
        AttachmentOption("Gallery / Photos", Icons.Default.Image, SaloneEmeraldPrimary, "PHOTO"),
        AttachmentOption("Camera", Icons.Default.CameraAlt, Color(0xFFE11D48), "CAMERA"),
        AttachmentOption("Document / PDF", Icons.Default.InsertDriveFile, SaloneAtlanticBlue, "DOCUMENT"),
        AttachmentOption("Audio / Music", Icons.Default.Headphones, SaloneGold, "AUDIO"),
        AttachmentOption("Share Location", Icons.Default.LocationOn, Color(0xFF10B981), "LOCATION"),
        AttachmentOption("Contact Card", Icons.Default.Person, Color(0xFF8B5CF6), "CONTACT"),
        AttachmentOption("Create Poll", Icons.Default.Poll, Color(0xFFF97316), "POLL")
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Share Content 🇸🇱",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val rows = options.chunked(3)
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    rowItems.forEach { option ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(90.dp)
                                .clickable {
                                    onOptionSelected(option.actionType)
                                    onDismiss()
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(option.color.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = option.title,
                                    tint = option.color,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = option.title,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
