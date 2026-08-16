package com.agon.app.data.models

enum class SaloneLanguage(val displayName: String, val nativeName: String, val region: String) {
    ENGLISH("English", "English", "Official"),
    KRIO("Krio (Salone)", "Krio", "Freetown & Nationwide Lingua Franca"),
    MENDE("Mende", "Mɛnde yia", "Southern & Eastern Sierra Leone"),
    TEMNE("Temne", "Kətʰɛmnɛ", "Northern & North-Western Sierra Leone"),
    LIMBA("Limba", "Hulimba", "Northern Sierra Leone"),
    FULLAH("Fullah", "Pulaar", "Nationwide & Fouta"),
    KONO("Kono", "Kɔnɔ", "Kono District")
}

object SaloneDictionary {
    fun getGreeting(lang: SaloneLanguage): String = when (lang) {
        SaloneLanguage.ENGLISH -> "Welcome to Salon Na We Yon"
        SaloneLanguage.KRIO -> "Kusheh! Salon Na We Yon"
        SaloneLanguage.MENDE -> "Bua! Salon Na We Yon"
        SaloneLanguage.TEMNE -> "Séké! Salon Na We Yon"
        SaloneLanguage.LIMBA -> "Mwali! Salon Na We Yon"
        SaloneLanguage.FULLAH -> "No ngool-daa! Salon Na We Yon"
        SaloneLanguage.KONO -> "Kusheh! Salon Na We Yon"
    }

    fun getChatsTabLabel(lang: SaloneLanguage): String = when (lang) {
        SaloneLanguage.ENGLISH -> "Chats"
        SaloneLanguage.KRIO -> "Tok Dem (Chats)"
        SaloneLanguage.MENDE -> "Tɔwɛisia"
        SaloneLanguage.TEMNE -> "Kəlɔm"
        SaloneLanguage.LIMBA -> "Hupokothina"
        SaloneLanguage.FULLAH -> "Kalaamuuji"
        SaloneLanguage.KONO -> "Tok-dem"
    }

    fun getCallsTabLabel(lang: SaloneLanguage): String = when (lang) {
        SaloneLanguage.ENGLISH -> "Calls"
        SaloneLanguage.KRIO -> "Kɔl Dem (Calls)"
        SaloneLanguage.MENDE -> "Kɔɔngaa"
        SaloneLanguage.TEMNE -> "Kətɛm"
        SaloneLanguage.LIMBA -> "Hukul"
        SaloneLanguage.FULLAH -> "Noddaango"
        SaloneLanguage.KONO -> "Kɔl"
    }

    fun getStoriesTabLabel(lang: SaloneLanguage): String = when (lang) {
        SaloneLanguage.ENGLISH -> "Stories"
        SaloneLanguage.KRIO -> "Stori / Wetin Dey Kam"
        SaloneLanguage.MENDE -> "Kpɛlɛɛ"
        SaloneLanguage.TEMNE -> "Kəsoma"
        SaloneLanguage.LIMBA -> "Story"
        SaloneLanguage.FULLAH -> "Haala"
        SaloneLanguage.KONO -> "Stori"
    }

    fun getDiscoverTabLabel(lang: SaloneLanguage): String = when (lang) {
        SaloneLanguage.ENGLISH -> "Discover"
        SaloneLanguage.KRIO -> "Fain Kontri (Discover)"
        SaloneLanguage.MENDE -> "Gbaalɛi"
        SaloneLanguage.TEMNE -> "Kəcɛp"
        SaloneLanguage.LIMBA -> "Find"
        SaloneLanguage.FULLAH -> "Yiytu"
        SaloneLanguage.KONO -> "Discover"
    }

    fun getSettingsTabLabel(lang: SaloneLanguage): String = when (lang) {
        SaloneLanguage.ENGLISH -> "Settings"
        SaloneLanguage.KRIO -> "Setin Dem (Settings)"
        SaloneLanguage.MENDE -> "Setingaa"
        SaloneLanguage.TEMNE -> "Kəbap"
        SaloneLanguage.LIMBA -> "Settings"
        SaloneLanguage.FULLAH -> "Teeltagol"
        SaloneLanguage.KONO -> "Settings"
    }

    fun getOnlineStatus(lang: SaloneLanguage): String = when (lang) {
        SaloneLanguage.ENGLISH -> "Online"
        SaloneLanguage.KRIO -> "De ya naw"
        SaloneLanguage.MENDE -> "I na"
        SaloneLanguage.TEMNE -> "Ɔ yi an"
        SaloneLanguage.LIMBA -> "Yan"
        SaloneLanguage.FULLAH -> "O mo don"
        SaloneLanguage.KONO -> "De ya"
    }

    fun getTypingText(lang: SaloneLanguage, name: String): String = when (lang) {
        SaloneLanguage.ENGLISH -> "$name is typing..."
        SaloneLanguage.KRIO -> "$name dey rayt tok..."
        SaloneLanguage.MENDE -> "$name lɔi nyɛi..."
        SaloneLanguage.TEMNE -> "$name ɔ som..."
        SaloneLanguage.LIMBA -> "$name yan..."
        SaloneLanguage.FULLAH -> "$name ina winda..."
        SaloneLanguage.KONO -> "$name dey rait..."
    }
}
