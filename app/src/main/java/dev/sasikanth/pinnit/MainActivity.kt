package dev.sasikanth.pinnit

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
import dev.sasikanth.pinnit.databinding.ActivityMainBinding
import dev.sasikanth.pinnit.di.injector
import dev.sasikanth.pinnit.di.viewModels
import dev.sasikanth.pinnit.options.OptionItem
import dev.sasikanth.pinnit.options.OptionsBottomSheet
import dev.sasikanth.pinnit.utils.EventObserver
import dev.sasikanth.pinnit.utils.PinnitPreferences
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var pinnitPreferences: PinnitPreferences

  private lateinit var navController: NavController
  private var permissionDialog: AlertDialog? = null

  private val mainViewModel by viewModels { injector.mainViewModel }

  override fun onCreate(savedInstanceState: Bundle?) {
    setTheme(R.style.Theme_Pinnit_Main)
    injector.inject(this)
    super.onCreate(savedInstanceState)

    permissionDialog = MaterialAlertDialogBuilder(this)
        .setTitle(R.string.missing_permissions_title)
        .setMessage(R.string.missing_permissions_desc)
        .setCancelable(false)
        .setPositiveButton(R.string.grant_permission) { _, _ ->
          openNotificationSettings()
        }
        .setNegativeButton(R.string.notif_permission_dialog_negative_action_DEPRECATED) { _, _ ->
          finish()
        }
        .create()

    val binding: ActivityMainBinding =
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    binding.mainViewModel = mainViewModel
    binding.lifecycleOwner = this

    navController = findNavController(R.id.nav_host_fragment)

    navController.addOnDestinationChangedListener { _, destination, _ ->
      when (destination.id) {
        R.id.historyFragment -> {
          binding.apply {
            appBarLabel.text = getString(R.string.history)
            bottomAppBar.setPageActionButtonTitle(R.string.create)
            bottomAppBar.isTooltipVisible(false)
            binding.clearHistory.isVisible = true
          }
        }
        R.id.appsFragment -> {
          binding.apply {
            appBarLabel.text = getString(R.string.your_apps)
            bottomAppBar.isTooltipVisible(true)
            binding.clearHistory.isVisible = false
          }
        }
      }
    }

    binding.clearHistory.setOnClickListener {
      MaterialAlertDialogBuilder(this)
          .setTitle(getString(R.string.clear_history_dialog_title))
          .setMessage(getString(R.string.clear_history_dialog_subtitle))
          .setPositiveButton(R.string.yes) { _, _ ->
            mainViewModel.deleteUnPinned()
          }
          .setNegativeButton(R.string.no) { _, _ ->
          }
          .show()
    }

    mainViewModel.pinnitDeleted.observe(this, EventObserver { pinnitItem ->
      Snackbar.make(binding.mainRootView, R.string.notification_deleted, Snackbar.LENGTH_LONG)
          .setAnchorView(binding.bottomAppBar)
          .setAction(R.string.undo) {
            mainViewModel.insertNotification(pinnitItem)
          }
          .show()
    })

    mainViewModel.showOptionsMenu.observe(this, EventObserver {
      val history = OptionItem(
          id = R.id.historyFragment,
          title = R.string.history,
          icon = R.drawable.sld_history,
          isSelected = navController.currentDestination?.id == R.id.historyFragment
      )
      val apps = OptionItem(
          id = R.id.appsFragment,
          title = R.string.your_apps,
          icon = R.drawable.sld_apps,
          isSelected = navController.currentDestination?.id == R.id.appsFragment
      )
      val about = OptionItem(
          R.id.aboutFragment,
          R.string.about,
          R.drawable.sld_about
      )

      OptionsBottomSheet()
          .addOptions(
              history,
              apps,
              about
          )
          .setOnOptionSelectedListener { optionItem ->
            if (optionItem.id != R.id.aboutFragment) {
              navController.navigate(optionItem.id)
            } else {
              showAbout()
            }
          }
          .show(supportFragmentManager)
    })

    mainViewModel.notifAction.observe(this, EventObserver {
      // TODO: Launch create page
    })
  }

  private fun showAbout() {
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

  override fun onStart() {
    super.onStart()
    if (!isNotificationAccessGiven()) {
      if (!permissionDialog!!.isShowing) {
        permissionDialog!!.show()
      }
    } else {
      if (permissionDialog!!.isShowing) {
        permissionDialog!!.cancel()
      }
    }
  }

  override fun onDestroy() {
    permissionDialog!!.cancel()
    permissionDialog = null
    super.onDestroy()
  }

  private fun isNotificationAccessGiven(): Boolean {
    return NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
  }

  private fun openNotificationSettings() {
    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
  }
}
