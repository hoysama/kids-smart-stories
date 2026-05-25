package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.api.QuizQuestion
import com.example.data.local.StoryEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.KidsStoriesViewModel
import com.example.ui.viewmodel.GenerationState
import kotlinx.coroutines.delay

// Beautiful options for kid customization
data class StoryPresetOption(val labelAr: String, val labelEn: String, val emoji: String)

val heroTypes = listOf(
    StoryPresetOption("بطل شجاع", "Brave Explorer", "🦁"),
    StoryPresetOption("روبوت ذكي", "Clever Robot", "🤖"),
    StoryPresetOption("قطة فضولية", "Curious Kitten", "🐱"),
    StoryPresetOption("جنيّة طيبة", "Kind Fairy", "✨"),
    StoryPresetOption("أرنب مفكر", "Wise Rabbit", "🐰")
)

val storySettings = listOf(
    StoryPresetOption("الغابة السحرية", "Enchanted Forest", "🌲"),
    StoryPresetOption("الفضاء الواسع", "Deep Space", "🚀"),
    StoryPresetOption("أعماق البحر", "Undersea World", "🐙"),
    StoryPresetOption("مزرعة الجد", "Grandpa's Farm", "🏡"),
    StoryPresetOption("قلعة الغيوم", "Cloudy Castle", "☁️")
)

val educationalThemes = listOf(
    StoryPresetOption("الصدق والأمانة", "Honesty & Truth", "💎"),
    StoryPresetOption("مساعدة الآخرين", "Helping Others", "🤝"),
    StoryPresetOption("حب القراءة والمدرسة", "Loving Science", "📚"),
    StoryPresetOption("النظافة والصحة", "Health & Hygiene", "🍎"),
    StoryPresetOption("الشجاعة للصغار", "Courage & Patience", "🛡️")
)

val storyTones = listOf(
    StoryPresetOption("مغامرة حماسية", "Exciting Adventure", "🦁"),
    StoryPresetOption("مضحك ومسلي", "Funny & Silly", "🎈"),
    StoryPresetOption("خيال سحري", "Magical Mystery", "🔮"),
    StoryPresetOption("هادئ للنوم", "Calm Bedtime", "🌙")
)

val loadingCaptionsAr = listOf(
    "يتم تحضير بطل القصة... 🦸",
    "تلوين الغابة السحرية... 🎨",
    "تعليم الفراشات كيف تطير وتتكلم... 🦋",
    "كتابة المغامرة المثيرة بالأحرف الذهبية... ✨",
    "تحضير أسئلة اختبار الذكاء... 🧠"
)

val loadingCaptionsEn = listOf(
    "Summoning the little hero... 🦸",
    "Painting the magical forest... 🎨",
    "Teaching butterflies how to sing... 🦋",
    "Writing your adventure in gold... ✨",
    "Setting up the intelligence quiz... 🧠"
)

@Composable
fun StoriesDashboard(
    viewModel: KidsStoriesViewModel,
    modifier: Modifier = Modifier
) {
    val allStories by viewModel.allStories.collectAsState()
    val selectedStory by viewModel.selectedStory.collectAsState()
    val generationState by viewModel.generationState.collectAsState()

    // Inputs
    var heroName by remember { mutableStateOf("") }
    var selectedHeroType by remember { mutableStateOf(heroTypes[0]) }
    var selectedSetting by remember { mutableStateOf(storySettings[0]) }
    var selectedTheme by remember { mutableStateOf(educationalThemes[0]) }
    var selectedTone by remember { mutableStateOf(storyTones[0]) }
    var selectedLang by remember { mutableStateOf("ar") } // Default to "ar" Arabic

    val isRtl = selectedLang == "ar"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.background.copy(alpha = 0.9f)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp) // Spacing for bottom content
        ) {
            // Header Hero Banner
            HeaderHeroSection(isRtl)

            // Saved Library Row (Only show if we have stories)
            if (allStories.isNotEmpty()) {
                SavedStoriesSection(
                    stories = allStories,
                    isRtl = isRtl,
                    onStoryClick = { viewModel.selectStory(it) },
                    onStoryDelete = { viewModel.deleteStory(it) }
                )
            } else {
                EmptyLibrarySection(isRtl)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Settings/Generator Panel
            CreatorFormSection(
                heroName = heroName,
                onHeroNameChange = { heroName = it },
                selectedHeroType = selectedHeroType,
                onHeroTypeChange = { selectedHeroType = it },
                selectedSetting = selectedSetting,
                onSettingChange = { selectedSetting = it },
                selectedTheme = selectedTheme,
                onThemeChange = { selectedTheme = it },
                selectedTone = selectedTone,
                onToneChange = { selectedTone = it },
                selectedLang = selectedLang,
                onLangChange = { selectedLang = it },
                isRtl = isRtl,
                onSubmit = {
                    viewModel.generateStory(
                        heroName = heroName,
                        heroType = if (selectedLang == "ar") selectedHeroType.labelAr else selectedHeroType.labelEn,
                        theme = if (selectedLang == "ar") selectedTheme.labelAr else selectedTheme.labelEn,
                        setting = if (selectedLang == "ar") selectedSetting.labelAr else selectedSetting.labelEn,
                        tone = if (selectedLang == "ar") selectedTone.labelAr else selectedTone.labelEn,
                        language = selectedLang
                    )
                }
            )
        }

        // Standard Loader Dialog during generation state
        if (generationState is GenerationState.Loading) {
            StoryGenerationLoader(selectedLang)
        }

        // Fullscreen Story Reader Overlay
        AnimatedVisibility(
            visible = selectedStory != null,
            enter = fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f),
            exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.95f)
        ) {
            selectedStory?.let { story ->
                StoryReaderView(
                    story = story,
                    viewModel = viewModel,
                    onBack = {
                        viewModel.selectStory(null)
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderHeroSection(isRtl: Boolean) {
    val titleText = if (isRtl) "أهلاً بكَ في صانع القصص الذكي! ✨" else "Welcome to Kids Smart Stories! ✨"
    val subtitleText = if (isRtl) {
        "اصنع قصتك التعليمية الخاصة الآن وكن بطل المغامرة الأكبر!"
    } else {
        "Design your own educational story now and become the ultimate hero of the adventure!"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 44.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = titleText,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                lineHeight = 32.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = subtitleText,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = 15.sp,
                lineHeight = 22.sp,
                letterSpacing = 0.5.sp
            ),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SavedStoriesSection(
    stories: List<StoryEntity>,
    isRtl: Boolean,
    onStoryClick: (StoryEntity) -> Unit,
    onStoryDelete: (Int) -> Unit
) {
    val titleText = if (isRtl) "📚 مكتبة قصصي الذكية" else "📚 My Smart Library"

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titleText,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            textAlign = if (isRtl) TextAlign.Right else TextAlign.Left
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(stories, key = { it.id }) { story ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .width(220.dp)
                        .height(140.dp)
                        .shadow(4.dp, shape = RoundedCornerShape(20.dp))
                        .clickable { onStoryClick(story) }
                        .testTag("story_card_${story.id}")
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = story.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini Tag detailing hero
                                Text(
                                    text = "${story.heroName} ${if (story.language == "ar") "🦸" else "🦸‍♂️"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                IconButton(
                                    onClick = { onStoryDelete(story.id) },
                                    modifier = Modifier.size(28.dp).testTag("delete_story_${story.id}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyLibrarySection(isRtl: Boolean) {
    val message = if (isRtl) {
        "لا توجد قصص محفوظة بعد. هيا ننشئ أول قصة ذكية ومثيرة لك بالأسفل! 👇"
    } else {
        "No saved stories yet. Let's create your first super story below! 👇"
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor = SunnyGold.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .border(2.dp, SunnyGold.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🦄",
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = DarkCocoa,
                textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun CreatorFormSection(
    heroName: String,
    onHeroNameChange: (String) -> Unit,
    selectedHeroType: StoryPresetOption,
    onHeroTypeChange: (StoryPresetOption) -> Unit,
    selectedSetting: StoryPresetOption,
    onSettingChange: (StoryPresetOption) -> Unit,
    selectedTheme: StoryPresetOption,
    onThemeChange: (StoryPresetOption) -> Unit,
    selectedTone: StoryPresetOption,
    onToneChange: (StoryPresetOption) -> Unit,
    selectedLang: String,
    onLangChange: (String) -> Unit,
    isRtl: Boolean,
    onSubmit: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(28.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .shadow(6.dp, shape = RoundedCornerShape(28.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Language selector tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEDE7F6)),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("ar" to "العَرَبِيّة 🇸🇦", "en" to "English 🇬🇧").forEach { (langCode, label) ->
                    val isSelected = selectedLang == langCode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { onLangChange(langCode) }
                            .padding(vertical = 12.dp)
                            .testTag("lang_tab_$langCode"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else DarkCocoa,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Input: Hero Name
            Text(
                text = if (isRtl) "✨ اكتب اسم البطل (أو البطلة):" else "✨ Write Hero's Name:",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = heroName,
                onValueChange = onHeroNameChange,
                placeholder = {
                    Text(
                        text = if (isRtl) "مثال: أحمد، مريم، يوسف..." else "e.g. Leo, Chloe, Emma...",
                        style = TextStyle(
                            textDirection = if (isRtl) TextDirection.Rtl else TextDirection.Ltr
                        )
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .testTag("hero_name_input"),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                singleLine = true
            )

            // Selector Component: Hero Type
            OptionSelectorGrid(
                title = if (isRtl) "🦸‍♂️ اختر نوع البطل:" else "🦸‍♂️ Choose Hero Type:",
                options = heroTypes,
                selected = selectedHeroType,
                onSelect = onHeroTypeChange,
                isRtl = isRtl
            )

            // Selector Component: Setting
            OptionSelectorGrid(
                title = if (isRtl) "🌍 أين تحدث القصة؟" else "🌍 Where does it happen?",
                options = storySettings,
                selected = selectedSetting,
                onSelect = onSettingChange,
                isRtl = isRtl
            )

            // Selector Component: Moral Theme
            OptionSelectorGrid(
                title = if (isRtl) "💎 الدروس التعليمية والقيم المعنوية:" else "💎 Educational & Moral Theme:",
                options = educationalThemes,
                selected = selectedTheme,
                onSelect = onThemeChange,
                isRtl = isRtl
            )

            // Selector Component: Tone / Style
            OptionSelectorGrid(
                title = if (isRtl) "🎭 أسلوب ونمط سرد القصة:" else "🎭 Story Vibe & Tone:",
                options = storyTones,
                selected = selectedTone,
                onSelect = onToneChange,
                isRtl = isRtl
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Pulse crafting button
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarmCoral
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .testTag("craft_story_button"),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = if (isRtl) "اصنع قصتي السحرية الآن ✨🧙‍♂️" else "Craft My Magical Story ✨🧙‍♂️",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun OptionSelectorGrid(
    title: String,
    options: List<StoryPresetOption>,
    selected: StoryPresetOption,
    onSelect: (StoryPresetOption) -> Unit,
    isRtl: Boolean
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(options) { option ->
                val isSelected = selected == option
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) SunnyGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                        .border(
                            2.dp,
                            if (isSelected) SunnyGold else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelect(option) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = option.emoji, fontSize = 18.sp, modifier = Modifier.padding(end = 6.dp))
                        Text(
                            text = if (isRtl) option.labelAr else option.labelEn,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StoryGenerationLoader(selectedLang: String) {
    val isAr = selectedLang == "ar"
    val listCaptions = if (isAr) loadingCaptionsAr else loadingCaptionsEn

    var listIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3500)
            listIndex = (listIndex + 1) % listCaptions.size
        }
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 8.dp,
            color = SoftCream,
            modifier = Modifier
                .width(300.dp)
                .padding(16.dp)
                .border(3.dp, SunnyGold, RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(1.1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.fillMaxSize(),
                        color = SunnyGold.copy(alpha = 0.2f),
                        strokeWidth = 6.dp,
                    )
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        color = WarmCoral,
                        strokeWidth = 6.dp
                    )
                    Text(
                        text = "🧙‍♂️",
                        fontSize = 28.sp,
                        modifier = Modifier.offset(y = (-2).dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isAr) "تحضير سحر القصص..." else "Preparing story magic...",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(500))
                ) {
                    Text(
                        text = listCaptions[listIndex],
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = DarkCocoa,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.height(44.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StoryReaderView(
    story: StoryEntity,
    viewModel: KidsStoriesViewModel,
    onBack: () -> Unit
) {
    val isAr = story.language == "ar"

    val quizQuestions = remember(story) { viewModel.getQuizQuestionsForSelectedStory() }
    val quizAnswers by viewModel.quizAnswers.collectAsState()
    val quizCompleted by viewModel.quizCompleted.collectAsState()

    Surface(
        color = SoftCream,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Elegant top menu bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = {
                        onBack()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                        .shadow(1.dp, CircleShape)
                        .testTag("reader_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Back",
                        tint = DarkCocoa
                    )
                }

                Text(
                    text = if (isAr) "📖 مغامرتي الذكية" else "📖 My Magical Story",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = DarkCocoa
                    )
                )

                // Empty balancing box to keep the title perfectly centered
                Box(
                    modifier = Modifier.size(44.dp)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Title Area
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = SunnyGold.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .border(2.dp, SunnyGold, RoundedCornerShape(24.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✨ " + story.title + " ✨",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    lineHeight = 30.sp,
                                    color = DarkCocoa
                                ),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "🧙‍♂️ ${story.heroType}  |  🌍 ${story.setting}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                // Story Content Display
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .shadow(2.dp, shape = RoundedCornerShape(28.dp))
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                text = story.storyContent,
                                style = MaterialTheme.typography.bodyLarge.merge(
                                    TextStyle(
                                        fontSize = 18.sp,
                                        lineHeight = 28.sp,
                                        letterSpacing = 0.5.sp,
                                        color = DarkCocoa,
                                        textDirection = if (isAr) TextDirection.Rtl else TextDirection.Ltr
                                    )
                                )
                            )
                        }
                    }
                }

                // Dynamic Moral Lesson Box (العبرة الأخلاقية والتعليمية)
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 14.dp)
                            .border(2.dp, MaterialTheme.colorScheme.secondary, RoundedCornerShape(24.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "💎", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = if (isAr) "ماذا تعلمنا من القصة؟ (الهدف التعليمي)" else "What did we learn? (Educational Moral)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = story.moral,
                                style = MaterialTheme.typography.bodyMedium.merge(
                                    TextStyle(
                                        fontSize = 15.sp,
                                        lineHeight = 22.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = DarkCocoa,
                                        textDirection = if (isAr) TextDirection.Rtl else TextDirection.Ltr
                                    )
                                )
                            )
                        }
                    }
                }

                // Comprehension Quiz Section (تحدي الفهم الذكي)
                if (quizQuestions.isNotEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF3E5F5)
                            ),
                            shape = RoundedCornerShape(28.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                                .border(2.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(28.dp))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "🏆", fontSize = 26.sp, modifier = Modifier.padding(end = 8.dp))
                                        Text(
                                            text = if (isAr) "تحدي الأذكياء التفاعلي!" else "Interactive Smart Challenge!",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            ),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // Reset Quiz Button
                                    if (quizCompleted) {
                                        IconButton(
                                            onClick = { viewModel.resetQuiz() },
                                            modifier = Modifier.size(36.dp).testTag("reset_quiz_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Refresh,
                                                contentDescription = "Reset Quiz",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                quizQuestions.forEachIndexed { qIndex, question ->
                                    val selectedAns = quizAnswers[qIndex]

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 18.dp)
                                    ) {
                                        Text(
                                            text = "${qIndex + 1}. ${question.question}",
                                            style = MaterialTheme.typography.bodyLarge.merge(
                                                TextStyle(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    lineHeight = 22.sp,
                                                    color = DarkCocoa,
                                                    textDirection = if (isAr) TextDirection.Rtl else TextDirection.Ltr
                                                )
                                            )
                                        )

                                        Spacer(modifier = Modifier.height(10.dp))

                                        question.options.forEachIndexed { oIndex, option ->
                                            val isSelected = selectedAns == oIndex
                                            val isCorrect = question.correctOptionIndex == oIndex

                                            val optionBg = when {
                                                quizCompleted && isCorrect -> MagicMint.copy(alpha = 0.25f)
                                                quizCompleted && isSelected && !isCorrect -> WarmCoral.copy(alpha = 0.25f)
                                                isSelected -> SunnyGold.copy(alpha = 0.25f)
                                                else -> Color.White
                                            }

                                            val optionBorder = when {
                                                quizCompleted && isCorrect -> MagicMint
                                                quizCompleted && isSelected && !isCorrect -> WarmCoral
                                                isSelected -> SunnyGold
                                                else -> Color.LightGray.copy(alpha = 0.5f)
                                            }

                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 4.dp)
                                                    .clip(RoundedCornerShape(14.dp))
                                                    .background(optionBg)
                                                    .border(2.dp, optionBorder, RoundedCornerShape(14.dp))
                                                    .clickable(enabled = !quizCompleted) {
                                                        viewModel.answerQuizQuestion(qIndex, oIndex)
                                                    }
                                                    .padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = when (oIndex) {
                                                        0 -> "A"
                                                        1 -> "B"
                                                        else -> "C"
                                                    },
                                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                                    color = DarkCocoa,
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .background(Color.White, CircleShape)
                                                        .wrapContentSize(Alignment.Center)
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = option,
                                                    style = MaterialTheme.typography.bodyMedium.merge(
                                                        TextStyle(
                                                            fontSize = 14.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = DarkCocoa,
                                                            textDirection = if (isAr) TextDirection.Rtl else TextDirection.Ltr
                                                        )
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (!quizCompleted) {
                                    val allAnswered = quizAnswers.size == quizQuestions.size
                                    Button(
                                        onClick = { viewModel.submitQuiz() },
                                        enabled = allAnswered,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("submit_quiz_button"),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text(
                                            text = if (isAr) "تحقق من الإجابات! 🔍" else "Verify My Answers! 🔍",
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    // Compliment based on score
                                    val correctCount = quizQuestions.filterIndexed { index, q ->
                                        quizAnswers[index] == q.correctOptionIndex
                                    }.size

                                    val congratulationsMessage = if (isAr) {
                                        when (correctCount) {
                                            3 -> "ممتاز جداً! حصلت على 3/3 وأنت بطل مبدع وذكي! 🌟🥇"
                                            2 -> "رائع! حصلت على 2/3. حاول مرة أخرى لتحصل على العلامة الكاملة! 👍"
                                            else -> "أحسنت محاولة جيدة! حصلت على $correctCount/3. أعد قراءة القصة وتجاوز التحدي! 💪"
                                        }
                                    } else {
                                        when (correctCount) {
                                            3 -> "Incredible! You scored 3/3. You are a genius explorer! 🌟🥇"
                                            2 -> "Well done! You scored 2/3. Try again for a perfect score! 👍"
                                            else -> "Good try! You scored $correctCount/3. Reread the story to ace the test! 💪"
                                        }
                                    }

                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (correctCount == 3) SunnyGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        shape = RoundedCornerShape(18.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.5.dp, SunnyGold, RoundedCornerShape(18.dp))
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(16.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = congratulationsMessage,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    lineHeight = 22.sp
                                                ),
                                                color = DarkCocoa,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
