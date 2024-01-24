package com.example.migeba.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.datalogic.decode.configuration.IntentWedge;
import com.evrencoskun.tableviewsample.migeba_tableview.Dalageba_TableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.Shegroveba_TableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_Cell;

import java.util.List;

public class DecodeDalagebaIntentReceiver extends BroadcastReceiver {
    public static final String ACTION_BROADCAST_RECEIVER = "com.datalogic.examples.decode_action";
    public static final String CATEGORY_BROADCAST_RECEIVER = "com.datalogic.examples.decode_category";
    public static final String EXTRA_DATA_STRING = IntentWedge.EXTRA_BARCODE_STRING;

    @Override
    public void onReceive(Context context, Intent wedgeIntent) {

        String action = wedgeIntent.getAction();

        if (action.equals(ACTION_BROADCAST_RECEIVER)) {
            String barcode = wedgeIntent.getStringExtra(EXTRA_DATA_STRING);
            if (Dalageba_TableViewListener.mTableView == null) {
                return;
            } else if (Dalageba_TableViewListener.mTableView.getAdapter().getCellColumnItems(1).size() <= 0) {
                return;
            }
            if (barcode.length() > 0) {
                String series = barcode.split(" ")[0];
                List<Migeba_Cell> series_list = Dalageba_TableViewListener.mTableView.getAdapter().getCellColumnItems(1);
                for (int j = 0; j < series_list.size(); j++) {
                    if (series_list.get(j).getFilterableKeyword().equals(series)) {

                        Dalageba_TableViewListener.Edit_Card_Location(j, context);
                    }
                }

//                Dalageba_TableViewListener.Edit_Card_Location(0, context);
            }
        }
    }

    public static void showMessage(String message, Context context) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

