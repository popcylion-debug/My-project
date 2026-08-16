package com.agon.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agon.app.data.models.AccountTier
import com.agon.app.ui.theme.SaloneAtlanticBlue
import com.agon.app.ui.theme.SaloneEmeraldPrimary
import com.agon.app.ui.theme.SaloneGold
import com.agon.app.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPlan by remember { mutableStateOf("Yearly") } // Monthly, Yearly, Lifetime
    var selectedPaymentMethod by remember { mutableStateOf("Orange Money (+232)") }
    var showPaymentSuccessDialog by remember { mutableStateOf(false) }

    val perks = listOf(
        "🚀 4GB Maximum File & Media Uploads (vs 100MB)",
        "⭐ Gold Verified Salone Crest Badge on Profile",
        "🌐 Unlimited Krio & Tribal AI Real-time Translations",
        "🎨 Exclusive Cotton Tree & Gold Animated Themes",
        "🎙️ Voice-to-Text Transcription for Sierra Leone Accents",
        "🔒 Enhanced 256-bit Cloud Backup & Priority Sockets"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Salon Na We Yon VIP 🇸🇱", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gold Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SaloneGold.copy(alpha = 0.2f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(SaloneGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Upgrade to Salon Na We Yon VIP", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Unlock high speed, 4GB files, and full AI capabilities across Sierra Leone.", fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Subscription Plans
            item {
                Text("Select Membership Plan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PlanOptionCard(
                        title = "Annual VIP Pass (Best Value 🔥)",
                        price = "NLe 1,400 / year ($70)",
                        subtext = "Save 25% • Includes Business Hub Access",
                        isSelected = selectedPlan == "Yearly",
                        onClick = { selectedPlan = "Yearly" }
                    )
                    PlanOptionCard(
                        title = "Monthly VIP Pass",
                        price = "NLe 150 / month ($8)",
                        subtext = "Cancel anytime",
                        isSelected = selectedPlan == "Monthly",
                        onClick = { selectedPlan = "Monthly" }
                    )
                    PlanOptionCard(
                        title = "Lifetime Founder Pass",
                        price = "NLe 3,500 ($175)",
                        subtext = "One-time payment • Permanent Gold Badge",
                        isSelected = selectedPlan == "Lifetime",
                        onClick = { selectedPlan = "Lifetime" }
                    )
                }
            }

            // Perks List
            item {
                Text("VIP Features Included", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        perks.forEach { perk ->
                            Text(text = perk, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Payment Methods
            item {
                Text("Payment Method (Sierra Leone & Global)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(6.dp))

                listOf("Orange Money (+232)", "AfriMoney (+232)", "Debit / Credit Card (Visa/Mastercard)", "PayPal / Crypto").forEach { method ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selectedPaymentMethod = method },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedPaymentMethod == method) SaloneEmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedPaymentMethod == method,
                                onClick = { selectedPaymentMethod = method }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(method, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Pay Button
            item {
                Button(
                    onClick = {
                        viewModel.repository.upgradeAccount(AccountTier.VIP_PLUS, selectedPaymentMethod)
                        showPaymentSuccessDialog = true
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pay with $selectedPaymentMethod", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    if (showPaymentSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showPaymentSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SaloneEmeraldPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("VIP Activated! 🇸🇱")
                }
            },
            text = {
                Text("Congratulations! Your Salon Na We Yon account is now upgraded to VIP. Enjoy 4GB media transfers, gold badge and unlimited Salone AI translations.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPaymentSuccessDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaloneEmeraldPrimary)
                ) {
                    Text("Awesome!")
                }
            }
        )
    }
}

@Composable
fun PlanOptionCard(
    title: String,
    price: String,
    subtext: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) SaloneEmeraldPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, SaloneEmeraldPrimary) else null
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onClick)
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(subtext, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(price, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = SaloneEmeraldPrimary)
        }
    }
}
