package com.aitia.app.domain.insights

data class DiagnosisAdvice(
    val exceptionType: String,
    val title: String,
    val rootCauseSummary: String,
    val plainEnglishExplanation: String = "",
    val commonPitfalls: List<String>,
    val recommendedFixCode: String,
    val severityLevel: String = "High"
)

object AitiaDiagnostician {

    private val knowledgeBase = mapOf(
        "NullPointerException" to DiagnosisAdvice(
            exceptionType = "java.lang.NullPointerException",
            title = "Null Pointer Dereference",
            rootCauseSummary = "Attempted to access a method or property on an object reference that evaluates to null at runtime.",
            plainEnglishExplanation = "The app reached for something in memory that didn't exist yet (like trying to read a username before the user typed it), so it panicked and stopped.",
            commonPitfalls = listOf(
                "Interoperating with Java libraries without explicit @Nullable / @NonNull annotations.",
                "Using the force-unwrap operator (!!) on nullable StateFlow or ViewModel state.",
                "Accessing view bindings or context after Fragment onDestroyView() or Activity onDestroy()."
            ),
            recommendedFixCode = """
// 1. Use safe-call (?.) with elvis operator (?:)
val result = userProfile?.address?.city ?: "Unknown"

// 2. Or explicit let block
userProfile?.let { profile ->
    updateUiWith(profile)
}
            """.trimIndent()
        ),
        "SecurityException" to DiagnosisAdvice(
            exceptionType = "java.lang.SecurityException",
            title = "Security Exception / Missing Permission",
            rootCauseSummary = "The application attempted to perform a protected operation (such as opening Camera, recording audio, accessing fine location, or posting notifications) without the requisite runtime permission or AndroidManifest declaration.",
            plainEnglishExplanation = "Android locked the door because the app didn't ask the user for permission first (e.g. using the camera or microphone).",
            commonPitfalls = listOf(
                "Missing <uses-permission> in AndroidManifest.xml.",
                "Targeting Android 13+ (API 33) without requesting runtime POST_NOTIFICATIONS permission.",
                "Targeting Android 14+ (API 34) with foreground service without foregroundServiceType declaration.",
                "Invoking camera or audio recording before checking ContextCompat.checkSelfPermission() == PERMISSION_GRANTED."
            ),
            recommendedFixCode = """
// 1. Declare permission in AndroidManifest.xml:
// <uses-permission android:name="android.permission.CAMERA" />

// 2. Request runtime permission before executing action:
if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
    launchCamera()
} else {
    permissionLauncher.launch(Manifest.permission.CAMERA)
}
            """.trimIndent()
        ),
        "UninitializedPropertyAccessException" to DiagnosisAdvice(
            exceptionType = "kotlin.UninitializedPropertyAccessException",
            title = "Lateinit Property Not Initialized",
            rootCauseSummary = "A lateinit var was accessed before an initial assignment occurred in the lifecycle.",
            plainEnglishExplanation = "You promised Kotlin you'd set this variable before using it (using 'lateinit'), but your code tried to read it too early.",
            commonPitfalls = listOf(
                "Accessing injected dependencies before Dagger/Hilt/Koin graph injection completes.",
                "Referencing lateinit adapter/viewBinding inside companion object or before onViewCreated().",
                "Asynchronous coroutine callback accessing property before background worker completes setup."
            ),
            recommendedFixCode = """
// Check initialization before accessing
if (::myRepository.isInitialized) {
    myRepository.fetchData()
} else {
    // Or consider nullable type with initial null
    private var myRepository: Repository? = null
}
            """.trimIndent()
        ),
        "ConcurrentModificationException" to DiagnosisAdvice(
            exceptionType = "java.util.ConcurrentModificationException",
            title = "Collection Modified During Iteration",
            rootCauseSummary = "A non-thread-safe collection (e.g. ArrayList, HashMap) was modified while being iterated over.",
            plainEnglishExplanation = "One part of your code was looping through a list while another background worker added or deleted items from it at the exact same moment.",
            commonPitfalls = listOf(
                "Calling list.remove() or list.add() inside a standard for-each loop.",
                "Mutating a shared List state concurrently across background Dispatchers.IO and Dispatchers.Main."
            ),
            recommendedFixCode = """
// 1. Use Immutable Lists in Compose StateFlow
val newList = _items.value.toMutableList().apply { remove(item) }
_items.value = newList.toList()

// 2. Or thread-safe CopyOnWriteArrayList / ConcurrentHashMap
val safeList = CopyOnWriteArrayList<Item>()
            """.trimIndent()
        ),
        "NetworkOnMainThreadException" to DiagnosisAdvice(
            exceptionType = "android.os.NetworkOnMainThreadException",
            title = "Blocking Network Call on Main (UI) Thread",
            rootCauseSummary = "An HTTP request, socket connection, or DNS lookup was triggered synchronously on the Android main thread, violating strict mode and causing ANR.",
            plainEnglishExplanation = "The phone tried to download data directly on the screen thread, which would freeze the screen for the user. Android blocked it instantly to keep the UI smooth.",
            commonPitfalls = listOf(
                "Invoking OkHttp client.newCall().execute() directly in ViewModel or Composable.",
                "Synchronous database or URL read inside Application.onCreate()."
            ),
            recommendedFixCode = """
// Switch execution context to Dispatchers.IO
suspend fun fetchApiData(): Result<Data> = withContext(Dispatchers.IO) {
    try {
        val response = httpClient.get("https://api.example.com/data")
        Result.success(response)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
            """.trimIndent()
        ),
        "OutOfMemoryError" to DiagnosisAdvice(
            exceptionType = "java.lang.OutOfMemoryError",
            title = "Heap Memory Exhaustion (OOM)",
            rootCauseSummary = "The application allocated more heap memory than allowed by the Android OS process limit (typically due to uncompressed Bitmaps or memory leaks).",
            plainEnglishExplanation = "The app tried to hold too much data in RAM (usually giant uncompressed camera photos or leaked screens), running out of room.",
            commonPitfalls = listOf(
                "Decoding large raw camera Bitmaps into memory without downsampling (inSampleSize).",
                "Holding static references to Activity Context in singletons or static fields.",
                "Unregistered BroadcastReceivers or uncancelled CoroutineScopes outliving their Lifecycle."
            ),
            recommendedFixCode = """
// Downsample image decode with BitmapFactory.Options
val options = BitmapFactory.Options().apply {
    inJustDecodeBounds = true
}
BitmapFactory.decodeFile(filePath, options)
options.inSampleSize = calculateInSampleSize(options, reqWidth = 1080, reqHeight = 1920)
options.inJustDecodeBounds = false
val bitmap = BitmapFactory.decodeFile(filePath, options)
            """.trimIndent()
        ),
        "TransactionTooLargeException" to DiagnosisAdvice(
            exceptionType = "android.os.TransactionTooLargeException",
            title = "Binder IPC Buffer Overflow (> 1 MB)",
            rootCauseSummary = "Passed too much data (large Bitmap, heavy List, or serialized payload) in an Intent Bundle, Fragment arguments, or savedInstanceState.",
            plainEnglishExplanation = "You tried to send a package bigger than Android's 1MB messenger limit between screens. Pass an ID instead of the whole object!",
            commonPitfalls = listOf(
                "Passing raw byte arrays or Bitmaps in Intent.putExtra().",
                "Saving entire database lists in rememberSaveable or onSaveInstanceState()."
            ),
            recommendedFixCode = """
// Pass ID or local URI instead of full payload
val intent = Intent(context, DetailActivity::class.java).apply {
    putExtra("EXTRA_ISSUE_ID", issue.id) // Pass only primitive ID
}
// Load full object from local Room database in destination ViewModel
            """.trimIndent()
        ),
        "SQLiteConstraintException" to DiagnosisAdvice(
            exceptionType = "android.database.sqlite.SQLiteConstraintException",
            title = "Room Database Constraint Violation",
            rootCauseSummary = "Attempted to insert duplicate unique keys, violate foreign key relationships, or insert null into a NOT NULL column in Room.",
            plainEnglishExplanation = "The local database rejected your save because a rule was broken (like saving a ticket for a project that doesn't exist, or saving duplicate IDs).",
            commonPitfalls = listOf(
                "Inserting child entity with a foreign key pointing to a non-existent parent ID.",
                "Missing OnConflictStrategy.REPLACE or OnConflictStrategy.IGNORE on @Insert DAO."
            ),
            recommendedFixCode = """
@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun upsertIssue(issue: IssueEntity): Long

// Ensure foreign keys have onDelete = ForeignKey.CASCADE if appropriate
            """.trimIndent()
        ),
        "JobCancellationException" to DiagnosisAdvice(
            exceptionType = "kotlinx.coroutines.JobCancellationException",
            title = "Coroutine Scope Cancelled Prematurely",
            rootCauseSummary = "A running coroutine was cancelled (e.g. ViewModel was cleared or parent job failed), but an active child job did not handle cancellation cooperatively.",
            plainEnglishExplanation = "The background task was stopped normally (like when the user left the screen), but a try/catch accidentally swallowed the stop signal.",
            commonPitfalls = listOf(
                "Catching generic Exception and swallowing CancellationException inside try-catch.",
                "Using GlobalScope instead of viewModelScope / lifecycleScope."
            ),
            recommendedFixCode = """
try {
    doWork()
} catch (e: CancellationException) {
    throw e // Always rethrow CancellationException in Kotlin Coroutines!
} catch (e: Exception) {
    handleError(e)
}
            """.trimIndent()
        )
    )

    fun diagnose(exceptionType: String?, errorMessage: String?): DiagnosisAdvice? {
        if (exceptionType.isNullOrBlank() && errorMessage.isNullOrBlank()) return null

        val target = (exceptionType ?: "") + " " + (errorMessage ?: "")
        for ((key, advice) in knowledgeBase) {
            if (target.contains(key, ignoreCase = true) || target.contains(advice.exceptionType, ignoreCase = true)) {
                return advice
            }
        }
        return null
    }

    fun getAllDiagnoses(): List<DiagnosisAdvice> = knowledgeBase.values.toList()
}
