package com.dogadoption.app.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Helper that asks for storage/media permission before opening the image picker.
 * Usage: call checkAndPickImage() when the user taps the photo area.
 */
public class ImagePickerHelper {

    public static final int PERMISSION_REQUEST_CODE = 1001;

    private final Activity activity;
    private final ActivityResultLauncher<Intent> launcher;

    public ImagePickerHelper(Activity activity, ActivityResultLauncher<Intent> launcher) {
        this.activity = activity;
        this.launcher = launcher;
    }

    /** Call this from the photo tap listener. */
    public void checkAndPickImage() {
        String permission = getRequiredPermission();
        if (ContextCompat.checkSelfPermission(activity, permission)
                == PackageManager.PERMISSION_GRANTED) {
            openPicker();
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
            // User previously denied — show explanation dialog first
            new AlertDialog.Builder(activity)
                    .setTitle("Photo Permission Needed")
                    .setMessage("This app needs access to your photos to set a profile or dog picture. Please allow access to continue.")
                    .setPositiveButton("Allow", (d, w) ->
                            ActivityCompat.requestPermissions(activity,
                                    new String[]{permission}, PERMISSION_REQUEST_CODE))
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            // First time asking — request directly
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Call from onRequestPermissionsResult in your Activity.
     * Returns true if permission was granted and picker was opened.
     */
    public boolean onPermissionResult(int requestCode, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openPicker();
                return true;
            } else {
                new AlertDialog.Builder(activity)
                        .setTitle("Permission Denied")
                        .setMessage("Cannot access photos without permission. You can enable it in Settings > App Permissions.")
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
        return false;
    }

    private void openPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        launcher.launch(intent);
    }

    /** Returns the correct permission string for the device's Android version. */
    public static String getRequiredPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    /** Persist the URI so it survives app restarts. */
    public static void persistUri(Activity activity, Uri uri) {
        try {
            activity.getContentResolver().takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception ignored) {}
    }
}
