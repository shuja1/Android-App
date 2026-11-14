# Google Play Console — Data Safety & Policy Declarations

## 1. Data Safety Section
- **Does your app collect or share any of the required user data types?** `No`
- **Explanation:** Elemensys GST Calculator is an offline tax calculation utility. No data leaves the user's device, and there are no analytics, crash telemetry, or ad SDKs.

## 2. App Content Declarations
- **Privacy Policy URL:** Upload `PRIVACY_POLICY.html` to your Elemensys HTTPS domain and enter that URL in *App content → Privacy policy*.
- **Ads:** `No, my app does not contain ads`
- **App Access:** `All functionality is available without special access` (no login required).
- **Target Audience:** Select `18 and over` (or General Business Utility; do not select children under 13 unless targeting kids).
- **News App:** `No`
- **Financial Features:** `No` (Utility calculator only — does not provide banking, lending, money transfer, brokerage, or cryptocurrency services).
- **Government Apps:** `No` (Independent utility by Elemensys; does not claim government affiliation).

## 3. Android Permissions Audit
- **Dangerous Runtime Permissions Requested:** None.
- **Storage Compliance:** Uses zero-permission Android Storage Access Framework (`ActivityResultContracts.CreateDocument`) and `FileProvider` for PDF exports.
