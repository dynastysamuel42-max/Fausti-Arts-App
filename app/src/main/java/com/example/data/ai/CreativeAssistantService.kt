package com.example.data.ai

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

private const val TAG = "CreativeAssistant"

data class CreativeSuggestion(
    val recommendedKeywords: List<String>,
    val clarifyingQuestions: List<String>,
    val enhancedDescription: String
)

class CreativeAssistantService {

    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
            }
        )
    }

    /**
     * Prompts the client with questions, keywords, and an enhanced detailed description
     * to articulate their vision for Fausti Arts studio.
     */
    suspend fun articulateVision(
        serviceCategory: String,
        projectTitle: String,
        initialIdea: String,
        targetAudience: String = "",
        preferredVibe: String = ""
    ): Result<String> {
        return try {
            val prompt = """
                You are the Senior Creative Director & Executive Art Strategist at FAUSTI ARTS ("Your Imagination, Our Creation"), a premier international luxury creative design studio.
                
                A client is preparing a project request. Help them articulate a comprehensive, compelling, professional creative brief for our design team.
                
                PROJECT DETAILS:
                - Service: $serviceCategory
                - Title: $projectTitle
                - Client's Initial Notes/Vision: ${if (initialIdea.isBlank()) "Looking for high-impact creative work." else initialIdea}
                ${if (targetAudience.isNotBlank()) "- Target Audience: $targetAudience" else ""}
                ${if (preferredVibe.isNotBlank()) "- Desired Aesthetic/Mood: $preferredVibe" else ""}
                
                Please structure your response with:
                1. ✦ CREATIVE DIRECTION & MOOD: (2-3 sentences articulating the visual atmosphere and tone)
                2. ✦ KEY OBJECTIVES: (Bullet points of what this project must accomplish)
                3. ✦ DETAILED SPECIFICATIONS: (Specific visual motifs, color palette, typography or composition details)
                4. ✦ RECOMMENDED KEYWORDS: (5-8 comma-separated design descriptors)
                5. ✦ CLARIFYING QUESTIONS FOR THE CLIENT: (2-3 questions to consider to refine the scope)
                
                Keep the tone elegant, prestigious, inspiring, and professional. Write in ready-to-use prose suitable for the project description field.
            """.trimIndent()

            val response = withTimeout(60_000L) {
                model.generateContent(prompt)
            }

            val text = response.text
            if (!text.isNullOrBlank()) {
                Result.success(text)
            } else {
                Result.failure(Exception("AI returned an empty response. Using studio creative brief template."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI assistance timed out", e)
            Result.failure(Exception("AI request timed out. Please try again or use the template below."))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "AI generation failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Provides curated questions and keywords for instant one-tap client guidance.
     */
    fun getCuratedCategoryGuidance(category: String): Pair<List<String>, List<String>> {
        val keywords = when (category) {
            "Logo Design and Branding" -> listOf(
                "Minimalist Luxury", "Gold Foil Accents", "Geometric Monogram",
                "Editorial Serif", "Timeless Emblem", "High-Fashion Aesthetics"
            )
            "Graphic Design" -> listOf(
                "Bold Typographic Grid", "Matte Obsidian Finish", "Tactile Packaging",
                "Swiss International Style", "Vibrant Editorial", "Clean Composition"
            )
            "Digital Artwork" -> listOf(
                "Cinematic Lighting", "Atmospheric Cyberpunk", "Renaissance Chiaroscuro",
                "Stylized Concept Art", "Surreal Dreamscape", "Rich Neon Reflections"
            )
            "Website Design" -> listOf(
                "Dark Luxe Interactive", "Fluid Micro-Interactions", "Glassmorphic Cards",
                "Generous Whitespace", "Mobile-First Ergonomics", "Modern Figma Prototype"
            )
            "Video Editing" -> listOf(
                "Cinematic Pacing", "Film Grain & Color Grade", "Dynamic Sound Design",
                "High-Energy Commercial Cut", "Smooth Match Cuts", "4K Ultra-HD Delivery"
            )
            "Animation" -> listOf(
                "Kinetic Typography", "2.5D Isometric World", "Fluid Logo Reveal",
                "Organic Spring Physics", "Seamless Loop", "Vector Character Rig"
            )
            "Character Design" -> listOf(
                "Memorable Silhouette", "Stylized Mascot", "Expression Model Sheet",
                "Turnaround Angles", "Rich Personality", "Vector Scalable"
            )
            "Posters and Flyers" -> listOf(
                "Avant-Garde Hierarchy", "High-Contrast Palette", "Architectural Scale",
                "Striking Headline", "Screen-Print Texture", "Exhibition Ready"
            )
            "Social Media Designs" -> listOf(
                "Instagram Carousel Pack", "Brand Story Templates", "Cohesive Grid Aesthetic",
                "High-Conversion CTA", "Bold Visual Hooks", "Dark & Gold Accents"
            )
            "Photo Editing" -> listOf(
                "High-End Skin Retouch", "Commercial Product Grade", "Editorial Color Shift",
                "Clean Background Extraction", "Subtle Lens Flare", "Crisp Detailing"
            )
            else -> listOf(
                "Prestigious Aesthetic", "Handcrafted Artistry", "Modern Studio Polish",
                "Distinctive Identity", "Compelling Narrative", "Bespoke Deliverables"
            )
        }

        val questions = when (category) {
            "Logo Design and Branding" -> listOf(
                "What core emotions should someone feel when seeing your mark?",
                "Do you prefer a wordmark, abstract monogram, or pictorial emblem?",
                "Are there competitor brands whose aesthetic you admire or want to avoid?"
            )
            "Website Design" -> listOf(
                "What is the single most important action a visitor should take?",
                "Do you prefer dark luxury obsidian or light warm cream themes?",
                "Which features are essential (portfolio showcase, contact booking, client portal)?"
            )
            "Digital Artwork" -> listOf(
                "What is the intended delivery format (vinyl, print, web cover)?",
                "What time period or atmosphere (futuristic, mythological, gothic)?",
                "What focal subject should command immediate attention?"
            )
            "Video Editing" -> listOf(
                "What is the desired video runtime and target platform?",
                "What tone of background music and audio pacing do you envision?",
                "Do you require subtitles, motion titles, or custom sound effects?"
            )
            else -> listOf(
                "Who is the primary audience experiencing this piece?",
                "What is your ideal deadline and milestone schedule?",
                "What specific creative references or benchmark examples inspire you?"
            )
        }

        return Pair(keywords, questions)
    }

    /**
     * Fallback template generator if offline or API unavailable.
     */
    fun generateOfflineTemplate(
        category: String,
        title: String,
        initialIdea: String,
        selectedKeywords: List<String>
    ): String {
        val keywordsText = if (selectedKeywords.isNotEmpty()) {
            selectedKeywords.joinToString(", ")
        } else {
            "Modern, Elegant, Premium, Studio Quality"
        }

        return buildString {
            appendLine("✦ CREATIVE BRIEF: ${title.ifBlank { "New Creative Project" }}")
            appendLine("✦ SERVICE: $category")
            appendLine()
            appendLine("✦ VISION & CONCEPT:")
            if (initialIdea.isNotBlank()) {
                appendLine(initialIdea)
            } else {
                appendLine("Create a distinctive, modern creative project with an emphasis on visual prestige and precision craft.")
            }
            appendLine()
            appendLine("✦ KEYWORDS & AESTHETIC DIRECTION:")
            appendLine(keywordsText)
            appendLine()
            appendLine("✦ CORE REQUIREMENTS:")
            appendLine("• High-resolution source files and production deliverables.")
            appendLine("• Cohesive color harmony adhering to modern luxury standards.")
            appendLine("• Optimized for both digital and print applications as required.")
        }
    }
}
