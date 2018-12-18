package com.gameaholix.coinops;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

public abstract class BaseDialogFragment extends DialogFragment {
    private static final String TAG = BaseDialogFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Hide keyboard after touch event occurs outside of EditText in DialogFragment
        // Solution used from:
        // https://stackoverflow.com/questions/16024297/is-there-an-equivalent-for-dispatchtouchevent-from-activity-in-dialog-or-dialo
        if (getActivity() != null) {
            return new Dialog(getActivity(), getTheme()) {
                @Override
                public boolean dispatchTouchEvent(@NonNull MotionEvent motionEvent) {
                    if (getCurrentFocus() != null) {
                        InputMethodManager imm =
                                (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                        getCurrentFocus().clearFocus();
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }
            };
        } else {
            return super.onCreateDialog(savedInstanceState);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // set width and height of this DialogFragment, code block used from
        // https://stackoverflow.com/questions/12478520/how-to-set-dialogfragments-width-and-height
        if (getShowsDialog() && getDialog().getWindow() != null) {
            ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }
    }

    public boolean textInputIsValid(String inputText) {
        boolean result = true;

        // TODO: possibly add more validation checks, and return false if any one of them fails.
        if (TextUtils.isEmpty(inputText)) {
            Log.d(TAG, "User input was blank or empty.");
            result = false;
        }

        return result;
    }

    public void hideKeyboard(TextView view) {
        InputMethodManager imm = (InputMethodManager) view
                .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
