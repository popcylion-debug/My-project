package com.agon.app.data.models

import kotlinx.serialization.Serializable

@Serializable
data class CatalogProduct(
    val id: String,
    val name: String,
    val priceText: String, // e.g. "NLe 450" / "$25"
    val description: String,
    val imageUrl: String? = null,
    val category: String = "Apparel"
)

@Serializable
data class BusinessProfileData(
    val businessName: String = "Salone Tech & Garments Hub",
    val businessCategory: String = "Technology & Retail",
    val description: String = "Official Sierra Leone tech accessories, traditional Ronko wear, and premier electronics delivery across Freetown, Bo, Kenema & Makeni.",
    val address: String = "24 Siaka Stevens Street, Freetown, Sierra Leone",
    val businessHours: String = "Mon - Sat: 8:00 AM - 7:00 PM GMT",
    val website: String = "https://salonnaweyon.sl/business/hub",
    val supportEmail: String = "support@salonnaweyon.sl",
    val catalog: List<CatalogProduct> = listOf(
        CatalogProduct("p1", "Authentic Salone Cotton Tree Jersey", "NLe 350", "Original Sierra Leone Green-White-Blue athletic breathable jersey."),
        CatalogProduct("p2", "Salone Na We Yon VIP Metal SIM NFC Badge", "NLe 200", "Instant contact sharing NFC card."),
        CatalogProduct("p3", "Smart Freetown Sound Wireless Earbuds", "NLe 650", "Noise cancelling wireless earbuds with Salone Bass Boost.")
    ),
    val quickReplies: List<String> = listOf(
        "Kusheh! Thank you for reaching out to Salone Tech. How can we assist you today?",
        "We deliver everywhere in Freetown within 2 hours and nationwide via courier.",
        "Payment is accepted via Orange Money (+232 76 000 000), AfriMoney, or Card.",
        "Your order has been dispatched and is currently on the way!"
    ),
    val customerLabels: List<String> = listOf("New Inquiry", "Paid Order", "VIP Customer", "Wholesale Buyer", "Follow-up Needed")
)

@Serializable
data class ChatBusinessMeta(
    val chatId: String,
    val assignedAgent: String = "Aminata Bangura",
    val activeLabel: String = "VIP Customer",
    val staffNotes: String = "Prefers delivery in Aberdeen, Freetown. Orders every Friday."
)
