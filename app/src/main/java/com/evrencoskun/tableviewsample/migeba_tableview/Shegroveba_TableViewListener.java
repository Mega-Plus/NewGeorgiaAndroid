/*
 * MIT License
 *
 * Copyright (c) 2021 Evren Coşkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.evrencoskun.tableviewsample.migeba_tableview;

import static android.content.ContentValues.TAG;
import static com.example.migeba.MainActivity.CONN_EXECUTE_SQL;
import static com.example.migeba.MainActivity.CONN_RESULTSET_SQL;
import static com.example.migeba.MainActivity.VibrateForMs;
import static com.example.migeba.MainActivity.getAppContext;
import static com.example.migeba.Shegroveba.FINISH_SHEGROVEBA;
import static com.example.migeba.Shegroveba.getColumnHeaders;
import static com.example.migeba.Shegroveba.getRowHeaders;
import static com.example.migeba.Shegroveba.zednadebi_id;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.listener.ITableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.holder.Migeba_ColumnHeaderViewHolder;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_Cell;
import com.evrencoskun.tableviewsample.migeba_tableview.popup.Migeba_ColumnHeaderLongPressPopup;
import com.evrencoskun.tableviewsample.migeba_tableview.popup.Migeba_RowHeaderLongPressPopup;
import com.example.migeba.MainActivity;
import com.example.migeba.R;
import com.example.migeba.Shegroveba;
import com.example.migeba.Utils.DebugTools;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.platform.MaterialContainerTransform;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class Shegroveba_TableViewListener implements ITableViewListener {
    @NonNull
    private final Context context;
    @NonNull
    public static TableView mTableView;
    public static List<List<Migeba_Cell>> products_cache;
    public static StringBuilder changesQuery;
    public static List<String> savedProducts = new ArrayList<>();
    public static List<String> deletedProducts = new ArrayList<>();
    public static AlertDialog alertDialog;
    public static MaterialButton saveBtn;
    public static TextView shegroveba_progress;


    public static AutoCompleteTextView loca_tv;
    public static List<String> locationsList = new ArrayList<>();
    public static Integer currentRow = 1;

    public static String currentSerie = "";

    public static Boolean isEditing = false;


    public Shegroveba_TableViewListener(@NonNull TableView tableView) {
        this.context = tableView.getContext();
        this.mTableView = tableView;

        savedProducts = new ArrayList<>();
        deletedProducts = new ArrayList<>();
        changesQuery = new StringBuilder("");
    }


    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        Shegroveba.productEditButton.setVisibility(View.VISIBLE);
    }


    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        // Do what you want.
        Edit_Card_Shegroveba(row, context);
    }

    public static void Edit_Card_Shegroveba(Integer row, Context context) {

        if (mTableView == null) {
            return;
        } else if (mTableView.getAdapter().getCellColumnItems(1).size() <= 0) {
            return;
        }


        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }


        isEditing = true;
        currentRow = row;
        AlertDialog.Builder mb = new AlertDialog.Builder(context, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);

        mb.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.card_edit_shegroveba, null, false);
        v.setBackgroundColor(Color.TRANSPARENT);
        v.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        mb.setView(v);
        alertDialog = mb.create();


        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        alertDialog.show();
        TextInputLayout inputraodenoba = alertDialog.findViewById(R.id.card_edit_raodenoba);
        TextInputLayout inputraodenobayutshi = alertDialog.findViewById(R.id.card_edit_raodenobayutshi);
        TextInputLayout inputseriavada = alertDialog.findViewById(R.id.card_edit_seriavada);
        shegroveba_progress = alertDialog.findViewById(R.id.shegroveba_progress);
        shegroveba_progress.setText(savedProducts.size() + "/" + mTableView.getAdapter().getCellColumnItems(1).size());

        saveBtn = alertDialog.findViewById(R.id.edit_card_nextBtn);
        MaterialButton edit_card_next = alertDialog.findViewById(R.id.edit_card_nextAndSave);
        MaterialButton edit_card_p_id = alertDialog.findViewById(R.id.edit_card_p_id);
        MaterialButton close_dialog = alertDialog.findViewById(R.id.dialog_close);
//        MaterialButton edit_card_previous = alertDialog.findViewById(R.id.button_previous);
        MaterialButton edit_card_manufacturer = alertDialog.findViewById(R.id.edit_card_manufacturer);
        MaterialButton edit_card_country = alertDialog.findViewById(R.id.edit_card_country);
        Migeba_Cell migebaCell_manufacturer = (Migeba_Cell) mTableView.getAdapter().getCellItem(4, row);
        Migeba_Cell migebaCell_country = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
        edit_card_manufacturer.setText(migebaCell_manufacturer.getFilterableKeyword());
        edit_card_country.setText(migebaCell_country.getFilterableKeyword());

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEditing = false;
                alertDialog.dismiss();
            }
        });

        edit_card_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditCardNextProduct(row, saveBtn, context, alertDialog);
            }
        });

//        edit_card_previous.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Edit_Card_Shegroveba(row - 1, context);
//                alertDialog.cancel();
//                try {
//                    mTableView.setSelectedCell(mTableView.getSelectedColumn(), row - 1);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        if (row == 0) {
//            edit_card_previous.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    alertDialog.cancel();
//                }
//            });
        }
        int rowindex = mTableView.getAdapter().getRowHeaderRecyclerViewAdapter().getItemCount() - 1 + deletedProducts.size();

        if (row == rowindex) {
            if (savedProducts.size() == row) {
                edit_card_next.setText("დასრულება");
                edit_card_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    FINISH_MIGEBA();
                        alertDialog.cancel();
                    }
                });
            } else {
                edit_card_next.setText("შემდეგი");
                edit_card_next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        EditCardNextProduct(row, saveBtn, context, alertDialog);
                    }
                });
            }
        }


        TextView dasaxeleba = alertDialog.findViewById(R.id.name);
        TextView g_id = alertDialog.findViewById(R.id.g_id);
        Migeba_Cell migebaCell_name = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
        Migeba_Cell migebaCell_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, row);
        Migeba_Cell migebaCell_raodenobaYutshi = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
        Migeba_Cell migebaCell_location = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
        Migeba_Cell migebaCell_seria = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, row);
        currentSerie = migebaCell_seria.getFilterableKeyword();
        Migeba_Cell migebaCell_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
        Migeba_Cell g_p_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(9, row);
        Migeba_Cell sl_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(12, row);
        inputraodenoba.getEditText().setText(String.valueOf(migebaCell_raodenoba.getData()));
        inputraodenobayutshi.getEditText().setText(String.valueOf(migebaCell_raodenobaYutshi.getData()));
        inputseriavada.getEditText().setText("სერ: (" + String.valueOf(migebaCell_seria.getData()) + ") ვადა: (" + String.valueOf(migebaCell_vada.getData()) + ")");

        loca_tv = alertDialog.findViewById(R.id.location_autocomplete);
        loca_tv.setText(String.valueOf(migebaCell_location.getData()));

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // UTILITY FUNCTIONS
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//        MaterialButton button_seriebi_raod = alertDialog.findViewById(R.id.button_seriebi_raod);
        MaterialButton button_rezervi = alertDialog.findViewById(R.id.button_rezervi);
        MaterialButton button_dasax_raod = alertDialog.findViewById(R.id.button_dasax_raod);
        MaterialButton edit_card_delete = alertDialog.findViewById(R.id.button_delete);

        FrameLayout dialog_secondary_bg = alertDialog.findViewById(R.id.dialog_secondary_bg);
        FrameLayout dialog_secondary = alertDialog.findViewById(R.id.dialog_secondary);

        Migeba_Cell seria = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, row);
//        ResultSet query_seriebisraodenoba = CONN_RESULTSET_SQL("SELECT COUNT (*) FROM [A_PLUS].[dbo].[SELL] where SL_SERIA = '" + seria.getData().toString() + "'");
//
//        String seriebisraodenoba = "ვერ მოიძებნა";
//        try {
//            query_seriebisraodenoba.next();
//            seriebisraodenoba = query_seriebisraodenoba.getString(1);
//        } catch (Exception e) {
//            Log.e(TAG, "Edit_Card_Shegroveba: " + e.getMessage());
//        }


        ///SERIEBI RAODENOBA//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        MaterialContainerTransform transformOpenSeriebi = new MaterialContainerTransform();
//        transformOpenSeriebi.setStartView(button_seriebi_raod);
//        transformOpenSeriebi.setEndView(dialog_secondary);
//        transformOpenSeriebi.addTarget(dialog_secondary);
//        transformOpenSeriebi.setScrimColor(Color.TRANSPARENT);
////
        MaterialContainerTransform transformCloseSeriebi = new MaterialContainerTransform();


        MaterialContainerTransform transformOpenRezervi = new MaterialContainerTransform();
        transformOpenSeriebi.setStartView(button_rezervi);
        transformOpenSeriebi.setEndView(dialog_secondary);
        transformOpenSeriebi.addTarget(dialog_secondary);
        transformOpenSeriebi.setScrimColor(Color.TRANSPARENT);

        MaterialContainerTransform transformCloseRezervi = new MaterialContainerTransform();
        transformCloseSeriebi.setStartView(dialog_secondary);
        transformCloseSeriebi.setEndView(button_rezervi);
        transformCloseSeriebi.addTarget(button_rezervi);
        transformCloseSeriebi.setScrimColor(Color.TRANSPARENT);

        String finalRezervi = "";
        button_rezervi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button_rezervi.setVisibility(View.INVISIBLE);
                dialog_secondary_bg.setVisibility(View.VISIBLE);
                dialog_secondary.setVisibility(View.VISIBLE);
                TextView primary = dialog_secondary.findViewById(R.id.dialog_primaryText);
                TextView secondary = dialog_secondary.findViewById(R.id.dialog_secondaryText);
                ImageView icon = dialog_secondary.findViewById(R.id.dialog_icon);
                TextInputLayout dialog_secondaryEditText = dialog_secondary.findViewById(R.id.dialog_secondaryEditText);
                MaterialButton positiveButton = dialog_secondary.findViewById(R.id.dialog_positiveButton);
                MaterialButton negativeButton = dialog_secondary.findViewById(R.id.dialog_negativeButton);


                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformCloseSeriebi);
                        dialog_secondary_bg.setVisibility(View.INVISIBLE);
                        dialog_secondary.setVisibility(View.INVISIBLE);
                        button_rezervi.setVisibility(View.VISIBLE);
                    }
                });
                TextInputEditText rezervi_edittext = (TextInputEditText) dialog_secondaryEditText.getEditText();

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CONN_EXECUTE_SQL("update t1  set   [P_LOC_REZERVI]='" + rezervi_edittext.getText().toString() + "' from [A_PLUS].[dbo].[PRODUCT] as t1\n" +
                                "  left join A_PLUS.dbo.SELL on P_ID = SL_P_ID\n" +
                                "  where SL_SERIA = '" + migebaCell_seria.getData().toString() + "'");
                        TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformCloseSeriebi);
                        dialog_secondary_bg.setVisibility(View.INVISIBLE);
                        dialog_secondary.setVisibility(View.INVISIBLE);
                        button_rezervi.setVisibility(View.VISIBLE);
                    }
                });

                ResultSet query_rezervi = CONN_RESULTSET_SQL("SELECT DISTINCT top 1\n" +
                        "      [P_LOC_REZERVI]\n" +
                        "  FROM [A_PLUS].[dbo].[PRODUCT]\n" +
                        "  left join A_PLUS.dbo.SELL on P_ID = SL_P_ID\n" +
                        "  where SL_SERIA = '" + migebaCell_seria.getData().toString() + "'");


                String rezervi = "ვერ მოიძებნა";
                try {
                    while (query_rezervi.next()) {
                        if (query_rezervi.getString("P_LOC_REZERVI").equals("null")) return;
                        rezervi = query_rezervi.getString("P_LOC_REZERVI");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                positiveButton.setText("შენახვა");
                primary.setText("რეზერვი");
                secondary.setVisibility(View.GONE);
//                secondary.setText(sb.toString() != null ? sb.toString() : lokaciebi);

                dialog_secondaryEditText.setVisibility(View.VISIBLE);
                rezervi_edittext.setText(rezervi);

                rezervi_edittext.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if (charSequence.toString().endsWith("\n") && charSequence.toString().length() > 1) {
                            CONN_EXECUTE_SQL("update t1  set   [P_LOC_REZERVI]='" + charSequence.toString().substring(0, charSequence.toString().length() - 1) + "' from [A_PLUS].[dbo].[PRODUCT] as t1\n" +
                                    "  left join A_PLUS.dbo.SELL on P_ID = SL_P_ID\n" +
                                    "  where SL_SERIA = '" + migebaCell_seria.getData().toString() + "'");
                            TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformCloseSeriebi);
                            dialog_secondary_bg.setVisibility(View.INVISIBLE);
                            dialog_secondary.setVisibility(View.INVISIBLE);
                            button_rezervi.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });


                icon.setImageIcon(Icon.createWithResource(getAppContext(), R.drawable.ic_outline_list_24));
                TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformOpenSeriebi);

            }
        });


        ///DASAXELEBEBIS RAODENOBA//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        MaterialContainerTransform transformOpenDasaxebisraodenoba = new MaterialContainerTransform();
        transformOpenDasaxebisraodenoba.setStartView(button_dasax_raod);
        transformOpenDasaxebisraodenoba.setEndView(dialog_secondary);
        transformOpenDasaxebisraodenoba.addTarget(dialog_secondary);
        transformOpenDasaxebisraodenoba.setScrimColor(Color.TRANSPARENT);

        MaterialContainerTransform transformCloseDasaxebisraodenoba = new MaterialContainerTransform();
        transformCloseDasaxebisraodenoba.setStartView(dialog_secondary);
        transformCloseDasaxebisraodenoba.setEndView(button_dasax_raod);
        transformCloseDasaxebisraodenoba.addTarget(button_dasax_raod);
        transformCloseDasaxebisraodenoba.setScrimColor(Color.TRANSPARENT);

        button_dasax_raod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ResultSet query_dasaxraodenoba = CONN_RESULTSET_SQL("declare @SERIA varchar(50) = '" + migebaCell_seria.getData().toString() + "'\n" +
                        "        SELECT \n" +
                        "          G_ID, \n" +
                        "          G_P_ID, \n" +
                        "          (\n" +
                        "            isnull(G_QUANT, 0)- isnull(GACERIL, 0)- isnull(CHAMOCERIL, 0)- isnull(gakiduli, 0)+ isnull(DB_QUANT_SUM, 0)\n" +
                        "          ) as nasht\n" +
                        "        FROM (select * from [A_PLUS].[dbo].[GET]     where G_SERIA = @SERIA)as tt\n" +
                        "          left join (\n" +
                        "            SELECT \n" +
                        "              GACER_G_SERIA, \n" +
                        "              GACER_G_ZDN, \n" +
                        "              sum ([GACER_QUANT]) as 'GACERIL' \n" +
                        "              \n" +
                        "            FROM \n" +
                        "              [A_PLUS].[dbo].GACER \n" +
                        "              where GACER_G_SERIA = @SERIA\n" +
                        "            GROUP BY \n" +
                        "              GACER_G_SERIA, \n" +
                        "              GACER_G_ZDN\n" +
                        "          ) AS GACER2 ON G_SERIA = GACER2.GACER_G_SERIA \n" +
                        "          and G_ZDN = GACER2.GACER_G_ZDN \n" +
                        "          left join (\n" +
                        "            SELECT \n" +
                        "              CHAMOCER_G_SERIA, \n" +
                        "              CHAMOCER_G_ZDN, \n" +
                        "              sum ([CHAMOCER_QUANT]) as 'CHAMOCERIL' \n" +
                        "            FROM \n" +
                        "              [A_PLUS].[dbo].CHAMOCER \n" +
                        "              where CHAMOCER_G_SERIA = @SERIA\n" +
                        "            GROUP BY \n" +
                        "              CHAMOCER_G_SERIA, \n" +
                        "              CHAMOCER_G_ZDN\n" +
                        "          ) AS CHAMOCER2 ON G_SERIA = CHAMOCER2.CHAMOCER_G_SERIA \n" +
                        "          and G_ZDN = CHAMOCER2.CHAMOCER_G_ZDN \n" +
                        "          left JOIN (\n" +
                        "            SELECT \n" +
                        "              SL_SERIA, \n" +
                        "              SL_ZEDNAD, \n" +
                        "              sum ([SL_RAOD]) as 'gakiduli' \n" +
                        "            FROM \n" +
                        "              [A_PLUS].[dbo].sell \n" +
                        "               where SL_SERIA = @SERIA\n" +
                        "            GROUP BY \n" +
                        "              SL_SERIA, \n" +
                        "              SL_ZEDNAD\n" +
                        "          ) AS tsum ON G_SERIA = tsum.SL_SERIA \n" +
                        "          and G_ZDN = tsum.SL_ZEDNAD \n" +
                        "          left outer join (\n" +
                        "            SELECT \n" +
                        "              DB_G_SERIA, \n" +
                        "              DB_G_ZED, \n" +
                        "              sum (DB_QUANT) as 'DB_QUANT_SUM' \n" +
                        "            FROM \n" +
                        "              [A_PLUS].[dbo].DABRUNEBA \n" +
                        "               where DB_G_SERIA = @SERIA\n" +
                        "            GROUP BY \n" +
                        "              DB_G_SERIA, \n" +
                        "              DB_G_ZED\n" +
                        "          ) AS DABRUNEBA ON G_SERIA = DABRUNEBA.DB_G_SERIA \n" +
                        "          and G_ZDN = DABRUNEBA.DB_G_ZED \n" +
                        "          left join [A_PLUS].dbo.PRODUCT ON G_P_ID = P_ID \n" +
                        "        where \n" +
                        "          (\n" +
                        "            isnull(G_QUANT, 0)- isnull(GACERIL, 0)- isnull(CHAMOCERIL, 0)- isnull(gakiduli, 0)+ isnull(DB_QUANT_SUM, 0)\n" +
                        "          )> 0\n" +
                        "        order by \n" +
                        "          G_TIME");


                String dasaxelebebisraodenoba = "ნაშთი ვერ მოიძებნა";
                try {
                    query_dasaxraodenoba.next();
                    dasaxelebebisraodenoba = "მოიძებნა " + query_dasaxraodenoba.getString("nasht");
                } catch (Exception e) {
                    Log.e(TAG, "Edit_Card_Shegroveba: " + e.getMessage());
                }

                button_dasax_raod.setVisibility(View.INVISIBLE);
                dialog_secondary_bg.setVisibility(View.VISIBLE);
                dialog_secondary.setVisibility(View.VISIBLE);
                TextView primary = dialog_secondary.findViewById(R.id.dialog_primaryText);
                TextView secondary = dialog_secondary.findViewById(R.id.dialog_secondaryText);
                ImageView icon = dialog_secondary.findViewById(R.id.dialog_icon);
                MaterialButton positiveButton = dialog_secondary.findViewById(R.id.dialog_positiveButton);
                MaterialButton negativeButton = dialog_secondary.findViewById(R.id.dialog_negativeButton);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformCloseDasaxebisraodenoba);
                        dialog_secondary_bg.setVisibility(View.INVISIBLE);
                        dialog_secondary.setVisibility(View.INVISIBLE);
                        button_dasax_raod.setVisibility(View.VISIBLE);
                    }
                });

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformCloseDasaxebisraodenoba);
                        dialog_secondary_bg.setVisibility(View.INVISIBLE);
                        dialog_secondary.setVisibility(View.INVISIBLE);
                        button_dasax_raod.setVisibility(View.VISIBLE);
                    }
                });

                positiveButton.setText("OK");
                primary.setText("ნაშთი");
                secondary.setText(dasaxelebebisraodenoba);

                icon.setImageIcon(Icon.createWithResource(getAppContext(), R.drawable.ic_outline_list_24));
                TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformOpenDasaxebisraodenoba);

            }
        });


        ///FAQTURIS GAUQMEBA//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        MaterialContainerTransform transformOpenDeleteFacture = new MaterialContainerTransform();
        transformOpenDeleteFacture.setStartView(edit_card_delete);
        transformOpenDeleteFacture.setEndView(dialog_secondary);
        transformOpenDeleteFacture.addTarget(dialog_secondary);
        transformOpenDeleteFacture.setScrimColor(Color.TRANSPARENT);

        MaterialContainerTransform transformCloseDeleteFacture = new MaterialContainerTransform();
        transformCloseDeleteFacture.setStartView(dialog_secondary);
        transformCloseDeleteFacture.setEndView(edit_card_delete);
        transformCloseDeleteFacture.addTarget(edit_card_delete);
        transformCloseDeleteFacture.setScrimColor(Color.TRANSPARENT);


        edit_card_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edit_card_delete.setVisibility(View.INVISIBLE);
                dialog_secondary_bg.setVisibility(View.VISIBLE);
                dialog_secondary.setVisibility(View.VISIBLE);
                TextView primary = dialog_secondary.findViewById(R.id.dialog_primaryText);
                TextView secondary = dialog_secondary.findViewById(R.id.dialog_secondaryText);


                ImageView icon = dialog_secondary.findViewById(R.id.dialog_icon);
                MaterialButton positiveButton = dialog_secondary.findViewById(R.id.dialog_positiveButton);
                MaterialButton negativeButton = dialog_secondary.findViewById(R.id.dialog_negativeButton);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformCloseDeleteFacture);
                        dialog_secondary_bg.setVisibility(View.INVISIBLE);
                        dialog_secondary.setVisibility(View.INVISIBLE);
                        edit_card_delete.setVisibility(View.VISIBLE);
                    }
                });

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO sad rogor unda waishalos arvici
                        MainActivity.CONN_EXECUTE_SQL("DELETE FROM A_PLUS.dbo.SELL WHERE SL_ID = '" + sl_id.getData().toString() + "'");
                        Shegroveba.activity.finish();
                    }
                });

                positiveButton.setText("დიახ");
                primary.setText("ნამდვილად გსურთ ამ პროდუქტის გაუქმება?");
                secondary.setText("\n" + migebaCell_name.getData().toString());
                icon.setImageIcon(Icon.createWithResource(getAppContext(), R.drawable.ic_baseline_delete_outline_24));

                TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformOpenDeleteFacture);

            }
        });

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //unfocusable and disabled for autocomplete textview and other edit texts
        loca_tv.setEnabled(false);
        loca_tv.setFocusable(false);
        TextInputLayout inputlocation = alertDialog.findViewById(R.id.location_textinputlayout);
        inputlocation.setEnabled(false);
        inputlocation.setFocusable(false);
        inputraodenoba.setEnabled(false);
        inputraodenoba.setFocusable(false);
        inputraodenobayutshi.setEnabled(false);
        inputraodenobayutshi.setFocusable(false);
        inputseriavada.setEnabled(false);
        inputseriavada.setFocusable(false);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //if saveChanges string includes current product serial and product name, make saveBtn green tint
        if (savedProducts.contains(String.valueOf(row + 1))) {
            if (deletedProducts.contains(String.valueOf(row + 1))) {
                //deleted
//                saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5733")));
                edit_card_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5733")));
            } else {
                //saved
                saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
            }
        }


        edit_card_p_id.setText(String.valueOf(row + 1));
        dasaxeleba.setText(String.valueOf(migebaCell_name.getData()));

        edit_card_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MaterialAlertDialogBuilder usure = new MaterialAlertDialogBuilder(context);
                usure.setTitle("ნამდვილად გსურთ გაუქმება?");
                usure.setMessage("პროდუქტი " + migebaCell_name.getData().toString() + " დაიმალება სიიდან, და ფაქტურიდან წაიშლება");
                usure.setPositiveButton("კი", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DebugTools.print(String.valueOf(row + 1));
                        deleteProduct(String.valueOf((row + 1)), row);

                        edit_card_delete.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF5733")));
                    }
                });
                usure.setNegativeButton("უკან", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                usure.show();
            }
        });

        ExtendedFloatingActionButton scanner_button = v.findViewById(R.id.scanner_button);

        scanner_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Shesruleba(context, seria, row, saveBtn, alertDialog,shegroveba_progress);

            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Shesruleba(context, seria, row, saveBtn, alertDialog, shegroveba_progress);
            }
        });
    }

    private static void Shesruleba(Context context, Migeba_Cell seria, Integer row, MaterialButton saveBtn, AlertDialog mainAlertDialog, TextView shegroveba_progress) {
        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(context);
        mb.setTitle("დაასკანერეთ შტრიხკოდი");
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.card_scanner, null, false);
        mb.setView(v);
        TextInputEditText et_scanner = v.findViewById(R.id.et_scanner);
        et_scanner.requestFocus();
        mb.setPositiveButton("ძებნა", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (et_scanner.getText().toString().split(" ")[0].equals(seria.getData().toString())) {
                    //kaia
                    dialogInterface.dismiss();
                    DebugTools.print(String.valueOf(row + 1));
                    saveChanges(String.valueOf((row + 1)));
                    EditCardNextProduct(row, saveBtn, context, mainAlertDialog);
                    saveBtn.setIcon(context.getDrawable(R.drawable.ic_baseline_check_24));
                    saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                } else {
                    //cudia
                    VibrateForMs(500);
                    Toast.makeText(getAppContext(), "დასკანერდა სხვა პროდუქტი!", Toast.LENGTH_LONG).show();
                    et_scanner.setError("დასკანერდა სხვა პროდუქტი!");
                }

            }
        });
        mb.setNegativeButton("უკან", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog alertDialog = mb.create();
        alertDialog.show();

        et_scanner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().endsWith("\n")) {

                    if (et_scanner.getText().toString().split(" ")[0].equals(seria.getData().toString())) {
                        //kaia
                        alertDialog.dismiss();
                        DebugTools.print(String.valueOf(row + 1));
                        saveChanges(String.valueOf((row + 1)));
//                mTableView.set
                        saveBtn.setIcon(context.getDrawable(R.drawable.ic_baseline_check_24));
                        saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                    } else {
                        //cudia
                        VibrateForMs(500);
                        Toast.makeText(getAppContext(), "დასკანერდა სხვა პროდუქტი!", Toast.LENGTH_LONG).show();
                        et_scanner.setError("დასკანერდა სხვა პროდუქტი!");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public static void EditCardNextProduct(Integer row, @NonNull MaterialButton saveBtn, Context context, AlertDialog alertDialog) {
        //row = 1 means second product selected.

        DebugTools.print(String.valueOf(row));
        saveBtn.setIcon(context.getDrawable(R.drawable.ic_baseline_check_24));
        saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

        //total amount of usable rows
        int rowCount = mTableView.getAdapter().getRowHeaderRecyclerViewAdapter().getItemCount();

        //while next product is in savedProducts, skip it
        int nextrow = row + 1;
        while (savedProducts.contains(String.valueOf(nextrow + 1))) {
            nextrow++;
        }

        if (nextrow < rowCount) {
            Edit_Card_Shegroveba(nextrow, context);
        } else {
            //if save products size == all rows (all completed)
            if (savedProducts.size() == rowCount) {
                FINISH_SHEGROVEBA();
            } else {
                EditCardNextProduct(-1, saveBtn, context, alertDialog);
            }
        }

        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteProduct(String sl_id, int row) {

        if (!savedProducts.contains(sl_id)) {
            savedProducts.add(sl_id);
            shegroveba_progress.setText(savedProducts.size() + "/" + mTableView.getAdapter().getCellColumnItems(1).size());

        }

        if (!deletedProducts.contains(sl_id)) {
            deletedProducts.add(sl_id);
            shegroveba_progress.setText(savedProducts.size() + "/" + mTableView.getAdapter().getCellColumnItems(1).size());

        }

//        mTableView.hideRow(row);

//        mTableView.getRowHeaderRecyclerView().

        Log.e(TAG, "deleted " + sl_id);
    }

    public static void saveChanges(String sl_id) {

        if (!savedProducts.contains(sl_id)) {
            savedProducts.add(sl_id);
            shegroveba_progress.setText(savedProducts.size() + "/" + mTableView.getAdapter().getCellColumnItems(1).size());
        }

        Log.e(TAG, "savedProducts: " + savedProducts);
    }

    public static void SetTableViewAdapter(List<List<Migeba_Cell>> products_cache) {
        mTableView.getAdapter().setAllItems(getColumnHeaders(), getRowHeaders(zednadebi_id), products_cache);
    }

    public void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    /**
     * Called when user long press any cell item.
     *
     * @param cellView : Long Pressed Cell ViewHolder.
     * @param column   : X (Column) position of Long Pressed Cell item.
     * @param row      : Y (Row) position of Long Pressed Cell item.
     */
    @Override
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView, final int column,
                                  int row) {

    }

    /**
     * Called when user click any column header item.
     *
     * @param columnHeaderView : Clicked Column Header ViewHolder.
     * @param column           : X (Column) position of Clicked Column Header item.
     */
    @Override
    public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int
            column) {
        // Do what you want.
        try {
            mTableView.setSelectedCell(column, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Migeba_Cell migebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, 0);
//        migeba.setProductEditButtonText(cell.getData().toString(), true);
    }

    /**
     * Called when user double click any column header item.
     *
     * @param columnHeaderView : Clicked Column Header ViewHolder.
     * @param column           : X (Column) position of Clicked Column Header item.
     */
    @Override
    public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder columnHeaderView, int column) {
        // Do what you want.
        showToast("Column header  " + column + " has been double clicked.");
    }

    /**
     * Called when user long press any column header item.
     *
     * @param columnHeaderView : Long Pressed Column Header ViewHolder.
     * @param column           : X (Column) position of Long Pressed Column Header item.
     */
    @Override
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView, int
            column) {

        if (columnHeaderView instanceof Migeba_ColumnHeaderViewHolder) {
            // Create Long Press Popup
            Migeba_ColumnHeaderLongPressPopup popup = new Migeba_ColumnHeaderLongPressPopup(
                    (Migeba_ColumnHeaderViewHolder) columnHeaderView, mTableView);
            // Show
            popup.show();
        }
    }

    /**
     * Called when user click any Row Header item.
     *
     * @param rowHeaderView : Clicked Row Header ViewHolder.
     * @param row           : Y (Row) position of Clicked Row Header item.
     */
    @Override
    public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
        // Do whatever you want.
        showToast("Row header " + row + " has been clicked.");
    }

    /**
     * Called when user double click any Row Header item.
     *
     * @param rowHeaderView : Clicked Row Header ViewHolder.
     * @param row           : Y (Row) position of Clicked Row Header item.
     */
    @Override
    public void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {
        // Do whatever you want.
        showToast("Row header " + row + " has been double clicked.");
    }

    /**
     * Called when user long press any row header item.
     *
     * @param rowHeaderView : Long Pressed Row Header ViewHolder.
     * @param row           : Y (Row) position of Long Pressed Row Header item.
     */
    @Override
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView, int row) {

        // Create Long Press Popup
        Migeba_RowHeaderLongPressPopup popup = new Migeba_RowHeaderLongPressPopup(rowHeaderView, mTableView);
        // Show
        popup.show();
    }


    private void showToast(String p_strMessage) {
//        Toast.makeText(mContext, p_strMessage, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "showToast: " + p_strMessage);
    }
}
