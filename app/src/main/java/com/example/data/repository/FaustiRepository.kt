package com.example.data.repository

import com.example.data.local.dao.FaustiArtsDao
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import kotlin.random.Random

class FaustiRepository(private val dao: FaustiArtsDao) {

    // Current authenticated user session state
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    fun setCurrentUser(user: UserEntity?) {
        _currentUser.value = user
    }

    // --- Users & Authentication ---
    fun getAllUsers(): Flow<List<UserEntity>> = dao.getAllUsers()
    fun getUsersByRole(role: String): Flow<List<UserEntity>> = dao.getUsersByRole(role)
    fun getUserById(id: Long): Flow<UserEntity?> = dao.getUserById(id)

    suspend fun login(email: String, password: String):Result<UserEntity> {
        val user = dao.findUserByEmail(email.trim().lowercase(Locale.ROOT))
        return if (user != null && user.passwordHash == password) {
            _currentUser.value = user
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid email or password."))
        }
    }

    suspend fun register(name: String, email: String, password: String, role: String = "CLIENT", phone: String = ""): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        val existing = dao.findUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(Exception("An account with this email already exists."))
        }
        val newUser = UserEntity(
            name = name.trim(),
            email = cleanEmail,
            passwordHash = password,
            role = role,
            phone = phone.trim()
        )
        val id = dao.insertUser(newUser)
        val registeredUser = newUser.copy(id = id)
        _currentUser.value = registeredUser
        return Result.success(registeredUser)
    }

    suspend fun createManagerAccount(name: String, email: String, password: String, permissions: List<String>): Result<UserEntity> {
        val cleanEmail = email.trim().lowercase(Locale.ROOT)
        val existing = dao.findUserByEmail(cleanEmail)
        if (existing != null) {
            return Result.failure(Exception("An account with this email already exists."))
        }
        val permissionsJson = "[" + permissions.joinToString(",") { "\"$it\"" } + "]"
        val manager = UserEntity(
            name = name.trim(),
            email = cleanEmail,
            passwordHash = password,
            role = "MANAGER",
            permissionsJson = permissionsJson
        )
        val id = dao.insertUser(manager)
        return Result.success(manager.copy(id = id))
    }

    suspend fun updateProfile(user: UserEntity) {
        dao.updateUser(user)
        if (_currentUser.value?.id == user.id) {
            _currentUser.value = user
        }
    }

    // --- Services ---
    fun getActiveServices(): Flow<List<ServiceEntity>> = dao.getActiveServices()
    fun getAllServices(): Flow<List<ServiceEntity>> = dao.getAllServices()
    fun getServiceById(id: Long): Flow<ServiceEntity?> = dao.getServiceById(id)

    suspend fun insertService(service: ServiceEntity): Long = dao.insertService(service)
    suspend fun updateService(service: ServiceEntity) = dao.updateService(service)
    suspend fun deleteService(service: ServiceEntity) = dao.deleteService(service)

    // --- Projects ---
    fun getAllProjects(): Flow<List<ProjectEntity>> = dao.getAllProjects()
    fun getProjectsByClient(clientId: Long): Flow<List<ProjectEntity>> = dao.getProjectsByClient(clientId)
    fun getProjectsByManager(managerId: Long): Flow<List<ProjectEntity>> = dao.getProjectsByManager(managerId)
    fun getProjectById(id: Long): Flow<ProjectEntity?> = dao.getProjectById(id)

    suspend fun submitProjectRequest(
        title: String,
        serviceCategory: String,
        description: String,
        deadline: String,
        budget: Double,
        specialInstructions: String = "",
        referenceAttachments: String = ""
    ): ProjectEntity {
        val user = _currentUser.value ?: throw IllegalStateException("User not logged in")
        val randomDigits = Random.nextInt(1000, 9999)
        val projectCode = "FA-$randomDigits"

        val project = ProjectEntity(
            projectCode = projectCode,
            clientId = user.id,
            clientName = user.name,
            title = title.trim(),
            serviceCategory = serviceCategory,
            description = description.trim(),
            deadline = deadline,
            budget = budget,
            specialInstructions = specialInstructions.trim(),
            referenceAttachments = referenceAttachments.trim(),
            status = "Request Received"
        )
        val id = dao.insertProject(project)

        // Notify Admins
        dao.insertNotification(
            NotificationEntity(
                userId = 1, // Admin
                projectId = id,
                title = "New Project Request Received",
                message = "Client ${user.name} submitted request for '$title' ($projectCode).",
                type = "PROJECT"
            )
        )

        // Also notify client
        dao.insertNotification(
            NotificationEntity(
                userId = user.id,
                projectId = id,
                title = "Project Request Received",
                message = "Your request for '$title' ($projectCode) has been submitted to Fausti Arts Studio.",
                type = "PROJECT"
            )
        )

        return project.copy(id = id)
    }

    suspend fun updateProject(project: ProjectEntity) {
        dao.updateProject(project.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun updateProjectStatus(projectId: Long, newStatus: String) {
        val project = dao.findProjectById(projectId) ?: return
        val updated = project.copy(status = newStatus, updatedAt = System.currentTimeMillis())
        dao.updateProject(updated)

        // Create status change notification for client
        dao.insertNotification(
            NotificationEntity(
                userId = project.clientId,
                projectId = projectId,
                title = "Project Status Updated",
                message = "Your project '${project.title}' is now in: $newStatus",
                type = "PROJECT"
            )
        )
    }

    suspend fun assignManagerToProject(projectId: Long, manager: UserEntity) {
        val project = dao.findProjectById(projectId) ?: return
        val updated = project.copy(
            managerId = manager.id,
            managerName = manager.name,
            updatedAt = System.currentTimeMillis()
        )
        dao.updateProject(updated)

        // Notify manager
        dao.insertNotification(
            NotificationEntity(
                userId = manager.id,
                projectId = projectId,
                title = "New Project Assigned",
                message = "You have been assigned as lead manager for '${project.title}'.",
                type = "PROJECT"
            )
        )
    }

    // --- Quotations & Negotiations ---
    fun getNegotiationsForProject(projectId: Long): Flow<List<QuotationNegotiationEntity>> =
        dao.getNegotiationsForProject(projectId)

    suspend fun sendCounterOffer(
        projectId: Long,
        proposedPrice: Double,
        note: String
    ) {
        val user = _currentUser.value ?: throw IllegalStateException("Not logged in")
        val project = dao.findProjectById(projectId) ?: return

        val role = user.role
        val negotiation = QuotationNegotiationEntity(
            projectId = projectId,
            senderRole = role,
            senderName = user.name,
            proposedPrice = proposedPrice,
            note = note.trim(),
            status = "PENDING"
        )
        dao.insertNegotiation(negotiation)

        // Update project current quotation & status if in negotiation
        val updated = project.copy(
            currentQuotation = proposedPrice,
            status = "Price Negotiation",
            updatedAt = System.currentTimeMillis()
        )
        dao.updateProject(updated)

        // Send notifications
        val targetUserId = if (role == "CLIENT") 1L else project.clientId
        dao.insertNotification(
            NotificationEntity(
                userId = targetUserId,
                projectId = projectId,
                title = "New Price Counteroffer",
                message = "${user.name} proposed $${String.format(Locale.US, "%.2f", proposedPrice)} for '${project.title}'.",
                type = "QUOTATION"
            )
        )
    }

    suspend fun respondToQuotation(
        projectId: Long,
        accept: Boolean,
        negotiationId: Long,
        agreedPrice: Double
    ) {
        val user = _currentUser.value ?: throw IllegalStateException("Not logged in")
        val project = dao.findProjectById(projectId) ?: return

        if (accept) {
            val updated = project.copy(
                agreedPrice = agreedPrice,
                currentQuotation = agreedPrice,
                status = "Approved",
                updatedAt = System.currentTimeMillis()
            )
            dao.updateProject(updated)

            dao.insertNotification(
                NotificationEntity(
                    userId = if (user.role == "CLIENT") 1L else project.clientId,
                    projectId = projectId,
                    title = "Quotation Accepted!",
                    message = "Quotation of $${String.format(Locale.US, "%.2f", agreedPrice)} has been accepted for '${project.title}'.",
                    type = "QUOTATION"
                )
            )
        } else {
            val updated = project.copy(
                status = "Price Negotiation",
                updatedAt = System.currentTimeMillis()
            )
            dao.updateProject(updated)
        }
    }

    // --- Messages ---
    fun getMessagesForProject(projectId: Long): Flow<List<MessageEntity>> =
        dao.getMessagesForProject(projectId)

    fun getAllMessages(): Flow<List<MessageEntity>> = dao.getAllMessages()

    suspend fun sendMessage(
        projectId: Long,
        content: String,
        attachmentType: String = "NONE",
        attachmentName: String = ""
    ) {
        val user = _currentUser.value ?: throw IllegalStateException("Not logged in")
        val project = dao.findProjectById(projectId) ?: return

        val message = MessageEntity(
            projectId = projectId,
            senderId = user.id,
            senderName = user.name,
            senderRole = user.role,
            content = content.trim(),
            attachmentType = attachmentType,
            attachmentName = attachmentName
        )
        dao.insertMessage(message)

        // Notify counter-party
        val recipientId = if (user.role == "CLIENT") (project.managerId ?: 1L) else project.clientId
        dao.insertNotification(
            NotificationEntity(
                userId = recipientId,
                projectId = projectId,
                title = "New Message from ${user.name}",
                message = "Project '${project.title}': ${content.take(60)}",
                type = "MESSAGE"
            )
        )
    }

    suspend fun markMessagesAsRead(projectId: Long) {
        val userRole = _currentUser.value?.role ?: return
        dao.markMessagesAsRead(projectId, userRole)
    }

    // --- Deliverables ---
    fun getDeliverablesForProject(projectId: Long): Flow<List<DeliverableEntity>> =
        dao.getDeliverablesForProject(projectId)

    suspend fun uploadDeliverable(
        projectId: Long,
        title: String,
        fileType: String,
        fileName: String,
        fileSize: String,
        downloadUrl: String,
        notes: String
    ) {
        val user = _currentUser.value ?: throw IllegalStateException("Not logged in")
        val project = dao.findProjectById(projectId) ?: return

        val deliverable = DeliverableEntity(
            projectId = projectId,
            title = title.trim(),
            fileType = fileType,
            fileName = fileName,
            fileSize = fileSize,
            downloadUrl = downloadUrl,
            notes = notes.trim(),
            uploadedBy = user.name
        )
        dao.insertDeliverable(deliverable)

        // Advance project status to Quality Review or Delivered
        dao.updateProject(project.copy(status = "Delivered", updatedAt = System.currentTimeMillis()))

        dao.insertNotification(
            NotificationEntity(
                userId = project.clientId,
                projectId = projectId,
                title = "Deliverables Uploaded",
                message = "New deliverables have been uploaded for '${project.title}'. You can now preview and approve.",
                type = "DELIVERABLE"
            )
        )
    }

    suspend fun approveDeliverable(deliverable: DeliverableEntity) {
        dao.updateDeliverable(deliverable.copy(isApproved = true))
        val project = dao.findProjectById(deliverable.projectId) ?: return
        dao.updateProject(project.copy(status = "Completed", updatedAt = System.currentTimeMillis()))

        dao.insertNotification(
            NotificationEntity(
                userId = project.managerId ?: 1L,
                projectId = project.id,
                title = "Delivery Approved by Client",
                message = "Client approved delivery '${deliverable.title}'. Project marked as Completed.",
                type = "DELIVERABLE"
            )
        )
    }

    // --- Revisions ---
    fun getRevisionsForProject(projectId: Long): Flow<List<RevisionEntity>> =
        dao.getRevisionsForProject(projectId)

    suspend fun requestRevision(projectId: Long, description: String, referenceAttachments: String = "") {
        val project = dao.findProjectById(projectId) ?: return

        val revision = RevisionEntity(
            projectId = projectId,
            description = description.trim(),
            referenceAttachments = referenceAttachments.trim(),
            status = "PENDING"
        )
        dao.insertRevision(revision)

        dao.updateProject(project.copy(status = "Revision Requested", updatedAt = System.currentTimeMillis()))

        dao.insertNotification(
            NotificationEntity(
                userId = project.managerId ?: 1L,
                projectId = projectId,
                title = "Revision Requested",
                message = "Client requested changes for '${project.title}'.",
                type = "REVISION"
            )
        )
    }

    suspend fun resolveRevision(revision: RevisionEntity, responseNotes: String) {
        dao.updateRevision(
            revision.copy(
                status = "COMPLETED",
                responseNotes = responseNotes.trim(),
                resolvedAt = System.currentTimeMillis()
            )
        )
        val project = dao.findProjectById(revision.projectId) ?: return
        dao.updateProject(project.copy(status = "In Production", updatedAt = System.currentTimeMillis()))
    }

    // --- Payments ---
    fun getPaymentsForProject(projectId: Long): Flow<List<PaymentEntity>> =
        dao.getPaymentsForProject(projectId)

    fun getAllPayments(): Flow<List<PaymentEntity>> = dao.getAllPayments()

    suspend fun recordPayment(
        projectId: Long,
        amount: Double,
        paymentType: String,
        paymentMethod: String,
        notes: String
    ): PaymentEntity {
        val user = _currentUser.value ?: throw IllegalStateException("Not logged in")
        val project = dao.findProjectById(projectId) ?: throw IllegalStateException("Project not found")

        val randomCode = Random.nextInt(1000, 9999)
        val receiptCode = "FA-REC-$randomCode"

        val payment = PaymentEntity(
            projectId = projectId,
            receiptCode = receiptCode,
            amount = amount,
            paymentType = paymentType,
            paymentMethod = paymentMethod,
            notes = notes.trim(),
            recordedBy = user.name
        )
        val id = dao.insertPayment(payment)

        val newPaidTotal = project.paidAmount + amount
        val agreedOrBudget = project.agreedPrice ?: project.budget
        val newPaymentStatus = when {
            newPaidTotal >= agreedOrBudget && agreedOrBudget > 0 -> "PAID"
            newPaidTotal > 0 -> "PARTIALLY_PAID"
            else -> "UNPAID"
        }

        dao.updateProject(
            project.copy(
                paidAmount = newPaidTotal,
                paymentStatus = newPaymentStatus,
                updatedAt = System.currentTimeMillis()
            )
        )

        dao.insertNotification(
            NotificationEntity(
                userId = project.clientId,
                projectId = projectId,
                title = "Payment Recorded & Receipt Generated",
                message = "Payment of $${String.format(Locale.US, "%.2f", amount)} ($receiptCode) has been recorded.",
                type = "PAYMENT"
            )
        )

        return payment.copy(id = id)
    }

    // --- Notifications ---
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>> =
        dao.getNotificationsForUser(userId)

    suspend fun markNotificationAsRead(id: Long) = dao.markNotificationAsRead(id)
    suspend fun markAllNotificationsAsRead(userId: Long) = dao.markAllNotificationsAsRead(userId)

    // --- Portfolio, Announcements, Testimonials ---
    fun getAllPortfolio(): Flow<List<PortfolioEntity>> = dao.getAllPortfolio()
    fun getFeaturedPortfolio(): Flow<List<PortfolioEntity>> = dao.getFeaturedPortfolio()
    suspend fun insertPortfolio(portfolio: PortfolioEntity) = dao.insertPortfolio(portfolio)
    suspend fun updatePortfolio(portfolio: PortfolioEntity) = dao.updatePortfolio(portfolio)
    suspend fun deletePortfolio(portfolio: PortfolioEntity) = dao.deletePortfolio(portfolio)

    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>> = dao.getAllAnnouncements()
    suspend fun insertAnnouncement(announcement: AnnouncementEntity) = dao.insertAnnouncement(announcement)
    suspend fun deleteAnnouncement(announcement: AnnouncementEntity) = dao.deleteAnnouncement(announcement)

    fun getAllTestimonials(): Flow<List<TestimonialEntity>> = dao.getAllTestimonials()
    suspend fun insertTestimonial(testimonial: TestimonialEntity) = dao.insertTestimonial(testimonial)
}
