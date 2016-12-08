package com.example.rene.myarrow.misc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

public class MyDialog extends DialogFragment {

    /** Kuerzel fuers Logging. */
    private static final String TAG = MyDialog.class.getSimpleName();

    public static String DIALOG_ARGUMENTS = "not_ready_dialog_fragment_arguments";

    Context _context;
    String mMessage;
    String mTitle;

    public MyDialog() {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog(): Start");
        /** example pulling of arguments */
        Bundle bundle = getArguments();
        if (bundle!=null) {
            bundle.get(DIALOG_ARGUMENTS);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
        alertDialogBuilder.setTitle(mTitle);
        alertDialogBuilder.setMessage(mMessage);
        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });

        AlertDialog alert = alertDialogBuilder.create();
        //
        // disable cancel outside touch
        //
        alert.setCanceledOnTouchOutside(false);
        //
        // disable cancel on press back button
        //
        setCancelable(false);
        //
        // Ende
        //
        Log.d(TAG, "onCreateDialog(): End");
        return alert;
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach(): Start");
        super.onAttach(activity);
        _context = activity;
        Log.d(TAG, "onAttach(): End");
    }

    public void setMessage(String message){
        mMessage = message;
    }

    public void setTitle(String title){
        mTitle = title;
    }
}
