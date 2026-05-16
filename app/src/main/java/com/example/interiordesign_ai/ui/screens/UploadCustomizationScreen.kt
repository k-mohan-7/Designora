package com.example.interiordesign_ai.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.interiordesign_ai.viewmodel.DesignOpState
import com.example.interiordesign_ai.viewmodel.DesignViewModel

// ─── Color palette data ───────────────────────────────────────────────────────

private data class PaletteColor(val name: String, val value: Color)

private val neutrals = listOf(
    PaletteColor("White",      Color(0xFFFFFFFF)),
    PaletteColor("Off White",  Color(0xFFF5F0E8)),
    PaletteColor("Cream",      Color(0xFFF5E6C8)),
    PaletteColor("Beige",      Color(0xFFE8DCC8)),
    PaletteColor("Light Grey", Color(0xFFBDBDBD)),
    PaletteColor("Dark Grey",  Color(0xFF424242))
)

private val warmTones = listOf(
    PaletteColor("Peach",      Color(0xFFFFB38A)),
    PaletteColor("Terracotta", Color(0xFFCC6B5A)),
    PaletteColor("Mustard",    Color(0xFFD4AA00)),
    PaletteColor("Warm Brown", Color(0xFF7B3F1A))
)

private val coolTones = listOf(
    PaletteColor("Sky Blue",   Color(0xFF90CAF9)),
    PaletteColor("Navy Blue",  Color(0xFF0D1B5E)),
    PaletteColor("Mint Green", Color(0xFF80E8A0)),
    PaletteColor("Teal",       Color(0xFF00897B)),
    PaletteColor("Olive Green",Color(0xFF827717))
)

private val luxuryShades = listOf(
    PaletteColor("Emerald",    Color(0xFF2DB87A)),
    PaletteColor("Burgundy",   Color(0xFF7B1C2A)),
    PaletteColor("Charcoal",   Color(0xFF3A3A3A)),
    PaletteColor("Matte Black",Color(0xFF1A1A1A))
)

private val budgetOptions    = listOf("Low (Under ₹2L)", "Medium (₹2L - 5L)", "Premium (₹5L - 10L)", "Luxury (₹10L+)")
private val furnitureOptions = listOf("Sofa Set", "Bed", "Dining Table", "Wardrobe", "TV Unit", "Study Table", "Kitchen Cabinets", "Bookshelf", "Coffee Table", "Shoe Rack")
private val designStyleOpts  = listOf("Modern", "Traditional", "Minimalist", "Luxury", "Industrial", "Bohemian", "Scandinavian", "Japandi", "Contemporary")
private val lightingOptions  = listOf("Warm Lighting","Cool Lighting","Natural Daylight","Ambient Lighting","Cove Lighting","Pendant Lighting","Luxury Chandelier","Minimal Spotlights")

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun UploadCustomizationScreen(
    onBack: () -> Unit,
    designViewModel: DesignViewModel = viewModel()
) {
    val context = LocalContext.current
    var selectedImageUri  by remember { mutableStateOf<Uri?>(null) }
    var showSourceDialog  by remember { mutableStateOf(false) }
    var cameraImageUri    by remember { mutableStateOf<Uri?>(null) }

    // Gallery picker
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    // Camera capture
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { saved ->
        if (saved) selectedImageUri = cameraImageUri
    }

    // Camera permission
    val cameraPermLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            val uri = createCameraUri(context)
            cameraImageUri = uri
            cameraLauncher.launch(uri)
        }
    }

    // ── State ─────────────────────────────────────────────────────────────
    var area            by remember { mutableStateOf("") }
    var description     by remember { mutableStateOf("") }       // NEW — optional prompt
    var budget          by remember { mutableStateOf("Medium (₹2L - 5L)") }
    var budgetExpanded  by remember { mutableStateOf(false) }

    // Color — single-select from swatches OR custom text
    var selectedColorName by remember { mutableStateOf<String?>("White") }
    var showCustomColor   by remember { mutableStateOf(false) }
    var customColor       by remember { mutableStateOf("") }

    // Furniture — single-select chips + custom
    var selectedFurniture by remember { mutableStateOf("Sofa Set") }
    var showCustomFurniture by remember { mutableStateOf(false) }
    var customFurniture   by remember { mutableStateOf("") }

    // Design Style — single-select chips + custom
    var selectedStyle     by remember { mutableStateOf("Modern") }
    var showCustomStyle   by remember { mutableStateOf(false) }
    var customStyle       by remember { mutableStateOf("") }

    // Lighting — single-select chips + custom
    var selectedLighting  by remember { mutableStateOf("Warm Lighting") }
    var showCustomLighting by remember { mutableStateOf(false) }
    var customLighting    by remember { mutableStateOf("") }

    val opState        by designViewModel.opState.collectAsState()
    val generatedImage by designViewModel.generatedImageUrl.collectAsState()
    val snackbarState  = remember { SnackbarHostState() }
    val isLoading      = opState is DesignOpState.Loading

    // Resolved values (custom overrides selection)
    val resolvedColor     = if (showCustomColor && customColor.isNotBlank()) customColor else selectedColorName ?: "White"
    val resolvedFurniture = if (showCustomFurniture && customFurniture.isNotBlank()) customFurniture else selectedFurniture
    val resolvedStyle     = if (showCustomStyle && customStyle.isNotBlank()) customStyle else selectedStyle
    val resolvedLighting  = if (showCustomLighting && customLighting.isNotBlank()) customLighting else selectedLighting

    LaunchedEffect(opState) {
        if (opState is DesignOpState.Error) {
            snackbarState.showSnackbar((opState as DesignOpState.Error).message)
            designViewModel.resetOpState()
        }
    }

    // ── Image source chooser dialog ────────────────────────────────────────
    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Select Image Source", fontWeight = FontWeight.SemiBold, fontSize = 17.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEDE8FF))
                            .clickable {
                                showSourceDialog = false
                                imagePicker.launch("image/*")
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.PhotoLibrary, contentDescription = null,
                            tint = AppPurple, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Choose from Gallery", fontSize = 15.sp, color = Color(0xFF1A1A2E),
                            fontWeight = FontWeight.Medium)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFFEDE8FF))
                            .clickable {
                                showSourceDialog = false
                                val hasPerm = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED
                                if (hasPerm) {
                                    val uri = createCameraUri(context)
                                    cameraImageUri = uri
                                    cameraLauncher.launch(uri)
                                } else {
                                    cameraPermLauncher.launch(Manifest.permission.CAMERA)
                                }
                            }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null,
                            tint = AppPurple, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Take a Photo", fontSize = 15.sp, color = Color(0xFF1A1A2E),
                            fontWeight = FontWeight.Medium)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSourceDialog = false }) {
                    Text("Cancel", color = Color(0xFF888888))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarState) },
        containerColor = AppBg
    ) { scaffoldPadding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
            .padding(scaffoldPadding)
    ) {
        Spacer(modifier = Modifier.height(52.dp))

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back",
                    tint = Color(0xFF1A1A2E), modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Upload & Customization", fontSize = 20.sp,
                fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            // ── Room Photo ─────────────────────────────────────────────────
            SectionLabel("Room Photo")
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(
                        BorderStroke(1.5.dp, SolidColor(Color(0xFFBBB3E8))),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showSourceDialog = true },
                contentAlignment = Alignment.Center
            ) {
                if (selectedImageUri != null) {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = "Selected Room Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(14.dp))
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEDE8FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "⬆", fontSize = 22.sp, color = AppPurple)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Upload Room Photo", fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1A1A2E))
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("Tap to select from gallery or capture", fontSize = 12.sp, color = Color(0xFF999999))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Area ───────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("Area (sq.ft)")
                Spacer(modifier = Modifier.width(4.dp))
                Text(" *", fontSize = 13.sp, color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = area,
                onValueChange = { area = it },
                placeholder = { Text("e.g. 600", fontSize = 14.sp, color = Color(0xFFBBBBBB)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = AppPurple
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Description (Optional) ─────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("Description")
                Spacer(modifier = Modifier.width(6.dp))
                Text("(Optional)", fontSize = 11.sp, color = Color(0xFF999999))
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = {
                    Text(
                        "e.g. Add a reading nook by the window, make the room feel airy and open",
                        fontSize = 13.sp, color = Color(0xFFBBBBBB), lineHeight = 18.sp
                    )
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = AppPurple
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Budget ─────────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("Budget")
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Filled.CurrencyRupee, contentDescription = null,
                    tint = AppPurple, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            ExposedDropdownMenuBox(
                expanded = budgetExpanded,
                onExpandedChange = { budgetExpanded = !budgetExpanded }
            ) {
                OutlinedTextField(
                    value = budget,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        Icon(Icons.Filled.ArrowDropDown, null, tint = Color(0xFF555555))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = AppPurple
                    )
                )
                ExposedDropdownMenu(
                    expanded = budgetExpanded,
                    onDismissRequest = { budgetExpanded = false }
                ) {
                    budgetOptions.forEach { opt ->
                        DropdownMenuItem(
                            text = { Text(opt, fontSize = 14.sp) },
                            onClick = { budget = opt; budgetExpanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Color Preference ───────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("Color Preference")
                Spacer(modifier = Modifier.width(6.dp))
                Text("🎨", fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))

            ColorGroup("Neutrals",      neutrals,     selectedColorName, !showCustomColor) { selectedColorName = it; showCustomColor = false }
            Spacer(modifier = Modifier.height(12.dp))
            ColorGroup("Warm Tones",    warmTones,    selectedColorName, !showCustomColor) { selectedColorName = it; showCustomColor = false }
            Spacer(modifier = Modifier.height(12.dp))
            ColorGroup("Cool Tones",    coolTones,    selectedColorName, !showCustomColor) { selectedColorName = it; showCustomColor = false }
            Spacer(modifier = Modifier.height(12.dp))
            ColorGroup("Luxury Shades", luxuryShades, selectedColorName, !showCustomColor) { selectedColorName = it; showCustomColor = false }

            Spacer(modifier = Modifier.height(10.dp))
            // Custom color option
            CustomOptionRow(
                label = "Custom Color",
                isActive = showCustomColor,
                customValue = customColor,
                placeholder = "e.g. Rose Gold, Sage Green",
                onToggle = { showCustomColor = !showCustomColor },
                onValueChange = { customColor = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Furniture Type (choice chips + custom) ─────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("Furniture Type")
                Spacer(modifier = Modifier.width(6.dp))
                Text("🛋", fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            SelectionChipGrid(
                options = furnitureOptions,
                selected = selectedFurniture,
                isCustomActive = showCustomFurniture,
                onSelect = { selectedFurniture = it; showCustomFurniture = false }
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomOptionRow(
                label = "Custom Furniture",
                isActive = showCustomFurniture,
                customValue = customFurniture,
                placeholder = "e.g. L-shaped Sofa, Bunk Bed",
                onToggle = { showCustomFurniture = !showCustomFurniture },
                onValueChange = { customFurniture = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Design Style (choice chips + custom) ───────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("Design Style")
                Spacer(modifier = Modifier.width(6.dp))
                Text("🧩", fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            SelectionChipGrid(
                options = designStyleOpts,
                selected = selectedStyle,
                isCustomActive = showCustomStyle,
                onSelect = { selectedStyle = it; showCustomStyle = false }
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomOptionRow(
                label = "Custom Style",
                isActive = showCustomStyle,
                customValue = customStyle,
                placeholder = "e.g. Art Deco, Wabi-Sabi",
                onToggle = { showCustomStyle = !showCustomStyle },
                onValueChange = { customStyle = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Lighting (choice chips + custom) ───────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionLabel("Lighting")
                Spacer(modifier = Modifier.width(6.dp))
                Text("💡", fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            SelectionChipGrid(
                options = lightingOptions,
                selected = selectedLighting,
                isCustomActive = showCustomLighting,
                onSelect = { selectedLighting = it; showCustomLighting = false }
            )
            Spacer(modifier = Modifier.height(8.dp))
            CustomOptionRow(
                label = "Custom Lighting",
                isActive = showCustomLighting,
                customValue = customLighting,
                placeholder = "e.g. Neon Accent, Fairy Lights",
                onToggle = { showCustomLighting = !showCustomLighting },
                onValueChange = { customLighting = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Generate Design button ─────────────────────────────────────
            Button(
                onClick = {
                    val uri = selectedImageUri
                    if (uri != null) {
                        designViewModel.generateAndSaveDesign(
                            context       = context,
                            imageUri      = uri,
                            title         = "$resolvedStyle Room Design",
                            area          = area,
                            budget        = budget,
                            colorPref     = resolvedColor,
                            furnitureType = resolvedFurniture,
                            designStyle   = resolvedStyle,
                            lighting      = resolvedLighting,
                            description   = description
                        )
                    }
                },
                enabled = !isLoading && selectedImageUri != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppPurple,
                    disabledContainerColor = Color(0xFFCCCCCC)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Generating…", fontSize = 14.sp, color = Color.White)
                } else {
                    Text("Generate Design", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }

            // ── Generated Result ───────────────────────────────────────────
            if (generatedImage != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Generated Design", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = generatedImage,
                    contentDescription = "Generated Design",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
    }
}

// ─── Reusable selection-chip grid (2 columns, wrapping) ──────────────────────

@Composable
private fun SelectionChipGrid(
    options: List<String>,
    selected: String,
    isCustomActive: Boolean,
    onSelect: (String) -> Unit
) {
    val chunked = options.chunked(2)
    chunked.forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            row.forEach { opt ->
                val isSelected = selected == opt && !isCustomActive
                SelectionChip(
                    label = opt,
                    selected = isSelected,
                    modifier = Modifier.weight(1f),
                    onClick = { onSelect(opt) }
                )
            }
            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
private fun SelectionChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) AppPurple else Color.White)
            .border(
                BorderStroke(1.dp, if (selected) AppPurple else Color(0xFFDDDDDD)),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) Color.White else Color(0xFF444444),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

// ─── Custom option row (toggle + text field) ─────────────────────────────────

@Composable
private fun CustomOptionRow(
    label: String,
    isActive: Boolean,
    customValue: String,
    placeholder: String,
    onToggle: () -> Unit,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isActive) AppPurple.copy(alpha = 0.12f) else Color(0xFFF5F5F5))
                .border(
                    BorderStroke(1.dp, if (isActive) AppPurple else Color(0xFFDDDDDD)),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onToggle() }
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = null,
                    tint = if (isActive) AppPurple else Color(0xFF888888),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isActive) AppPurple else Color(0xFF666666)
                )
            }
        }
    }
    if (isActive) {
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = customValue,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.sp, color = Color(0xFFBBBBBB)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedBorderColor = AppPurple.copy(alpha = 0.3f),
                focusedBorderColor = AppPurple
            ),
            singleLine = true
        )
    }
}

// ─── Helpers ─────────────────────────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = AppPurple,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun ColorGroup(
    groupName: String,
    colors: List<PaletteColor>,
    selectedName: String?,
    highlightEnabled: Boolean,
    onSelect: (String) -> Unit
) {
    Text(text = groupName, fontSize = 13.sp, fontWeight = FontWeight.Medium,
        color = Color(0xFF444444))
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top
    ) {
        colors.forEach { pc ->
            val isSelected = highlightEnabled && selectedName == pc.name
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onSelect(pc.name) }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(pc.value)
                            .then(
                                if (isSelected)
                                    Modifier.border(2.5.dp, AppPurple, CircleShape)
                                else
                                    Modifier.border(1.dp, Color(0x22000000), CircleShape)
                            )
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = if (pc.value.luminance() > 0.5f) AppPurple else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pc.name,
                    fontSize = 10.sp,
                    color = Color(0xFF666666),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.widthIn(max = 48.dp)
                )
            }
        }
    }
}

private fun Color.luminance(): Float {
    val r = red; val g = green; val b = blue
    return 0.2126f * r + 0.7152f * g + 0.0722f * b
}

// ─── Camera URI helper ────────────────────────────────────────────────────────

private fun createCameraUri(context: android.content.Context): Uri {
    val dir = File(context.cacheDir, "camera_images").also { it.mkdirs() }
    val file = File.createTempFile("room_photo_", ".jpg", dir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}
