package com.scifi.storyapp.view.utils

import android.content.Context
import android.location.Geocoder
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.scifi.storyapp.R
import com.scifi.storyapp.databinding.CustomAlertBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object InterfaceUtils {
    fun showAlert(
        context: Context,
        message: String?,
        isWarning: Boolean = false,
        primaryButtonText: String? = context.getString(R.string.ok),
        onPrimaryButtonClick: (() -> Unit)? = null,
        secondaryButtonText: String? = null,
        onSecondaryButtonClick: (() -> Unit)? = null,
    ) {
        val dialogView = CustomAlertBinding.inflate(LayoutInflater.from(context))
        val alertDialog = AlertDialog.Builder(context)
            .setView(dialogView.root)
            .setCancelable(false)
            .create()
        dialogView.alertMessage.text = message
        dialogView.btnPrimary.text = primaryButtonText
        dialogView.btnPrimary.setTextColor(
            ContextCompat.getColor(
                context,
                if (isWarning) R.color.warning else R.color.green_600
            )
        )
        dialogView.btnPrimary.setOnClickListener {
            alertDialog.dismiss()
            onPrimaryButtonClick?.invoke()
        }
        if (secondaryButtonText != null) {
            dialogView.btnSecondary.visibility = View.VISIBLE
            dialogView.btnSecondary.text = secondaryButtonText
            dialogView.btnSecondary.setOnClickListener {
                alertDialog.dismiss()
                onSecondaryButtonClick?.invoke()
            }
        } else {
            dialogView.btnSecondary.visibility = View.GONE
        }
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    fun showLoading(view: View, isLoading: Boolean) {
        val progressCircular: View? = view.findViewById(R.id.progress_circular)
        progressCircular?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    fun formatToRelativeTime(createdAt: String): CharSequence? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        try {
            val date = format.parse(createdAt)
            date?.let {
                val now = System.currentTimeMillis()
                return DateUtils.getRelativeTimeSpanString(
                    date.time,
                    now,
                    DateUtils.MINUTE_IN_MILLIS
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun formatLocation(lat: Double, lon: Double, context: Context): String? {
        return try {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val addressList = geoCoder.getFromLocation(lat, lon, 1)
            val address = addressList?.get(0)

            address?.adminArea?.let {
                "$it, ${address.countryName ?: ""}"
            }

        } catch (e: IOException) {
            e.printStackTrace()
            null
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
    }
}