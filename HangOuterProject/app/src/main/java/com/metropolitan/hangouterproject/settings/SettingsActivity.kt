package com.metropolitan.hangouterproject.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.metropolitan.hangouterproject.MainActivity
import com.metropolitan.hangouterproject.R
import com.metropolitan.hangouterproject.app_module.MyApplication
import com.metropolitan.hangouterproject.utils.UtilsNavigationStyle
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject


class SettingsActivity : AppCompatActivity() {

    @Inject
    lateinit var preferencesSettings: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (application as MyApplication).getSettingsComponent().inject(this)
        UtilsNavigationStyle.setApplicationTheme(this, preferencesSettings.getBoolean("themeKey", false))
        setContentView(R.layout.activity_settings)
        setupNavigation()
    }

    // Back button handling at toolbar on activity_settings (Back to MainActivity)
    override fun onSupportNavigateUp(): Boolean {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
        return true
    }

    // Backing to MainActivity
    override fun onBackPressed() {
        finish()
        startActivity(Intent(this, MainActivity::class.java))
    }

    // Attaching base context which is used for localization, method is called before onCreate
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocalizationHelper(newBase).wrap(newBase, (newBase.applicationContext as MyApplication)))
    }

    // Setting up settings bar
    @SuppressLint("PrivateResource")
    private fun setupNavigation() {
        supportFragmentManager.beginTransaction().replace(R.id.settingsActivity, SettingsFragment()).commit()
        setSupportActionBar(toolbar_settings)

        val upArrow: Drawable? = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material)
        upArrow?.setColorFilter(ContextCompat.getColor(this, R.color.backgroundOrangeTheme), PorterDuff.Mode.SRC_ATOP)
        supportActionBar?.setHomeAsUpIndicator(upArrow)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Settings"
        toolbar_settings.setTitleTextColor(Color.WHITE)
    }
}
