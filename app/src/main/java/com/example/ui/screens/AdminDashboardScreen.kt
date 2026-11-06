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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.UserEntity
import com.example.ui.FaustiViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusSuccess
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    viewModel: FaustiViewModel,
    onSelectProject: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val allManagers by viewModel.allManagers.collectAsState()

    var showCreateManagerDialog by remember { mutableStateOf(false) }
    var showPostAnnouncementDialog by remember { mutableStateOf(false) }

    // Metrics calculations
    val totalClients = allUsers.count { it.role == "CLIENT" }
    val totalProjects = allProjects.size
    val newRequests = allProjects.count { it.status == "Request Received" || it.status == "Under Review" }
    val activeInProduction = allProjects.count { it.status == "In Production" || it.status == "Quality Review" }
    val completedProjects = allProjects.count { it.status == "Completed" || it.status == "Delivered" }
    val totalQuotedValue = allProjects.sumOf { it.agreedPrice ?: it.currentQuotation ?: it.budget }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("admin_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ADMIN EXECUTIVE SUITE",
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = GoldPrimary
                    )
                    Text(
                        text = "Complete control of Fausti Arts studio operations & management.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Stats Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(title = "CLIENTS", value = "$totalClients", sub = "Registered", color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                    MetricCard(title = "PROJECTS", value = "$totalProjects", sub = "Total Submissions", color = GoldPrimary, modifier = Modifier.weight(1f))
                    MetricCard(title = "REQUESTS", value = "$newRequests", sub = "New Pending", color = GoldLight, modifier = Modifier.weight(1f))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(title = "ACTIVE", value = "$activeInProduction", sub = "In Production", color = GoldPrimary, modifier = Modifier.weight(1f))
                    MetricCard(title = "COMPLETED", value = "$completedProjects", sub = "Delivered", color = StatusSuccess, modifier = Modifier.weight(1f))
                    MetricCard(title = "QUOTED VALUE", value = "$${String.format(Locale.US, "%.0f", totalQuotedValue)}", sub = "Pipeline Total", color = GoldPrimary, modifier = Modifier.weight(1f))
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showCreateManagerDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("admin_create_manager_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Manager Account", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { showPostAnnouncementDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("admin_post_announcement_button"),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, GoldPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldLight)
                ) {
                    Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Publish News", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        // Section: Manager Accounts & Permissions
        item {
            Column {
                Text(
                    text = "STUDIO ART DIRECTORS & MANAGERS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = GoldLight
                )
                Spacer(modifier = Modifier.height(10.dp))

                allManagers.forEach { mgr ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.25f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(GoldPrimary.copy(alpha = 0.2f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = mgr.name.firstOrNull()?.uppercase() ?: "M",
                                            fontWeight = FontWeight.Bold,
                                            color = GoldLight
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(mgr.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                        Text(mgr.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "MANAGER",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Assigned Permissions: ${mgr.permissionsJson.replace("\"", "").replace("[", "").replace("]", "")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Section: Recent Client Submissions & Management
        item {
            Column {
                Text(
                    text = "ALL ACTIVE CLIENT PROJECTS",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    ),
                    color = GoldLight
                )
                Spacer(modifier = Modifier.height(10.dp))

                allProjects.forEach { proj ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelectProject(proj.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${proj.projectCode} • ${proj.title}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                StatusBadge(status = proj.status)
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Client: ${proj.clientName} • Category: ${proj.serviceCategory}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                val price = proj.agreedPrice ?: proj.currentQuotation ?: proj.budget
                                Text(
                                    text = "$${String.format(Locale.US, "%.0f", price)}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = GoldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Create Manager Account Dialog
    if (showCreateManagerDialog) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val permissions = remember {
            mutableStateListOf(
                "MANAGE_PROJECTS",
                "COMMUNICATE",
                "UPLOAD_DELIVERABLES",
                "UPDATE_PROGRESS"
            )
        }

        val allAvailablePermissions = listOf(
            "MANAGE_PROJECTS" to "View & manage assigned projects",
            "COMMUNICATE" to "Communicate with clients in project chat",
            "UPLOAD_DELIVERABLES" to "Upload deliverables & design assets",
            "UPDATE_PROGRESS" to "Advance project statuses in pipeline"
        )

        Dialog(onDismissRequest = { showCreateManagerDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.5.dp, GoldPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "CREATE MANAGER ACCOUNT",
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Manager Full Name") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Login Email") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("ASSIGN PERMISSIONS:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = GoldLight)
                    Spacer(modifier = Modifier.height(4.dp))

                    allAvailablePermissions.forEach { (key, desc) ->
                        val isChecked = permissions.contains(key)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (isChecked) permissions.remove(key) else permissions.add(key)
                                }
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { check ->
                                    if (check) permissions.add(key) else permissions.remove(key)
                                },
                                colors = CheckboxDefaults.colors(checkedColor = GoldPrimary)
                            )
                            Text(text = desc, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showCreateManagerDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                                    viewModel.createManager(name, email, password, permissions.toList())
                                    showCreateManagerDialog = false
                                } else {
                                    viewModel.showMessage("Please fill all manager fields.")
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                        ) {
                            Text("Create Account")
                        }
                    }
                }
            }
        }
    }

    // Post Announcement Dialog
    if (showPostAnnouncementDialog) {
        var title by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }
        var author by remember { mutableStateOf(currentUser?.name ?: "Fausti Studio") }

        Dialog(onDismissRequest = { showPostAnnouncementDialog = false }) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.5.dp, GoldPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "PUBLISH STUDIO ANNOUNCEMENT",
                        style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Announcement Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Message Content") }, modifier = Modifier.fillMaxWidth(), maxLines = 4)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showPostAnnouncementDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (title.isNotBlank() && content.isNotBlank()) {
                                    viewModel.postAnnouncement(title, content, author, "HIGH")
                                    showPostAnnouncementDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                        ) {
                            Text("Publish")
                        }
                    }
                }
            }
        }
    }
}
