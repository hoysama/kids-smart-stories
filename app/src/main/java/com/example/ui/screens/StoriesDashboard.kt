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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
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
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.animation.animateContentSize

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

            // Kids achievements milestones & daily reading streak (Feature 7)
            KidsMilestonesSection(storiesCount = allStories.size, isRtl = isRtl)

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

        // Beautiful kid-friendly error dialog
        if (generationState is GenerationState.Error) {
            val errorMsg = (generationState as GenerationState.Error).message
            StoryGenerationErrorDialog(
                selectedLang = selectedLang,
                errorMessage = errorMsg,
                onDismiss = { viewModel.resetGenerationState() }
            )
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
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("kids_stories_prefs", Context.MODE_PRIVATE) }
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
                val rating = remember(story.id) { prefs.getInt("rating_${story.id}", 0) }
                val reaction = remember(story.id) { prefs.getString("reaction_${story.id}", "") ?: "" }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .width(220.dp)
                        .height(145.dp)
                        .shadow(4.dp, shape = RoundedCornerShape(20.dp))
                        .clickable { onStoryClick(story) }
                        .testTag("story_card_${story.id}")
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = story.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onBackground
                                )

                                if (rating > 0) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        repeat(rating) {
                                            Text("⭐", fontSize = 11.sp)
                                        }
                                        if (reaction.isNotEmpty()) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(reaction, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini Tag detailing hero
                                Text(
                                    text = "${story.heroName} ${if (story.language == "ar") "🦸" else "🦸‍♂️"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.sp,
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
fun StoryGenerationErrorDialog(
    selectedLang: String,
    errorMessage: String,
    onDismiss: () -> Unit
) {
    val isAr = selectedLang == "ar"
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 8.dp,
            color = SoftCream,
            modifier = Modifier
                .width(320.dp)
                .padding(12.dp)
                .border(2.5.dp, WarmCoral, RoundedCornerShape(28.dp))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Friendly error emoji badge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .background(WarmCoral.copy(alpha = 0.15f), CircleShape)
                        .border(1.5.dp, WarmCoral, CircleShape)
                ) {
                    Text(text = "🧚‍♀️🩹", fontSize = 28.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isAr) "عذراً يا بطل! حدث خطأ بسيط" else "Oh No! Something Went Wrong",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = DarkCocoa,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Detailed clear parent feedback
                Text(
                    text = if (isAr) {
                        "يبدو أن سحر الذكاء الاصطناعي واجه مشكلة أثناء صياغة القصة أو أن مفتاح الخدمة غير صحيح. يرجى التحقق مما يلي:\n\n" +
                        "1. الاتصال بشبكة الإنترنت 📶\n" +
                        "2. ضبط الـ API Key في لوحة Secrets 🔑\n\n" +
                        "التفاصيل: $errorMessage"
                    } else {
                        "It looks like our story magic ran into a glitch while writing. Please make sure that:\n\n" +
                        "1. You are connected to the internet 📶\n" +
                        "2. The Gemini API Key is set correctly in the Secrets panel 🔑\n\n" +
                        "Details: $errorMessage"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    ),
                    color = Color.Gray,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = WarmCoral),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isAr) "موافق، سأحاول مجدداً 🌟" else "Okay, I will Try Again 🌟",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
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
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("kids_stories_prefs", Context.MODE_PRIVATE) }
    val coroutineScope = rememberCoroutineScope()

    val quizQuestions = remember(story) { viewModel.getQuizQuestionsForSelectedStory() }
    val quizAnswers by viewModel.quizAnswers.collectAsState()
    val quizCompleted by viewModel.quizCompleted.collectAsState()

    // FEATURE 5: Kids-friendly configurable reading font size state
    var selectedFontSizeLevel by remember { mutableIntStateOf(prefs.getInt("kids_font_size", 1)) } // 0 = Junior, 1 = Explorer, 2 = Hero
    val textSp = when (selectedFontSizeLevel) {
        0 -> 16.sp
        1 -> 20.sp
        else -> 25.sp
    }
    val lineSp = when (selectedFontSizeLevel) {
        0 -> 25.sp
        1 -> 31.sp
        else -> 38.sp
    }

    // FEATURE 1: Beautiful paginated book chunks strategy
    val pages = remember(story.storyContent) {
        val raw = story.storyContent.split("\n\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (raw.size <= 1) {
            val sentences = story.storyContent.split(". ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
            if (sentences.size > 2) {
                sentences.chunked(3).map { it.joinToString(". ") + "." }
            } else {
                listOf(story.storyContent)
            }
        } else {
            raw
        }
    }
    var currentPageIndex by remember { mutableIntStateOf(0) }

    // FEATURE 3: Drawing & coloring canvas states
    var showDrawingCanvas by remember { mutableStateOf(false) }
    val drawnLines = remember { mutableStateListOf<DrawnLine>() }
    var currentDrawColor by remember { mutableStateOf(WarmCoral) }
    var currentStrokeWidth by remember { mutableStateOf(8f) }

    // FEATURE 6: Kids educational glossary bank
    val smartWords = remember(story) {
        if (isAr) {
            listOf(
                Triple("شجاعة 🦁", "Courage", "القدرة على مواجهة الأمور الصعبة برأس مرفوع وقلب قوي! 💪"),
                Triple("مغامرة 🚀", "Adventure", "استكشاف دروب وحكايات ممتعة لنتعلم أشياء جديدة! 🗺️"),
                Triple("تعاون 🤝", "Cooperation", "مساعدة أصدقائنا وأسرتنا لننجز أعمالاً عظيمة معاً! 🎈")
            )
        } else {
            listOf(
                Triple("Courage 🦁", "شجاعة", "Being brave enough to try our best even when things feel tough! 💪"),
                Triple("Adventure 🚀", "مغامرة", "Exploring wonderful experiences and seeking new knowledge! 🗺️"),
                Triple("Cooperation 🤝", "تعاون", "Working happily together with family and friends! 🎈")
            )
        }
    }

    // FEATURE 9: Rating states
    var ratedStars by remember { mutableIntStateOf(prefs.getInt("rating_${story.id}", 0)) }
    var selectedReaction by remember { mutableStateOf(prefs.getString("reaction_${story.id}", "") ?: "") }
    var showCelebrationFeedback by remember { mutableStateOf(false) }

    // Animation progress icon
    val emojiProgressIcon = when (story.setting) {
        "الغابة السحرية", "Enchanted Forest" -> "🐝"
        "الفضاء الواسع", "Deep Space" -> "🚀"
        "أعماق البحر", "Undersea World" -> "🐙"
        "مزرعة الجد", "Grandpa's Farm" -> "🏡"
        else -> "🌟"
    }

    Surface(
        color = SoftCream,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar Row with standard close and options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onBack() },
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

                // Quick font size toggle chip
                IconButton(
                    onClick = {
                        val next = (selectedFontSizeLevel + 1) % 3
                        selectedFontSizeLevel = next
                        prefs.edit().putInt("kids_font_size", next).apply()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                        .shadow(1.dp, CircleShape)
                ) {
                    Text(
                        text = when (selectedFontSizeLevel) {
                            0 -> "👶"
                            1 -> "🧒"
                            else -> "🦁"
                        },
                        fontSize = 18.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Kid-friendly Header Story banner
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = SunnyGold.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .border(2.dp, SunnyGold, RoundedCornerShape(24.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "✨ " + story.title + " ✨",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 21.sp,
                                    lineHeight = 28.sp,
                                    color = DarkCocoa
                                ),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "🧙‍♂️ ${story.heroType}  |  🌍 ${story.setting}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }

                // FEATURE 2: Magical Soundscapes & Synth Board Card
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .shadow(1.dp, RoundedCornerShape(20.dp))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isAr) "🎹 لوحة المؤثرات الصوتية السحرية" else "🎹 Magical Sound Board",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkCocoa,
                                modifier = Modifier.padding(bottom = 6.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                val sfxOptions = listOf(
                                    Pair("🪄", "magic"),
                                    Pair("🤖", "robot"),
                                    Pair("🛸", "laser"),
                                    Pair("🦁", "roar"),
                                    Pair("🎉", "victory")
                                )
                                sfxOptions.forEach { (emoji, sfxId) ->
                                    IconButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                KidsSoundSynth.playTone(sfxId)
                                            }
                                        },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                color = when(sfxId) {
                                                    "magic" -> Color(0xFFE1BEE7)
                                                    "robot" -> Color(0xFFCFD8DC)
                                                    "laser" -> Color(0xFFB3E5FC)
                                                    "roar" -> Color(0xFFFFCC80)
                                                    else -> Color(0xFFC8E6C9)
                                                },
                                                shape = CircleShape
                                            )
                                    ) {
                                        Text(text = emoji, fontSize = 20.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // FEATURE 1: Beautiful Paginated Book screen
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(26.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .shadow(2.dp, shape = RoundedCornerShape(26.dp))
                    ) {
                        Column(modifier = Modifier.padding(22.dp).animateContentSize()) {
                            // Current page content
                            val currentPageText = pages.getOrElse(currentPageIndex) { "" }
                            Text(
                                text = currentPageText,
                                style = MaterialTheme.typography.bodyLarge.merge(
                                    TextStyle(
                                        fontSize = textSp,
                                        lineHeight = lineSp,
                                        fontWeight = FontWeight.Medium,
                                        color = DarkCocoa,
                                        textDirection = if (isAr) TextDirection.Rtl else TextDirection.Ltr
                                    )
                                )
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            // Custom mathematically styled responsive slider with insect/rocket path indicators
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(20.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .background(Color.LightGray.copy(alpha = 0.35f), CircleShape)
                                )

                                val fraction = if (pages.size > 1) {
                                    currentPageIndex.toFloat() / (pages.size - 1)
                                } else {
                                    1f
                                }

                                if (fraction > 0f) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction)
                                            .height(6.dp)
                                            .background(SunnyGold, CircleShape)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = if (fraction == 1f) Arrangement.End else Arrangement.Start
                                ) {
                                    if (fraction > 0f && fraction < 1f) {
                                        Spacer(modifier = Modifier.fillMaxWidth(fraction - 0.05f).weight(1f, fill = false))
                                    }
                                    Text(
                                        text = emojiProgressIcon,
                                        fontSize = 18.sp,
                                        modifier = Modifier.offset(y = (-4).dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Back / Next buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = {
                                        if (currentPageIndex > 0) {
                                            currentPageIndex--
                                            coroutineScope.launch { KidsSoundSynth.playTone("laser") }
                                        }
                                    },
                                    enabled = currentPageIndex > 0,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = if (isAr) "⬅️ السابق" else "⬅️ Previous", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                Text(
                                    text = if (isAr) "صفحة ${currentPageIndex + 1} / ${pages.size}" else "P. ${currentPageIndex + 1} / ${pages.size}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = DarkCocoa)
                                )

                                Button(
                                    onClick = {
                                        if (currentPageIndex < pages.size - 1) {
                                            currentPageIndex++
                                            coroutineScope.launch { KidsSoundSynth.playTone("magic") }
                                        }
                                    },
                                    enabled = currentPageIndex < pages.size - 1,
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = if (isAr) "التالي ➡️" else "Next ➡️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // FEATURE 3: Collapsible Drawing Canvas Section
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(Color(0xFFEDE7F6))
                                .clickable { showDrawingCanvas = !showDrawingCanvas }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🎨", fontSize = 22.sp, modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = if (isAr) "دَفتَرُ الرّسمِ وتلوينِ البطلِ الصّغير" else "Coloring & Sketching Canvas",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF4527A0)
                                )
                            }
                            Text(text = if (showDrawingCanvas) "🔼" else "🔽", fontSize = 12.sp)
                        }

                        if (showDrawingCanvas) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(2.dp, Color(0xFF9575CD), RoundedCornerShape(24.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // Palette Color Circles
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        val palette = listOf(
                                            WarmCoral, SunnyGold, SkyBlue, MagicMint, Color(0xFFBA68C8), DarkCocoa
                                        )
                                        palette.forEach { col ->
                                            val isSelected = currentDrawColor == col
                                            Box(
                                                modifier = Modifier
                                                    .size(if (isSelected) 36.dp else 28.dp)
                                                    .background(col, CircleShape)
                                                    .border(
                                                        width = if (isSelected) 3.dp else 1.dp,
                                                        color = if (isSelected) Color.Black else Color.Gray.copy(alpha = 0.5f),
                                                        shape = CircleShape
                                                    )
                                                    .clickable { currentDrawColor = col }
                                            )
                                        }
                                    }

                                    // Canvas Drawing Panel with transparent Emoji base to trace color!
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(260.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color(0xFFFBFBFC))
                                            .border(1.5.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                    ) {
                                        // Trace guide in background
                                        Text(
                                            text = when(story.heroType) {
                                                "بطل شجاع", "Brave Explorer" -> "🦁"
                                                "روبوت ذكي", "Clever Robot" -> "🤖"
                                                "قطة فضولية", "Curious Kitten" -> "🐱"
                                                "جنيّة طيبة", "Kind Fairy" -> "✨"
                                                else -> "🐰"
                                            },
                                            fontSize = 90.sp,
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .alpha(0.12f)
                                        )

                                        androidx.compose.foundation.Canvas(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .pointerInput(Unit) {
                                                    detectDragGestures(
                                                        onDragStart = { offset ->
                                                            drawnLines.add(DrawnLine(listOf(offset), currentDrawColor, currentStrokeWidth))
                                                            prefs.edit().putBoolean("drew_something", true).apply()
                                                        },
                                                        onDrag = { change, _ ->
                                                            change.consume()
                                                            if (drawnLines.isNotEmpty()) {
                                                                val currentLine = drawnLines.last()
                                                                drawnLines[drawnLines.size - 1] = currentLine.copy(points = currentLine.points + change.position)
                                                            }
                                                        }
                                                    )
                                                }
                                        ) {
                                            drawnLines.forEach { line ->
                                                val path = Path()
                                                line.points.forEachIndexed { idx, pt ->
                                                    if (idx == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
                                                }
                                                drawPath(
                                                    path = path,
                                                    color = line.color,
                                                    style = Stroke(width = line.strokeWidth)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Controls: Undo + Clear
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Button(
                                            onClick = {
                                                if (drawnLines.isNotEmpty()) {
                                                    drawnLines.removeLast()
                                                }
                                            },
                                            enabled = drawnLines.isNotEmpty(),
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(text = "↩️ تراجع", color = DarkCocoa, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Brush Width Slider
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "✏️", fontSize = 14.sp)
                                            androidx.compose.material3.Slider(
                                                value = currentStrokeWidth,
                                                onValueChange = { currentStrokeWidth = it },
                                                valueRange = 4f..20f,
                                                modifier = Modifier.width(100.dp)
                                            )
                                        }

                                        Button(
                                            onClick = { drawnLines.clear() },
                                            enabled = drawnLines.isNotEmpty(),
                                            colors = ButtonDefaults.buttonColors(containerColor = WarmCoral),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(text = "🗑️ مسح الكل", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // FEATURE 6: Educational Vocabulary Bank
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFE8F5E9)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .border(2.dp, Color(0xFF81C784), RoundedCornerShape(24.dp))
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "💡", fontSize = 24.sp, modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = if (isAr) "بنك الكلمات الذكية للأذكياء" else "Smart Vocabulary Bank",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF2E7D32)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            smartWords.forEach { (word, eng, description) ->
                                var expandedWord by remember { mutableStateOf(false) }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                        .clickable { expandedWord = !expandedWord }
                                        .padding(12.dp)
                                        .animateContentSize()
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = word, fontWeight = FontWeight.Bold, color = DarkCocoa, fontSize = 14.sp)
                                        Text(text = eng, fontWeight = FontWeight.SemiBold, color = Color.Gray, fontSize = 12.sp)
                                    }
                                    if (expandedWord) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(text = description, fontSize = 13.sp, color = DarkCocoa, lineHeight = 18.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Dynamic Moral Lesson Box
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
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

                // Comprehension Quiz Section
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
                                        onClick = {
                                            viewModel.submitQuiz()
                                            // Handle quiz completion badge milestones
                                            val correctCount = quizQuestions.filterIndexed { index, q ->
                                                quizAnswers[index] == q.correctOptionIndex
                                            }.size
                                            if (correctCount == quizQuestions.size) {
                                                prefs.edit().putInt("quiz_genius_count", prefs.getInt("quiz_genius_count", 0) + 1).apply()
                                                coroutineScope.launch { KidsSoundSynth.playTone("victory") }
                                            } else {
                                                coroutineScope.launch { KidsSoundSynth.playTone("victory") }
                                            }
                                        },
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

                // FEATURE 9: Star Rating and Custom Interaction feedback emojis reviews Board
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(26.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .shadow(2.dp, shape = RoundedCornerShape(26.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(22.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (isAr) "⭐ قيم القصة وعبر عن شعورك!" else "⭐ Rate this Story & Share Feelings!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = DarkCocoa),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Golden Rating Stars interactive row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                (1..5).forEach { starIdx ->
                                    val isGlow = ratedStars >= starIdx
                                    Text(
                                        text = "⭐",
                                        fontSize = 32.sp,
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .clickable {
                                                ratedStars = starIdx
                                                prefs.edit().putInt("rating_${story.id}", starIdx).apply()
                                                showCelebrationFeedback = true
                                                coroutineScope.launch {
                                                    KidsSoundSynth.playTone("victory")
                                                }
                                            }
                                            .scale(if (isGlow) 1.2f else 1f)
                                            .alpha(if (isGlow) 1f else 0.3f)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Fun stickers reaction bar
                            Text(
                                text = if (isAr) "اختر ملصقاً تعبيرياً:" else "Choose a story reaction:",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.Gray),
                                modifier = Modifier.padding(bottom = 6.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                val stickers = listOf("😍", "😂", "😮", "🧙‍♂️", "🧸", "🦕")
                                stickers.forEach { sticker ->
                                    val isPicked = selectedReaction == sticker
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(46.dp)
                                            .background(
                                                color = if (isPicked) SunnyGold.copy(alpha = 0.25f) else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .border(
                                                width = if (isPicked) 2.dp else 0.dp,
                                                color = if (isPicked) SunnyGold else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                selectedReaction = sticker
                                                prefs.edit().putString("reaction_${story.id}", sticker).apply()
                                                showCelebrationFeedback = true
                                                coroutineScope.launch { KidsSoundSynth.playTone("magic") }
                                            }
                                    ) {
                                        Text(text = sticker, fontSize = 24.sp)
                                    }
                                }
                            }

                            if (showCelebrationFeedback) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isAr) "شكرًا لك يا بطل على التقييم! 🎉🥳" else "Thank you for the rating, little hero! 🎉🥳",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
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

data class DrawnLine(
    val points: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun KidsMilestonesSection(storiesCount: Int, isRtl: Boolean) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("kids_stories_prefs", Context.MODE_PRIVATE) }
    
    // Compute day reading streaks
    val streakDays = remember(storiesCount) {
        val count = prefs.getInt("reading_streak", 1)
        if (storiesCount > 0) count else 0
    }

    val drewSomething = remember { prefs.getBoolean("drew_something", false) }
    val quizGeniusCount = remember { prefs.getInt("quiz_genius_count", 0) }

    Card(
        colors = CardDefaults.cardColors(containerColor = SunnyGold.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.5.dp, SunnyGold.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title + streak counter
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isRtl) "🏆 أوسمة وإنجازات البطل الصغير" else "🏆 Little Hero Milestones",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = DarkCocoa
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("🔥", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "$streakDays " + (if (isRtl) "أيام" else "Days"),
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                        color = DarkCocoa
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display badges horizontally row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge 1: First story explorer
                BadgeIcon(
                    emoji = "📚",
                    label = if (isRtl) "أول كتاب" else "First Story",
                    unlocked = storiesCount >= 1
                )

                // Badge 2: Creative artist drawer
                BadgeIcon(
                    emoji = "🎨",
                    label = if (isRtl) "رسام مبدع" else "Creative Kid",
                    unlocked = drewSomething
                )

                // Badge 3: Smart quiz champion scorer
                BadgeIcon(
                    emoji = "👑",
                    label = if (isRtl) "ملك الذكاء" else "Quiz Genius",
                    unlocked = quizGeniusCount >= 1
                )

                // Badge 4: Story Library master
                BadgeIcon(
                    emoji = "🧚",
                    label = if (isRtl) "القارئ الفائق" else "Super Reader",
                    unlocked = storiesCount >= 3
                )
            }
        }
    }
}

@Composable
fun BadgeIcon(emoji: String, label: String, unlocked: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(54.dp)
                .background(
                    color = if (unlocked) SunnyGold.copy(alpha = 0.2f) else Color.LightGray.copy(alpha = 0.2f),
                    shape = CircleShape
                )
                .border(
                    width = if (unlocked) 2.dp else 1.dp,
                    color = if (unlocked) SunnyGold else Color.LightGray.copy(alpha = 0.5f),
                    shape = CircleShape
                )
        ) {
            Text(
                text = emoji,
                fontSize = 26.sp,
                modifier = Modifier.alpha(if (unlocked) 1f else 0.4f)
            )
            if (unlocked) {
                Text(
                    text = "✓",
                    fontSize = 10.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(16.dp)
                        .background(MagicMint, CircleShape)
                        .wrapContentSize(Alignment.Center)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                fontWeight = if (unlocked) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (unlocked) DarkCocoa else Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

