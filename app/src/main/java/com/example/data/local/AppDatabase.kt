package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        ServiceEntity::class,
        ProjectEntity::class,
        QuotationNegotiationEntity::class,
        MessageEntity::class,
        DeliverableEntity::class,
        RevisionEntity::class,
        PaymentEntity::class,
        NotificationEntity::class,
        PortfolioEntity::class,
        AnnouncementEntity::class,
        TestimonialEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun faustiArtsDao(): FaustiArtsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fausti_arts_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.faustiArtsDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: FaustiArtsDao) {
            // Seed Users
            val adminId = dao.insertUser(
                UserEntity(
                    id = 1,
                    name = "Fausti Senior Director",
                    email = "admin@faustiarts.com",
                    passwordHash = "admin123",
                    role = "ADMIN",
                    phone = "+1 (555) 019-2831",
                    bio = "Founder & Executive Creative Director at FAUSTI ARTS Studio."
                )
            )

            val managerId = dao.insertUser(
                UserEntity(
                    id = 2,
                    name = "Elena Rostova",
                    email = "elena@faustiarts.com",
                    passwordHash = "manager123",
                    role = "MANAGER",
                    phone = "+1 (555) 014-9922",
                    bio = "Senior Project Director & Lead Brand Strategist.",
                    permissionsJson = "[\"MANAGE_PROJECTS\", \"COMMUNICATE\", \"UPLOAD_DELIVERABLES\", \"UPDATE_PROGRESS\"]"
                )
            )

            val clientId = dao.insertUser(
                UserEntity(
                    id = 3,
                    name = "Dynasty Samuel",
                    email = "dynasty@gmail.com",
                    passwordHash = "client123",
                    role = "CLIENT",
                    phone = "+1 (555) 018-7711",
                    bio = "Entrepreneur & Creative Founder."
                )
            )

            // Seed 11 Services
            val services = listOf(
                ServiceEntity(
                    name = "Graphic Design",
                    category = "Brand Identity & Collateral",
                    description = "High-impact visual communication for modern brands, marketing campaigns, and packaging.",
                    startingPrice = 250.0,
                    estimatedDays = 4,
                    sampleImageName = "graphic_design"
                ),
                ServiceEntity(
                    name = "Logo Design and Branding",
                    category = "Visual Identity",
                    description = "Distinctive bespoke logos, full brand guidelines, typography systems, and luxury corporate iconography.",
                    startingPrice = 450.0,
                    estimatedDays = 5,
                    sampleImageName = "branding"
                ),
                ServiceEntity(
                    name = "Posters and Flyers",
                    category = "Print & Outdoor",
                    description = "Arresting artistic posters, event promotion flyers, and architectural scale prints that capture attention.",
                    startingPrice = 180.0,
                    estimatedDays = 3,
                    sampleImageName = "posters"
                ),
                ServiceEntity(
                    name = "Social Media Designs",
                    category = "Digital Marketing",
                    description = "Curated multi-platform content suites, Instagram carousel packs, banners, and video thumbnail templates.",
                    startingPrice = 200.0,
                    estimatedDays = 3,
                    sampleImageName = "social"
                ),
                ServiceEntity(
                    name = "Digital Artwork",
                    category = "Fine Digital Art",
                    description = "Custom illustrative art pieces, concept art, rich stylized digital paintings, and album cover masterpieces.",
                    startingPrice = 350.0,
                    estimatedDays = 7,
                    sampleImageName = "digital_art"
                ),
                ServiceEntity(
                    name = "Photo Editing",
                    category = "Post-Production",
                    description = "High-end editorial retouching, skin grading, color grading, background compositing, and commercial rendering.",
                    startingPrice = 120.0,
                    estimatedDays = 2,
                    sampleImageName = "photo_edit"
                ),
                ServiceEntity(
                    name = "Video Editing",
                    category = "Motion & Cinematic",
                    description = "Cinematic brand reels, YouTube / TikTok pacing, color grading, dynamic sound design, and promotional trailers.",
                    startingPrice = 400.0,
                    estimatedDays = 6,
                    sampleImageName = "video_edit"
                ),
                ServiceEntity(
                    name = "Animation",
                    category = "Motion Graphics",
                    description = "2D & 2.5D explainer animations, dynamic logo reveals, UI micro-interactions, and kinetic typography.",
                    startingPrice = 500.0,
                    estimatedDays = 8,
                    sampleImageName = "animation"
                ),
                ServiceEntity(
                    name = "Character Design",
                    category = "Illustration & IP",
                    description = "Memorable mascots, narrative character turnarounds, expression sheets, and stylized brand ambassadors.",
                    startingPrice = 380.0,
                    estimatedDays = 6,
                    sampleImageName = "character"
                ),
                ServiceEntity(
                    name = "Website Design",
                    category = "UI/UX & Web",
                    description = "World-class digital agency web experiences, interactive landing pages, mobile-first design systems, and Figma prototypes.",
                    startingPrice = 850.0,
                    estimatedDays = 12,
                    sampleImageName = "website"
                ),
                ServiceEntity(
                    name = "Custom Creative Projects",
                    category = "Bespoke Production",
                    description = "Unconventional cross-medium artistic commissions, immersive exhibition graphics, and experimental brand activations.",
                    startingPrice = 600.0,
                    estimatedDays = 10,
                    sampleImageName = "custom"
                )
            )
            dao.insertServices(services)

            // Seed Sample Projects
            val project1Id = dao.insertProject(
                ProjectEntity(
                    projectCode = "FA-2401",
                    clientId = clientId,
                    clientName = "Dynasty Samuel",
                    managerId = managerId,
                    managerName = "Elena Rostova",
                    title = "Aura Luxury Fashion Branding",
                    serviceCategory = "Logo Design and Branding",
                    description = "Complete luxury visual identity system including primary logo, monogram emblem, gold foil packaging mockups, and typography guidelines.",
                    referenceAttachments = "moodboard_aura.pdf, color_palette.png",
                    deadline = "2026-11-25",
                    budget = 750.0,
                    agreedPrice = 700.0,
                    specialInstructions = "Focus on minimalist gold line art with premium matte obsidian black packaging.",
                    status = "In Production",
                    currentQuotation = 700.0,
                    paidAmount = 350.0,
                    paymentStatus = "PARTIALLY_PAID"
                )
            )

            val project2Id = dao.insertProject(
                ProjectEntity(
                    projectCode = "FA-2402",
                    clientId = clientId,
                    clientName = "Dynasty Samuel",
                    managerId = managerId,
                    managerName = "Elena Rostova",
                    title = "Luminary Cyberpunk Album Cover",
                    serviceCategory = "Digital Artwork",
                    description = "Rich digital painting artwork for music streaming platforms and 12-inch vinyl sleeve.",
                    referenceAttachments = "track_demo.mp3, sketch_concept.jpg",
                    deadline = "2026-11-18",
                    budget = 500.0,
                    agreedPrice = 550.0,
                    specialInstructions = "Atmospheric neon purples, glowing cyan reflections, and dark futuristic architecture.",
                    status = "Delivered",
                    currentQuotation = 550.0,
                    paidAmount = 550.0,
                    paymentStatus = "PAID"
                )
            )

            val project3Id = dao.insertProject(
                ProjectEntity(
                    projectCode = "FA-2403",
                    clientId = clientId,
                    clientName = "Dynasty Samuel",
                    managerId = null,
                    managerName = null,
                    title = "Fausti Interactive Agency Portal",
                    serviceCategory = "Website Design",
                    description = "High-fidelity modern website design prototype with dark mode, interactive creative project showcase, and client portal.",
                    deadline = "2026-12-10",
                    budget = 1200.0,
                    agreedPrice = null,
                    specialInstructions = "Require responsive tablet and desktop Figma source files.",
                    status = "Price Negotiation",
                    currentQuotation = 1350.0,
                    paidAmount = 0.0,
                    paymentStatus = "UNPAID"
                )
            )

            // Seed Negotiations for Project 3
            dao.insertNegotiation(
                QuotationNegotiationEntity(
                    projectId = project3Id,
                    senderRole = "ADMIN",
                    senderName = "Fausti Studio Director",
                    proposedPrice = 1450.0,
                    note = "Initial studio estimate based on responsive Figma design system and 12 custom page templates.",
                    status = "COUNTERED"
                )
            )
            dao.insertNegotiation(
                QuotationNegotiationEntity(
                    projectId = project3Id,
                    senderRole = "CLIENT",
                    senderName = "Dynasty Samuel",
                    proposedPrice = 1250.0,
                    note = "We can streamline the pages to 8 core templates to match our Q4 launch budget.",
                    status = "COUNTERED"
                )
            )
            dao.insertNegotiation(
                QuotationNegotiationEntity(
                    projectId = project3Id,
                    senderRole = "ADMIN",
                    senderName = "Fausti Studio Director",
                    proposedPrice = 1350.0,
                    note = "Counteroffer: $1,350 including 9 core templates, complete design token library, and 2 rounds of revisions.",
                    status = "PENDING"
                )
            )

            // Seed Deliverables for Project 2
            dao.insertDeliverable(
                DeliverableEntity(
                    projectId = project2Id,
                    title = "Luminary Album Artwork (3000x3000px High-Res TIFF)",
                    fileType = "IMAGE",
                    fileName = "Luminary_Cover_Master_300DPI.tiff",
                    fileSize = "48.2 MB",
                    downloadUrl = "https://faustiarts.com/deliverables/fa-2402-cover.tiff",
                    notes = "Print-ready CMYK master with embedded color profile.",
                    uploadedBy = "Elena Rostova",
                    isApproved = true
                )
            )
            dao.insertDeliverable(
                DeliverableEntity(
                    projectId = project2Id,
                    title = "Streaming Platforms Digital Package (Spotify, Apple Music)",
                    fileType = "ARCHIVE",
                    fileName = "Luminary_Digital_Bundle.zip",
                    fileSize = "14.6 MB",
                    downloadUrl = "https://faustiarts.com/deliverables/fa-2402-streaming.zip",
                    notes = "Contains RGB web-optimized assets, canvas loops, and social banners.",
                    uploadedBy = "Elena Rostova",
                    isApproved = true
                )
            )

            // Seed Payments
            dao.insertPayment(
                PaymentEntity(
                    projectId = project1Id,
                    receiptCode = "FA-REC-9011",
                    amount = 350.0,
                    paymentType = "DEPOSIT",
                    paymentMethod = "BANK_TRANSFER",
                    notes = "50% upfront production deposit received.",
                    recordedBy = "Fausti Studio Director"
                )
            )
            dao.insertPayment(
                PaymentEntity(
                    projectId = project2Id,
                    receiptCode = "FA-REC-8842",
                    amount = 550.0,
                    paymentType = "FINAL",
                    paymentMethod = "MANUAL_RECORD",
                    notes = "Full project payment settled upon delivery approval.",
                    recordedBy = "Fausti Studio Director"
                )
            )

            // Seed Messages
            dao.insertMessage(
                MessageEntity(
                    projectId = project1Id,
                    senderId = 1,
                    senderName = "Fausti Studio Director",
                    senderRole = "ADMIN",
                    content = "Welcome Dynasty! Our senior team has commenced concept sketches for Aura Luxury Branding."
                )
            )
            dao.insertMessage(
                MessageEntity(
                    projectId = project1Id,
                    senderId = 2,
                    senderName = "Elena Rostova",
                    senderRole = "MANAGER",
                    content = "Hi Dynasty, I have uploaded our initial moodboard explorations. Let me know your thoughts on the typography weight!"
                )
            )
            dao.insertMessage(
                MessageEntity(
                    projectId = project1Id,
                    senderId = 3,
                    senderName = "Dynasty Samuel",
                    senderRole = "CLIENT",
                    content = "The serif with the gold monogram looks magnificent! Exactly what we envisioned."
                )
            )

            // Seed Notifications
            dao.insertNotification(
                NotificationEntity(
                    userId = clientId,
                    projectId = project3Id,
                    title = "New Studio Counteroffer Available",
                    message = "Fausti Arts has submitted a counteroffer of $1,350 for the Fausti Interactive Agency Portal project.",
                    type = "QUOTATION"
                )
            )
            dao.insertNotification(
                NotificationEntity(
                    userId = clientId,
                    projectId = project2Id,
                    title = "Project Deliverables Ready",
                    message = "Master artwork files for Luminary Cyberpunk Album Cover have been uploaded and are ready for download.",
                    type = "DELIVERABLE"
                )
            )

            // Seed Portfolio Items
            val portfolioItems = listOf(
                PortfolioEntity(
                    title = "Sovereign Haute Horlogerie",
                    category = "Logo Design and Branding",
                    description = "Bespoke monogram identity and luxury editorial catalogue for Swiss precision timepieces.",
                    clientName = "Sovereign Geneve",
                    completionDate = "October 2026",
                    imageUrl = "",
                    featured = true
                ),
                PortfolioEntity(
                    title = "Nova Kinetic Sound Festival",
                    category = "Posters and Flyers",
                    description = "Large-format typographic posters celebrating electronic music and avant-garde generative visuals.",
                    clientName = "Nova Electronic Collective",
                    completionDate = "September 2026",
                    imageUrl = "",
                    featured = true
                ),
                PortfolioEntity(
                    title = "Elysian Botanical Skincare",
                    category = "Graphic Design",
                    description = "Eco-luxe glass packaging with gold foil stamp accents and tactile embossed label systems.",
                    clientName = "Elysian Organics",
                    completionDate = "August 2026",
                    imageUrl = "",
                    featured = true
                ),
                PortfolioEntity(
                    title = "Apex Hypercar Web Experience",
                    category = "Website Design",
                    description = "Immersive 3D web experience spotlighting electric supercar engineering and bespoke customizer.",
                    clientName = "Apex Automobili",
                    completionDate = "July 2026",
                    imageUrl = "",
                    featured = false
                ),
                PortfolioEntity(
                    title = "Solaria Neo-Tokyo Concept Art",
                    category = "Digital Artwork",
                    description = "Vibrant retro-futuristic cityscape illustration series for an international animation anthology.",
                    clientName = "Studio Mirai",
                    completionDate = "June 2026",
                    imageUrl = "",
                    featured = true
                )
            )
            dao.insertPortfolioList(portfolioItems)

            // Seed Announcements
            val announcements = listOf(
                AnnouncementEntity(
                    title = "FAUSTI ARTS Autumn Creative Showcase 2026",
                    content = "We are thrilled to unveil our new global creative studio platform. Clients can now collaborate directly with our art directors, negotiate project milestones, and track live deliverables.",
                    date = "November 2026",
                    author = "Fausti Studio",
                    priority = "HIGH"
                ),
                AnnouncementEntity(
                    title = "Expanded 3D Motion & Cinematic Animation Wing",
                    content = "Our studio has onboarded dedicated Unreal Engine and Blender motion designers to accelerate turnaround on commercial animation briefs.",
                    date = "October 2026",
                    author = "Elena Rostova",
                    priority = "NORMAL"
                )
            )
            dao.insertAnnouncements(announcements)

            // Seed Testimonials
            val testimonials = listOf(
                TestimonialEntity(
                    clientName = "Marcus Vance",
                    company = "Vance & Co. Luxury Goods",
                    rating = 5,
                    feedback = "Fausti Arts transformed our entire brand presence. Their mastery of gold foil aesthetics and typography is unmatched in the industry.",
                    projectTitle = "Haute Horlogerie Brand Identity",
                    date = "October 2026"
                ),
                TestimonialEntity(
                    clientName = "Aria Chen",
                    company = "Mirai Interactive Media",
                    rating = 5,
                    feedback = "Working through the negotiation and direct revision system was effortless. The final digital artwork exceeded our expectations.",
                    projectTitle = "Solaria Concept Series",
                    date = "August 2026"
                ),
                TestimonialEntity(
                    clientName = "David Sterling",
                    company = "Apex Performance",
                    rating = 5,
                    feedback = "Fast turnaround, impeccable communication, and world-class design standards. Fausti Arts is our go-to creative agency.",
                    projectTitle = "Apex Digital Brand Portal",
                    date = "July 2026"
                )
            )
            dao.insertTestimonials(testimonials)
        }
    }
}
