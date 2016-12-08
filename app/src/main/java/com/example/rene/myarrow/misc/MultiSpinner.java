package com.example.rene.myarrow.misc;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

/**
 * Created by nily on 13.01.2016.
 */
public class MultiSpinner extends Spinner implements
        DialogInterface.OnMultiChoiceClickListener, DialogInterface.OnCancelListener {

        /** Kuerzel fuers Logging. */
        private static final String TAG = MultiSpinner.class.getSimpleName();

        private List<String> items;
        private boolean[] selected;
        private String defaultText;
        private MultiSpinnerListener listener;

        public MultiSpinner(Context context) {
            super(context);
        }

        public MultiSpinner(Context arg0, AttributeSet arg1) {
            super(arg0, arg1);
        }

        public MultiSpinner(Context arg0, AttributeSet arg1, int arg2) {
            super(arg0, arg1, arg2);
        }

        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            selected[which] = isChecked;
        }

        @Override
        public void onCancel(DialogInterface dialog) {
            // refresh text on spinner
            StringBuffer spinnerBuffer = new StringBuffer();
            boolean someUnselected = false;
            for (int i = 0; i < items.size(); i++) {
                Log.d(TAG, "onCancel(): items[0] - " + items.get(i));
                if (selected[i]) {
                    spinnerBuffer.append(items.get(i));
                    spinnerBuffer.append(", ");
                } else {
                    someUnselected = true;
                }
            }
            Log.d(TAG, "onCancel(): spinnerText erstellen");
            String spinnerText;
            if (someUnselected) {
                spinnerText = spinnerBuffer.toString();
                if (spinnerText.length() > 2)
                    spinnerText = spinnerText.substring(0, spinnerText.length() - 2);
            } else {
                spinnerText = defaultText;
            }
            Log.d(TAG, "onCancel(): spinnerText - " + spinnerText);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item,
                    new String[] { spinnerText });
            Log.d(TAG, "onCancel(): setAdapter");
            setAdapter(adapter);
            Log.d(TAG, "onCancel(): selected - " + selected.length);
            for (int n=0; n<(selected.length); n++){
                Log.d(TAG, "onCancel(): selected - " + selected[n]);
            }
            listener.onItemsSelected(selected);
        }

        @Override
        public boolean performClick() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMultiChoiceItems(
                    items.toArray(new CharSequence[items.size()]), selected, this);
            builder.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            builder.setOnCancelListener(this);
            builder.show();
            return true;
        }

        public void setItems(List<String> items, String allText,
                             MultiSpinnerListener listener) {
            this.items = items;
            this.defaultText = allText;
            this.listener = listener;

            // all unselected by default
            selected = new boolean[items.size()];
            for (int i = 0; i < selected.length; i++)
                selected[i] = false;

            // all text on the spinner
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                    android.R.layout.simple_spinner_item, new String[] { allText });
            setAdapter(adapter);
        }

        public interface MultiSpinnerListener {
            void onItemsSelected(boolean[] selected);
        }
    }
