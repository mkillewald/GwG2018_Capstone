package com.gameaholix.coinops.utility;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;

import com.gameaholix.coinops.R;

public class PromptUser {

    public static void displayAlert(Context context, int title, int message) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public static void displaySnackbar(CoordinatorLayout layout, int resId) {
        final Snackbar snackbar = Snackbar.make(layout, resId, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.dismiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });

        // get snackbar layout params and add 2 to bottom margin
        // this was needed to fully cover the AdMob banner when snackbar is displayed.
        FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params =
                (FrameLayout.LayoutParams) snackBarView.getChildAt(0).getLayoutParams();

        params.setMargins(params.leftMargin,
                params.topMargin,
                params.rightMargin,
                params.bottomMargin + 2);

        snackBarView.getChildAt(0).setLayoutParams(params);
        snackbar.show();
    }

}
