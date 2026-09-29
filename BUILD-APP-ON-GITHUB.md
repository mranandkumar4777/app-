# Get the phone app without installing anything (GitHub builds it)

1. Make a free account at github.com and create a **new repository** (any name; Private is fine).
2. Click **uploading an existing file**, drag in **everything inside the `presenter-app` folder**
   (including the hidden `.github` folder — if your file manager hides it, use GitHub Desktop
   or `git push` instead), then **Commit changes** to the `main` branch.
3. Open the **Actions** tab. "Build Android APK" starts by itself (about 3-5 minutes).
   If it doesn't, click it -> **Run workflow**.
4. When it shows a green check, open the repo's **Releases** page on your phone
   (right side of the repo home page -> "Presenter Remote (latest)") and tap **PresenterRemote.apk**.
   Allow "install from this source" when Android asks.

## Connecting afterwards
Start Presenter on the computer, click **Phone**, and scan the QR code with the phone's camera.
Choose **Presenter Remote** when it asks. The PIN is inside the QR code, so there is nothing to type.
After the first time the app remembers the computer, so you just open it.
