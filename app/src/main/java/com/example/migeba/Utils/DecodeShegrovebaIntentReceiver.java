package com.example.migeba.Utils;

import static com.evrencoskun.tableviewsample.migeba_tableview.Shegroveba_TableViewListener.currentSerie;
import static com.evrencoskun.tableviewsample.migeba_tableview.Shegroveba_TableViewListener.isEditing;
import static com.evrencoskun.tableviewsample.migeba_tableview.Shegroveba_TableViewListener.saveBtn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import com.datalogic.decode.configuration.IntentWedge;
import com.evrencoskun.tableviewsample.migeba_tableview.Shegroveba_TableViewListener;

public class DecodeShegrovebaIntentReceiver extends BroadcastReceiver {
    public static final String ACTION_BROADCAST_RECEIVER = "com.datalogic.examples.decode_action";
    public static final String CATEGORY_BROADCAST_RECEIVER = "com.datalogic.examples.decode_category";
    public static final String EXTRA_DATA_STRING = IntentWedge.EXTRA_BARCODE_STRING;

    @Override
    public void onReceive(Context context, Intent wedgeIntent) {

        String action = wedgeIntent.getAction();

        if (action.equals(ACTION_BROADCAST_RECEIVER)) {
            String barcode = wedgeIntent.getStringExtra(EXTRA_DATA_STRING);
            if (Shegroveba_TableViewListener.mTableView == null) {
                return;
            } else if (Shegroveba_TableViewListener.mTableView.getAdapter().getCellColumnItems(1).size() <= 0) {
                return;
            }
            if (barcode.length() > 0) {
                String series = barcode.split(" ")[0];

                if (isEditing) {
                    if (series.equals(currentSerie))
                        saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                    saveBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Shegroveba_TableViewListener.saveChanges(String.valueOf((Shegroveba_TableViewListener.currentRow + 1)));
                            Shegroveba_TableViewListener.EditCardNextProduct(Shegroveba_TableViewListener.currentRow, saveBtn, context, null);
                        }
                    });
                }
//                Shegroveba_TableViewListener.Edit_Card_Shegroveba(0, context);
            }
        }
    }

    public static void showMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

