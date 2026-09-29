# Starting Presenter

Pick the one for your computer and double-click it. It starts the server
and opens the Control window in your browser automatically — no terminal
typing needed.

- **Mac:** `Presenter.app`
  (First time only: right-click it → Open, then click Open again — macOS
  blocks apps it doesn't recognize on the first launch. After that, a
  normal double-click works.)
- **Windows:** `Presenter.bat`
- **Linux:** `presenter.sh` (double-click and choose "Run", or run
  `./presenter.sh` in a terminal)

Running it again while it's already open just opens another browser tab
pointed at it — it won't start a second server.

To stop the server: close the black server window (Windows), or run
`pkill -f "node server.js"` (Mac/Linux) — or just leave it running in the
background, it uses almost no resources when idle.

## Being upfront about what this is

This isn't a fully self-contained app that runs with zero setup — it's
still the same Node.js server as before, just launched with one click
instead of typing `node server.js` yourself. **Node.js still needs to be
installed once**, the same as before (free, from nodejs.org). Building a
true no-dependencies installer (one that bundles Node itself into a
single .exe/.app with no separate install step) needs internet access to
download packaging tools, which isn't available in the environment that
built this — so this one-click launcher is the closest to "just an app"
that could be put together here.

Everything else — remote control from your phone, the PIN, NDI/OBS setup
— works exactly as described in `MOBILE-REMOTE-SETUP.md`.
