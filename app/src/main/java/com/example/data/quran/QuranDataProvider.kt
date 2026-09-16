package com.example.data.quran

import com.example.data.model.AzkarCategory
import com.example.data.model.Juz
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.model.ZikrItem

object QuranDataProvider {

    val surahs: List<Surah> = listOf(
        Surah(1, "الفَاتِحَة", "Al-Fatihah", "مكية", 7, 1, 1, "The Opening"),
        Surah(2, "البَقَرَة", "Al-Baqarah", "مدنية", 286, 2, 1, "The Cow"),
        Surah(3, "آل عِمْرَان", "Aal-E-Imran", "مدنية", 200, 50, 3, "Family of Imran"),
        Surah(4, "النِّسَاء", "An-Nisa", "مدنية", 176, 77, 4, "The Women"),
        Surah(5, "المَائِدَة", "Al-Ma'idah", "مدنية", 120, 106, 6, "The Table Spread"),
        Surah(6, "الأَنْعَام", "Al-An'am", "مكية", 165, 128, 7, "The Cattle"),
        Surah(7, "الأَعْرَاف", "Al-A'raf", "مكية", 206, 151, 8, "The Heights"),
        Surah(8, "الأَنْفَال", "Al-Anfal", "مدنية", 75, 177, 9, "The Spoils of War"),
        Surah(9, "التَّوْبَة", "At-Tawbah", "مدنية", 129, 187, 10, "The Repentance"),
        Surah(10, "يُونُس", "Yunus", "مكية", 109, 208, 11, "Jonah"),
        Surah(11, "هُود", "Hud", "مكية", 123, 221, 11, "Hud"),
        Surah(12, "يُوسُف", "Yusuf", "مكية", 111, 235, 12, "Joseph"),
        Surah(13, "الرَّعْد", "Ar-Ra'd", "مدنية", 43, 249, 13, "The Thunder"),
        Surah(14, "إِبْرَاهِيم", "Ibrahim", "مكية", 52, 255, 13, "Abraham"),
        Surah(15, "الحِجْر", "Al-Hijr", "مكية", 99, 262, 14, "The Rocky Tract"),
        Surah(16, "النَّحْل", "An-Nahl", "مكية", 128, 267, 14, "The Bee"),
        Surah(17, "الإِسْرَاء", "Al-Isra", "مكية", 111, 282, 15, "The Night Journey"),
        Surah(18, "الكَهْف", "Al-Kahf", "مكية", 110, 293, 15, "The Cave"),
        Surah(19, "مَرْيَم", "Maryam", "مكية", 98, 305, 16, "Mary"),
        Surah(20, "طه", "Ta-Ha", "مكية", 135, 312, 16, "Ta-Ha"),
        Surah(21, "الأَنْبِيَاء", "Al-Anbiya", "مكية", 112, 322, 17, "The Prophets"),
        Surah(22, "الحَجّ", "Al-Hajj", "مدنية", 78, 332, 17, "The Pilgrimage"),
        Surah(23, "المُؤْمِنُون", "Al-Mu'minun", "مكية", 118, 342, 18, "The Believers"),
        Surah(24, "النُّور", "An-Nur", "مدنية", 64, 350, 18, "The Light"),
        Surah(25, "الفُرْقَان", "Al-Furqan", "مكية", 77, 359, 18, "The Criterion"),
        Surah(26, "الشُّعَرَاء", "Ash-Shu'ara", "مكية", 227, 367, 19, "The Poets"),
        Surah(27, "النَّمْل", "An-Naml", "مكية", 93, 377, 19, "The Ant"),
        Surah(28, "القَصَص", "Al-Qasas", "مكية", 88, 385, 20, "The Stories"),
        Surah(29, "العَنْكَبُوت", "Al-Ankabut", "مكية", 69, 396, 20, "The Spider"),
        Surah(30, "الرُّوم", "Ar-Rum", "مكية", 60, 404, 21, "The Romans"),
        Surah(31, "لُقْمَان", "Luqman", "مكية", 34, 411, 21, "Luqman"),
        Surah(32, "السَّجْدَة", "As-Sajdah", "مكية", 30, 415, 21, "The Prostration"),
        Surah(33, "الأَحْزَاب", "Al-Ahzab", "مدنية", 73, 418, 21, "The Combined Forces"),
        Surah(34, "سَبَأ", "Saba", "مكية", 54, 428, 22, "Sheba"),
        Surah(35, "فَاطِر", "Fatir", "مكية", 45, 434, 22, "Originator"),
        Surah(36, "يس", "Ya-Sin", "مكية", 83, 440, 22, "Ya-Sin"),
        Surah(37, "الصَّافَّات", "As-Saffat", "مكية", 182, 446, 23, "Those who set the Ranks"),
        Surah(38, "ص", "Sad", "مكية", 88, 453, 23, "The Letter 'Saad'"),
        Surah(39, "الزُّمَر", "Az-Zumar", "مكية", 75, 458, 23, "The Troops"),
        Surah(40, "غَافِر", "Ghafir", "مكية", 85, 467, 24, "The Forgiver"),
        Surah(41, "فُصِّلَت", "Fussilat", "مكية", 54, 477, 24, "Explained in Detail"),
        Surah(42, "الشُّورَى", "Ash-Shura", "مكية", 53, 483, 25, "The Consultation"),
        Surah(43, "الزُّخْرُف", "Az-Zukhruf", "مكية", 89, 489, 25, "The Ornaments of Gold"),
        Surah(44, "الدُّخَان", "Ad-Dukhan", "مكية", 59, 496, 25, "The Smoke"),
        Surah(45, "الجَاثِيَة", "Al-Jathiyah", "مكية", 37, 499, 25, "The Crouching"),
        Surah(46, "الأَحْقَاف", "Al-Ahqaf", "مكية", 35, 502, 26, "The Wind-Curved Sandhills"),
        Surah(47, "مُحَمَّد", "Muhammad", "مدنية", 38, 507, 26, "Muhammad"),
        Surah(48, "الفَتْح", "Al-Fath", "مدنية", 29, 511, 26, "The Victory"),
        Surah(49, "الحُجُرَات", "Al-Hujurat", "مدنية", 18, 515, 26, "The Rooms"),
        Surah(50, "ق", "Qaf", "مكية", 45, 518, 26, "The Letter 'Qaf'"),
        Surah(51, "الذَّارِيَات", "Adh-Dhariyat", "مكية", 60, 520, 26, "The Winnowing Winds"),
        Surah(52, "الطُّور", "At-Tur", "مكية", 49, 523, 27, "The Mount"),
        Surah(53, "النَّجْم", "An-Najm", "مكية", 62, 526, 27, "The Star"),
        Surah(54, "القَمَر", "Al-Qamar", "مكية", 55, 528, 27, "The Moon"),
        Surah(55, "الرَّحْمَٰن", "Ar-Rahman", "مدنية", 78, 531, 27, "The Beneficent"),
        Surah(56, "الوَاقِعَة", "Al-Waqi'ah", "مكية", 96, 534, 27, "The Inevitable"),
        Surah(57, "الحَدِيد", "Al-Hadid", "مدنية", 29, 537, 27, "The Iron"),
        Surah(58, "المُجَادِلَة", "Al-Mujadila", "مدنية", 22, 542, 28, "The Pleading Woman"),
        Surah(59, "الحَشْر", "Al-Hashr", "مدنية", 24, 545, 28, "The Exile"),
        Surah(60, "المُمْتَحَنَة", "Al-Mumtahanah", "مدنية", 13, 549, 28, "She that is to be examined"),
        Surah(61, "الصَّفّ", "As-Saff", "مدنية", 14, 551, 28, "The Ranks"),
        Surah(62, "الجُمُعَة", "Al-Jumu'ah", "مدنية", 11, 553, 28, "The Congregation"),
        Surah(63, "المُنَافِقُون", "Al-Munafiqun", "مدنية", 11, 554, 28, "The Hypocrites"),
        Surah(64, "التَّغَابُن", "At-Taghabun", "مدنية", 18, 556, 28, "The Mutual Disillusion"),
        Surah(65, "الطَّلَاق", "At-Talaq", "مدنية", 12, 558, 28, "The Divorce"),
        Surah(66, "التَّحْرِيم", "At-Tahrim", "مدنية", 12, 560, 28, "The Prohibition"),
        Surah(67, "المُلْك", "Al-Mulk", "مكية", 30, 562, 29, "The Sovereignty"),
        Surah(68, "القَلَم", "Al-Qalam", "مكية", 52, 564, 29, "The Pen"),
        Surah(69, "الحَاقَّة", "Al-Haqqah", "مكية", 52, 566, 29, "The Reality"),
        Surah(70, "المَعَارِج", "Al-Ma'arij", "مكية", 44, 568, 29, "The Ascending Stairways"),
        Surah(71, "نُوح", "Nuh", "مكية", 28, 570, 29, "Noah"),
        Surah(72, "الجِنّ", "Al-Jinn", "مكية", 28, 572, 29, "The Jinn"),
        Surah(73, "المُزَّمِّل", "Al-Muzzammil", "مكية", 20, 574, 29, "The Enshrouded One"),
        Surah(74, "المُدَّثِّر", "Al-Muddaththir", "مكية", 56, 575, 29, "The Cloaked One"),
        Surah(75, "القِيَامَة", "Al-Qiyamah", "مكية", 40, 577, 29, "The Resurrection"),
        Surah(76, "الإِنْسَان", "Al-Insan", "مدنية", 31, 578, 29, "The Human"),
        Surah(77, "المُرْسَلَات", "Al-Mursalat", "مكية", 50, 580, 29, "The Emissaries"),
        Surah(78, "النَّبَأ", "An-Naba", "مكية", 40, 582, 30, "The Tidings"),
        Surah(79, "النَّازِعَات", "An-Nazi'at", "مكية", 46, 583, 30, "Those who drag forth"),
        Surah(80, "عَبَسَ", "Abasa", "مكية", 42, 585, 30, "He Frowned"),
        Surah(81, "التَّكْوِير", "At-Takwir", "مكية", 29, 586, 30, "The Overthrowing"),
        Surah(82, "الانْفِطَار", "Al-Infitar", "مكية", 19, 587, 30, "The Cleaving"),
        Surah(83, "المُطَفِّفِين", "Al-Mutaffifin", "مكية", 36, 587, 30, "The Defrauding"),
        Surah(84, "الانْشِقَاق", "Al-Inshiqaq", "مكية", 25, 589, 30, "The Splitting Open"),
        Surah(85, "البُرُوج", "Al-Buruj", "مكية", 22, 590, 30, "The Mansions of the Stars"),
        Surah(86, "الطَّارِق", "At-Tariq", "مكية", 17, 591, 30, "The Morning Star"),
        Surah(87, "الأَعْلَى", "Al-A'la", "مكية", 19, 591, 30, "The Most High"),
        Surah(88, "الغَاشِيَة", "Al-Ghashiyah", "مكية", 26, 592, 30, "The Overwhelming"),
        Surah(89, "الفَجْر", "Al-Fajr", "مكية", 30, 593, 30, "The Dawn"),
        Surah(90, "البَلَد", "Al-Balad", "مكية", 20, 594, 30, "The City"),
        Surah(91, "الشَّمْس", "Ash-Shams", "مكية", 15, 595, 30, "The Sun"),
        Surah(92, "اللَّيْل", "Al-Layl", "مكية", 21, 595, 30, "The Night"),
        Surah(93, "الضُّحَى", "Ad-Duha", "مكية", 11, 596, 30, "The Morning Hours"),
        Surah(94, "الشَّرْح", "Ash-Sharh", "مكية", 8, 596, 30, "The Relief"),
        Surah(95, "التِّين", "At-Tin", "مكية", 8, 597, 30, "The Fig"),
        Surah(96, "العَلَق", "Al-Alaq", "مكية", 19, 597, 30, "The Clot"),
        Surah(97, "القَدْر", "Al-Qadr", "مكية", 5, 598, 30, "The Power"),
        Surah(98, "البَيِّنَة", "Al-Bayyinah", "مدنية", 8, 598, 30, "The Clear Proof"),
        Surah(99, "الزَّلْزَلَة", "Az-Zalzalah", "مدنية", 8, 599, 30, "The Earthquake"),
        Surah(100, "العَادِيَات", "Al-Adiyat", "مكية", 11, 599, 30, "The Courser"),
        Surah(101, "القَارِعَة", "Al-Qari'ah", "مكية", 11, 600, 30, "The Calamity"),
        Surah(102, "التَّكَاثُر", "At-Takathur", "مكية", 8, 600, 30, "The Rivalry in World Increase"),
        Surah(103, "العَصْر", "Al-Asr", "مكية", 3, 601, 30, "The Declining Day"),
        Surah(104, "الهُمَزَة", "Al-Humazah", "مكية", 9, 601, 30, "The Traducer"),
        Surah(105, "الفِيل", "Al-Fil", "مكية", 5, 601, 30, "The Elephant"),
        Surah(106, "قُرَيْش", "Quraysh", "مكية", 4, 602, 30, "Quraysh"),
        Surah(107, "المَاعُون", "Al-Ma'un", "مكية", 7, 602, 30, "The Small Kindnesses"),
        Surah(108, "الكَوْثَر", "Al-Kawthar", "مكية", 3, 602, 30, "The Abundance"),
        Surah(109, "الكَافِرُون", "Al-Kafirun", "مكية", 6, 603, 30, "The Disbelievers"),
        Surah(110, "النَّصْر", "An-Nasr", "مدنية", 3, 603, 30, "The Divine Support"),
        Surah(111, "المَسَد", "Al-Masad", "مكية", 5, 603, 30, "The Palm Fiber"),
        Surah(112, "الإِخْلَاص", "Al-Ikhlas", "مكية", 4, 604, 30, "The Sincerity"),
        Surah(113, "الفَلَق", "Al-Falaq", "مكية", 5, 604, 30, "The Daybreak"),
        Surah(114, "النَّاس", "An-Nas", "مكية", 6, 604, 30, "Mankind")
    )

    val juzList: List<Juz> = listOf(
        Juz(1, 1, "الفَاتِحَة", 1, 1, 21),
        Juz(2, 2, "البَقَرَة", 142, 22, 41),
        Juz(3, 2, "البَقَرَة", 253, 42, 61),
        Juz(4, 3, "آل عِمْرَان", 93, 62, 81),
        Juz(5, 4, "النِّسَاء", 24, 82, 101),
        Juz(6, 4, "النِّسَاء", 148, 102, 121),
        Juz(7, 5, "المَائِدَة", 82, 122, 141),
        Juz(8, 6, "الأَنْعَام", 111, 142, 161),
        Juz(9, 7, "الأَعْرَاف", 88, 162, 181),
        Juz(10, 8, "الأَنْفَال", 41, 182, 201),
        Juz(11, 9, "التَّوْبَة", 93, 202, 221),
        Juz(12, 11, "هُود", 6, 222, 241),
        Juz(13, 12, "يُوسُف", 53, 242, 261),
        Juz(14, 15, "الحِجْر", 1, 262, 281),
        Juz(15, 17, "الإِسْرَاء", 1, 282, 301),
        Juz(16, 18, "الكَهْف", 75, 302, 321),
        Juz(17, 21, "الأَنْبِيَاء", 1, 322, 341),
        Juz(18, 23, "المُؤْمِنُون", 1, 342, 361),
        Juz(19, 25, "الفُرْقَان", 21, 362, 381),
        Juz(20, 27, "النَّمْل", 56, 382, 401),
        Juz(21, 29, "العَنْكَبُوت", 46, 402, 421),
        Juz(22, 33, "الأَحْزَاب", 31, 422, 441),
        Juz(23, 36, "يس", 28, 442, 461),
        Juz(24, 39, "الزُّمَر", 32, 462, 481),
        Juz(25, 41, "فُصِّلَت", 47, 482, 501),
        Juz(26, 46, "الأَحْقَاف", 1, 502, 521),
        Juz(27, 51, "الذَّارِيَات", 31, 522, 541),
        Juz(28, 58, "المُجَادِلَة", 1, 542, 561),
        Juz(29, 67, "المُلْك", 1, 562, 581),
        Juz(30, 78, "النَّبَأ", 1, 582, 604)
    )

    val reciters: List<Reciter> = listOf(
        Reciter(
            id = "alafasy",
            nameAr = "مشاري بن راشد العفاسي",
            nameEn = "Mishary Rashid Alafasy",
            style = "مرتل",
            serverUrl = "https://server8.mp3quran.net/afs/",
            bioAr = "إمام المسجد الكبير بدولة الكويت، صاحب الصوت الندي والشهرة العالمية في التلاوة العذبة.",
            isFeatured = true
        ),
        Reciter(
            id = "abdulbasit_murattal",
            nameAr = "عبد الباسط عبد الصمد (مرتل)",
            nameEn = "Abdul Basit (Murattal)",
            style = "مرتل",
            serverUrl = "https://server7.mp3quran.net/basit/",
            bioAr = "صوت مكة، وأحد أعلام التلاوة التاريخية في العالم الإسلامي.",
            isFeatured = true
        ),
        Reciter(
            id = "abdulbasit_mujawwad",
            nameAr = "عبد الباسط عبد الصمد (مجود)",
            nameEn = "Abdul Basit (Mujawwad)",
            style = "مجود",
            serverUrl = "https://server7.mp3quran.net/basit_mjwd/",
            bioAr = "التلاوة التجويدية الخاشعة الشهيرة للشيخ عبد الباسط.",
            isFeatured = false
        ),
        Reciter(
            id = "maher",
            nameAr = "ماهر المعيقلي",
            nameEn = "Maher Al-Muaiqly",
            style = "مرتل",
            serverUrl = "https://server12.mp3quran.net/maher/",
            bioAr = "إمام وخطيب المسجد الحرام بمكة المكرمة، تلاوة مؤثرة تأسر القلوب.",
            isFeatured = true
        ),
        Reciter(
            id = "husary",
            nameAr = "محمود خليل الحصري",
            nameEn = "Mahmoud Khalil Al-Husary",
            style = "مرتل",
            serverUrl = "https://server13.mp3quran.net/husr/",
            bioAr = "شيخ المقارئ المصرية وأستاذ أحكام التجويد المتقن.",
            isFeatured = true
        ),
        Reciter(
            id = "minshawi_murattal",
            nameAr = "محمد صديق المنشاوي",
            nameEn = "Mohamed Siddiq El-Minshawi",
            style = "مرتل",
            serverUrl = "https://server10.mp3quran.net/minsh/",
            bioAr = "الصوت الباكي الخاشع، رائد مدرسة التلاوة في مصر والعالم الإسلامي.",
            isFeatured = true
        ),
        Reciter(
            id = "ghamadi",
            nameAr = "سعد الغامدي",
            nameEn = "Saad Al-Ghamdi",
            style = "مرتل",
            serverUrl = "https://server7.mp3quran.net/s_gmd/",
            bioAr = "قارئ سعودي متميز بنبرته الخاشعة وتلاوته السلسة.",
            isFeatured = true
        ),
        Reciter(
            id = "sudais",
            nameAr = "عبد الرحمن السديس",
            nameEn = "Abdul Rahman Al-Sudais",
            style = "مرتل",
            serverUrl = "https://server11.mp3quran.net/sds/",
            bioAr = "إمام وخطيب المسجد الحرام والرئيس العام لشؤون الحرمين الشريفين.",
            isFeatured = true
        ),
        Reciter(
            id = "dossari",
            nameAr = "ياسر الدوسري",
            nameEn = "Yasser Al-Dossari",
            style = "مرتل",
            serverUrl = "https://server11.mp3quran.net/yasser/",
            bioAr = "إمام وخطيب المسجد الحرام بمكة، يتميز بالخشوع والقوة في الأداء.",
            isFeatured = true
        ),
        Reciter(
            id = "ajamy",
            nameAr = "أحمد بن علي العجمي",
            nameEn = "Ahmed Al-Ajmi",
            style = "مرتل",
            serverUrl = "https://server10.mp3quran.net/ajm/",
            bioAr = "قارئ سعودي معروف بصوته المميز المليء بالعاطفة والتأثير.",
            isFeatured = true
        ),
        Reciter(
            id = "shatri",
            nameAr = "أبو بكر الشاطري",
            nameEn = "Abu Bakr Al-Shatri",
            style = "مرتل",
            serverUrl = "https://server11.mp3quran.net/shatri/",
            bioAr = "قارئ يمني شهير بنغمته الهادئة وقراءته الحجازية الرائعة.",
            isFeatured = false
        )
    )

    val azkarCategories: List<AzkarCategory> = listOf(
        AzkarCategory(
            id = "morning",
            nameAr = "أذكار الصباح",
            iconName = "wb_sunny",
            description = "تبدأ من بعد صلاة الفجر وحتى شروق الشمس أو قبيل الظهر",
            items = listOf(
                ZikrItem(
                    id = 1,
                    title = "آية الكرسي",
                    text = "اللّهُ لاَ إِلَـهَ إِلاَّ هُوَ الْحَيُّ الْقَيُّومُ لاَ تَأْخُذُهُ سِنَةٌ وَلاَ نَوْمٌ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الأَرْضِ مَن ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلاَّ بِإِذْنِهِ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلاَ يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلاَّ بِمَا شَاء وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالأَرْضَ وَلاَ يَؤُودُهُ حِفْظُهُمَا وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                    repeatCount = 1,
                    rewardOrVirtue = "من قرأها حين يصبح أجير من الجن حتى يمسي",
                    reference = "سورة البقرة: 255"
                ),
                ZikrItem(
                    id = 2,
                    title = "الإخلاص والمعوذتين",
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ {قُلْ هُوَ اللَّهُ أَحَدٌ * اللَّهُ الصَّمَدُ * لَمْ يَلِدْ وَلَمْ يُولَدْ * وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ} ، {قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ...} ، {قُلْ أَعُوذُ بِرَبِّ النَّاسِ...}",
                    repeatCount = 3,
                    rewardOrVirtue = "تكفيك من كل شيء",
                    reference = "رواه أبو داود والترمذي"
                ),
                ZikrItem(
                    id = 3,
                    title = "أصبحنا وأصبح الملك لله",
                    text = "أَصْبَحْنَا وَأَصْبَحَ الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ لا إِلَهَ إِلا اللَّهُ وَحْدَهُ لا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ، رَبِّ أَسْأَلُكَ خَيْرَ مَا فِي هَذَا الْيَوْمِ وَخَيْرَ مَا بَعْدَهُ، وَأَعُوذُ بِكَ مِنْ شَرِّ مَا فِي هَذَا الْيَوْمِ وَشَرِّ مَا بَعْدَهُ، رَبِّ أَعُوذُ بِكَ مِنَ الْكَسَلِ وَسُوءِ الْكِبَرِ، رَبِّ أَعُوذُ بِكَ مِنْ عَذَابٍ فِي النَّارِ وَعَذَابٍ فِي الْقَبْرِ",
                    repeatCount = 1,
                    rewardOrVirtue = "سؤال خير اليوم والاستعاذة من الشرور",
                    reference = "رواه مسلم"
                ),
                ZikrItem(
                    id = 4,
                    title = "سيد الاستغفار",
                    text = "اللَّهُمَّ أَنْتَ رَبِّي لا إِلَهَ إِلا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لا يَغْفِرُ الذُّنُوبَ إِلا أَنْتَ",
                    repeatCount = 1,
                    rewardOrVirtue = "من قالها موقنا بها حين يصبح فمات من يومه دخل الجنة",
                    reference = "رواه البخاري"
                ),
                ZikrItem(
                    id = 5,
                    title = "التحصين الشامل",
                    text = "بِسْمِ اللَّهِ الَّذِي لا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الأَرْضِ وَلا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
                    repeatCount = 3,
                    rewardOrVirtue = "لم يضره شيء",
                    reference = "رواه أبو داود والترمذي"
                ),
                ZikrItem(
                    id = 6,
                    title = "رضيت بالله رباً",
                    text = "رَضِيتُ بِاللَّهِ رَبَّاً، وَبِالإِسْلامِ دِينَاً، وَبِمُحَمَّدٍ صَلَّى اللَّهُ عَلَيْهِ وَسَلَّمَ نَبِيَّاً",
                    repeatCount = 3,
                    rewardOrVirtue = "كان حقاً على الله أن يرضيه يوم القيامة",
                    reference = "رواه أحمد والترمذي"
                ),
                ZikrItem(
                    id = 7,
                    title = "الصلاة على النبي",
                    text = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ",
                    repeatCount = 10,
                    rewardOrVirtue = "من صلى علي حين يصبح عشراً أدركته شفاعتي يوم القيامة",
                    reference = "رواه الطبراني"
                )
            )
        ),
        AzkarCategory(
            id = "evening",
            nameAr = "أذكار المساء",
            iconName = "nights_stay",
            description = "تبدأ من بعد صلاة العصر وحتى غروب الشمس أو منتصف الليل",
            items = listOf(
                ZikrItem(
                    id = 101,
                    title = "آية الكرسي",
                    text = "اللّهُ لاَ إِلَـهَ إِلاَّ هُوَ الْحَيُّ الْقَيُّومُ لاَ تَأْخُذُهُ سِنَةٌ وَلاَ نَوْمٌ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الأَرْضِ مَن ذَا الَّذِي يَشْفَعُ عِنْدَهُ إِلاَّ بِإِذْنِهِ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ وَلاَ يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلاَّ بِمَا شَاء وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالأَرْضَ وَلاَ يَؤُودُهُ حِفْظُهُمَا وَهُوَ الْعَلِيُّ الْعَظِيمُ",
                    repeatCount = 1,
                    rewardOrVirtue = "من قرأها حين يمسي أجير من الجن حتى يصبح",
                    reference = "سورة البقرة: 255"
                ),
                ZikrItem(
                    id = 102,
                    title = "أمسينا وأمسى الملك لله",
                    text = "أَمْسَيْنَا وَأَمْسَى الْمُلْكُ لِلَّهِ، وَالْحَمْدُ لِلَّهِ لا إِلَهَ إِلا اللَّهُ وَحْدَهُ لا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                    repeatCount = 1,
                    rewardOrVirtue = "الاعتراف بملك الله وحمده في المساء",
                    reference = "رواه مسلم"
                ),
                ZikrItem(
                    id = 103,
                    title = "سيد الاستغفار",
                    text = "اللَّهُمَّ أَنْتَ رَبِّي لا إِلَهَ إِلا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لا يَغْفِرُ الذُّنُوبَ إِلا أَنْتَ",
                    repeatCount = 1,
                    rewardOrVirtue = "من قالها حين يمسي فمات دخل الجنة",
                    reference = "رواه البخاري"
                ),
                ZikrItem(
                    id = 104,
                    title = "أعوذ بكلمات الله التامات",
                    text = "أَعُوذُ بِكَلِمَاتِ اللَّهِ التَّامَّاتِ مِنْ شَرِّ مَا خَلَقَ",
                    repeatCount = 3,
                    rewardOrVirtue = "لم يضره شيء تلك الليلة",
                    reference = "رواه مسلم"
                )
            )
        ),
        AzkarCategory(
            id = "khatm_quran",
            nameAr = "دعاء ختم القرآن الكريم",
            iconName = "auto_stories",
            description = "الدعاء المأثور الجامع عند إتمام قراءة القرآن الكريم",
            items = listOf(
                ZikrItem(
                    id = 201,
                    title = "دعاء ختم القرآن",
                    text = "اللَّهُمَّ ارْحَمْنِي بالقُرْآنِ وَاجْعَلهُ لِي إِمَاماً وَنُوراً وَهُدًى وَرَحْمَةً * اللَّهُمَّ ذَكِّرْنِي مِنْهُ مَا نَسِيتُ وَعَلِّمْنِي مِنْهُ مَا جَهِلْتُ وَارْزُقْنِي تِلاَوَتَهُ آنَاءَ اللَّيْلِ وَأَطْرَافَ النَّهَارِ وَاجْعَلْهُ لِي حُجَّةً يَا رَبَّ العَالَمِينَ * اللَّهُمَّ أَصْلِحْ لِي دِينِي الَّذِي هُوَ عِصْمَةُ أَمْرِي، وَأَصْلِحْ لِي دُنْيَايَ الَّتِي فِيهَا مَعَاشِي، وَأَصْلِحْ لِي آخِرَتِي الَّتِي فِيهَا مَعَادِي، وَاجْعَلِ الحَيَاةَ زِيَادَةً لِي فِي كُلِّ خَيْرٍ وَاجْعَلِ المَوْتَ رَاحَةً لِي مِنْ كُلِّ شَرٍّ * اللَّهُمَّ اجْعَلْ خَيْرَ عُمْرِي آخِرَهُ وَخَيْرَ عَمَلِي خَوَاتِمَهُ وَخَيْرَ أَيَّامِي يَوْمَ أَلْقَاكَ فِيهِ * اللَّهُمَّ إِنِّي أَسْأَلُكَ عِيشَةً هَنِيَّةً وَمِيتَةً سَوِيَّةً وَمَرَدّاً غَيْرَ مُخْزٍ وَلاَ فَاضِحٍ.",
                    repeatCount = 1,
                    rewardOrVirtue = "يستحب الدعاء عند ختم القرآن الكريم لما فيه من استجابة ورجاء الرحمة والمغفرة",
                    reference = "دعاء ختم القرآن المأثور"
                )
            )
        ),
        AzkarCategory(
            id = "after_prayer",
            nameAr = "أذكار بعد الصلاة",
            iconName = "mosque",
            description = "الأذكار المسنونة دبر كل صلاة مكتوبة",
            items = listOf(
                ZikrItem(
                    id = 301,
                    title = "الاستغفار والسلام",
                    text = "أَسْتَغْفِرُ اللَّهَ (ثَلاثَاً)، اللَّهُمَّ أَنْتَ السَّلامُ، وَمِنْكَ السَّلامُ، تَبَارَكْتَ يَا ذَا الْجَلالِ وَالإِكْرَامِ",
                    repeatCount = 1,
                    rewardOrVirtue = "سنة المصطفى صلى الله عليه وسلم بعد التسليم",
                    reference = "صحيح مسلم"
                ),
                ZikrItem(
                    id = 302,
                    title = "التسبيح والتحميد والتكبير",
                    text = "سُبْحَانَ اللَّهِ (33) ، الْحَمْدُ لِلَّهِ (33) ، اللَّهُ أَكْبَرُ (33) ، وتَمَام المائة: لا إِلَهَ إِلا اللَّهُ وَحْدَهُ لا شَرِيكَ لَهُ، لَهُ الْمُلْكُ وَلَهُ الْحَمْدُ وَهُوَ عَلَى كُلِّ شَيْءٍ قَدِيرٌ",
                    repeatCount = 33,
                    rewardOrVirtue = "غُفِرَتْ خَطَايَاهُ وَإِنْ كَانَتْ مِثْلَ زَبَدِ الْبَحْرِ",
                    reference = "صحيح مسلم"
                )
            )
        )
    )
}
