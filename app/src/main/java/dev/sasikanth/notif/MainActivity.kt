package dev.sasikanth.notif

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.sasikanth.notif.databinding.ActivityMainBinding
import dev.sasikanth.notif.shared.Option
import dev.sasikanth.notif.shared.OptionsBottomSheet
import dev.sasikanth.notif.shared.isNightMode
import dev.sasikanth.notif.utils.EventObserver
import dev.sasikanth.notif.utils.NotifPreferences
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private val mainViewModel: MainViewModel by viewModel()
    private val notifPreferences: NotifPreferences by inject()

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.historyFragment) {
                binding.appBarLabel.text = getString(R.string.history)
                binding.notifActionButton.text = getString(R.string.clear_history)
            } else {
                binding.appBarLabel.text = getString(R.string.current)
                binding.notifActionButton.text = getString(R.string.create)
            }
        }

        mainViewModel.showOptionsMenu.observe(this, EventObserver {
            val darkMode = Option(
                0,
                R.string.option_dark_mode,
                R.drawable.ic_noitf_dark_mode,
                isNightMode,
                true
            )
            val history = Option(1, R.string.option_history, R.drawable.ic_notif_history)
            val apps = Option(2, R.string.option_your_apps, R.drawable.ic_notif_apps)
            val about = Option(3, R.string.option_about, R.drawable.ic_noitf_about)

            OptionsBottomSheet()
                .addOption(darkMode, history, apps, about)
                .setOnOptionSelectedListener(OptionsBottomSheet.OnOptionSelected {
                    when (it.id) {
                        0 -> {
                            if (isNightMode) {
                                notifPreferences.themePreference = NotifPreferences.Theme.LIGHT
                            } else {
                                notifPreferences.themePreference = NotifPreferences.Theme.DARK
                            }
                        }
                        1 -> {
                            if (navController.currentDestination?.id != R.id.historyFragment) {
                                navController.navigate(R.id.actionHistoryFragment)
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
                // TODO: Launch create and pin bottom sheet
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
