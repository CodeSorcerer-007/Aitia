package com.aitia.app.data.sample

import com.aitia.app.data.local.AitiaDatabase
import com.aitia.app.data.local.entity.ChecklistItemEntity
import com.aitia.app.data.local.entity.EnvironmentProfileEntity
import com.aitia.app.data.local.entity.IssueEntity
import com.aitia.app.data.local.entity.IssueNoteEntity
import com.aitia.app.data.local.entity.IssueTimelineEventEntity
import com.aitia.app.data.local.entity.ProjectEntity
import com.aitia.app.data.local.entity.ProjectVersionEntity
import com.aitia.app.data.local.entity.TagEntity
import com.aitia.app.data.local.entity.TestingSessionEntity
import com.aitia.app.domain.model.IssueStatus
import com.aitia.app.domain.model.IssueType
import com.aitia.app.domain.model.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit

class SampleDataSeeder(private val database: AitiaDatabase) {

    suspend fun seedSampleData() = withContext(Dispatchers.IO) {
        val now = Instant.now()

        // 1. Projects
        val weatherProjId = database.projectDao().insertProject(
            ProjectEntity(
                name = "WeatherApp",
                description = "Modern hyper-local weather forecasting app with Radar Compose canvas.",
                packageName = "com.example.weather",
                platform = "Android",
                currentVersion = "1.4.2",
                colorHex = "#58A6FF",
                iconName = "cloud",
                createdAt = now.minus(14, ChronoUnit.DAYS),
                updatedAt = now.minus(1, ChronoUnit.HOURS)
            )
        )

        val vaultProjId = database.projectDao().insertProject(
            ProjectEntity(
                name = "VaultKey Authenticator",
                description = "Hardware-backed biometric passkey and 2FA authenticator.",
                packageName = "io.vaultkey.app",
                platform = "Android",
                currentVersion = "2.1.0",
                colorHex = "#A371F7",
                iconName = "lock",
                createdAt = now.minus(30, ChronoUnit.DAYS),
                updatedAt = now.minus(2, ChronoUnit.DAYS)
            )
        )

        val novaPayProjId = database.projectDao().insertProject(
            ProjectEntity(
                name = "NovaPay Android SDK",
                description = "In-app payment sheet and checkout SDK for merchant apps.",
                packageName = "com.novapay.sdk",
                platform = "Android",
                currentVersion = "3.0.1-rc2",
                colorHex = "#3FB950",
                iconName = "payment",
                createdAt = now.minus(60, ChronoUnit.DAYS),
                updatedAt = now.minus(3, ChronoUnit.HOURS)
            )
        )

        // Project Versions
        database.projectDao().insertVersion(
            ProjectVersionEntity(
                projectId = weatherProjId,
                versionName = "1.4.2",
                buildNumber = "142",
                isCurrent = true,
                createdAt = now.minus(2, ChronoUnit.DAYS)
            )
        )
        database.projectDao().insertVersion(
            ProjectVersionEntity(
                projectId = weatherProjId,
                versionName = "1.4.1",
                buildNumber = "141",
                isCurrent = false,
                createdAt = now.minus(10, ChronoUnit.DAYS)
            )
        )

        // 2. Environments
        val onePlusEnvId = database.environmentDao().insertEnvironment(
            EnvironmentProfileEntity(
                projectId = weatherProjId,
                name = "OnePlus Nord 5 — Primary Device",
                device = "OnePlus Nord 5",
                androidVersion = "Android 16 (Baklava API 36)",
                appVersion = "1.4.2",
                buildNumber = "142",
                notes = "Physical hardware device running OxygenOS 16 / Android 16."
            )
        )

        val pixel8EnvId = database.environmentDao().insertEnvironment(
            EnvironmentProfileEntity(
                projectId = weatherProjId,
                name = "Pixel 8 — Secondary Test Device",
                device = "Pixel 8",
                androidVersion = "Android 15 (API 35)",
                appVersion = "1.4.2",
                buildNumber = "142",
                notes = "Physical hardware device running Android 15."
            )
        )

        val s24EnvId = database.environmentDao().insertEnvironment(
            EnvironmentProfileEntity(
                projectId = weatherProjId,
                name = "Samsung S24 — OneUI Test",
                device = "Galaxy S24 Ultra",
                androidVersion = "Android 14 (API 34)",
                appVersion = "1.4.1",
                buildNumber = "141",
                notes = "Samsung OneUI 6.1 test profile."
            )
        )

        // 3. Testing Sessions
        val session1Id = database.testingSessionDao().insertSession(
            TestingSessionEntity(
                projectId = weatherProjId,
                name = "Profile & Camera Flow QA",
                startedAt = now.minus(3, ChronoUnit.HOURS),
                endedAt = now.minus(2, ChronoUnit.HOURS),
                environmentId = pixel8EnvId,
                notes = "Deep exploratory testing on photo upload, cropping, and runtime camera permissions."
            )
        )

        // 4. Issues

        // Issue 1: Camera Crash (The primary exemplar from Master Prompt)
        val issue1Id = database.issueDao().insertIssue(
            IssueEntity(
                projectId = weatherProjId,
                title = "Camera crashes when opening from Change Profile Photo",
                description = "Tapping the Camera button in Profile -> Edit Photo causes an immediate fatal crash on Android 15.",
                type = IssueType.CRASH,
                status = IssueStatus.VERIFIED,
                priority = Priority.CRITICAL,
                screen = "Profile -> Edit Profile",
                stepsToReproduce = "1. Open WeatherApp\n2. Navigate to Profile tab\n3. Tap 'Edit Profile'\n4. Tap Avatar icon -> 'Change Photo'\n5. Tap 'Take Photo' (Camera Intent)",
                expectedBehavior = "System Camera intent opens to capture a profile photo.",
                actualBehavior = "Fatal exception: ActivityNotFoundException / SecurityException.",
                technicalDetails = """
FATAL EXCEPTION: main
Process: com.example.weather, PID: 18420
java.lang.SecurityException: Permission Denial: starting Intent { act=android.media.action.IMAGE_CAPTURE }
    at android.app.Instrumentation.checkStartActivityResult(Instrumentation.java:2320)
    at android.app.Activity.startActivityForResult(Activity.java:5430)
    at com.example.weather.ui.profile.ProfileEditScreenKt.launchCamera(ProfileEditScreen.kt:84)
    at com.example.weather.ui.profile.ProfileEditScreenKt.access${'$'}launchCamera(ProfileEditScreen.kt:1)
                """.trimIndent(),
                exceptionType = "SecurityException",
                errorMessage = "Permission Denial: starting Intent { act=android.media.action.IMAGE_CAPTURE }",
                sourceFile = "ProfileEditScreen.kt",
                sourceLine = "84",
                suspectedCause = "Camera permission callback was not registered with ActivityResultLauncher before starting intent on Android 15.",
                solution = "Migrated camera launch to rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) and verified CAMERA permission grant state.",
                verification = "Retested on physical Pixel 8 / Android 15. Camera opens smoothly and saves high-res thumbnail without crashing.",
                environmentId = pixel8EnvId,
                testingSessionId = session1Id,
                isPinned = true,
                createdAt = now.minus(3, ChronoUnit.HOURS),
                updatedAt = now.minus(30, ChronoUnit.MINUTES),
                resolvedAt = now.minus(30, ChronoUnit.MINUTES)
            )
        )

        // Timeline for Issue 1
        database.timelineDao().insertTimelineEvent(
            IssueTimelineEventEntity(
                issueId = issue1Id,
                eventType = "CREATED",
                title = "Issue Created",
                description = "Captured during Profile & Camera Flow QA session.",
                timestamp = now.minus(3, ChronoUnit.HOURS)
            )
        )
        database.timelineDao().insertTimelineEvent(
            IssueTimelineEventEntity(
                issueId = issue1Id,
                eventType = "STATUS_CHANGED",
                title = "Status changed to Investigating",
                description = "Reproduced 4/4 times on Android 15.",
                timestamp = now.minus(2, ChronoUnit.HOURS)
            )
        )
        database.timelineDao().insertTimelineEvent(
            IssueTimelineEventEntity(
                issueId = issue1Id,
                eventType = "FIX_RECORDED",
                title = "Fix Recorded",
                description = "Migrated to ActivityResultContracts.TakePicture() with runtime grant check.",
                timestamp = now.minus(1, ChronoUnit.HOURS)
            )
        )
        database.timelineDao().insertTimelineEvent(
            IssueTimelineEventEntity(
                issueId = issue1Id,
                eventType = "STATUS_CHANGED",
                title = "Status changed to Verified",
                description = "Verified on Pixel 8 hardware device.",
                timestamp = now.minus(30, ChronoUnit.MINUTES)
            )
        )

        // Notes for Issue 1
        database.noteDao().insertNote(
            IssueNoteEntity(
                issueId = issue1Id,
                text = "10:32 — Confirmed crash only occurs on Android 15 due to stricter intent target package enforcement.",
                createdAt = now.minus(2, ChronoUnit.HOURS).minus(30, ChronoUnit.MINUTES)
            )
        )
        database.noteDao().insertNote(
            IssueNoteEntity(
                issueId = issue1Id,
                text = "11:15 — Root cause confirmed: Manifest declared CAMERA but forgot file provider URI configuration for target SDK 35.",
                createdAt = now.minus(1, ChronoUnit.HOURS).minus(45, ChronoUnit.MINUTES)
            )
        )

        // Checklist for Issue 1
        database.checklistDao().insertChecklistItem(
            ChecklistItemEntity(issueId = issue1Id, text = "Reproduce crash on Pixel 8", isCompleted = true, position = 0)
        )
        database.checklistDao().insertChecklistItem(
            ChecklistItemEntity(issueId = issue1Id, text = "Add FileProvider in AndroidManifest.xml", isCompleted = true, position = 1)
        )
        database.checklistDao().insertChecklistItem(
            ChecklistItemEntity(issueId = issue1Id, text = "Update compose camera launcher contract", isCompleted = true, position = 2)
        )
        database.checklistDao().insertChecklistItem(
            ChecklistItemEntity(issueId = issue1Id, text = "Verify on Android 14 and Android 15", isCompleted = true, position = 3)
        )

        // Issue 2: Radar Canvas Memory Leak (Performance Bug)
        val issue2Id = database.issueDao().insertIssue(
            IssueEntity(
                projectId = weatherProjId,
                title = "Radar canvas re-renders indefinitely in background tab",
                description = "When switching from Radar tab to Forecast tab, the canvas animation loop keeps consuming 18% CPU.",
                type = IssueType.PERFORMANCE,
                status = IssueStatus.INVESTIGATING,
                priority = Priority.HIGH,
                screen = "Radar Screen",
                stepsToReproduce = "1. Open Radar Tab\n2. Let animated cloud tiles load\n3. Switch to 7-Day Forecast tab\n4. Inspect Android Studio CPU Profiler",
                expectedBehavior = "Radar canvas animation should pause when Composable leaves composition.",
                actualBehavior = "infiniteRepeatable animator remains active in background.",
                technicalDetails = "withInfiniteAnimationFrameMillis is not cancelled on onDispose in RadarCanvas.kt:112",
                sourceFile = "RadarCanvas.kt",
                sourceLine = "112",
                suspectedCause = "LaunchedEffect key was Unit instead of lifecycle state or tab selection state.",
                isPinned = true,
                createdAt = now.minus(1, ChronoUnit.DAYS),
                updatedAt = now.minus(4, ChronoUnit.HOURS)
            )
        )

        database.checklistDao().insertChecklistItem(
            ChecklistItemEntity(issueId = issue2Id, text = "Profile CPU usage with Android Studio Profiler", isCompleted = true, position = 0)
        )
        database.checklistDao().insertChecklistItem(
            ChecklistItemEntity(issueId = issue2Id, text = "Add DisposableEffect or lifecycle key to animation loop", isCompleted = false, position = 1)
        )
        database.checklistDao().insertChecklistItem(
            ChecklistItemEntity(issueId = issue2Id, text = "Test battery drain with Battery Historian", isCompleted = false, position = 2)
        )

        // Issue 3: Biometric prompt dismisses prematurely on rotation (VaultKey)
        val issue3Id = database.issueDao().insertIssue(
            IssueEntity(
                projectId = vaultProjId,
                title = "BiometricPrompt fails with ERROR_CANCELED during screen rotation",
                description = "Rotating the phone while the Fingerprint/Face prompt is open resets the Fragment and loses cryptographic cipher.",
                type = IssueType.SECURITY,
                status = IssueStatus.OPEN,
                priority = Priority.HIGH,
                screen = "Vault Unlock Dialog",
                stepsToReproduce = "1. Launch VaultKey\n2. Trigger biometric unlock prompt\n3. Rotate device 90 degrees\n4. Observe prompt cancelation and authentication failure",
                expectedBehavior = "Biometric prompt state should survive configuration change or cleanly re-authenticate.",
                actualBehavior = "Authentication callback returns ERROR_CANCELED and locks vault for 30 seconds.",
                technicalDetails = "BiometricPrompt.AuthenticationCallback.onAuthenticationError(13, 'Cancel')",
                sourceFile = "BiometricAuthManager.kt",
                sourceLine = "65",
                createdAt = now.minus(2, ChronoUnit.DAYS),
                updatedAt = now.minus(1, ChronoUnit.DAYS)
            )
        )

        // Issue 4: NovaPay currency formatting error in French locale
        database.issueDao().insertIssue(
            IssueEntity(
                projectId = novaPayProjId,
                title = "Currency symbol placed before amount instead of after in fr-FR locale",
                description = "In French locale, € is displayed as '€ 45,00' instead of '45,00 €'.",
                type = IssueType.UI_UX,
                status = IssueStatus.FIXED,
                priority = Priority.LOW,
                screen = "Payment Checkout Sheet",
                stepsToReproduce = "1. Set system language to Français (France)\n2. Open NovaPay Checkout\n3. Observe total line item format",
                expectedBehavior = "Formatted according to ICU standard French locale: '45,00 €'.",
                actualBehavior = "Formatted as '$ 45.00' with replaced symbol: '€ 45,00'.",
                solution = "Replaced custom string template with NumberFormat.getCurrencyInstance(Locale.getDefault()).",
                verification = "Tested in en-US, fr-FR, de-DE, and ja-JP locales. Formatting adheres strictly to standard.",
                createdAt = now.minus(4, ChronoUnit.DAYS),
                updatedAt = now.minus(2, ChronoUnit.DAYS),
                resolvedAt = now.minus(2, ChronoUnit.DAYS)
            )
        )

        // Tags
        val tagCamera = database.tagDao().insertTag(TagEntity(name = "camera", colorHex = "#58A6FF"))
        val tagPerm = database.tagDao().insertTag(TagEntity(name = "permissions", colorHex = "#F85149"))
        val tagAndroid15 = database.tagDao().insertTag(TagEntity(name = "android-15", colorHex = "#A371F7"))
        val tagPerf = database.tagDao().insertTag(TagEntity(name = "performance", colorHex = "#E3B341"))

        database.tagDao().insertIssueTagCrossRef(com.aitia.app.data.local.entity.IssueTagCrossRef(issue1Id, tagCamera))
        database.tagDao().insertIssueTagCrossRef(com.aitia.app.data.local.entity.IssueTagCrossRef(issue1Id, tagPerm))
        database.tagDao().insertIssueTagCrossRef(com.aitia.app.data.local.entity.IssueTagCrossRef(issue1Id, tagAndroid15))
        database.tagDao().insertIssueTagCrossRef(com.aitia.app.data.local.entity.IssueTagCrossRef(issue2Id, tagPerf))
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        database.clearAllTables()
    }
}
