package com.presenter.remote

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

/**
 * Presenter Remote: a thin native shell around the /remote page that server.js serves.
 * Adds what a browser tab can't: a saved address, screen kept awake, and volume keys
 * for Next / Prev.
 */
class MainActivity : Activity() {
    private val bg = Color.parseColor("#16140F")
    private val gold = Color.parseColor("#C9A227")
    private val dim = Color.parseColor("#A79E8A")
    private lateinit var web: WebView
    private lateinit var panel: LinearLayout
    private lateinit var address: EditText
    private lateinit var error: TextView
    private var onRemote = false

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private val prefs get() = getSharedPreferences("remote", MODE_PRIVATE)

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        val match = ViewGroup.LayoutParams.MATCH_PARENT

        web = WebView(this).apply {
            setBackgroundColor(bg)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true // the page remembers the PIN here
            webViewClient = object : WebViewClient() {
                override fun onReceivedError(view: WebView, request: WebResourceRequest, err: WebResourceError) {
                    if (request.isForMainFrame) {
                        showConnect("Couldn't reach that computer. Check you're on the same Wi-Fi and that Presenter is running.")
                    }
                }
            }
        }

        fun label(t: String, size: Float, c: Int) = TextView(this).apply {
            text = t; textSize = size; setTextColor(c)
        }
        address = EditText(this).apply {
            hint = "192.168.1.23"
            setHintTextColor(dim)
            setTextColor(Color.WHITE)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            imeOptions = EditorInfo.IME_ACTION_GO
            setSingleLine()
            setOnEditorActionListener { _, _, _ -> open(text.toString()); true }
        }
        error = label("", 14f, Color.parseColor("#E0796A")).apply { setPadding(0, dp(14), 0, 0) }
        val connect = Button(this).apply {
            text = "Connect"
            isAllCaps = false
            setTextColor(bg)
            setBackgroundColor(gold)
            setOnClickListener { open(address.text.toString()) }
        }
        panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(28), dp(28), dp(28), dp(28))
            addView(label("Presenter Remote", 24f, Color.WHITE))
            addView(label(
                "Type the address shown in the Presenter window on your computer (the Phone button), " +
                    "or scan its QR code with your camera and choose Presenter Remote.", 14f, dim
            ).apply { setPadding(0, dp(8), 0, dp(20)) })
            addView(address)
            addView(connect, LinearLayout.LayoutParams(match, ViewGroup.LayoutParams.WRAP_CONTENT).apply { topMargin = dp(16) })
            addView(error)
        }

        setContentView(FrameLayout(this).apply {
            setBackgroundColor(bg)
            addView(web, FrameLayout.LayoutParams(match, match))
            addView(panel, FrameLayout.LayoutParams(match, match))
        })

        val start = intent?.data?.toString() ?: prefs.getString("addr", null)
        if (start != null) open(start) else showConnect(null)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.data?.let { open(it.toString()) }
    }

    private fun showConnect(msg: String?) {
        onRemote = false
        web.visibility = View.GONE
        panel.visibility = View.VISIBLE
        error.text = msg ?: ""
        address.setText(prefs.getString("addr", ""))
    }

    private fun showRemote() {
        onRemote = true
        panel.visibility = View.GONE
        web.visibility = View.VISIBLE
    }

    /** Accepts "192.168.1.23", "192.168.1.23:8787" or a full link (with ?pin= from the QR code). */
    private fun open(raw: String) {
        val text = raw.trim()
        val full = if (text.startsWith("http://") || text.startsWith("https://")) text else "http://$text"
        val uri = Uri.parse(full)
        val host = uri.host
        if (text.isEmpty() || host.isNullOrEmpty()) {
            showConnect("That address doesn't look right.")
            return
        }
        val port = if (uri.port == -1) 8787 else uri.port
        val base = "${uri.scheme}://$host:$port/remote"
        val pin = uri.getQueryParameter("pin")
        prefs.edit().putString("addr", "$host:$port").apply()
        showRemote()
        web.loadUrl(if (!pin.isNullOrEmpty()) "$base?pin=$pin" else base)
    }

    private fun press(id: String) {
        // Only act when the remote screen is showing (not the PIN screen).
        web.evaluateJavascript(
            "(function(){var s=document.getElementById('remoteScreen');var b=document.getElementById('$id');" +
                "if(s&&!s.classList.contains('hidden')&&b)b.click();})()", null
        )
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val id = when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> "nextBtn"
            KeyEvent.KEYCODE_VOLUME_UP -> "prevBtn"
            else -> null
        }
        if (onRemote && id != null) {
            if (event.repeatCount == 0) press(id)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (onRemote) showConnect(null) else super.onBackPressed()
    }
}
