# Privacy Policy — Elemensys GST Calculator

**Effective Date:** November 14, 2026  
**Developer:** Elemensys  
**Contact Email:** teamelemensys@gmail.com

## 1. Overview
Elemensys ("we", "our", or "us") provides **Elemensys GST Calculator** as an offline utility application for calculating Goods and Services Tax (GST) and generating local PDF tax calculation reports.

## 2. Information Collection and Use
Elemensys GST Calculator does **not** collect, transmit, sell, or share any personal data, financial calculations, or device identifiers.
- All GST inputs, calculation history, and optional business header fields (Business Name, GSTIN, Business Address) are stored **strictly locally on your device** using Android DataStore Preferences.
- The application operates offline and does not use analytics SDKs, advertising trackers, or cloud databases.

## 3. PDF Export and Storage Access
When you export or share a PDF report:
- **Save PDF** uses the system Android Storage Access Framework (`ACTION_CREATE_DOCUMENT`), granting access only to the destination file you explicitly choose.
- **Share PDF** writes a temporary PDF to the app's internal cache and shares it via Android `FileProvider` strictly with the app you select.
- No broad storage permissions (`READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, `READ_MEDIA_*`) are requested.

## 4. Third-Party Services
Elemensys GST Calculator does not include third-party ad networks or telemetry providers.

## 5. Children's Privacy
This app is a general business and financial utility and does not knowingly collect personal data from children.

## 6. Changes to This Policy
Updates to this Privacy Policy will be posted with an updated Effective Date.

## 7. Contact Us
If you have any questions about this Privacy Policy, contact:
**Email:** teamelemensys@gmail.com
