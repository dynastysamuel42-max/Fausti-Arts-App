package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FaustiViewModel
import com.example.ui.components.AiAssistantDialog
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectRequestScreen(
    viewModel: FaustiViewModel,
    initialCategory: String = "Logo Design and Branding",
    onProjectCreated: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val aiState by viewModel.aiAssistantState.collectAsState()

    var projectTitle by remember { mutableStateOf("") }
    var serviceCategory by remember { mutableStateOf(initialCategory) }
    var description by remember { mutableStateOf("") }
    var attachments by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("2026-11-30") }
    var budgetText by remember { mutableStateOf("600") }
    var specialInstructions by remember { mutableStateOf("") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    var showAiAssistantDialog by remember { mutableStateOf(false) }

    val categories = listOf(
        "Graphic Design",
        "Logo Design and Branding",
        "Posters and Flyers",
        "Social Media Designs",
        "Digital Artwork",
        "Photo Editing",
        "Video Editing",
        "Animation",
        "Character Design",
        "Website Design",
        "Custom Creative Projects"
    )

    val (curatedKeywords, clarifyingQuestions) = remember(serviceCategory) {
        viewModel.getCategoryGuidance(serviceCategory)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("project_request_screen")
    ) {
        Text(
            text = "REQUEST A PROJECT",
            style = MaterialTheme.typography.displayMedium.copy(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            ),
            color = GoldPrimary
        )

        Text(
            text = "Collaborate with Fausti Arts art directors on your next masterpiece.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // AI Vision Assistant Showcase Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_assistant_banner"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            border = BorderStroke(1.5.dp, GoldPrimary.copy(alpha = 0.8f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(GoldPrimary, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI-Powered Creative Assistant",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldLight
                        )
                        Text(
                            text = "Need help articulating your vision? Let our AI prompt you with smart questions and generate a detailed brief.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.resetAiState()
                        showAiAssistantDialog = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("open_ai_assistant_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    )
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open Fausti AI Creative Assistant",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Fields
        // 1. Project Title
        OutlinedTextField(
            value = projectTitle,
            onValueChange = { projectTitle = it },
            label = { Text("Project Title *") },
            placeholder = { Text("e.g., Sovereign Luxury Watch Branding") },
            leadingIcon = { Icon(Icons.Default.Title, contentDescription = null, tint = GoldLight) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("project_title_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 2. Service Category Dropdown
        ExposedDropdownMenuBox(
            expanded = categoryDropdownExpanded,
            onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = serviceCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Service Category *") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .testTag("service_category_selector"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            ExposedDropdownMenu(
                expanded = categoryDropdownExpanded,
                onDismissRequest = { categoryDropdownExpanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat) },
                        onClick = {
                            serviceCategory = cat
                            categoryDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick keyword pills for service category
        Text(
            text = "Suggested keywords for $serviceCategory:",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(6.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            curatedKeywords.take(4).forEach { keyword ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable {
                        description = if (description.isBlank()) {
                            "Preferred aesthetic: $keyword."
                        } else {
                            "$description\n• Key aesthetic: $keyword"
                        }
                    }
                ) {
                    Text(
                        text = "+ $keyword",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Detailed Project Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Detailed Project Description *") },
            placeholder = { Text("Describe your vision, goals, target audience, mandatory brand guidelines, and deliverables. You can also use the AI Assistant above to generate this!") },
            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = GoldLight) },
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .testTag("project_description_input"),
            shape = RoundedCornerShape(14.dp),
            maxLines = 10,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 4. Budget & Deadline Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = budgetText,
                onValueChange = { budgetText = it },
                label = { Text("Estimated Budget ($) *") },
                leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = GoldLight) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("project_budget_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = deadline,
                onValueChange = { deadline = it },
                label = { Text("Preferred Deadline *") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = GoldLight) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("project_deadline_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 5. Reference Attachments / URLs
        OutlinedTextField(
            value = attachments,
            onValueChange = { attachments = it },
            label = { Text("Reference Attachments / Moodboard URLs") },
            placeholder = { Text("e.g., moodboard_drive_link, inspiration.png, brand_assets.pdf") },
            leadingIcon = { Icon(Icons.Default.AttachFile, contentDescription = null, tint = GoldLight) },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("project_attachments_input"),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // 6. Special Instructions
        OutlinedTextField(
            value = specialInstructions,
            onValueChange = { specialInstructions = it },
            label = { Text("Special Instructions or Constraints") },
            placeholder = { Text("Any specific dimension requirements, source file formats, confidentiality notes...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .testTag("project_instructions_input"),
            shape = RoundedCornerShape(14.dp),
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GoldPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Submit Button
        Button(
            onClick = {
                if (projectTitle.isBlank()) {
                    viewModel.showMessage("Please enter a project title.")
                    return@Button
                }
                if (description.isBlank()) {
                    viewModel.showMessage("Please provide a project description or use the AI Assistant.")
                    return@Button
                }
                val budget = budgetText.toDoubleOrNull() ?: 100.0

                viewModel.submitProjectRequest(
                    title = projectTitle,
                    serviceCategory = serviceCategory,
                    description = description,
                    deadline = deadline,
                    budget = budget,
                    specialInstructions = specialInstructions,
                    referenceAttachments = attachments,
                    onSuccess = { newProjectId ->
                        onProjectCreated(newProjectId)
                    }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_project_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GoldPrimary,
                contentColor = Color.Black
            )
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Submit Project Request",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showAiAssistantDialog) {
        AiAssistantDialog(
            serviceCategory = serviceCategory,
            projectTitle = projectTitle,
            currentDescription = description,
            curatedKeywords = curatedKeywords,
            clarifyingQuestions = clarifyingQuestions,
            aiState = aiState,
            onGenerate = { audience, vibe, notes ->
                viewModel.generateAiVisionBrief(
                    serviceCategory = serviceCategory,
                    projectTitle = projectTitle,
                    initialIdea = notes,
                    targetAudience = audience,
                    preferredVibe = vibe
                )
            },
            onApplyDescription = { generatedBrief ->
                description = generatedBrief
            },
            onDismiss = { showAiAssistantDialog = false }
        )
    }
}
