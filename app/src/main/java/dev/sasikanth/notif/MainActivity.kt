package dev.sasikanth.notif

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dev.sasikanth.notif.databinding.ActivityMainBinding
import dev.sasikanth.notif.di.injector
import dev.sasikanth.notif.di.viewModels
import dev.sasikanth.notif.shared.Option.OptionItem
import dev.sasikanth.notif.shared.Option.OptionSeparator
import dev.sasikanth.notif.shared.OptionsBottomSheet
import dev.sasikanth.notif.shared.isNightMode
import dev.sasikanth.notif.utils.EventObserver
import dev.sasikanth.notif.utils.NotifPreferences
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var notifPreferences: NotifPreferences

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
        super.onCreate(savedInstanceState)
        injector.inject(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.currentFragment -> {
                    binding.apply {
                        notifActionSearch.isVisible = true
                        notifActionButton.isVisible = true
                        tooltip.isVisible = false
                        appBarLabel.text = getString(R.string.current)
                        notifActionButton.text = getString(R.string.create)
                    }
                }
                R.id.historyFragment -> {
                    binding.apply {
                        notifActionSearch.isVisible = true
                        notifActionButton.isVisible = true
                        tooltip.isVisible = false
                        appBarLabel.text = getString(R.string.history)
                        notifActionButton.text = getString(R.string.clear_history)
                    }
                }
                R.id.appsFragment -> {
                    binding.apply {
                        notifActionSearch.isVisible = false
                        notifActionButton.isVisible = false
                        tooltip.isVisible = true
                        appBarLabel.text = getString(R.string.apps)
                    }
                }
            }
        }

        mainViewModel.notifDeleted.observe(this, EventObserver { notifItem ->
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
                icon = R.drawable.ic_notif_current,
                isSelected = navController.currentDestination?.id == R.id.currentFragment
            )
            val apps = OptionItem(
                id = 2,
                title = R.string.option_your_apps,
                icon = R.drawable.ic_notif_apps,
                isSelected = navController.currentDestination?.id == R.id.appsFragment
            )
            val historyNotifs = OptionItem(
                id = 3,
                title = R.string.option_history,
                icon = R.drawable.ic_notif_history,
                isSelected = navController.currentDestination?.id == R.id.historyFragment
            )
            val about = OptionItem(4, R.string.option_about, R.drawable.ic_noitf_about)
            val optionSeparator = OptionSeparator
            val darkMode = OptionItem(
                5,
                R.string.option_dark_mode,
                R.drawable.ic_noitf_dark_mode,
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
                            navController.navigate(R.id.actionHistoryFragment)
                        }
                        4 -> {
                            val dialog = MaterialAlertDialogBuilder(this).apply {
                                setView(R.layout.notif_about_dialog)
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
                                            getString(R.string.support_subject, BuildConfig.VERSION_NAME)
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
                                notifPreferences.themePreference = NotifPreferences.Theme.LIGHT
                            } else {
                                notifPreferences.themePreference = NotifPreferences.Theme.DARK
                            }
                        }
                    }
                })
                .show(supportFragmentManager)
        })

        mainViewModel.notifAction.observe(this, EventObserver {
            if (navController.currentDestination?.id == R.id.historyFragment) {
                mainViewModel.deleteUnPinnedNotifs()
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
        if (!NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)) {
            return false
        }
        return true
    }

    private fun openNotificationSettings() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}
