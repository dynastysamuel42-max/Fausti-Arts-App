package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

@Dao
interface FaustiArtsDao {

    // --- Users ---
    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    fun getUserByEmail(email: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = :role ORDER BY name ASC")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    // --- Services ---
    @Query("SELECT * FROM services WHERE isActive = 1 ORDER BY id ASC")
    fun getActiveServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services ORDER BY id ASC")
    fun getAllServices(): Flow<List<ServiceEntity>>

    @Query("SELECT * FROM services WHERE id = :id LIMIT 1")
    fun getServiceById(id: Long): Flow<ServiceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServices(services: List<ServiceEntity>)

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Delete
    suspend fun deleteService(service: ServiceEntity)

    // --- Projects ---
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE clientId = :clientId ORDER BY updatedAt DESC")
    fun getProjectsByClient(clientId: Long): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE managerId = :managerId ORDER BY updatedAt DESC")
    fun getProjectsByManager(managerId: Long): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    fun getProjectById(id: Long): Flow<ProjectEntity?>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun findProjectById(id: Long): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    // --- Quotations & Negotiations ---
    @Query("SELECT * FROM negotiations WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getNegotiationsForProject(projectId: Long): Flow<List<QuotationNegotiationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNegotiation(negotiation: QuotationNegotiationEntity): Long

    @Update
    suspend fun updateNegotiation(negotiation: QuotationNegotiationEntity)

    // --- Messages ---
    @Query("SELECT * FROM messages WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getMessagesForProject(projectId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("UPDATE messages SET isRead = 1 WHERE projectId = :projectId AND senderRole != :userRole")
    suspend fun markMessagesAsRead(projectId: Long, userRole: String)

    // --- Deliverables ---
    @Query("SELECT * FROM deliverables WHERE projectId = :projectId ORDER BY uploadedAt DESC")
    fun getDeliverablesForProject(projectId: Long): Flow<List<DeliverableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeliverable(deliverable: DeliverableEntity): Long

    @Update
    suspend fun updateDeliverable(deliverable: DeliverableEntity)

    // --- Revisions ---
    @Query("SELECT * FROM revisions WHERE projectId = :projectId ORDER BY requestedAt DESC")
    fun getRevisionsForProject(projectId: Long): Flow<List<RevisionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevision(revision: RevisionEntity): Long

    @Update
    suspend fun updateRevision(revision: RevisionEntity)

    // --- Payments ---
    @Query("SELECT * FROM payments WHERE projectId = :projectId ORDER BY recordedAt DESC")
    fun getPaymentsForProject(projectId: Long): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments ORDER BY recordedAt DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity): Long

    // --- Notifications ---
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllNotificationsAsRead(userId: Long)

    // --- Portfolio ---
    @Query("SELECT * FROM portfolio ORDER BY id DESC")
    fun getAllPortfolio(): Flow<List<PortfolioEntity>>

    @Query("SELECT * FROM portfolio WHERE featured = 1 ORDER BY id DESC")
    fun getFeaturedPortfolio(): Flow<List<PortfolioEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolio(portfolio: PortfolioEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPortfolioList(portfolioList: List<PortfolioEntity>)

    @Update
    suspend fun updatePortfolio(portfolio: PortfolioEntity)

    @Delete
    suspend fun deletePortfolio(portfolio: PortfolioEntity)

    // --- Announcements ---
    @Query("SELECT * FROM announcements ORDER BY id DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<AnnouncementEntity>)

    @Delete
    suspend fun deleteAnnouncement(announcement: AnnouncementEntity)

    // --- Testimonials ---
    @Query("SELECT * FROM testimonials ORDER BY id DESC")
    fun getAllTestimonials(): Flow<List<TestimonialEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestimonial(testimonial: TestimonialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestimonials(testimonials: List<TestimonialEntity>)

    @Delete
    suspend fun deleteTestimonial(testimonial: TestimonialEntity)
}
