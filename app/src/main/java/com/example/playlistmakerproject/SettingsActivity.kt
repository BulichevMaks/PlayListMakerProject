package com.example.playlistmakerproject

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonBack = findViewById<LinearLayout>(R.id.buttonBack)
        val buttonShare = findViewById<LinearLayout>(R.id.share)
        val buttonSupport = findViewById<LinearLayout>(R.id.support)
        val buttonUserAgreement = findViewById<LinearLayout>(R.id.user_agreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        buttonBack.setOnClickListener {
            finish()
        }

        buttonShare.setOnClickListener {
            val message = getString(R.string.course_link)
            val sendIntent: Intent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.putExtra(Intent.EXTRA_TEXT, message)
                this.type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }

        buttonSupport.setOnClickListener {
            val message = getString(R.string.message_email)
            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SENDTO
                this.data = Uri.parse("mailto:")
                this.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_theme))
                this.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
                this.putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(shareIntent)
        }

        buttonUserAgreement.setOnClickListener {
            val uri = Uri.parse(getString(R.string.practicum_offer_link))
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        themeSwitcher.isChecked = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)

        }


    }

}