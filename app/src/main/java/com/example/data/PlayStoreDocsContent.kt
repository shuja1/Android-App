package com.example.data

data class PlayStoreDocSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val content: String
)

data class PlayChecklistItem(
    val id: String,
    val title: String,
    val description: String,
    val defaultDone: Boolean = false
)

object PlayStoreDocsContent {

    val checklistItems: List<PlayChecklistItem> = listOf(
        PlayChecklistItem(
            id = "app_title",
            title = "App Title <= 30 Characters",
            description = "\"Elemensys GST Calculator\" is 24 characters with no emojis, ALL CAPS, or promotional words.",
            defaultDone = true
        ),
        PlayChecklistItem(
            id = "unique_package",
            title = "Unique Application ID Configured",
            description = "Set to com.aistudio.gstcalculator.kxpqwm (verify or update if publishing under your Elemensys keystore).",
            defaultDone = true
        ),
        PlayChecklistItem(
            id = "adaptive_icon",
            title = "Elemensys Logo & Adaptive Icon Ready",
            description = "Adaptive launcher icon and mipmap PNGs configured across mdpi through xxxhdpi.",
            defaultDone = true
        ),
        PlayChecklistItem(
            id = "zero_permissions",
            title = "Zero Broad/Sensitive Permissions",
            description = "No storage, location, camera, or contacts permissions requested. PDF save uses Android SAF & FileProvider.",
            defaultDone = true
        ),
        PlayChecklistItem(
            id = "privacy_policy_host",
            title = "Host Privacy Policy URL",
            description = "Upload play_store_docs/PRIVACY_POLICY.html to your Elemensys website (HTTPS) and paste URL in Play Console.",
            defaultDone = false
        ),
        PlayChecklistItem(
            id = "store_graphics",
            title = "512x512 Icon & 1024x500 Feature Graphic Generated",
            description = "Ready in play_store_docs/ (play_store_icon_512x512.png & feature_graphic_1024x500.png). Add 2+ phone screenshots in Play Console.",
            defaultDone = true
        ),
        PlayChecklistItem(
            id = "signed_aab",
            title = "Build Signed Release App Bundle (.aab)",
            description = "Run release bundle build with your upload keystore and upload app-release.aab to Play Console.",
            defaultDone = false
        )
    )

    val storeListingText = """
App Name (max 30 chars):
Elemensys GST Calculator

Short Description (max 80 chars):
Fast GST calculator for all tax slabs with step-by-step math & PDF report export.

Full Description:
Elemensys GST Calculator is a clean, accurate Goods and Services Tax (GST) calculation tool designed for business owners, accountants, freelancers, and students.

Key Features:
• All GST Percentage Slabs: Instant one-tap calculation for 0%, 0.25%, 1%, 3%, 5%, 12%, 18%, 28%, and 40%, plus Custom GST % and optional Compensation Cess %.
• Exclusive & Inclusive Modes: Add GST to a base price or reverse-calculate the net taxable amount from a GST-inclusive total.
• Intra-State & Inter-State Breakdown: Automatic split between CGST + SGST (half rate each) or full IGST.
• Step-by-Step Math Breakdown: Every calculation displays numbered steps with exact formulas and substituted numbers so you can verify and audit every figure.
• All-Slabs Quick Comparison: View how your entered amount calculates across every GST rate slab at once.
• Export PDF Reports: Generate clean, formatted A4 PDF tax calculation reports with optional Business Name, GSTIN, Party Name, and HSN/SAC code. Switch between 10 currency symbols (INR, USD, EUR, GBP, AED, SGD, AUD, CAD, JPY, SAR), save directly to your device, or share instantly.
• Updated HSN/SAC & Product GST Directory: Search recent GST Council updated HSN & SAC codes with applicable tax rates and one-tap rate selection.
• Basic Arithmetic Calculator: Full-featured calculator with parentheses, percentage math, live preview, calculation tape, and one-tap transfer to the GST calculator.
• All-in-One Unit Converter: Convert across 11 categories including Length, Weight, Area, Volume, Temperature, Speed, Time, Data Storage, Pressure, Energy, and Number Base.
• Saved Calculation History: Revisit recent calculations offline anytime and re-export PDF reports.
• Quantity & Discount Support: Compute multi-unit totals and pre-tax discounts accurately using decimal-precision financial arithmetic.

Built for Privacy & Simplicity:
Elemensys GST Calculator works completely offline and does not collect, track, or upload your financial calculations.

Support & Contact:
teamelemensys@gmail.com
    """.trimIndent()

    val privacyPolicyMarkdown = """
# Privacy Policy for Elemensys GST Calculator

Effective Date: November 14, 2026
Developer: Elemensys
Contact Email: teamelemensys@gmail.com

## 1. Overview
Elemensys ("we", "our", or "us") built Elemensys GST Calculator as a standalone offline utility application. We respect your privacy and are committed to protecting it.

## 2. Information Collection and Use
Elemensys GST Calculator does **not** collect, transmit, sell, or share any personal information, financial records, or device identifiers.
- All GST calculations, optional business profile details (Business Name, GSTIN, Address), and saved calculation history are stored **exclusively on your device** using local Android storage.
- The app operates offline without requiring user registration, login, analytics SDKs, or advertising trackers.

## 3. PDF Export & File Access
When you choose to export or share a PDF report:
- Saving a PDF uses the Android Storage Access Framework (system file picker), giving the app access only to the specific file location you choose.
- Sharing a PDF uses a secure Android FileProvider URI strictly for the temporary report you chose to share.
- Elemensys GST Calculator does not request broad storage permissions.

## 4. Third-Party Services
This app does not integrate third-party advertising networks, behavioral analytics, or cloud tracking services.

## 5. Children's Privacy
This application is a general-audience business and finance utility tool and does not knowingly collect any personal information from children.

## 6. Changes to This Privacy Policy
We may update this Privacy Policy if new features are added in future releases. Updates will be reflected by revising the Effective Date above.

## 7. Contact Us
If you have questions about this Privacy Policy, contact us at:
Email: teamelemensys@gmail.com
    """.trimIndent()

    val dataSafetyText = """
Google Play Console — Data Safety & App Declarations Guide

1. Data Collection & Sharing:
• Does your app collect or share any of the required user data types? -> NO
• Reason: All calculations, business profile fields, and history stay strictly local on the user's device. No network transmission occurs.

2. Ads Declaration:
• Does your app contain ads? -> NO

3. App Access:
• All functionality in the app is available without any special access or login credentials.

4. Target Audience & Content:
• Target age group: 18 and over (or General Business Utility; select appropriate non-child audience).
• Is this a news app? -> NO
• Does the app have any financial features requiring banking/lending declarations? -> NO (It is a tax calculation utility, not a banking, loan, crypto, or payment service).

5. App Category & Tags:
• Category: Business (or Finance / Tools)
• Contact Email: teamelemensys@gmail.com

6. Permissions Justification:
• No dangerous runtime permissions declared in AndroidManifest.xml.
• Uses Android Storage Access Framework (ACTION_CREATE_DOCUMENT) and FileProvider only when user exports/shares PDF reports.
    """.trimIndent()

    val releaseGuideText = """
Google Play Console — Release Build & Upload Steps

1. App Identity Check:
• App Title: Elemensys GST Calculator (24 / 30 characters)
• Target SDK: 36 (Android 16) | Min SDK: 24 (Android 7.0)
• Version Code: 1 | Version Name: 1.0

2. Signing & Bundle Generation:
• Generate or use your Elemensys upload keystore (.jks).
• Set KEYSTORE_PATH, STORE_PASSWORD, and KEY_PASSWORD in your CI/environment.
• Build the signed App Bundle:
  gradle :app:bundleRelease
• Output file: app/build/outputs/bundle/release/app-release.aab

3. Required Play Console Store Assets:
• High-res App Icon: 512 x 512 px PNG (32-bit, no alpha)
• Feature Graphic: 1024 x 500 px PNG/JPG
• Phone Screenshots: Minimum 2 screenshots (16:9 or 9:16, min 320px)
• Privacy Policy URL: Host play_store_docs/PRIVACY_POLICY.html on an HTTPS URL.

4. Exported Repository Docs:
All ready-to-upload Play Store files are saved in the project folder:
• play_store_docs/STORE_LISTING.md
• play_store_docs/PRIVACY_POLICY.md
• play_store_docs/PRIVACY_POLICY.html
• play_store_docs/DATA_SAFETY_AND_DECLARATIONS.md
• play_store_docs/PLAY_CONSOLE_RELEASE_CHECKLIST.md
    """.trimIndent()

    val sections: List<PlayStoreDocSection> = listOf(
        PlayStoreDocSection(
            id = "store_listing",
            title = "Store Listing Copy",
            subtitle = "App Title (24/30 chars), Short & Full Descriptions",
            content = storeListingText
        ),
        PlayStoreDocSection(
            id = "privacy_policy",
            title = "Privacy Policy",
            subtitle = "Compliant offline-first policy ready for Play Console",
            content = privacyPolicyMarkdown
        ),
        PlayStoreDocSection(
            id = "data_safety",
            title = "Data Safety & Declarations",
            subtitle = "Exact Play Console questionnaire answers",
            content = dataSafetyText
        ),
        PlayStoreDocSection(
            id = "release_guide",
            title = "Release Bundle & Upload Guide",
            subtitle = "Signed AAB build & asset specifications",
            content = releaseGuideText
        )
    )
}
