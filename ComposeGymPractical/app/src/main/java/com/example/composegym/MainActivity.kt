package com.example.composegym

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Палитра взята из Figma: node-id=3:523, компонент GYM Band Pro.
private val Night = Color(0xFF16191A)
private val NightSurface = Color(0xFF222628)
private val NightOutline = Color(0xFF303537)
private val Lime = Color(0xFFB7FF36)
private val White = Color(0xFFF6F7F3)
private val Muted = Color(0xFF858D90)

private val darkColors = darkColorScheme(
    primary = Lime, onPrimary = Night,
    secondary = Muted, background = Night, onBackground = White,
    surface = NightSurface, onSurface = White, outline = NightOutline
)
private val lightColors = lightColorScheme(
    primary = Color(0xFF507400), onPrimary = Color.White,
    secondary = Color(0xFF576064), background = Color(0xFFF6F7F3),
    onBackground = Night, surface = Color.White, onSurface = Night,
    outline = Color(0xFFD0D7D4)
)

@Composable
fun GymTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) darkColors else lightColors,
        typography = Typography(),
        shapes = Shapes(medium = RoundedCornerShape(18.dp)),
        content = content
    )
}

data class Message(val author: String, val body: String)

private val sampleMessages = listOf(
    Message("Тренер Алексей", "Добро пожаловать в GYM! Сегодня начнём с разминки и лёгкой силовой тренировки. Не забывайте про воду и отдых между подходами."),
    Message("GYM Support", "Ваш браслет GYM Band Pro найден рядом. Заряд аккумулятора — 94%. Нажмите, чтобы посмотреть подробности подключения устройства."),
    Message("Тренер Алексей", "План тренировки: 10 минут разминка, 3 подхода приседаний по 12 повторений, 3 подхода отжиманий по 10 повторений и растяжка."),
    Message("GYM Club", "Вы выполнили первую тренировку! Отличное начало. Продолжайте заниматься регулярно — прогресс складывается из небольших шагов."),
    Message("Тренер Алексей", "На завтра запланирован восстановительный день. Рекомендуем спокойную прогулку и упражнения на подвижность.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

@Composable
fun App() {
    var darkTheme by remember { mutableStateOf(true) }
    GymTheme(darkTheme) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            BoxWithConstraints {
                val tablet = maxWidth >= 600.dp
                if (tablet) {
                    Row(Modifier.fillMaxSize().padding(24.dp), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column(Modifier.weight(0.7f).fillMaxHeight(), verticalArrangement = Arrangement.Center) {
                            BrandHeader()
                            Spacer(Modifier.height(20.dp))
                            Text("Ваши сообщения и тренировки в одном месте", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
                            Spacer(Modifier.height(16.dp))
                            Text("Планшетный интерфейс адаптирован с помощью BoxWithConstraints и Row.", color = MaterialTheme.colorScheme.secondary)
                        }
                        Column(Modifier.weight(1f)) {
                            HeaderSwitch(darkTheme) { darkTheme = it }
                            Conversation(sampleMessages)
                        }
                    }
                } else {
                    Column(Modifier.fillMaxSize()) {
                        HeaderSwitch(darkTheme) { darkTheme = it }
                        Conversation(sampleMessages)
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandHeader() {
    Text("GYM", fontSize = 48.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
    Text("TRAIN • TRACK • REPEAT", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
}

@Composable
private fun HeaderSwitch(darkTheme: Boolean, onThemeChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("GYM", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(12.dp))
        Text("Сообщения", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        Text("Тёмная", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
        Switch(checked = darkTheme, onCheckedChange = onThemeChange, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun MessageCard(msg: Message) {
    var isExpanded by remember(msg) { mutableStateOf(false) }
    val bg by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colorScheme.primary.copy(alpha = 0.17f)
                      else MaterialTheme.colorScheme.surface,
        label = "messageColor"
    )
    Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 6.dp), verticalAlignment = Alignment.Top) {
        Image(
            painter = painterResource(R.drawable.avatar),
            contentDescription = "Аватар отправителя ${msg.author}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(44.dp).clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(msg.author, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(5.dp))
            Surface(
                modifier = Modifier.fillMaxWidth().clip(MaterialTheme.shapes.medium)
                    .clickable { isExpanded = !isExpanded }.animateContentSize()
                    .border(1.dp, MaterialTheme.colorScheme.outline, MaterialTheme.shapes.medium),
                color = bg,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(Modifier.padding(14.dp)) {
                    Text(
                        msg.body,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(if (isExpanded) "Свернуть ↑" else "Подробнее ↓", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn(contentPadding = PaddingValues(bottom = 18.dp)) {
        items(messages) { message -> MessageCard(message) }
    }
}

@Preview(name = "Телефон · тёмная", showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun PreviewPhoneDark() { GymTheme(true) { Surface { Conversation(sampleMessages) } } }

@Preview(name = "Телефон · светлая", showBackground = true, widthDp = 393, heightDp = 852, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewPhoneLight() { GymTheme(false) { Surface { Conversation(sampleMessages) } } }

@Preview(name = "Планшет", showBackground = true, widthDp = 800, heightDp = 600)
@Composable
fun PreviewTablet() { App() }

@Preview(name = "Hello world!")
@Composable
fun PreviewText() { GymTheme(true) { Text("Hello world!") } }
