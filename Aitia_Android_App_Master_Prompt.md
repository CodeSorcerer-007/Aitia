# AITIA — Native Android Developer Bug & Debugging Notebook

## Master Build Prompt

You are a **senior Android software engineer, product designer, UX/UI designer, motion designer, and developer-productivity specialist**.

Build a **production-quality native Android application named `Aitia` (Αἰτία)**.

The app is designed for **software developers, software engineers, QA engineers, testers, students, and solo developers** to quickly capture, organize, investigate, and resolve:

- Bugs
- Crashes
- Errors
- UI/UX problems
- Performance problems
- Security concerns
- Testing observations
- Feature/improvement ideas
- Other development notes

The core concept is:

> **Aitia helps developers capture what went wrong, understand why it happened, and keep a record of how it was fixed.**

The name **Aitia (Αἰτία)** comes from the Greek concept of **cause/reason** and should be reflected subtly in the product identity: the app is about finding the cause behind software problems.

---

# 1. PRODUCT VISION

Aitia should feel like a developer's:

**Bug Notebook + Debugging Journal + Lightweight Issue Tracker + Testing Log**

It is **not** intended to replace Jira, Linear, GitHub Issues, Trello, or enterprise QA software.

The product should optimize for one very important moment:

> A developer is testing an application, notices something is wrong, opens Aitia, records it in a few seconds, and continues working.

Later, they return to the issue and progressively add evidence, investigation notes, logs, suspected cause, fix, and verification.

### Core product loop

**Capture → Investigate → Fix → Verify → Learn**

---

# 2. THE WOW FACTOR IS A CORE REQUIREMENT

Do **not** build a visually generic CRUD application.

Aitia should be an app that developers genuinely enjoy opening every day.

The UI/UX must feel:

- Premium
- Modern
- Polished
- Fast
- Developer-focused
- Distinctive
- Visually memorable
- Intuitive
- Satisfying to use
- Professional enough to feel like a serious developer tool

The goal is:

> **"This is one of the nicest utility apps on my phone."**

Aitia should have a recognizable visual identity instead of looking like a default Material form with lists.

## Important balance

The app must be **visually stunning without becoming distracting**.

Use visual polish to improve:

- hierarchy
- comprehension
- speed
- feedback
- emotional satisfaction
- sense of progress

Do not add flashy effects merely for decoration.

---

# 3. VISUAL DESIGN DIRECTION

Use **Jetpack Compose + Material 3** as the foundation, but customize the design system heavily.

Create a distinctive Aitia design language.

### Suggested visual personality

Think:

**Linear + Raycast + modern developer tools + premium mobile utility app**

but do not copy any existing product.

Use:

- sophisticated typography
- generous spacing
- strong card hierarchy
- subtle depth
- refined surfaces
- smooth transitions
- tasteful gradients where appropriate
- expressive icons
- subtle glow/highlight treatments for important states
- elegant empty states
- polished loading states
- beautiful dark mode

Avoid:

- excessive gradients
- childish gamification
- rainbow colors
- cluttered dashboards
- giant decorative graphics
- excessive cards everywhere
- noisy animations
- generic template-app appearance

---

# 4. AITIA BRANDING

Use the name:

# AITIA

Greek:

**Αἰτία**

Meaning:

**Cause / reason / underlying cause**

Create a clean, modern logo/icon concept around the idea of:

- finding the root cause
- tracing connections
- an abstract "A"
- a node/path
- diagnostic reasoning
- a subtle Greek-inspired geometric motif

Do not make the branding look like an ancient mythology app.

It should feel like a **premium modern developer product**.

Possible product tagline:

> **Capture. Investigate. Resolve.**

Alternative supporting phrase:

> **Find the cause. Fix the problem.**

Use the branding consistently across:

- launcher icon
- splash screen
- empty states
- onboarding
- app header
- widgets
- export documents

---

# 5. PLATFORM & TECHNOLOGY

Build a **true native Android application**.

Preferred stack:

- Kotlin
- Jetpack Compose
- Material 3
- Android Jetpack
- Room
- Kotlin Coroutines
- Flow / StateFlow
- ViewModel
- Navigation Compose
- DataStore
- WorkManager only when genuinely useful
- Android system pickers/APIs where appropriate

Use a maintainable architecture such as:

- MVVM
- Repository pattern
- Clean separation of UI/data/domain concerns

Use dependency injection if it meaningfully improves maintainability.

Target modern Android versions while maintaining sensible compatibility with commonly used devices.

The app must be:

- offline-first
- responsive
- efficient
- stable
- testable

---

# 6. OFFLINE-FIRST & PRIVACY-FIRST

Core functionality must work without an account or network connection.

The following must work completely offline:

- create issues
- edit issues
- search
- filter
- projects
- notes
- testing sessions
- attachments
- analytics
- drafts
- import/export
- issue relationships

Store developer data locally by default.

The app should never automatically upload:

- source code
- logs
- screenshots
- crash information
- debugging notes
- project information

The product should communicate that the user's developer information remains local unless explicitly exported or shared.

---

# 7. PRIMARY USER FLOW

Example:

A developer is testing an app called `WeatherApp`.

They navigate:

`Profile → Edit Profile → Change Photo`

They tap the camera button.

The application crashes.

They open Aitia and create:

**Crash: Camera crashes when opening from Change Profile Photo**

They record:

- Project: WeatherApp
- Type: Crash
- Priority: High
- Screen: Edit Profile
- Steps:
  `Profile → Edit Profile → Change Photo → Tap Camera`
- Expected:
  `Camera should open`
- Actual:
  `Application crashes`

They optionally attach:

- screenshot
- screen recording
- logcat output
- stack trace

Later they add:

**Suspected Cause**

`Camera permission callback is not handled correctly.`

Then:

**Fix**

`Added runtime permission validation before launching camera intent.`

Then:

**Verification**

`Retested on Pixel 8 / Android 15. Crash no longer reproduces.`

Finally:

`Open → Investigating → Fixed → Verified`

This entire lifecycle should feel exceptionally smooth.

---

# 8. HOME SCREEN

The home screen should immediately communicate:

**"What needs my attention?"**

Do not make the home screen feel like an enterprise analytics dashboard.

## Header

Show:

**Aitia**

Optional subtle supporting text:

`Capture. Investigate. Resolve.`

Provide a polished profile/settings entry.

## Primary action

Make **Quick Capture** extremely prominent.

Possible interaction:

- Floating action button
- expandable action button
- center action button
- prominent `+` capture button

Quick Capture should be accessible in one tap from the home screen.

## Quick action shortcuts

Provide:

- `+ Bug`
- `+ Crash`
- `+ Error`
- `+ Note`

These should have distinct but restrained visual treatment.

## At-a-glance metrics

Show a small set of useful metrics:

- Open
- High/Critical
- Crashes
- Fixed
- Recently Added

Use visually elegant cards or compact stat modules.

## Recent Issues

Show recent items with:

- title
- type
- project
- status
- priority
- timestamp
- attachment indicator
- optional subtle severity marker

Use swipe gestures only where they genuinely improve speed.

---

# 9. QUICK CAPTURE — MOST IMPORTANT UX

Quick Capture is one of the defining features of Aitia.

The user should be able to capture an issue in **under 30 seconds**, ideally much faster.

When they tap `Quick Capture`, immediately focus the primary input.

Show:

### What happened?

Large input field.

Example:

`App crashes when opening camera from profile`

Then provide compact controls:

- Type
- Priority
- Project

And:

**Save**

Everything else should be optional.

Do not force users through a long form before they can save.

After saving, they can enrich the issue later.

## Draft preservation

If the user leaves midway, automatically save a local draft.

Example:

`Unsaved Draft`

---

# 10. ISSUE TYPES

Keep the list compact:

- Bug
- Crash
- Error
- UI / UX
- Performance
- Security
- Test Observation
- Feature / Improvement
- Other

Do not create dozens of issue types.

---

# 11. ISSUE CREATION

### Required

Only:

- Title
- Type

Everything else may be optional.

### Optional

- Project
- Status
- Priority
- Description
- Screen / Feature
- Steps to Reproduce
- Expected Behavior
- Actual Behavior
- Device
- Android Version
- App Version
- Build Number
- Environment
- Network condition
- Tags
- Technical Details
- Attachments

Provide smart defaults.

---

# 12. ISSUE DETAIL SCREEN

This should be one of the most polished screens in the app.

Display:

### Header

- title
- issue type
- priority
- status
- project
- creation date
- updated date

### Description

Full description.

### Reproduction

Steps to reproduce.

### Expected vs Actual

Use clear side-by-side or stacked presentation:

**Expected**

Camera opens normally.

**Actual**

Application crashes immediately.

### Environment

Example:

- Device: Pixel 8
- Android: 15
- App Version: 1.4.2
- Build: 142

### Technical Details / Logs

Display logs in a developer-friendly monospaced style.

### Attachments

Support:

- screenshots
- images
- screen recordings
- text/log files

### Investigation Notes

A chronological debugging journal.

Example:

`10:32 — Confirmed crash only occurs on Android 15.`

`10:57 — Reproduced 4/4 times.`

`11:15 — Suspect camera permission handling.`

### Suspected Cause

### Fix / Solution

### Verification

### Related Issues

### Checklist

Make each section easy to edit without forcing the user into a giant "edit everything" screen.

---

# 13. ISSUE TIMELINE

Maintain a clean chronological history:

- created
- edited
- status changes
- notes added
- attachments added
- priority changes
- fix recorded
- verification recorded

Example:

`Aug 29, 10:31 — Issue created`

`Aug 29, 10:46 — Status changed to Investigating`

`Aug 29, 11:02 — Investigation note added`

`Aug 29, 11:34 — Fix recorded`

`Aug 29, 11:50 — Status changed to Verified`

Animate new timeline items subtly.

---

# 14. STATUSES

Use only:

- Open
- Investigating
- Blocked
- Fixed
- Verified
- Closed

Make changing status fast and satisfying.

Use:

- bottom sheets
- chips
- contextual menus
- subtle transitions

Do not build a complex workflow engine.

---

# 15. PRIORITY / SEVERITY

Use:

- Low
- Medium
- High
- Critical

Provide clear meaning.

Critical:
- application unusable
- severe data loss
- major security issue
- repeated production crash

High:
- major functionality broken

Medium:
- significant problem with workaround

Low:
- minor problem

Do not rely on color alone to communicate severity.

---

# 16. SEARCH

Implement fast global search across:

- titles
- descriptions
- project names
- tags
- technical details
- screen names
- notes
- error messages
- file/class names where stored

Search should feel instant.

Searching:

`camera`

should find issues where camera only appears in:

- description
- notes
- logs
- tags

Support highlighting or sensible result emphasis where appropriate.

---

# 17. FILTERS & SORTING

Filters:

- Project
- Type
- Status
- Priority
- Tags
- Date
- Version
- Environment
- Resolved / Unresolved

Sorting:

- Recently updated
- Recently created
- Priority
- Oldest unresolved
- Status
- Resolution time

Use polished filter bottom sheets rather than cluttering the main screen.

---

# 18. TAGS

Allow optional tags such as:

- camera
- permissions
- android-15
- firebase
- api
- ui
- login
- database
- networking

Autocomplete from existing tags.

Keep tagging fast.

---

# 19. PROJECTS

Projects represent applications being developed/tested.

Project fields:

- name
- description
- app/package name
- platform
- current version
- icon
- optional visual accent

Project screen should show:

- open issues
- critical issues
- recent issues
- resolved issues
- versions
- testing sessions

Keep project management lightweight.

Do not add:

- sprints
- story points
- teams
- complex permissions
- enterprise workflows

---

# 20. TESTING SESSIONS

Add a lightweight concept called:

**Testing Session**

Example:

`Login Flow — Aug 29`

The developer starts a session and records observations during testing.

Issues created during the session are associated with it automatically when appropriate.

At the end show:

- issues found
- crashes
- bugs
- notes
- session duration
- affected areas

Make starting/stopping a session visually satisfying but unobtrusive.

---

# 21. ENVIRONMENT PROFILES

Let developers save reusable test environments.

Example:

### Pixel 8 — Main Test Device

- Android 15
- App version 1.4.2

Another:

### Samsung S24 — Secondary Device

- Android 14
- App version 1.4.1

Selecting an environment should automatically populate relevant issue metadata.

---

# 22. APP VERSION TRACKING

Allow issues to be associated with:

- discovered version
- fixed version

Example:

**Introduced:** 1.4.0

**Fixed:** 1.4.2

Projects can maintain a lightweight version list.

---

# 23. RELEASE / VERSION VIEW

Group issues by app version.

Example:

### v1.4.2

`12 issues found`

`9 fixed · 2 open · 1 critical`

### v1.4.1

`8 issues found`

`8 resolved`

Keep this lightweight.

---

# 24. ATTACHMENTS

Support:

- image
- screenshot
- screen recording/video
- text/log file

Use Android's native pickers where appropriate.

For images:

- thumbnail preview
- full-screen viewer
- remove
- share

For files:

- filename
- type
- size where useful
- open/share action

Avoid unnecessary duplicate copies and handle storage responsibly.

---

# 25. TECHNICAL DETAILS / LOGS

Create a dedicated developer-friendly area.

Example:

```text
FATAL EXCEPTION: main
java.lang.SecurityException:
Permission denied...
```

Provide:

- Copy
- Paste
- Clear

Use monospaced typography.

A `Paste Error` action should quickly move focus to the technical details field.

Never attempt to secretly read other apps' private logs.

The primary workflow is manual paste/attach.

---

# 26. LIGHTWEIGHT ERROR PARSING

When a user pastes a stack trace or technical error, analyze the text locally and extract obvious information where possible.

Example:

```text
java.lang.NullPointerException
at com.example.weather.HomeFragment.kt:54
```

Suggest:

**Exception**
`NullPointerException`

**File**
`HomeFragment.kt`

**Line**
`54`

Allow the user to accept/edit these values.

This should be a useful convenience feature, not an oversized AI system.

---

# 27. DUPLICATE DETECTION

When creating an issue, check for likely duplicates using simple local text similarity.

Example:

User enters:

`Camera crashes when changing profile photo`

Aitia suggests:

> Possible duplicate  
> `Camera crashes from profile screen`

Actions:

- Open existing issue
- Link as duplicate
- Continue creating anyway

Never block the user from creating an issue.

---

# 28. RELATED ISSUES

Allow lightweight relationships:

- Related to
- Duplicate of
- Blocked by
- Caused by
- Fixes

Example:

`Crash #18`

Caused by:

`Bug #12 — Permission state not initialized`

---

# 29. CHECKLISTS

Allow a simple checklist within an issue.

Example:

### Investigation

☐ Reproduce crash

☐ Check permissions

☐ Check logs

☐ Test Android 14

☐ Test Android 15

☐ Retest after fix

Checklist progress should have subtle visual feedback.

---

# 30. RESOLUTION WORKFLOW

When moving an issue to `Fixed`, optionally request:

### Fix Summary

`What changed?`

### Verification

`How was it tested?`

### Verified On

Environment/device.

Then allow:

`Mark as Verified`

Atria should preserve the final record as useful engineering history.

---

# 31. ANALYTICS

Include a small analytics area.

Useful metrics:

- total issues
- open issues
- resolved issues
- crash count
- issues by type
- issues by priority
- issues by project
- average resolution time
- issues discovered per testing session
- issues by version
- issues by environment

Charts should be simple, elegant, and useful.

Do not create a giant BI dashboard.

The main question should be:

> **"What problems keep happening in my software?"**

---

# 32. INSIGHTS

Generate lightweight local insights from stored data.

Examples:

> `Camera-related issues are currently your most common crash category.`

> `3 critical issues remain unresolved.`

> `Android 15 accounts for most recent crashes.`

> `Login-related bugs increased across your last 3 testing sessions.`

Prefer transparent rule-based insights at first.

Do not require cloud AI.

---

# 33. HOME SCREEN WIDGET

Provide a simple Android home-screen widget.

Example:

**Aitia**

`5 Open Issues`

`2 Critical`

`+ Quick Capture`

Tapping Quick Capture should open the fast entry experience.

Keep the widget useful and visually clean.

---

# 34. NOTIFICATIONS

Notifications should be optional and restrained.

Useful examples:

> `You have 3 critical unresolved issues.`

Allow reminders only when useful or explicitly configured by the user.

Never spam.

---

# 35. BACKUP & EXPORT

Support:

- JSON
- CSV
- Markdown
- Plain text

Provide project export and issue-set export.

Use Android's system share sheet.

### Backup

Provide:

`Export Backup`

`Import Backup`

Use a versioned JSON format that can support future migrations.

---

# 36. APP LOCK

Provide optional protection using:

- biometric authentication
- PIN

Setting:

`Lock Aitia on launch`

This protects:

- logs
- screenshots
- code snippets
- debugging notes
- project information

Keep it simple.

---

# 37. CODE SNIPPETS

Allow short code snippets to be attached to issues.

Provide:

- language selector
- code formatting
- copy button

Example:

```kotlin
cameraLauncher.launch(cameraIntent)
```

Do not turn Aitia into a full IDE.

---

# 38. MARKDOWN SUPPORT

Support lightweight Markdown where useful:

- bold
- italic
- inline code
- code blocks
- bullet lists

Especially useful for:

- technical notes
- reproduction steps
- logs
- fix descriptions

Keep the editor simple.

---

# 39. PINNED ISSUES

Allow important issues to be pinned.

Examples:

- production crash
- payment failure
- database corruption
- critical security problem

Pinned issues can appear near the top of relevant views.

---

# 40. ARCHIVE & DELETE

Allow completed issues to be archived.

Archived items remain searchable.

Permanent deletion must require explicit confirmation.

After deletion, show an undo action with a Snackbar.

---

# 41. DARK MODE

Support:

- Light
- Dark
- System Default

Dark mode should receive special design attention.

The app should look exceptionally good in dark mode because developers frequently use dark interfaces.

Use contrast carefully so that:

- status is obvious
- code/logs are readable
- cards don't all blend together
- surfaces have clear hierarchy

---

# 42. MOTION DESIGN — MAKE THE APP FEEL ALIVE

This is a major requirement.

Use **purposeful micro-interactions** throughout the app.

Examples:

### Creating an issue

When the user saves:

- subtle button compression
- satisfying haptic feedback
- smooth transition into the issue list
- newly created issue gently enters the list
- optional brief confirmation animation

### Status changes

When:

`Investigating → Fixed`

use:

- chip/state transition
- small icon transformation
- subtle haptic
- smooth content transition

When:

`Fixed → Verified`

provide an especially satisfying but restrained completion animation.

### Checklist

Checking an item should:

- animate the checkbox
- animate text state
- slightly update progress

### Filtering

Filter chips and results should transition smoothly instead of snapping.

### Issue timeline

New events should enter naturally rather than appearing abruptly.

### Navigation

Use shared-element-like continuity where practical.

### Bottom sheets

Use smooth native-style motion.

Do not over-animate.

---

# 43. HAPTIC FEEDBACK

Use haptics thoughtfully.

Potential moments:

- successful save
- issue creation
- status change
- checklist completion
- toggle changes
- important destructive actions
- successful backup/export
- biometric/app lock events

Use different intensity where Android APIs allow it.

Never vibrate excessively.

Haptics should make interactions feel **physical and satisfying**, not noisy.

Respect system settings and accessibility expectations.

---

# 44. SOUND

Sound should be optional.

Do not require sound.

If used, it must be extremely subtle and disableable.

Haptics and motion should carry most of the interaction feedback.

---

# 45. POLISHED MICRO-INTERACTIONS

Look for opportunities for:

- press states
- springy but controlled transitions
- swipe actions
- animated counters
- smooth chip changes
- animated empty states
- contextual FAB behavior
- collapsing headers
- smooth keyboard transitions
- subtle pull-to-refresh where useful
- skeleton loading if any asynchronous operation exists
- polished snackbars
- animated icon states

Every animation must improve understanding or delight.

---

# 46. EMPTY STATES

Never show blank screens.

Example:

### No Issues Yet

`Found your first bug? Capture it here and come back to fix it.`

CTA:

`+ Add Issue`

For projects:

`Create a project for the app you're testing.`

For analytics:

`Your debugging patterns will appear here as you capture more issues.`

Make empty states feel intentional and premium.

---

# 47. ONBOARDING

Keep onboarding very short.

Do not create a long tutorial.

Possible three-screen flow:

### 1 — Capture

`Record bugs before you forget them.`

### 2 — Investigate

`Keep logs, notes, evidence, and reproduction steps together.`

### 3 — Resolve

`Track fixes and verify what changed.`

Then:

`Start using Aitia`

Allow skipping.

The onboarding itself should showcase the visual quality of the app.

---

# 48. GESTURES

Use gestures where they genuinely improve speed.

Possible examples:

- swipe issue to change/archive
- swipe between issue sections where appropriate
- long press for quick actions
- pull down to search/reveal controls where natural
- drag checklist items only if clearly useful

Do not hide important functionality behind gestures.

---

# 49. ACCESSIBILITY

Support:

- TalkBack
- content descriptions
- font scaling
- large text
- adequate touch targets
- color contrast
- reduced motion preferences where appropriate

Do not rely only on:

- red/green
- color
- animation

to communicate meaning.

Respect Android accessibility and animation preferences.

---

# 50. KEYBOARD / INPUT UX

Optimize text input heavily.

When Quick Capture opens:

- focus automatically
- show keyboard
- keep Save prominent
- support multiline text
- preserve drafts

Support normal copy/paste behavior.

Make forms easy to use one-handed.

---

# 51. SMART DEFAULTS

Examples:

When creating a Crash:

- Type = Crash
- Status = Open
- Priority = Medium

When creating a Test Observation:

- Type = Test Observation
- Status = Open

Do not over-automate.

Everything should remain editable.

---

# 52. NAVIGATION

Use a simple navigation structure.

Recommended bottom navigation:

### Home

Overview and recent issues.

### Issues

All issues, search, filters.

### Projects

Projects, versions, environments, testing sessions.

### Analytics

Lightweight statistics and insights.

### Settings

Appearance, backup, security, preferences.

Make Quick Capture globally accessible through a prominent action.

---

# 53. PROJECT VIEW

A project detail page should feel like a compact command center.

Show:

- project identity
- open issue count
- critical issue count
- recent issues
- current version
- testing sessions
- environments
- version history

Use visual hierarchy rather than a large table.

---

# 54. SETTINGS

Include only useful settings.

### Appearance

- Light
- Dark
- System

### Security

- App lock
- Biometric
- PIN

### Data

- Backup
- Restore
- Export

### Notifications

- reminders
- critical issue reminders

### Preferences

- default issue priority
- default project
- haptic feedback
- reduced motion
- confirmation preferences

### About

- Aitia version
- acknowledgements
- privacy information

---

# 55. DATA MODEL

Suggested entities:

## Project

- id
- name
- description
- packageName
- platform
- currentVersion
- iconUri
- createdAt
- updatedAt

## ProjectVersion

- id
- projectId
- versionName
- buildNumber
- createdAt

## Issue

- id
- projectId
- title
- description
- type
- status
- priority
- screen
- expectedBehavior
- actualBehavior
- stepsToReproduce
- technicalDetails
- exceptionType
- errorMessage
- sourceFile
- sourceLine
- suspectedCause
- solution
- verification
- introducedVersionId
- fixedVersionId
- environmentId
- testingSessionId
- createdAt
- updatedAt
- resolvedAt
- isPinned
- isArchived

## IssueNote

- id
- issueId
- text
- createdAt

## Attachment

- id
- issueId
- uri/path
- filename
- mimeType
- size
- createdAt

## Tag

- id
- name

## IssueTag

- issueId
- tagId

## RelatedIssue

- id
- sourceIssueId
- targetIssueId
- relationshipType

## ChecklistItem

- id
- issueId
- text
- isCompleted
- position

## TestingSession

- id
- projectId
- name
- startedAt
- endedAt
- environmentId

## Environment

- id
- projectId
- name
- device
- androidVersion
- appVersion
- buildNumber

## IssueTimelineEvent

- id
- issueId
- eventType
- metadata
- createdAt

Keep the schema maintainable and avoid unnecessary complexity.

---

# 56. ARCHITECTURE REQUIREMENTS

Use:

- UI layer
- ViewModels
- repository layer
- local data source
- Room
- reactive state
- proper error handling

Never place business logic inside Composables.

Avoid excessive abstraction.

Use reusable components for:

- issue cards
- chips
- status controls
- priority selectors
- attachment previews
- timeline rows
- metric cards
- empty states
- dialogs
- bottom sheets

---

# 57. PERFORMANCE

Aitia is itself a developer tool, so it should feel extremely fast.

Prioritize:

- quick cold startup
- smooth scrolling
- instant issue creation
- efficient local search
- lazy lists
- efficient attachment loading
- no unnecessary background processing
- no memory leaks
- correct lifecycle handling

The app should remain responsive with thousands of issues.

---

# 58. ROBUST ERROR HANDLING

Aitia should handle its own errors gracefully.

Examples:

- failed attachment import
- inaccessible file
- malformed backup
- invalid data
- database migration problems
- storage limitations

Provide human-readable messages.

Never expose raw technical errors unnecessarily.

---

# 59. SECURITY

Treat stored developer information as potentially sensitive.

Implement:

- secure handling of local data
- safe file access
- no unnecessary external communication
- safe backup handling
- explicit export/share actions

Do not accidentally expose private logs through exported/share intents.

---

# 60. MVP PRIORITY

Build this in stages.

## Phase 1 — Essential MVP

Implement:

1. Projects
2. Issues
3. Quick Capture
4. Issue types
5. Status
6. Priority
7. Search
8. Filters
9. Issue detail
10. Investigation notes
11. Attachments
12. Room database
13. Dark/light theme
14. Import/export
15. Basic analytics
16. Polished visual system
17. Core animations
18. Useful haptics

The MVP must already feel like a finished product, not a rough prototype.

## Phase 2 — High-value enhancements

Add:

- Testing Sessions
- Environment profiles
- Error parsing
- Duplicate suggestions
- Related issues
- Issue timeline
- Checklists
- Version tracking
- Home-screen widget
- App lock
- richer motion design
- advanced empty states

## Phase 3 — Optional future integrations

Consider later:

- Git integration
- GitHub/GitLab issue export
- crash reporting integrations
- AI-assisted debugging
- cloud sync
- team collaboration

Do not build these simply to increase feature count.

---

# 61. ANTI-BLOAT RULE

Every feature must pass this test:

> **Does this make recording, investigating, resolving, or learning from a software problem easier?**

Do not add:

- chat
- social networking
- team messaging
- complex project management
- sprint planning
- story points
- employee management
- complex calendars
- gamification
- unnecessary AI
- enterprise permission systems

Aitia should remain:

> **A beautiful, fast, deeply useful developer issue notebook.**

---

# 62. WOW UX ACCEPTANCE CRITERIA

Before considering the UI finished, manually review the app from these perspectives.

### First impression

Does Aitia look noticeably more polished than an ordinary Android CRUD app?

### Speed

Can I capture a bug in less than 30 seconds?

### Daily use

Would a developer enjoy using this every day?

### Debugging

Can I understand what happened without opening multiple unrelated tools?

### Visual hierarchy

Can I instantly tell:

- what is urgent
- what is unresolved
- what changed
- what needs attention

### Motion

Do animations make the product feel premium without slowing it down?

### Haptics

Do important actions feel satisfying without becoming annoying?

### Dark mode

Does the app look excellent in dark mode?

### Accessibility

Does the experience remain usable with large text, TalkBack, reduced motion, and high contrast needs?

---

# 63. PRODUCT PERSONALITY

Aitia should feel:

**Calm when everything is fine.**

**Focused when something breaks.**

**Structured when investigating.**

**Satisfying when a problem is resolved.**

The product should subtly create a sense of progression:

`Unknown → Captured → Investigating → Understood → Fixed → Verified`

The UI should make this journey visually understandable.

---

# 64. FINAL DELIVERABLE

Generate a **complete, runnable native Android project**, not a mockup.

Deliver:

- complete Kotlin source code
- Jetpack Compose UI
- Material 3 theme
- Room database
- migrations
- navigation
- ViewModels
- repositories
- models/entities
- search/filter
- attachments
- backup/import/export
- issue timeline
- analytics
- dark/light/system themes
- haptics
- animations
- widgets where implemented
- sample/demo data option
- unit tests for important business logic
- UI tests for major flows
- README with setup/build instructions

The application should compile and run.

Do not leave core functionality as pseudo-code.

Do not use placeholder screens for major features.

Do not build only a static prototype.

---

# 65. FINAL QUALITY BAR

The final application should make a developer think:

> **"Why didn't I have this before?"**

and ideally:

> **"This is my favorite app for keeping track of bugs while I develop."**

Aitia succeeds when the user can:

**Notice a problem → Capture it instantly → Add evidence → Investigate it → Find the cause → Record the fix → Verify the result**

with almost no friction.

## Final product statement

> **AITIA — Find the cause. Fix the problem.**

Build the product around this idea.
