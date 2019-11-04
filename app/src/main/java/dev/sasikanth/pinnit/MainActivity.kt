package dev.sasikanth.pinnit

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dev.sasikanth.pinnit.databinding.ActivityMainBinding
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.di.viewModels
import dev.sasikanth.pinnit.shared.Option.OptionItem
import dev.sasikanth.pinnit.shared.Option.OptionSeparator
import dev.sasikanth.pinnit.shared.OptionsBottomSheet
import dev.sasikanth.pinnit.shared.isNightMode
import dev.sasikanth.pinnit.utils.EventObserver
import dev.sasikanth.pinnit.utils.PinnitPreferences
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var pinnitPreferences: PinnitPreferences

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val mainViewModel by viewModels { injector.mainViewModel }
    private val notifPermissionDialog: AlertDialog by lazy {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.notif_permission_dialog_title)
            .setMessage(R.string.notif_permission_dialog_content)
            .setCancelable(false)
            .setPositiveButton(R.string.notif_permission_dialog_positive_action) { _, _ ->
                openNotificationSettings()
            }
            .setNegativeButton(R.string.notif_permission_dialog_negative_action) { _, _ ->
                finish()
            }
            .create()
    }

    override fun onStart() {
        super.onStart()
        if (!isNotificationAccessGiven()) {
            if (!notifPermissionDialog.isShowing) {
                notifPermissionDialog.show()
            }
        } else {
            if (notifPermissionDialog.isShowing) {
                notifPermissionDialog.cancel()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notifPermissionDialog.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Pinnit_Main)
        injector.inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.currentFragment -> {
                    binding.apply {
                        appBarLabel.text = getString(R.string.current)
                        bottomAppBar.setPageActionButtonTitle(R.string.create)
                        bottomAppBar.isTooltipVisible(false)
                    }
                }
                R.id.historyFragment -> {
                    binding.apply {
                        appBarLabel.text = getString(R.string.history)
                        bottomAppBar.setPageActionButtonTitle(R.string.clear_history)
                        bottomAppBar.isTooltipVisible(false)
                    }
                }
                R.id.appsFragment -> {
                    binding.apply {
                        appBarLabel.text = getString(R.string.apps)
                        bottomAppBar.isTooltipVisible(true)
                    }
                }
            }
        }

        mainViewModel.pinnitDeleted.observe(this, EventObserver { notifItem ->
            Snackbar.make(binding.mainRootView, R.string.notif_deleted, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.bottomAppBar)
                .setAction(R.string.undo) {
                    mainViewModel.saveNotif(notifItem)
                }
                .show()
        })

        mainViewModel.showOptionsMenu.observe(this, EventObserver {

            val currentNotifs = OptionItem(
                id = 1,
                title = R.string.current,
                icon = R.drawable.ic_pinnit_current,
                isSelected = navController.currentDestination?.id == R.id.currentFragment
            )
            val apps = OptionItem(
                id = 2,
                title = R.string.option_your_apps,
                icon = R.drawable.ic_pinnit_apps,
                isSelected = navController.currentDestination?.id == R.id.appsFragment
            )
            val historyNotifs = OptionItem(
                id = 3,
                title = R.string.history,
                icon = R.drawable.ic_pinnit_history,
                isSelected = navController.currentDestination?.id == R.id.historyFragment
            )
            val about = OptionItem(4, R.string.option_about, R.drawable.ic_pinnit_about)
            val optionSeparator = OptionSeparator
            val darkMode = OptionItem(
                5,
                R.string.option_dark_mode,
                R.drawable.ic_pinnit_dark_mode,
                isNightMode,
                true
            )

            OptionsBottomSheet()
                .addOption(
                    currentNotifs, apps, historyNotifs, about, optionSeparator, darkMode
                )
                .setOnOptionSelectedListener(OptionsBottomSheet.OnOptionSelected {
                    when (it.id) {
                        1 -> {
                            navController.navigate(R.id.currentFragment)
                        }
                        2 -> {
                            navController.navigate(R.id.appsFragment)
                        }
                        3 -> {
                            navController.navigate(R.id.historyFragment)
                        }
                        4 -> {
                            val dialog = MaterialAlertDialogBuilder(this).apply {
                                setView(R.layout.pinnit_about_dialog)
                            }.create()

                            if (!dialog.isShowing) {
                                dialog.show()
                                dialog.findViewById<AppCompatTextView>(R.id.app_version)?.text =
                                    getString(R.string.app_version, BuildConfig.VERSION_NAME)
                                dialog.findViewById<MaterialButton>(R.id.contact_support)
                                    ?.setOnClickListener {
                                        val intent = Intent(Intent.ACTION_SEND)
                                        intent.type = "text/plain"
                                        intent.putExtra(
                                            Intent.EXTRA_EMAIL,
                                            arrayOf("contact@msasikanth.com")
                                        )
                                        intent.putExtra(
                                            Intent.EXTRA_SUBJECT,
                                            getString(
                                                R.string.support_subject,
                                                BuildConfig.VERSION_NAME
                                            )
                                        )
                                        startActivity(
                                            Intent.createChooser(
                                                intent,
                                                getString(R.string.send_email)
                                            )
                                        )
                                    }
                            }
                        }
                        5 -> {
                            if (isNightMode) {
                                pinnitPreferences.themePreference = PinnitPreferences.Theme.LIGHT
                            } else {
                                pinnitPreferences.themePreference = PinnitPreferences.Theme.DARK
                            }
                        }
                    }
                })
                .show(supportFragmentManager)
        })

        mainViewModel.notifAction.observe(this, EventObserver {
            if (navController.currentDestination?.id == R.id.historyFragment) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(getString(R.string.clear_history_dialog_title))
                    .setMessage(getString(R.string.clear_history_dialog_subtitle))
                    .setPositiveButton(R.string.yes) { _, _ ->
                        mainViewModel.deleteUnPinnedNotifs()
                    }
                    .setNegativeButton(R.string.no) { _, _ ->
                    }
                    .show()
            } else {
                // TODO: Launch create page
            }
        })
    }

    override fun onBackPressed() {
        if (navController.currentDestination?.id != R.id.currentFragment) {
            navController.navigate(R.id.currentFragment)
        } else {
            super.onBackPressed()
        }
    }

    private fun isNotificationAccessGiven(): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
    }

    private fun openNotificationSettings() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}
