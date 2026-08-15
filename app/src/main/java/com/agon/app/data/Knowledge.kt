package com.agon.app.data

data class KnowledgeHit(val title: String, val body: String, val tags: List<String>)

object Knowledge {
    val corpus: List<KnowledgeHit> = listOf(
        KnowledgeHit(
            "Country",
            "Sierra Leone is a West African nation on the Atlantic. The capital is Freetown. Independence from Britain came on 27 April 1961. The republic was declared in 1971. The leone (SLE) is the currency after the 2022 redenomination. The motto is Unity, Freedom, Justice. Population is a little over 8 million.",
            listOf("country", "independence", "capital", "leone", "currency", "motto", "west africa"),
        ),
        KnowledgeHit(
            "Name and flag",
            "The name Sierra Leone comes from Portuguese Serra Lyoa — Lion Mountains — given by Pedro de Sintra around 1462 when he saw the rumbling hills above the peninsula. The flag is a horizontal tricolour: green for agriculture and mountains, white for unity and justice, blue for the harbour and the hope of contributing to world peace.",
            listOf("name", "flag", "lion", "serra lyoa", "pedro", "green", "white", "blue"),
        ),
        KnowledgeHit(
            "Geography",
            "Sierra Leone sits between Guinea and Liberia. The Freetown Peninsula has forested mountains dropping to Atlantic beaches. Inland you find rain forest, woodland savanna, and the Loma Mountains. Mount Bintumani (Loma Mansa) is the highest peak at about 1,945 metres. Major rivers include the Rokel/Seli, Moa, Sewa, Jong, Little Scarcies and Great Scarcies.",
            listOf("geography", "guinea", "liberia", "bintumani", "loma", "rivers", "rokel", "sewa", "moa"),
        ),
        KnowledgeHit(
            "Provinces and districts",
            "The country has five administrative regions: Eastern, Northern, North West, Southern, and Western Area. There are 16 districts. Bo is the largest city in the south and a trading hub. Kenema is the east’s commercial heart. Makeni serves the north. Port Loko and Kambia sit toward Guinea. Kono is famous for diamonds. Pujehun and Bonthe face the southern coast and islands.",
            listOf("province", "district", "bo", "kenema", "makeni", "kono", "port loko", "bonthe", "western"),
        ),
        KnowledgeHit(
            "Bo City",
            "Bo is the capital of the Southern Province and of Bo District. People call it the reservation city and the heart of Mende country. It grew as a railway and trading town. Njala University has a campus nearby. Bo Government Hospital and a busy lorry park sit in the centre. Henry Tucker of Bo City built Salon Na We Yon so people from Bo and the rest of Salone can talk from anywhere.",
            listOf("bo", "southern", "mende", "njala", "henry", "tucker", "reservation"),
        ),
        KnowledgeHit(
            "Freetown",
            "Freetown was founded in 1792 by the Sierra Leone Company for freed Black people from Nova Scotia, later joined by Maroons from Jamaica and recaptives from intercepted slave ships. The Cotton Tree in the old city is the living monument of that founding. Fourah Bay College (1827) is West Africa’s oldest western-style university. The harbour is one of the world’s largest natural harbours.",
            listOf("freetown", "cotton tree", "fourah bay", "nova scotia", "maroons", "harbour", "1792"),
        ),
        KnowledgeHit(
            "People and languages",
            "Sierra Leone is a mosaic. The two largest groups are the Temne (north and north-west) and the Mende (south and east). Others include Limba, Kono, Koranko, Fullah, Mandingo, Kissi, Loko, Sherbro, Susu, Yalunka, Vai, Krim and Gola. Krio people, descendants of liberated Africans, shaped Freetown. Krio is the national lingua franca. English is the official language. Many people also speak their ethnic language at home.",
            listOf("tribe", "mende", "temne", "limba", "krio", "kono", "language", "people", "ethnic"),
        ),
        KnowledgeHit(
            "Krio language",
            "Krio is an English-based creole with words from Yoruba, Igbo, Akan and local languages. Greetings: How di body? (how are you) — Di body fine. Kushe / kushe-o is a warm hello. Tɛnki means thank you. A de kam means I am coming. Wetin na yu nem? asks your name. Salone na we yon means Sierra Leone is ours. It is the everyday bridge across tribes.",
            listOf("krio", "how di body", "kushe", "tenki", "language", "creole", "salone na we yon"),
        ),
        KnowledgeHit(
            "Food",
            "The national plate is rice with plasas — a palaver sauce of greens. Cassava leaf stew cooked with palm oil, egusi or groundnut, fish or beef, is the Sunday classic. Potato leaf, crain crain (jute mallow), okra soup, groundnut stew and pepper soup are daily food. Jollof rice appears at parties. Street food includes fry fry, rolex-style wraps, roasted cassava, agidi, akara and grilled fish. Palm wine (poyo) is tapped from oil palm.",
            listOf("food", "cassava", "plasas", "jollof", "groundnut", "potato leaf", "poyo", "palm", "rice"),
        ),
        KnowledgeHit(
            "History before colony",
            "The coast was part of long-distance trade well before Europeans. Temne, Bullom and other peoples lived on the peninsula. Inland Mende polities and northern kingdoms rose and fell. Portuguese sailors arrived in the 15th century. Later the British used the river as a base against the slave trade after 1807, settling recaptives in villages such as Regent, Gloucester, Leicester and Bathurst.",
            listOf("history", "portuguese", "slave", "recaptive", "temne", "mende", "colony"),
        ),
        KnowledgeHit(
            "Independence and modern politics",
            "Independence: 27 April 1961, first Prime Minister Sir Milton Margai. The SLPP and later the APC have been the main parties. Siaka Stevens led a long one-party era. Multiparty politics returned in the 1990s. The civil war (1991–2002) devastated the country. Since the war, elections have transferred power between SLPP and APC. The current republic uses a president, parliament and local councils.",
            listOf("independence", "milton margai", "slpp", "apc", "stevens", "president", "politics"),
        ),
        KnowledgeHit(
            "Civil war",
            "The Revolutionary United Front crossed from Liberia in 1991. The war lasted until 2002, with terrible abuse of civilians, child soldiers and the infamous amputation campaigns. ECOMOG, later UNAMSIL, and British intervention in 2000 helped end the fighting. The Lomé Peace Accord and a Truth and Reconciliation Commission tried to rebuild. January 18 is now a day of reflection. Never again is a living promise.",
            listOf("war", "ruf", "unamsil", "ecomog", "lome", "peace", "2002"),
        ),
        KnowledgeHit(
            "Ebola and COVID",
            "The 2014–2016 Ebola outbreak killed thousands in Sierra Leone, Guinea and Liberia. Health workers, burial teams and communities stopped it at huge cost. COVID-19 later tested the rebuilt health system. People remember hand-washing stations, lock-downs and the radio as a lifeline.",
            listOf("ebola", "covid", "health", "outbreak"),
        ),
        KnowledgeHit(
            "Diamonds and minerals",
            "Kono, Kenema and Tongo fields made Sierra Leone famous for diamonds — and infamous through blood diamonds during the war. The Kimberley Process later tried to certify clean stones. The country also has rutile, bauxite, iron ore (Tonkolili, Marampa), gold and promising offshore oil talk. Mining wealth has not always reached ordinary people; that debate is still live.",
            listOf("diamond", "kono", "rutile", "bauxite", "iron", "gold", "mining", "kimberley"),
        ),
        KnowledgeHit(
            "Wildlife and parks",
            "Outamba-Kilimi in the north has hippos and primates. Gola Rainforest on the Liberian border is a bird paradise and chimpanzee home. Tiwai Island in the Moa River is a community wildlife sanctuary. Tacugama Chimpanzee Sanctuary above Freetown rescues orphans. Sea turtles nest on the peninsula beaches. The pygmy hippo still hides in southern forests.",
            listOf("wildlife", "gola", "tiwai", "tacugama", "chimpanzee", "hippo", "park", "turtle"),
        ),
        KnowledgeHit(
            "Beaches and islands",
            "River Number Two, Bureh, Lumley, Lakka, Tokeh and Kent are loved peninsula beaches. Banana Islands sit off Kent with colonial ruins and diving. The Turtle Islands and Sherbro Island lie to the south. Bunce Island in the Sierra Leone River was a major slave-trading fort — a place of pilgrimage and grief.",
            listOf("beach", "river number two", "bureh", "banana", "bunce", "sherbro", "tokeh", "lumley"),
        ),
        KnowledgeHit(
            "Music",
            "Palm-wine guitar, gumbe, and later Afro-pop shaped Salone sound. S.E. Rogie made 'My Lovely Elizabeth' a world song. Dr. Oloh, AfroNational, and the Civil War-era refugee musicians carried the flag. Today artists mix Krio rap, gospel, afrobeats and traditional bundu or socoh rhythms. Radio still rules the provinces.",
            listOf("music", "rogie", "oloh", "gumbe", "afrobeats", "radio", "krio rap"),
        ),
        KnowledgeHit(
            "Football",
            "The Leone Stars are the national team. Football is a national religion — street games, academy sides, and Premier League clubs. Famous names include Mohamed Kallon, Kei Kamara, and a new generation in Europe. Siaka Stevens Stadium (national stadium) in Freetown hosts big nights. When the Stars play, the whole country stops.",
            listOf("football", "leone stars", "kallon", "kei kamara", "stadium"),
        ),
        KnowledgeHit(
            "Religion",
            "Sierra Leone is known for interfaith peace. A Muslim majority lives beside a large Christian community and people who keep traditional practices. Mosques and churches share streets. The Inter-Religious Council helped during the war and Ebola. Greeting a neighbour on Friday or Sunday is ordinary courtesy.",
            listOf("religion", "muslim", "christian", "interfaith", "mosque", "church"),
        ),
        KnowledgeHit(
            "Education",
            "Fourah Bay College, Njala University, University of Makeni, Eastern Technical University (Kenema), Milton Margai and many teacher colleges train the next generation. The 1990s war smashed schools; rebuilding is unfinished. Free Quality School Education is a major national policy. Krio and English mix in every classroom.",
            listOf("school", "university", "fourah bay", "njala", "education", "fqse"),
        ),
        KnowledgeHit(
            "Economy and work",
            "Most people farm rice, cassava, oil palm, cocoa, coffee and groundnuts. Freetown works in trade, ports, government and services. Remittances from the diaspora keep many homes. The informal market — Lumley, PZ, Bo town, Kenema Main — is the real economy. Okada (motorbike taxi) and keke keep cities moving.",
            listOf("economy", "rice", "cocoa", "okada", "keke", "market", "diaspora", "work"),
        ),
        KnowledgeHit(
            "Climate",
            "A tropical monsoon climate: a heavy rainy season roughly May to November and a dry harmattan-influenced season December to April. August can drown Freetown streets. Farmers time rice nurseries to the first serious rains. Deforestation on the peninsula has made flooding and landslides worse — the 2017 Regent/Sugar Loaf disaster is remembered.",
            listOf("climate", "rain", "harmattan", "flood", "landslide", "regent"),
        ),
        KnowledgeHit(
            "How to greet",
            "In Krio: Kushe-o! How di body? Reply: Di body fine, tɛnki. In Mende a morning greeting is Bua / Bi wa. Temne speakers use To-məni or similar local forms. Always greet before you ask a favour. Use auntie, uncle, boss, or chief with respect for older people.",
            listOf("greet", "hello", "kushe", "bua", "manners", "respect"),
        ),
        KnowledgeHit(
            "Travel tips",
            "Visitors need a passport and usually a visa or visa-on-arrival arrangement — check current rules. Yellow fever vaccination is often required. The leone is used everywhere; US dollars appear in bigger hotels. Leona / Orange / Africell SIMs are cheap. Roads from Freetown to Bo and Kenema are the main arteries. Rainy season travel takes longer. Dress modestly outside beach towns.",
            listOf("travel", "visa", "yellow fever", "sim", "road", "bo", "kenema"),
        ),
        KnowledgeHit(
            "This app",
            "Salon Na We Yon means Sierra Leone is ours. It is a worldwide chat house for real Salone people — messages, photos, videos, voice rooms and 24-hour status. An Ask SL guide answers questions about the country. Developed by Henry Tucker from Bo City, Sierra Leone. Accounts stay on this device and your words travel on the live network to everyone else using the same app.",
            listOf("app", "henry", "tucker", "salon na we yon", "about", "developer"),
        ),
    )

    fun answer(question: String): String {
        val q = question.lowercase().trim()
        if (q.isBlank()) return "Ask me anything about Sierra Leone — history, food, language, districts, or home."
        val scored = corpus.map { hit ->
            val hay = (hit.title + " " + hit.body + " " + hit.tags.joinToString(" ")).lowercase()
            val words = q.split(Regex("[^a-z0-9]+")).filter { it.length > 2 }
            val score = words.count { hay.contains(it) } +
                hit.tags.count { tag -> q.contains(tag) } * 2
            hit to score
        }.sortedByDescending { it.second }
        val best = scored.take(3).filter { it.second > 0 }
        if (best.isEmpty()) {
            return "I do not have a precise page for that yet, but I can talk about Freetown, Bo, Krio, cassava leaf, the flag, diamonds, Gola forest, Leone Stars, or the war and peace years. Ask again with one of those."
        }
        return buildString {
            best.forEachIndexed { i, pair ->
                if (i > 0) append("\n\n")
                append(pair.first.body)
            }
        }
    }
}
