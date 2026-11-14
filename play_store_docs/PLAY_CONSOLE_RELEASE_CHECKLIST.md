# Play Console Release & Upload Checklist — Elemensys GST Calculator

## 1. Metadata & Manifest Compliance
- [x] **App Name:** `Elemensys GST Calculator` (24 characters <= 30 char limit, no emojis/buzzwords)
- [x] **Application ID:** `com.aistudio.gstcalculator.kxpqwm`
- [x] **Target SDK:** `36` | **Min SDK:** `24`
- [x] **Adaptive Launcher Icon:** Elemensys logo configured in `res/drawable/` and `res/mipmap-*/`
- [x] **Zero Broad Permissions:** Verified no storage/location/camera permissions

## 2. Build Signed Android App Bundle (.aab)
1. Ensure you have your Elemensys release upload keystore (`.jks`) with key alias `upload`.
2. Export environment variables in your build environment:
   ```bash
   export KEYSTORE_PATH=/path/to/elemensys-upload-key.jks
   export STORE_PASSWORD=your_store_password
   export KEY_PASSWORD=your_key_password
   ```
3. Build the release App Bundle:
   ```bash
   gradle :app:bundleRelease
   ```
4. Upload `app/build/outputs/bundle/release/app-release.aab` to **Google Play Console → Production (or Internal Testing) → Create new release**.

## 3. Required Store Graphics Assets
- [x] **High-res App Icon (`512 x 512 px` PNG):** Ready at `play_store_docs/play_store_icon_512x512.png`
- [x] **Feature Graphic (`1024 x 500 px` PNG):** Ready at `play_store_docs/feature_graphic_1024x500.png`
- [ ] **Phone Screenshots:** Take 2+ screenshots from the Streaming Emulator or a physical device (Calculator with Steps, PDF Report Sheet, HSN Search, Basic Calculator, Converter)
- [ ] **Privacy Policy URL:** Host `play_store_docs/PRIVACY_POLICY.html` over HTTPS.
