package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.DeliverableEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.QuotationNegotiationEntity
import com.example.data.local.entity.RevisionEntity
import com.example.data.local.entity.UserEntity
import com.example.ui.FaustiViewModel
import com.example.ui.components.StatusBadge
import com.example.ui.theme.GoldDark
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.StatusSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    viewModel: FaustiViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project by viewModel.selectedProject.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val negotiations by viewModel.selectedProjectNegotiations.collectAsState()
    val messages by viewModel.selectedProjectMessages.collectAsState()
    val deliverables by viewModel.selectedProjectDeliverables.collectAsState()
    val revisions by viewModel.selectedProjectRevisions.collectAsState()
    val payments by viewModel.selectedProjectPayments.collectAsState()
    val managers by viewModel.allManagers.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Overview", "Negotiation", "Chat", "Deliverables", "Revisions", "Payments")

    // Dialog states
    var showCounterOfferDialog by remember { mutableStateOf(false) }
    var showDeliverableDialog by remember { mutableStateOf(false) }
    var showRevisionDialog by remember { mutableStateOf(false) }
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }
    var showAssignManagerDialog by remember { mutableStateOf(false) }

    if (project == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Loading project details...", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val currentProj = project!!
    val isClient = currentUser?.role == "CLIENT"
    val isAdmin = currentUser?.role == "ADMIN"
    val isManager = currentUser?.role == "MANAGER"
    val canManage = isAdmin || (isManager && currentProj.managerId == currentUser?.id)

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("project_detail_screen")
    ) {
        // Top App Header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentProj.projectCode,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = GoldPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            StatusBadge(status = currentProj.status)
                        }
                        Text(
                            text = currentProj.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            ),
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (canManage) {
                        IconButton(onClick = { showStatusDialog = true }, modifier = Modifier.testTag("change_status_button")) {
                            Icon(Icons.Default.Edit, contentDescription = "Change Status", tint = GoldLight)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Metadata bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Client: ${currentProj.clientName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (currentProj.managerName != null) "Director: ${currentProj.managerName}" else "Unassigned Manager",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = if (currentProj.managerName != null) GoldLight else MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Sub-tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = GoldPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = GoldPrimary,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, tabTitle ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = tabTitle,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        // Sub-tab contents
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTabIndex) {
                0 -> ProjectOverviewTab(
                    project = currentProj,
                    canManage = canManage,
                    onAssignManagerClick = { showAssignManagerDialog = true }
                )
                1 -> ProjectNegotiationTab(
                    project = currentProj,
                    negotiations = negotiations,
                    currentUser = currentUser,
                    onOpenCounterOffer = { showCounterOfferDialog = true },
                    onAccept = { negId, price ->
                        viewModel.respondToQuotation(currentProj.id, true, negId, price)
                    },
                    onReject = { negId, price ->
                        viewModel.respondToQuotation(currentProj.id, false, negId, price)
                    }
                )
                2 -> ProjectChatTab(
                    project = currentProj,
                    messages = messages,
                    currentUser = currentUser,
                    onSendMessage = { text, attType, attName ->
                        viewModel.sendMessage(currentProj.id, text, attType, attName)
                    }
                )
                3 -> ProjectDeliverablesTab(
                    project = currentProj,
                    deliverables = deliverables,
                    canManage = canManage,
                    isClient = isClient,
                    onUploadClick = { showDeliverableDialog = true },
                    onApproveClick = { del -> viewModel.approveDeliverable(del) },
                    onRequestRevisionClick = { showRevisionDialog = true }
                )
                4 -> ProjectRevisionsTab(
                    project = currentProj,
                    revisions = revisions,
                    canManage = canManage,
                    onRequestRevisionClick = { showRevisionDialog = true },
                    onResolveClick = { rev, notes -> viewModel.resolveRevision(rev, notes) }
                )
                5 -> ProjectPaymentsTab(
                    project = currentProj,
                    payments = payments,
                    isAdmin = isAdmin,
                    onRecordPaymentClick = { showPaymentDialog = true }
                )
            }
        }
    }

    // Dialogs
    if (showCounterOfferDialog) {
        CounterOfferDialog(
            currentPrice = currentProj.currentQuotation ?: currentProj.budget,
            onDismiss = { showCounterOfferDialog = false },
            onSubmit = { price, note ->
                viewModel.sendCounterOffer(currentProj.id, price, note)
                showCounterOfferDialog = false
            }
        )
    }

    if (showDeliverableDialog) {
        UploadDeliverableDialog(
            onDismiss = { showDeliverableDialog = false },
            onSubmit = { title, type, name, size, url, notes ->
                viewModel.uploadDeliverable(currentProj.id, title, type, name, size, url, notes)
                showDeliverableDialog = false
            }
        )
    }

    if (showRevisionDialog) {
        RequestRevisionDialog(
            onDismiss = { showRevisionDialog = false },
            onSubmit = { desc, refs ->
                viewModel.requestRevision(currentProj.id, desc, refs)
                showRevisionDialog = false
            }
        )
    }

    if (showPaymentDialog) {
        RecordPaymentDialog(
            agreedPrice = currentProj.agreedPrice ?: currentProj.budget,
            currentPaid = currentProj.paidAmount,
            onDismiss = { showPaymentDialog = false },
            onSubmit = { amount, type, method, notes ->
                viewModel.recordPayment(currentProj.id, amount, type, method, notes)
                showPaymentDialog = false
            }
        )
    }

    if (showStatusDialog) {
        StatusChangeDialog(
            currentStatus = currentProj.status,
            onDismiss = { showStatusDialog = false },
            onSave = { newStatus ->
                viewModel.updateProjectStatus(currentProj.id, newStatus)
                showStatusDialog = false
            }
        )
    }

    if (showAssignManagerDialog) {
        AssignManagerDialog(
            managers = managers,
            onDismiss = { showAssignManagerDialog = false },
            onSelect = { manager ->
                viewModel.assignManager(currentProj.id, manager)
                showAssignManagerDialog = false
            }
        )
    }
}

// -------------------------------------------------------------
// SUB-TAB 0: OVERVIEW
// -------------------------------------------------------------
@Composable
fun ProjectOverviewTab(
    project: ProjectEntity,
    canManage: Boolean,
    onAssignManagerClick: () -> Unit
) {
    val allStatuses = listOf(
        "Request Received",
        "Under Review",
        "Price Negotiation",
        "Awaiting Approval",
        "Approved",
        "In Production",
        "Quality Review",
        "Revision Requested",
        "Completed",
        "Delivered"
    )

    val currentStatusIndex = allStatuses.indexOf(project.status).coerceAtLeast(0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "DETAILED PROJECT DESCRIPTION",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = project.description,
                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (project.specialInstructions.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "SPECIAL INSTRUCTIONS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = project.specialInstructions,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (project.referenceAttachments.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "REFERENCE ATTACHMENTS",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = GoldLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = project.referenceAttachments,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Budget & Timeline Summary Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("TARGET GUIDELINE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = if (project.budget > 0) "$${String.format(Locale.US, "%.0f", project.budget)}" else "Custom Quote",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column {
                            Text("AGREED QUOTE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val agreed = project.agreedPrice
                            Text(
                                text = if (agreed != null) "$${String.format(Locale.US, "%.0f", agreed)}" else "Custom Quote Pending",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = GoldPrimary
                            )
                        }
                        Column {
                            Text("DEADLINE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(project.deadline, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = GoldLight)
                        }
                    }

                    if (canManage) {
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = onAssignManagerClick,
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, GoldPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (project.managerName != null) "Reassign Art Director (${project.managerName})" else "Assign Project to Art Director",
                                color = GoldLight
                            )
                        }
                    }
                }
            }
        }

        // 10 Status Production Pipeline
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "STUDIO PRODUCTION TIMELINE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    allStatuses.forEachIndexed { index, statusName ->
                        val isDone = index <= currentStatusIndex
                        val isCurrent = index == currentStatusIndex

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isCurrent -> GoldPrimary
                                            isDone -> StatusSuccess
                                            else -> MaterialTheme.colorScheme.surfaceVariant
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone && !isCurrent) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                } else {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = statusName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (isCurrent) GoldPrimary else if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 1: NEGOTIATION
// -------------------------------------------------------------
@Composable
fun ProjectNegotiationTab(
    project: ProjectEntity,
    negotiations: List<QuotationNegotiationEntity>,
    currentUser: UserEntity?,
    onOpenCounterOffer: () -> Unit,
    onAccept: (negId: Long, price: Double) -> Unit,
    onReject: (negId: Long, price: Double) -> Unit
) {
    val lastNegotiation = negotiations.lastOrNull()
    val isClient = currentUser?.role == "CLIENT"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Summary Header Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "PRICE NEGOTIATION SYSTEM",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("CURRENT VALUATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        val price = project.agreedPrice ?: project.currentQuotation ?: project.budget
                        Text(
                            text = "$${String.format(Locale.US, "%.2f", price)}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = GoldPrimary
                        )
                    }

                    Button(
                        onClick = onOpenCounterOffer,
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("send_counteroffer_button")
                    ) {
                        Icon(Icons.Default.MonetizationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Send Counteroffer", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "NEGOTIATION HISTORY & COUNTEROFFERS",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = GoldLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (negotiations.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No negotiations recorded yet. Submit a counteroffer above to begin.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(negotiations) { neg ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (neg.senderRole == "CLIENT") MaterialTheme.colorScheme.surfaceVariant else GoldPrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${neg.senderName} (${neg.senderRole})",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (neg.senderRole == "CLIENT") MaterialTheme.colorScheme.onSurface else GoldLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", neg.proposedPrice)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = GoldPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = neg.note,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            // If this is the latest pending offer from the other party, show accept/reject buttons!
                            val isFromOtherParty = (isClient && neg.senderRole != "CLIENT") || (!isClient && neg.senderRole == "CLIENT")
                            if (neg.id == lastNegotiation?.id && neg.status == "PENDING" && isFromOtherParty) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Button(
                                        onClick = { onAccept(neg.id, neg.proposedPrice) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("accept_quotation_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess, contentColor = Color.White)
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Accept Offer", fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = { onReject(neg.id, neg.proposedPrice) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(40.dp)
                                            .testTag("reject_quotation_button"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                    ) {
                                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Decline Offer")
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

// -------------------------------------------------------------
// SUB-TAB 2: CHAT & MESSAGING
// -------------------------------------------------------------
@Composable
fun ProjectChatTab(
    project: ProjectEntity,
    messages: List<MessageEntity>,
    currentUser: UserEntity?,
    onSendMessage: (content: String, attType: String, attName: String) -> Unit
) {
    var messageText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Message list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                val isMyMessage = msg.senderId == currentUser?.id

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMyMessage) 16.dp else 4.dp,
                            bottomEnd = if (isMyMessage) 4.dp else 16.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMyMessage) GoldPrimary else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(1.dp, if (isMyMessage) GoldPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth(0.82f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${msg.senderName} (${msg.senderRole})",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isMyMessage) Color.Black else GoldLight
                                )
                                val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                                Text(
                                    text = timeFormat.format(Date(msg.timestamp)),
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = if (isMyMessage) Color.Black.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = msg.content,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isMyMessage) Color.Black else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Message input bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Send message to studio team...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                maxLines = 3
            )

            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        onSendMessage(messageText, "NONE", "")
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary)
                    .testTag("send_message_button")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 3: DELIVERABLES
// -------------------------------------------------------------
@Composable
fun ProjectDeliverablesTab(
    project: ProjectEntity,
    deliverables: List<DeliverableEntity>,
    canManage: Boolean,
    isClient: Boolean,
    onUploadClick: () -> Unit,
    onApproveClick: (DeliverableEntity) -> Unit,
    onRequestRevisionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "PROJECT DELIVERABLES",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Text(
                    text = "Download masters, review work, or approve final deliveries.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (canManage) {
                Button(
                    onClick = onUploadClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("upload_deliverable_button")
                ) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Upload Work", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (deliverables.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = GoldLight, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No files delivered yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(deliverables) { del ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, if (del.isApproved) StatusSuccess else GoldPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = GoldPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = del.fileType,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                if (del.isApproved) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = StatusSuccess.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "APPROVED BY CLIENT",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = StatusSuccess,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = del.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = "${del.fileName} • ${del.fileSize} • Uploaded by ${del.uploadedBy}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (del.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Notes: ${del.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { /* simulated download */ },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Download")
                                }

                                if (isClient && !del.isApproved) {
                                    Button(
                                        onClick = { onApproveClick(del) },
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess, contentColor = Color.White),
                                        modifier = Modifier.weight(1.3f)
                                    ) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Approve Delivery", fontWeight = FontWeight.Bold)
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

// -------------------------------------------------------------
// SUB-TAB 4: REVISIONS
// -------------------------------------------------------------
@Composable
fun ProjectRevisionsTab(
    project: ProjectEntity,
    revisions: List<RevisionEntity>,
    canManage: Boolean,
    onRequestRevisionClick: () -> Unit,
    onResolveClick: (RevisionEntity, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "REVISION MANAGEMENT",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Text(
                    text = "Request specific design tweaks, typography edits or color updates.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onRequestRevisionClick,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("request_revision_button")
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Request Revision", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (revisions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No revisions requested for this project.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(revisions) { rev ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, if (rev.status == "COMPLETED") StatusSuccess else GoldPrimary.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (rev.status == "COMPLETED") StatusSuccess.copy(alpha = 0.2f) else GoldPrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = rev.status,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = if (rev.status == "COMPLETED") StatusSuccess else GoldLight,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                val df = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
                                Text(
                                    text = df.format(Date(rev.requestedAt)),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = rev.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (rev.responseNotes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Studio Response: ${rev.responseNotes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldLight
                                )
                            }

                            if (canManage && rev.status != "COMPLETED") {
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { onResolveClick(rev, "Changes applied in latest iteration.") },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                                ) {
                                    Text("Mark Revision Resolved")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SUB-TAB 5: PAYMENTS & RECEIPTS
// -------------------------------------------------------------
@Composable
fun ProjectPaymentsTab(
    project: ProjectEntity,
    payments: List<PaymentEntity>,
    isAdmin: Boolean,
    onRecordPaymentClick: () -> Unit
) {
    val agreedValuation = project.agreedPrice ?: project.budget
    val paidTotal = project.paidAmount
    val outstanding = (agreedValuation - paidTotal).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Valuation & Balance Card
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, GoldPrimary.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("PROJECT VALUATION", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${String.format(Locale.US, "%.2f", agreedValuation)}", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = GoldPrimary)
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (project.paymentStatus) {
                            "PAID" -> StatusSuccess.copy(alpha = 0.2f)
                            "PARTIALLY_PAID" -> GoldPrimary.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Text(
                            text = project.paymentStatus,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = when (project.paymentStatus) {
                                "PAID" -> StatusSuccess
                                "PARTIALLY_PAID" -> GoldLight
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("SETTLED AMOUNT", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${String.format(Locale.US, "%.2f", paidTotal)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = StatusSuccess)
                    }

                    Column {
                        Text("OUTSTANDING BALANCE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${String.format(Locale.US, "%.2f", outstanding)}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = if (outstanding > 0) GoldLight else StatusSuccess)
                    }
                }

                if (isAdmin) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onRecordPaymentClick,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Record Client Payment / Generate Receipt", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PAYMENT RECEIPTS & HISTORY",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = GoldLight
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (payments.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text("No payment receipts recorded for this project yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(payments) { p ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Receipt, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = p.receiptCode,
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = GoldLight
                                    )
                                }

                                Text(
                                    text = "$${String.format(Locale.US, "%.2f", p.amount)}",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = StatusSuccess
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Type: ${p.paymentType} • Method: ${p.paymentMethod} • Recorded by ${p.recordedBy}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            if (p.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Notes: ${p.notes}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// DIALOGS
// -------------------------------------------------------------
@Composable
fun CounterOfferDialog(
    currentPrice: Double,
    onDismiss: () -> Unit,
    onSubmit: (price: Double, note: String) -> Unit
) {
    var priceText by remember { mutableStateOf(currentPrice.toString()) }
    var note by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "PRICE COUNTEROFFER",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Proposed Price ($)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Explanation / Justification") },
                    placeholder = { Text("Explain your proposed budget or scope adjustments...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val price = priceText.toDoubleOrNull() ?: currentPrice
                            onSubmit(price, note)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("Send Counteroffer")
                    }
                }
            }
        }
    }
}

@Composable
fun UploadDeliverableDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, type: String, name: String, size: String, url: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var fileType by remember { mutableStateOf("IMAGE") }
    var fileName by remember { mutableStateOf("Master_Asset.png") }
    var fileSize by remember { mutableStateOf("24.5 MB") }
    var downloadUrl by remember { mutableStateOf("https://faustiarts.com/deliverables/sample") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "UPLOAD PROJECT DELIVERABLE",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Deliverable Title") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = fileName, onValueChange = { fileName = it }, label = { Text("File Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Director Notes / Instructions") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(title, fileType, fileName, fileSize, downloadUrl, notes) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("Upload Deliverable")
                    }
                }
            }
        }
    }
}

@Composable
fun RequestRevisionDialog(
    onDismiss: () -> Unit,
    onSubmit: (desc: String, refs: String) -> Unit
) {
    var desc by remember { mutableStateOf("") }
    var refs by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "REQUEST DESIGN REVISION",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Description of Changes Needed") },
                    placeholder = { Text("Specify changes in layout, colors, typography or composition...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = refs,
                    onValueChange = { refs = it },
                    label = { Text("Optional Reference Attachments / URLs") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSubmit(desc, refs) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("Submit Revision")
                    }
                }
            }
        }
    }
}

@Composable
fun RecordPaymentDialog(
    agreedPrice: Double,
    currentPaid: Double,
    onDismiss: () -> Unit,
    onSubmit: (amount: Double, type: String, method: String, notes: String) -> Unit
) {
    val remaining = (agreedPrice - currentPaid).coerceAtLeast(0.0)
    var amountText by remember { mutableStateOf(remaining.toString()) }
    var paymentType by remember { mutableStateOf("MILESTONE") }
    var paymentMethod by remember { mutableStateOf("BANK_TRANSFER") }
    var notes by remember { mutableStateOf("Client settled milestone payment.") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "RECORD CLIENT PAYMENT",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Payment Amount ($)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = paymentMethod,
                    onValueChange = { paymentMethod = it },
                    label = { Text("Payment Method (e.g., BANK_TRANSFER, CASH, MOBILE_MONEY)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Payment Notes / Reference") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: remaining
                            onSubmit(amt, paymentType, paymentMethod, notes)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("Record & Generate Receipt")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusChangeDialog(
    currentStatus: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    val statuses = listOf(
        "Request Received",
        "Under Review",
        "Price Negotiation",
        "Awaiting Approval",
        "Approved",
        "In Production",
        "Quality Review",
        "Revision Requested",
        "Completed",
        "Delivered"
    )
    var selected by remember { mutableStateOf(currentStatus) }
    var expanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ADVANCE PROJECT STATUS",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(14.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selected,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Stage") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        statuses.forEach { s ->
                            DropdownMenuItem(text = { Text(s) }, onClick = { selected = s; expanded = false })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onSave(selected) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black)
                    ) {
                        Text("Update Status")
                    }
                }
            }
        }
    }
}

@Composable
fun AssignManagerDialog(
    managers: List<UserEntity>,
    onDismiss: () -> Unit,
    onSelect: (UserEntity) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.5.dp, GoldPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ASSIGN ART DIRECTOR",
                    style = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold),
                    color = GoldPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (managers.isEmpty()) {
                    Text("No managers registered yet.", style = MaterialTheme.typography.bodyMedium)
                } else {
                    managers.forEach { mgr ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onSelect(mgr) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(mgr.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                                Spacer(modifier = Modifier.weight(1f))
                                Text("Assign", color = GoldLight, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Close") }
                }
            }
        }
    }
}
