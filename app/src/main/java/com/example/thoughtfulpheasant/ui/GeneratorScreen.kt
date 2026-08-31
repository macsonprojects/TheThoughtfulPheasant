package com.example.thoughtfulpheasant.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.thoughtfulpheasant.R
import com.example.thoughtfulpheasant.data.defaultMoodCategories
import com.example.thoughtfulpheasant.ui.theme.*
import kotlinx.coroutines.launch

val CloudShape = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val w = size.width
            val h = size.height
            
            // Start at the bottom left
            moveTo(w * 0.2f, h * 0.8f)
            
            // Bottom left puff
            cubicTo(w * 0.05f, h * 0.8f, 0f, h * 0.6f, w * 0.05f, h * 0.4f)
            
            // Top left puff
            cubicTo(w * 0.05f, h * 0.1f, w * 0.3f, 0f, w * 0.5f, h * 0.1f)
            
            // Top right puff
            cubicTo(w * 0.7f, 0f, w * 0.95f, h * 0.1f, w * 0.95f, h * 0.4f)
            
            // Bottom right puff
            cubicTo(w, h * 0.6f, w * 0.95f, h * 0.8f, w * 0.8f, h * 0.85f)
            
            // Bottom puff
            cubicTo(w * 0.7f, h, w * 0.3f, h, w * 0.2f, h * 0.8f)
            
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun GeneratorScreen(
    viewModel: GeneratorViewModel = viewModel(),
    onNavigateToEditor: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    GeneratorContent(
        uiState = uiState,
        onToggleMute = viewModel::onToggleMute,
        onImageTapped = viewModel::onImageTapped,
        onCategoryChange = viewModel::onCategoryChange,
        onNavigateToEditor = onNavigateToEditor
    )
}

@Composable
fun GeneratorContent(
    uiState: GeneratorUiState,
    onToggleMute: () -> Unit,
    onImageTapped: () -> Unit,
    onCategoryChange: (Int) -> Unit,
    onNavigateToEditor: () -> Unit
) {
    val scale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    var showAboutDialog by remember { mutableStateOf(false) }

    if (uiState.categories.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val categoryCount = uiState.categories.size
    // Centre point of an effectively-infinite range, rounded down to a
    // multiple of categoryCount so modulo arithmetic lines up cleanly,
    // then offset by the current tier so we land on the right category.
    val centerPage = Int.MAX_VALUE / 2 - (Int.MAX_VALUE / 2 % categoryCount)
    val pagerState = rememberPagerState(
        initialPage = centerPage + uiState.categoryIndex
    ) { Int.MAX_VALUE }

    LaunchedEffect(pagerState.currentPage) {
        onCategoryChange(pagerState.currentPage % uiState.categories.size)
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.meadow_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppDimens.PaddingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Title + Mute Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = AppDimens.PaddingSmall),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.app_title),
                        color = Color.TitleColor,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onToggleMute) {
                            Icon(
                                imageVector = if (uiState.isMuted) {
                                    Icons.AutoMirrored.Outlined.VolumeOff
                                } else {
                                    Icons.AutoMirrored.Outlined.VolumeUp
                                },
                                contentDescription = if (uiState.isMuted) {
                                    stringResource(R.string.unmute_voice)
                                } else {
                                    stringResource(R.string.mute_voice)
                                },
                                tint = if (uiState.isMuted) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    Color.TitleColor
                                }
                            )
                        }

                        IconButton(onClick = onNavigateToEditor) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = stringResource(R.string.editor_screen),
                                tint = Color.TitleColor
                            )
                        }

                        IconButton(onClick = { showAboutDialog = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.about_content_description),
                                tint = Color.TitleColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(0.4f))

                // Phrase Display
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CloudShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.CloudBackground
                    )
                ) {
                    Text(
                        text = uiState.displayedPhrase,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.CloudText,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = AppDimens.CloudPaddingHorizontal,
                                vertical = AppDimens.CloudPaddingVertical
                            )
                    )
                }

                Spacer(modifier = Modifier.weight(0.05f))

                // Thought dots angled towards bottom right
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Medium thought dot
                    Box(
                        modifier = Modifier
                            .size(AppDimens.DotSizeLarge)
                            .offset(x = AppDimens.DotOffsetXLarge)
                            .background(Color.CloudBackground, CircleShape)
                    )

                    // Small thought dot
                    Box(
                        modifier = Modifier
                            .size(AppDimens.DotSizeMedium)
                            .offset(x = AppDimens.DotOffsetXMedium)
                            .background(Color.CloudBackground, CircleShape)
                    )

                    // Tiny thought dot
                    Box(
                        modifier = Modifier
                            .size(AppDimens.DotSizeSmall)
                            .offset(x = AppDimens.DotOffsetXSmall)
                            .background(Color.CloudBackground, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.weight(0.05f))

                // Bottom Content: Clickable Image + Level Label
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AppDimens.SpacingMedium)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.roast_pheasant),
                        contentDescription = stringResource(R.string.tap_for_roast),
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppDimens.PheasantImageHeight)
                            .scale(scale.value)
                            .background(Color.Transparent)
                            .clip(RoundedCornerShape(20.dp))
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onImageTapped()

                                coroutineScope.launch {
                                    scale.animateTo(
                                        targetValue = 1.15f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                    scale.animateTo(
                                        targetValue = 1f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioHighBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                }
                            }
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                contentDescription = stringResource(R.string.previous_category),
                                tint = Color.PagerText
                            )
                        }

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                        ) { page ->
                            val index = page % categoryCount
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = uiState.categories[index].name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.PagerText,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        IconButton(onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = stringResource(R.string.next_category),
                                tint = Color.PagerText
                            )
                        }
                    }
                }

                if (!uiState.isTtsReady) {
                    Text(
                        text = stringResource(R.string.loading_voice_engine),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}


@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    val githubUrl = "https://github.com/MacsonProjects" // /TheThoughtfulPheasant"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.about_title),
                style = MaterialTheme.typography.titleMedium,
                color = Color.PagerText,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = stringResource(R.string.about_popup_title),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.app_version),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.copyright_info),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.licence_info),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.github_repository),
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.HyperlinkBlue,
                    modifier = Modifier.clickable {
                        uriHandler.openUri(githubUrl)
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.close_button))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GeneratorScreenPreview() {
    ThoughtfulPheasantTheme {
        GeneratorContent(
            uiState = GeneratorUiState(
                isTtsReady = true,
                isMuted = false,
                displayedPhrase = "Tap The Pheasant!",
                categoryIndex = 1,
                activeCategory = defaultMoodCategories[1],
                categories = defaultMoodCategories
            ),
            onToggleMute = {},
            onImageTapped = {},
            onCategoryChange = {},
            onNavigateToEditor = {}
        )
    }
}
