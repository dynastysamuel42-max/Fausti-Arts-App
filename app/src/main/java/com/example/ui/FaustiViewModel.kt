package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.ai.CreativeAssistantService
import com.example.data.local.entity.AnnouncementEntity
import com.example.data.local.entity.DeliverableEntity
import com.example.data.local.entity.MessageEntity
import com.example.data.local.entity.NotificationEntity
import com.example.data.local.entity.PaymentEntity
import com.example.data.local.entity.PortfolioEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.QuotationNegotiationEntity
import com.example.data.local.entity.RevisionEntity
import com.example.data.local.entity.ServiceEntity
import com.example.data.local.entity.TestimonialEntity
import com.example.data.local.entity.UserEntity
import com.example.data.repository.FaustiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface AiAssistantUiState {
    data object Idle : AiAssistantUiState
    data object Loading : AiAssistantUiState
    data class Success(val responseText: String) : AiAssistantUiState
    data class Error(val message: String) : AiAssistantUiState
}

class FaustiViewModel(
    private val repository: FaustiRepository,
    private val aiService: CreativeAssistantService = CreativeAssistantService()
) : ViewModel() {

    val currentUser: StateFlow<UserEntity?> = repository.currentUser

    // All data flows
    val allServices: StateFlow<List<ServiceEntity>> = repository.getAllServices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProjects: StateFlow<List<ProjectEntity>> = repository.getAllProjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allUsers: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allManagers: StateFlow<List<UserEntity>> = repository.getUsersByRole("MANAGER")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPortfolio: StateFlow<List<PortfolioEntity>> = repository.getAllPortfolio()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAnnouncements: StateFlow<List<AnnouncementEntity>> = repository.getAllAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTestimonials: StateFlow<List<TestimonialEntity>> = repository.getAllTestimonials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentEntity>> = repository.getAllPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Notifications for active user
    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    // Selected project detail
    private val _selectedProjectId = MutableStateFlow<Long?>(null)
    val selectedProjectId: StateFlow<Long?> = _selectedProjectId.asStateFlow()

    val selectedProject: StateFlow<ProjectEntity?> = combine(allProjects, selectedProjectId) { projects, id ->
        projects.find { it.id == id }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Project sub-flows
    private val _selectedProjectNegotiations = MutableStateFlow<List<QuotationNegotiationEntity>>(emptyList())
    val selectedProjectNegotiations: StateFlow<List<QuotationNegotiationEntity>> = _selectedProjectNegotiations.asStateFlow()

    private val _selectedProjectMessages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val selectedProjectMessages: StateFlow<List<MessageEntity>> = _selectedProjectMessages.asStateFlow()

    private val _selectedProjectDeliverables = MutableStateFlow<List<DeliverableEntity>>(emptyList())
    val selectedProjectDeliverables: StateFlow<List<DeliverableEntity>> = _selectedProjectDeliverables.asStateFlow()

    private val _selectedProjectRevisions = MutableStateFlow<List<RevisionEntity>>(emptyList())
    val selectedProjectRevisions: StateFlow<List<RevisionEntity>> = _selectedProjectRevisions.asStateFlow()

    private val _selectedProjectPayments = MutableStateFlow<List<PaymentEntity>>(emptyList())
    val selectedProjectPayments: StateFlow<List<PaymentEntity>> = _selectedProjectPayments.asStateFlow()

    // AI Assistant State
    private val _aiAssistantState = MutableStateFlow<AiAssistantUiState>(AiAssistantUiState.Idle)
    val aiAssistantState: StateFlow<AiAssistantUiState> = _aiAssistantState.asStateFlow()

    // Status snackbar / toast
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    init {
        // Collect notifications whenever current user changes
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    repository.getNotificationsForUser(user.id).collect { notifs ->
                        _notifications.value = notifs
                    }
                } else {
                    _notifications.value = emptyList()
                }
            }
        }
    }

    fun selectProject(projectId: Long?) {
        _selectedProjectId.value = projectId
        if (projectId != null) {
            viewModelScope.launch {
                repository.getNegotiationsForProject(projectId).collect {
                    _selectedProjectNegotiations.value = it
                }
            }
            viewModelScope.launch {
                repository.getMessagesForProject(projectId).collect {
                    _selectedProjectMessages.value = it
                }
            }
            viewModelScope.launch {
                repository.getDeliverablesForProject(projectId).collect {
                    _selectedProjectDeliverables.value = it
                }
            }
            viewModelScope.launch {
                repository.getRevisionsForProject(projectId).collect {
                    _selectedProjectRevisions.value = it
                }
            }
            viewModelScope.launch {
                repository.getPaymentsForProject(projectId).collect {
                    _selectedProjectPayments.value = it
                }
            }
            // Mark messages as read for this project
            viewModelScope.launch {
                repository.markMessagesAsRead(projectId)
            }
        }
    }

    // --- Authentication & User Operations ---
    fun login(email: String, pass: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val res = repository.login(email, pass)
            if (res.isSuccess) {
                showMessage("Welcome back, ${res.getOrNull()?.name}!")
                onSuccess()
            } else {
                showMessage(res.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun register(name: String, email: String, pass: String, role: String = "CLIENT", phone: String = "", onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val res = repository.register(name, email, pass, role, phone)
            if (res.isSuccess) {
                showMessage("Account created! Welcome to Fausti Arts.")
                onSuccess()
            } else {
                showMessage(res.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun switchUser(user: UserEntity) {
        repository.setCurrentUser(user)
        showMessage("Switched to ${user.name} (${user.role})")
    }

    fun logout() {
        repository.setCurrentUser(null)
        showMessage("Logged out successfully.")
    }

    fun updateProfile(name: String, phone: String, bio: String) {
        val current = currentUser.value ?: return
        viewModelScope.launch {
            val updated = current.copy(name = name.trim(), phone = phone.trim(), bio = bio.trim())
            repository.updateProfile(updated)
            showMessage("Profile updated.")
        }
    }

    fun changePassword(oldPass: String, newPass: String) {
        val current = currentUser.value ?: return
        if (current.passwordHash != oldPass) {
            showMessage("Current password does not match.")
            return
        }
        viewModelScope.launch {
            val updated = current.copy(passwordHash = newPass)
            repository.updateProfile(updated)
            showMessage("Password updated successfully.")
        }
    }

    fun createManager(name: String, email: String, pass: String, permissions: List<String>) {
        viewModelScope.launch {
            val res = repository.createManagerAccount(name, email, pass, permissions)
            if (res.isSuccess) {
                showMessage("Manager account for ${name} created successfully.")
            } else {
                showMessage(res.exceptionOrNull()?.message ?: "Failed to create manager.")
            }
        }
    }

    // --- Project Operations ---
    fun submitProjectRequest(
        title: String,
        serviceCategory: String,
        description: String,
        deadline: String,
        budget: Double,
        specialInstructions: String = "",
        referenceAttachments: String = "",
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val project = repository.submitProjectRequest(
                    title = title,
                    serviceCategory = serviceCategory,
                    description = description,
                    deadline = deadline,
                    budget = budget,
                    specialInstructions = specialInstructions,
                    referenceAttachments = referenceAttachments
                )
                showMessage("Project '${project.title}' (${project.projectCode}) submitted!")
                onSuccess(project.id)
            } catch (e: Exception) {
                showMessage("Error submitting request: ${e.message}")
            }
        }
    }

    fun updateProjectStatus(projectId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateProjectStatus(projectId, newStatus)
            showMessage("Status updated to: $newStatus")
        }
    }

    fun assignManager(projectId: Long, manager: UserEntity) {
        viewModelScope.launch {
            repository.assignManagerToProject(projectId, manager)
            showMessage("Assigned to ${manager.name}")
        }
    }

    // --- Negotiations ---
    fun sendCounterOffer(projectId: Long, price: Double, note: String) {
        viewModelScope.launch {
            repository.sendCounterOffer(projectId, price, note)
            showMessage("Counteroffer sent: $$price")
        }
    }

    fun respondToQuotation(projectId: Long, accept: Boolean, negotiationId: Long, price: Double) {
        viewModelScope.launch {
            repository.respondToQuotation(projectId, accept, negotiationId, price)
            showMessage(if (accept) "Quotation accepted!" else "Quotation rejected.")
        }
    }

    // --- Chat & Messages ---
    fun sendMessage(projectId: Long, content: String, attachmentType: String = "NONE", attachmentName: String = "") {
        viewModelScope.launch {
            repository.sendMessage(projectId, content, attachmentType, attachmentName)
        }
    }

    // --- Deliverables ---
    fun uploadDeliverable(
        projectId: Long,
        title: String,
        fileType: String,
        fileName: String,
        fileSize: String,
        downloadUrl: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.uploadDeliverable(projectId, title, fileType, fileName, fileSize, downloadUrl, notes)
            showMessage("Deliverable '$title' uploaded.")
        }
    }

    fun approveDeliverable(deliverable: DeliverableEntity) {
        viewModelScope.launch {
            repository.approveDeliverable(deliverable)
            showMessage("Delivery approved! Project completed.")
        }
    }

    // --- Revisions ---
    fun requestRevision(projectId: Long, description: String, referenceAttachments: String = "") {
        viewModelScope.launch {
            repository.requestRevision(projectId, description, referenceAttachments)
            showMessage("Revision request sent to studio.")
        }
    }

    fun resolveRevision(revision: RevisionEntity, responseNotes: String) {
        viewModelScope.launch {
            repository.resolveRevision(revision, responseNotes)
            showMessage("Revision resolved and updated to In Production.")
        }
    }

    // --- Payments ---
    fun recordPayment(projectId: Long, amount: Double, type: String, method: String, notes: String) {
        viewModelScope.launch {
            try {
                val payment = repository.recordPayment(projectId, amount, type, method, notes)
                showMessage("Payment of $$amount recorded (Receipt: ${payment.receiptCode})")
            } catch (e: Exception) {
                showMessage("Payment error: ${e.message}")
            }
        }
    }

    // --- Notifications ---
    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsRead() {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllNotificationsAsRead(user.id)
            showMessage("All notifications marked as read.")
        }
    }

    // --- Services Admin ---
    fun saveService(id: Long, name: String, category: String, desc: String, price: Double, days: Int) {
        viewModelScope.launch {
            if (id == 0L) {
                repository.insertService(
                    ServiceEntity(name = name, category = category, description = desc, startingPrice = price, estimatedDays = days)
                )
                showMessage("Service '$name' added.")
            } else {
                repository.updateService(
                    ServiceEntity(id = id, name = name, category = category, description = desc, startingPrice = price, estimatedDays = days)
                )
                showMessage("Service updated.")
            }
        }
    }

    fun deleteService(service: ServiceEntity) {
        viewModelScope.launch {
            repository.deleteService(service)
            showMessage("Service deleted.")
        }
    }

    // --- Portfolio Admin ---
    fun savePortfolio(title: String, category: String, desc: String, client: String, date: String, featured: Boolean) {
        viewModelScope.launch {
            repository.insertPortfolio(
                PortfolioEntity(
                    title = title,
                    category = category,
                    description = desc,
                    clientName = client,
                    completionDate = date,
                    featured = featured
                )
            )
            showMessage("Portfolio item added.")
        }
    }

    // --- Announcements Admin ---
    fun postAnnouncement(title: String, content: String, author: String, priority: String) {
        viewModelScope.launch {
            repository.insertAnnouncement(
                AnnouncementEntity(title = title, content = content, date = "November 2026", author = author, priority = priority)
            )
            showMessage("Announcement published.")
        }
    }

    // --- AI Assistant Logic ---
    fun generateAiVisionBrief(
        serviceCategory: String,
        projectTitle: String,
        initialIdea: String,
        targetAudience: String,
        preferredVibe: String
    ) {
        viewModelScope.launch {
            _aiAssistantState.value = AiAssistantUiState.Loading
            val result = aiService.articulateVision(
                serviceCategory = serviceCategory,
                projectTitle = projectTitle,
                initialIdea = initialIdea,
                targetAudience = targetAudience,
                preferredVibe = preferredVibe
            )
            if (result.isSuccess) {
                _aiAssistantState.value = AiAssistantUiState.Success(result.getOrNull().orEmpty())
            } else {
                val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "AI Assistant unavailable"
                // Generate studio template as helpful fallback
                val fallback = aiService.generateOfflineTemplate(
                    category = serviceCategory,
                    title = projectTitle,
                    initialIdea = initialIdea,
                    selectedKeywords = listOf(preferredVibe).filter { it.isNotBlank() }
                )
                _aiAssistantState.value = AiAssistantUiState.Success(fallback)
                showMessage("AI suggestion generated using studio creative brief engine.")
            }
        }
    }

    fun getCategoryGuidance(category: String) = aiService.getCuratedCategoryGuidance(category)

    fun resetAiState() {
        _aiAssistantState.value = AiAssistantUiState.Idle
    }
}

class FaustiViewModelFactory(private val repository: FaustiRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FaustiViewModel::class.java)) {
            return FaustiViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
