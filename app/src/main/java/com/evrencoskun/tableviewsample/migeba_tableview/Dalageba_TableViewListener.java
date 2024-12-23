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
import static com.example.migeba.Dalageba.getColumnHeaders;
import static com.example.migeba.Dalageba.getRowHeaders;
import static com.example.migeba.Dalageba.uploadDalageba;
import static com.example.migeba.MainActivity.CONN_EXECUTE_SQL;
import static com.example.migeba.MainActivity.CONN_RESULTSET_SQL;
import static com.example.migeba.MainActivity.ConnectAndPrint;
import static com.example.migeba.MainActivity.SELECTED_USER;
import static com.example.migeba.MainActivity.USER_ID;
import static com.example.migeba.MainActivity.getAppContext;
import static com.example.migeba.Utils.DebugTools.print;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.listener.ITableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.holder.Migeba_ColumnHeaderViewHolder;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_Cell;
import com.evrencoskun.tableviewsample.migeba_tableview.popup.Migeba_ColumnHeaderLongPressPopup;
import com.evrencoskun.tableviewsample.migeba_tableview.popup.Migeba_RowHeaderLongPressPopup;
import com.example.migeba.Dalageba;
import com.example.migeba.LocationChooser;
import com.example.migeba.R;
import com.example.migeba.Utils.geotranslate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.platform.MaterialContainerTransform;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Dalageba_TableViewListener implements ITableViewListener {
    @NonNull
    private final Context context;
    @NonNull
    public static TableView mTableView;
    public static List<List<Migeba_Cell>> products_cache;
    public static StringBuilder changesQuery;
    public static String LOCATION_SEARCH;
    public static String LOCATION_CHANGE_QUERY;
    public static List<String> savedProducts = new ArrayList<>();


    public static AutoCompleteTextView loca_tv;
    public static List<String> locationsList = new ArrayList<>();
    public static AlertDialog alertDialog;


    public Dalageba_TableViewListener(@NonNull TableView tableView) {
        this.context = tableView.getContext();
        this.mTableView = tableView;
        savedProducts = new ArrayList<>();
        changesQuery = new StringBuilder("");
    }


    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        // Do what you want.
//        showToast("Cell " + column + " " + row + " has been clicked.");
//        Migeba_Cell selectedMigebaCell = null;
//        if (column == 0)
//            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
//        if (column == 1)
//            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, row);
//        if (column == 2)
//            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
//        if (column == 3)
//            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, row);
//        if (column == 4)
//            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(4, row);
//        if (column == 5)
//            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);

//        String productName = selectedMigebaCell.getFilterableKeyword();
//        if (productName.length() > 0) {
//        migeba.setProductEditButtonText();
//        } else {
//            migeba.setProductEditButtonText("", false);
//        }

    }


    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        if (!Dalageba.tableViewFilter.getFilterItems().isEmpty()) {
            String filter = Dalageba.tableViewFilter.getFilterItems().get(0).getFilter();
            Migeba_Cell ro = ((Migeba_Cell) Dalageba.mTableView.getAdapter().getCellRowItems(row).get(0));
            Dalageba.tableViewFilter.set("");
            int i = Dalageba.mTableView.getAdapter().getCellColumnItems(0).indexOf(ro);
            Dalageba.tableViewFilter.set(filter);
            System.out.println(i);
            Edit_Card_Location(i, context);
        } else {
            Edit_Card_Location(row, context);
        }
    }

    public static void Edit_Card_Location(Integer row, Context context) {
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
        Context mContext = mTableView.getContext();
        AlertDialog.Builder mb = new AlertDialog.Builder(mContext, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.card_edit_dalageba, null, false);
        v.setBackgroundColor(Color.TRANSPARENT);
        v.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        mb.setView(v);
        AlertDialog mainAlertDialog = mb.create();
        mainAlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mainAlertDialog.show();
        TextInputLayout inputseria = mainAlertDialog.findViewById(R.id.card_edit_anagceri);
        TextInputLayout inputvada = mainAlertDialog.findViewById(R.id.vada);
        TextInputLayout inputraodenoba = mainAlertDialog.findViewById(R.id.card_edit_raodenoba);
        TextInputLayout inputmwarmoebeli = mainAlertDialog.findViewById(R.id.card_edit_mwarmoebeli);
        TextInputLayout inputraodenobayutshi = mainAlertDialog.findViewById(R.id.card_edit_raodenobayutshi);
        TextInputLayout inputqveyana = mainAlertDialog.findViewById(R.id.card_edit_qveyana);
        TextInputLayout inputtemp = mainAlertDialog.findViewById(R.id.card_edit_temp);
        TextInputLayout inputsabechidraodenoba = mainAlertDialog.findViewById(R.id.card_edit_sabechdiraodenoba);
        TextInputLayout inputnumerusi = mainAlertDialog.findViewById(R.id.card_edit_numerusi);



//        inputvada.setVisibility(View.GONE);
//        inputraodenoba.setVisibility(View.GONE);
        inputmwarmoebeli.setVisibility(View.GONE);
        inputraodenobayutshi.setVisibility(View.GONE);
        inputqveyana.setVisibility(View.GONE);
        inputtemp.setVisibility(View.GONE);
        inputnumerusi.setVisibility(View.GONE);

        TextView seria = mainAlertDialog.findViewById(R.id.location_name);
        TextView g_id = mainAlertDialog.findViewById(R.id.g_id);
        Migeba_Cell migebaCell_name = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
        Migeba_Cell migebaCell_seria = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, row);
        Migeba_Cell migebaCell_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
        Migeba_Cell migebaCell_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, row);
//        Migeba_Cell migebaCell_mwarmoebeli = (Migeba_Cell) mTableView.getAdapter().getCellItem(4, row);
        Migeba_Cell migebaCell_temp = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
//        Migeba_Cell migebaCell_raodenobaYutshi = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
//        Migeba_Cell migebaCell_qveyana = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
        Migeba_Cell migebaCell_location = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
//        Migeba_Cell migebaCell9 = (Migeba_Cell) mTableView.getAdapter().getCellItem(9, row); //// GAMOYENEBA AR QVS
//        Migeba_Cell migebaCell10 = (Migeba_Cell) mTableView.getAdapter().getCellItem(10, row); /// GAMOYENEBA AR QVS
//        Migeba_Cell migebaCell_numerusi = (Migeba_Cell) mTableView.getAdapter().getCellItem(11, row);
        Migeba_Cell g_p_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(9, row);

        //SCANNER
        g_id.setText(g_p_id.getData().toString());
        System.out.println("aidi1 " + g_id.getText().toString());
        ExtendedFloatingActionButton scanner_button = v.findViewById(R.id.scanner_button_edit);

        scanner_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(mContext);
                mb.setTitle("დაასკანერეთ შტრიხკოდი");
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View v = inflater.inflate(R.layout.card_scanner, null, false);
                mb.setView(v);
                TextInputEditText et_scanner = v.findViewById(R.id.et_scanner);
                et_scanner.requestFocus();
                mb.setPositiveButton("ძებნა", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String seria = et_scanner.getText().toString().split(" ")[0];

                        List<Migeba_Cell> series_list = mTableView.getAdapter().getCellColumnItems(1);
                        for (int j = 0; j < series_list.size(); j++) {
                            if (series_list.get(j).getFilterableKeyword().equals(seria)) {
                                dialogInterface.dismiss();
                                mainAlertDialog.dismiss();
                                Edit_Card_Location(j, mContext);
                            }
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

                            String seria = et_scanner.getText().toString().split(" ")[0];


                            List<Migeba_Cell> series_list = mTableView.getAdapter().getCellColumnItems(1);
                            for (int j = 0; j < series_list.size(); j++) {
                                if (series_list.get(j).getFilterableKeyword().equals(seria)) {
                                    alertDialog.dismiss();
                                    Edit_Card_Location(j, mContext);
                                }
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });


//        new DateInputMask(inputvada.getEditText());
        new Handler().postDelayed(new Runnable() {

            public void run() {
                inputseria.getEditText().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0));
                inputseria.getEditText().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0));
                inputseria.getEditText().setSelection(inputseria.getEditText().length());
            }
        }, 400);

        MaterialButton nextButton = mainAlertDialog.findViewById(R.id.edit_card_nextBtn);
        MaterialButton edit_card_p_id = mainAlertDialog.findViewById(R.id.edit_card_p_id);
        MaterialButton nextAndSaveButton = mainAlertDialog.findViewById(R.id.edit_card_nextAndSave);
        MaterialButton edit_card_previous = mainAlertDialog.findViewById(R.id.button_previous);
        MaterialButton close_dialog = mainAlertDialog.findViewById(R.id.dialog_close);


        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainAlertDialog.dismiss();
            }
        });
        HashMap<Integer, String> location_map = new HashMap<>();
        ///print
        MaterialButton edit_card_print = mainAlertDialog.findViewById(R.id.button_seriebi_raod);
        edit_card_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    for (int i = 0; i < Integer.parseInt(inputsabechidraodenoba.getEditText().getText().toString()); i++) {
                        new Handler().postDelayed(new Runnable() {

                            public void run() {
                                try {
                                    ConnectAndPrint(context,
                                            inputseria.getEditText().getText().toString().replace(" ", "") + " " + migebaCell_vada.getFilterableKeyword().replace(" ", "") + " " + g_p_id.getData().toString().replace(" ", ""),
                                            geotranslate.FROMGEO(migebaCell_name.getFilterableKeyword()),
                                            SELECTED_USER.toLowerCase() + " " + inputvada.getEditText().getText().toString(),
                                            "",
                                            migebaCell_raodenoba.getFilterableKeyword());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 750 * i);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ResultSet rs3 = CONN_RESULTSET_SQL("SELECT * FROM [A_PLUS].[dbo].[LOCATION]");

        try {
            while (rs3.next()) {
                String name = rs3.getString("L_NAME");
                String id = rs3.getString("L_ID");
                location_map.put(Integer.parseInt(id), name);

                print(name);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        nextAndSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!location_map.containsValue(loca_tv.getText().toString())) {
                    CONN_EXECUTE_SQL("declare @newname varchar(50) set @newname='" + loca_tv.getText().toString() + "'\n" +
                            "declare @newID int \n" +
                            "set @newID=( select TOP 1 [L_ID]   FROM [A_PLUS].[dbo].[LOCATION] where L_NAME=@newname)\n" +
                            "if(@newID is null)  begin insert into [A_PLUS].[dbo].[LOCATION] (L_NAME )\n" +
                            "output inserted.L_ID as [L_ID]\n" +
                            "values(@newname)end else begin select @newID as [L_ID] end");
                }
                Migeba_Cell dasaxeleba = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
                Migeba_Cell edited_seria = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, row);
                Migeba_Cell edited_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
                Migeba_Cell edited_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, row);
                Migeba_Cell edited_mwarmoebeli = (Migeba_Cell) mTableView.getAdapter().getCellItem(4, row);
                Migeba_Cell edited_temp = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
                Migeba_Cell edited_raodenobayutshi = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
                Migeba_Cell edited_qveyana = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
                Migeba_Cell edited_lokacia = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
                Migeba_Cell edited_numerusi = (Migeba_Cell) mTableView.getAdapter().getCellItem(11, row);
                List<Migeba_Cell> rowItems = new ArrayList<>();
                rowItems.add(dasaxeleba);
                rowItems.add(edited_seria);
                rowItems.add(edited_vada);
                rowItems.add(edited_raodenoba);
                rowItems.add(edited_mwarmoebeli);
                rowItems.add(edited_temp);
                rowItems.add(edited_raodenobayutshi);
                rowItems.add(edited_qveyana);
                rowItems.add(edited_lokacia);
                rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(9, row));
                rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(10, row));
                rowItems.add(edited_numerusi);

                nextAndSaveButton.setIcon(mContext.getDrawable(R.drawable.ic_baseline_check_24));
                nextAndSaveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

                products_cache.set(row, rowItems);
                SetTableViewAdapter(products_cache);
                System.out.println("Lokacia " + loca_tv.getText().toString());
                saveChanges(
                        String.valueOf(row),
                        loca_tv.getText().toString(),
                        g_id.getText().toString());
                Edit_Card_Location(row + 1, context);




                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        try {
                            mTableView.setSelectedCell(mTableView.getSelectedColumn(), row + 1);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);
                mainAlertDialog.cancel();

            }
        });


        edit_card_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Edit_Card_Location(row - 1, context);
                mainAlertDialog.cancel();
                try {
                    mTableView.setSelectedCell(mTableView.getSelectedColumn(), row - 1);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (row == 0) {
            edit_card_previous.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainAlertDialog.cancel();
                }
            });
        }
        int rowindex = mTableView.getAdapter().getRowHeaderRecyclerViewAdapter().getItemCount() - 1;


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainAlertDialog.cancel();
                if (row == rowindex) {

                    Edit_Card_Location(0, context);
                } else {

                    Edit_Card_Location(row + 1, context);
                }
            }
        });


        if (row == rowindex) {
            nextAndSaveButton.setText("დასრულება");
            nextAndSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!location_map.containsValue(loca_tv.getText().toString())) {
                        CONN_EXECUTE_SQL("declare @newname varchar(50) set @newname='" + loca_tv.getText().toString() + "'\n" +
                                "declare @newID int \n" +
                                "set @newID=( select TOP 1 [L_ID]   FROM [A_PLUS].[dbo].[LOCATION] where L_NAME=@newname)\n" +
                                "if(@newID is null)  begin insert into [A_PLUS].[dbo].[LOCATION] (L_NAME )\n" +
                                "output inserted.L_ID as [L_ID]\n" +
                                "values(@newname)end else begin select @newID as [L_ID] end");
                    }
                    Migeba_Cell dasaxeleba = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
                    Migeba_Cell edited_seria = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, row);
                    Migeba_Cell edited_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
                    Migeba_Cell edited_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, row);
                    Migeba_Cell edited_mwarmoebeli = (Migeba_Cell) mTableView.getAdapter().getCellItem(4, row);
                    Migeba_Cell edited_temp = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
                    Migeba_Cell edited_raodenobayutshi = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
                    Migeba_Cell edited_qveyana = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
                    Migeba_Cell edited_lokacia = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
                    Migeba_Cell edited_numerusi = (Migeba_Cell) mTableView.getAdapter().getCellItem(11, row);
                    List<Migeba_Cell> rowItems = new ArrayList<>();
                    rowItems.add(dasaxeleba);
                    rowItems.add(edited_seria);
                    rowItems.add(edited_vada);
                    rowItems.add(edited_raodenoba);
                    rowItems.add(edited_mwarmoebeli);
                    rowItems.add(edited_temp);
                    rowItems.add(edited_raodenobayutshi);
                    rowItems.add(edited_qveyana);
                    rowItems.add(edited_lokacia);
                    rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(9, row));
                    rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(10, row));
                    rowItems.add(edited_numerusi);

                    nextAndSaveButton.setIcon(mContext.getDrawable(R.drawable.ic_baseline_check_24));
                    nextAndSaveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

                    products_cache.set(row, rowItems);
                    SetTableViewAdapter(products_cache);
                    saveChanges(
                            String.valueOf(row + 1),
                            loca_tv.getText().toString(),
                            g_id.getText().toString());

                    uploadDalageba(new View(context));


                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            try {
                                mTableView.setSelectedCell(mTableView.getSelectedColumn(), row + 1);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                    mainAlertDialog.cancel();
                }
            });

        }


//        Migeba_Cell g_id_Migeba_cell = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
//        inputseria.getEditText().setText(String.valueOf(migebaCell_seria.getData()));
//        inputvada.getEditText().setText(String.valueOf(migebaCell_vada.getData()));
//        inputraodenoba.getEditText().setText(String.valueOf(migebaCell_raodenoba.getData()));
//        inputmwarmoebeli.getEditText().setText(String.valueOf(migebaCell_mwarmoebeli.getData()));
//        inputtemp.getEditText().setText(String.valueOf(migebaCell_temp.getData()));
//        inputraodenobayutshi.getEditText().setText(String.valueOf(migebaCell_raodenobaYutshi.getData()));
//        inputqveyana.getEditText().setText(String.valueOf(migebaCell_qveyana.getData()));
////        inputqveyana.getEditText().setText(String.valueOf(migebaCell_location.getData()));
//        inputnumerusi.getEditText().setText(String.valueOf(migebaCell_numerusi.getData()));
        //        inputsabechidraodenoba.getEditText().setText(String.valueOf(migebaCell_location.getData()));
//                input5.getEditText().setText(String.valueOf(cell5.getData()));
        loca_tv = mainAlertDialog.findViewById(R.id.location_autocomplete);
        loca_tv.setText(String.valueOf(migebaCell_location.getData()));

        ///REZERVI
        MaterialButton button_rezervi = mainAlertDialog.findViewById(R.id.button_rezervi);
        FrameLayout dialog_secondary_bg = mainAlertDialog.findViewById(R.id.dialog_secondary_bg);
        FrameLayout dialog_secondary = mainAlertDialog.findViewById(R.id.dialog_secondary);

        ///REZERVI//////////////////////////////////////////////////////////////////////////////////////////////////////////////
        MaterialContainerTransform transformOpenRezervi = new MaterialContainerTransform();
        transformOpenRezervi.setStartView(button_rezervi);
        transformOpenRezervi.setEndView(dialog_secondary);
        transformOpenRezervi.addTarget(dialog_secondary);
        transformOpenRezervi.setScrimColor(Color.TRANSPARENT);

        MaterialContainerTransform transformCloseRezervi = new MaterialContainerTransform();
        transformCloseRezervi.setStartView(dialog_secondary);
        transformCloseRezervi.setEndView(button_rezervi);
        transformCloseRezervi.addTarget(button_rezervi);
        transformCloseRezervi.setScrimColor(Color.TRANSPARENT);

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
                        TransitionManager.beginDelayedTransition((ViewGroup) mainAlertDialog.getWindow().getDecorView().getRootView(), transformCloseRezervi);
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
                        TransitionManager.beginDelayedTransition((ViewGroup) mainAlertDialog.getWindow().getDecorView().getRootView(), transformCloseRezervi);
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
                            TransitionManager.beginDelayedTransition((ViewGroup) mainAlertDialog.getWindow().getDecorView().getRootView(), transformCloseRezervi);
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
                TransitionManager.beginDelayedTransition((ViewGroup) mainAlertDialog.getWindow().getDecorView().getRootView(), transformOpenRezervi);

            }
        });

        inputseria.setEnabled(false);
        inputvada.setEnabled(false);
        inputraodenoba.setEnabled(false);
        inputseria.getEditText().setText(String.valueOf(migebaCell_seria.getData()));
        inputvada.getEditText().setText(String.valueOf(migebaCell_temp.getData()));
        inputvada.setHint("ტემპერატურა");
        inputraodenoba.getEditText().setText(String.valueOf(migebaCell_raodenoba.getData()));

        loca_tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    Intent intent = new Intent(mContext, LocationChooser.class);
                    loca_tv.setText("");
                    mContext.startActivity(intent);
                }
            }
        });


        locationsList.clear();

        locationsList.add("ახალის შექმნა");


        try {
            ResultSet rs = CONN_RESULTSET_SQL("SELECT distinct g_loc\n" +
                    "  FROM [A_PLUS].[dbo].[GET] where G_P_ID='" + g_p_id.getData() + "'");

            while (rs.next()) {
                if (rs.getString("g_loc") != null) {
                    locationsList.add(rs.getString("g_loc"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        mContext,
                        R.layout.dropdown_menu_popup_item,
                        locationsList);


        try {
            AutoCompleteTextView editTextFilledExposedDropdown =
                    mainAlertDialog.findViewById(R.id.location_autocomplete);
            editTextFilledExposedDropdown.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.d(TAG, "onClick: " + row + 1);
        edit_card_p_id.setText(String.valueOf(row + 1));
        seria.setText(String.valueOf(migebaCell_name.getData()));
//        g_id.setText(String.valueOf(cell_name.getData()));

//        edit_card_previous.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Edit_Card(row - 1);
//                mainAlertDialog.cancel();
//                try {
//                    mTableView.setSelectedCell(mTableView.getSelectedColumn(), row - 1);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        nextAndSaveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Cell cell00 = (Cell) mTableView.getAdapter().getCellItem(0, row);
////                Cell cell11 = new Cell(cell1.getId(), inputseria.getEditText().getText().toString());
////                Cell cell22 = new Cell(cell2.getId(), inputvada.getEditText().getText().toString());
////                Cell cell33 = new Cell(cell3.getId(), inputraodenoba.getEditText().getText().toString());
////                Cell cell44 = new Cell(cell4.getId(), inputmwarmoebeli.getEditText().getText().toString());
////                List<Cell> rowItems = new ArrayList<>();
////                rowItems.add(cell00);
////                rowItems.add(cell11);
////                rowItems.add(cell22);
////                rowItems.add(cell33);
////                rowItems.add(cell44);
////                rowItems.add((Cell) mTableView.getAdapter().getCellItem(5, row));
////                rowItems.add((Cell) mTableView.getAdapter().getCellItem(6, row));
////                rowItems.add((Cell) mTableView.getAdapter().getCellItem(7, row));
////                rowItems.add((Cell) mTableView.getAdapter().getCellItem(8, row));
////                rowItems.add((Cell) mTableView.getAdapter().getCellItem(9, row));
//
////                products_cache.set(row, rowItems);
////                SetTableViewAdapter(products_cache);
////                saveChanges(inputseria.getEditText().getText().toString(), inputvada.getEditText().getText().toString(), inputraodenoba.getEditText().getText().toString(), inputmwarmoebeli.getEditText().getText().toString(), g_id_cell.getData().toString());
//                Edit_Card(row + 1);
//                new Handler().postDelayed(new Runnable() {
//
//                    public void run() {
//                        try {
//                            mTableView.setSelectedCell(mTableView.getSelectedColumn(), row + 1);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, 500);
//
//
//                mainAlertDialog.cancel();
//            }
//        });

        //if saveChanges string includes current product serial and product name, make nextAndSaveButton green tint
        if (savedProducts.contains(String.valueOf(row + 1))) {
            nextAndSaveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }


//        nextAndSaveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!location_map.containsValue(loca_tv.getText().toString())) {
//                    CONN_EXECUTE_SQL("declare @newname varchar(50) set @newname='" + loca_tv.getText().toString() + "'\n" +
//                            "declare @newID int \n" +
//                            "set @newID=( select TOP 1 [L_ID]   FROM [A_PLUS].[dbo].[LOCATION] where L_NAME=@newname)\n" +
//                            "if(@newID is null)  begin insert into [A_PLUS].[dbo].[LOCATION] (L_NAME )\n" +
//                            "output inserted.L_ID as [L_ID]\n" +
//                            "values(@newname)end else begin select @newID as [L_ID] end");
//                }
//                Migeba_Cell dasaxeleba = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
//                Migeba_Cell edited_seria = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, row);
//                Migeba_Cell edited_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
//                Migeba_Cell edited_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, row);
//                Migeba_Cell edited_mwarmoebeli = (Migeba_Cell) mTableView.getAdapter().getCellItem(4, row);
//                Migeba_Cell edited_temp = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
//                Migeba_Cell edited_raodenobayutshi = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
//                Migeba_Cell edited_qveyana = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
//                Migeba_Cell edited_lokacia = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
//                Migeba_Cell edited_numerusi = (Migeba_Cell) mTableView.getAdapter().getCellItem(11, row);
//                List<Migeba_Cell> rowItems = new ArrayList<>();
//                rowItems.add(dasaxeleba);
//                rowItems.add(edited_seria);
//                rowItems.add(edited_vada);
//                rowItems.add(edited_raodenoba);
//                rowItems.add(edited_mwarmoebeli);
//                rowItems.add(edited_temp);
//                rowItems.add(edited_raodenobayutshi);
//                rowItems.add(edited_qveyana);
//                rowItems.add(edited_lokacia);
//                rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(9, row));
//                rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(10, row));
//                rowItems.add(edited_numerusi);
//
//                nextAndSaveButton.setIcon(mContext.getDrawable(R.drawable.ic_baseline_check_24));
//                nextAndSaveButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
//
//                products_cache.set(row, rowItems);
//                SetTableViewAdapter(products_cache);
//
//                saveChanges(
//                        String.valueOf(row + 1),
//                        loca_tv.getText().toString(),
//                        g_id.getText().toString());
//
//
//            }
//        });
    }

    public static void saveChanges(String row, String P_LOC_ID, String g_id) {

        savedProducts.add(row);

        changesQuery.append("UPDATE A_PLUS.dbo.GET_EDIT set " +
                " P_LOC_ID = '" + P_LOC_ID + "'," +
                " where g_id = '" + g_id + "' and G_ZDN = '" + Dalageba.zednadebi_id + "'" +
                "\n UPDATE A_PLUS.dbo.GET SET " +
                        " G_LOC = (SELECT L_ID FROM A_PLUS.dbo.LOCATION WHERE L_NAME = '" + P_LOC_ID + "')" +
                        " WHERE G_ID = '" + g_id + "'" +
                "if @@rowcount = 0 and @@error = 0\n" +
                "insert into A_PLUS.dbo.GET_EDIT (G_SERIA, G_QUANT, G_EXP, g_id, G_ZDN) VALUES (NULL,NULL,NULL,'" + g_id + "' ,'" + Dalageba.zednadebi_id + "') \n" +
                "UPDATE A_PLUS.dbo.GET_EDIT set " +
                " P_LOC_ID = '" + P_LOC_ID + "'," +
                " where g_id = '" + g_id + "';" +
                "\n UPDATE A_PLUS.dbo.GET SET " +
                " G_LOC = (SELECT L_ID FROM A_PLUS.dbo.LOCATION WHERE L_NAME = '" + P_LOC_ID + "')" +
                " WHERE G_ID = '" + g_id + "'");

//        LOCATION_SEARCH = "SELECT L_ID FROM A_PLUS.dbo.LOCATION WHERE L_NAME = '" + P_LOC_ID + "'";
//        LOCATION_CHANGE_QUERY = "UPDATE A_PLUS.dbo.GET SET G_LOC = "

        /*
            [G_SERIA]
            [G_EXP]
            [G_QUANT]
            [G_PRICE]
            [G_D_ID]
            [MAN_Q_name]
            [MAN_name]
            [G_MAN_ID]
            [G_MAN_Q_ID]
            [G_ZDN]
            [G_TIME]
            [G_BAR1]
            [P_ID]
            [P_NAME]
            [P_FORM]
            [P_ERT]
            [P_LOC_ID]
         */
        Log.e(TAG, "changesQuery: " + changesQuery);

//        String changesQuery = "IF EXISTS (SELECT 1 FROM A_PLUS.dbo.GET2 WHERE G_ID = '" + g_id + "' AND G_ZDN = '" + zednadebi_fab.getText().toString() + "')\n" +
//                "BEGIN\n" +
//                "    UPDATE A_PLUS.dbo.GET2 \n" +
//                "    SET " +
//                " G_SERIA = '" + G_SERIA + "'," +
//                " G_PRICE = '" + G_PRICE + "'," +
//                " G_QUANT = '" + G_QUANT + "'," +
//                " G_EXP = '" + G_EXP + "'" +
//                " where g_id = '" + g_id + "'" +
//                "END\n" +
//                "ELSE\n" +
//                "BEGIN\n" +
//                "insert into A_PLUS.dbo.GET2 (G_SERIA, G_PRICE, G_QUANT, G_EXP, g_id, G_ZDN) VALUES (NULL,NULL,NULL,NULL,'" + g_id + "' ,'" + zednadebi_fab.getText().toString() + "') \n" +
//                "UPDATE A_PLUS.dbo.GET2 set " +
//                " G_SERIA = '" + G_SERIA + "'," +
//                " G_PRICE = '" + G_PRICE + "'," +
//                " G_QUANT = '" + G_QUANT + "'," +
//                " G_EXP = '" + G_EXP + "'" +
//                " where g_id = '" + g_id + "'" +
//                "END";


//        changesQuery.append("" +
//                "insert into A_PLUS.dbo.GET2 (G_SERIA, G_PRICE, G_QUANT, G_EXP, g_id, G_ZDN) VALUES (NULL,NULL,NULL,NULL,'" + g_id + "' ,'" + zednadebi_fab.getText().toString() + "') \n" +
//                "UPDATE A_PLUS.dbo.GET2 set " +
//                " G_SERIA = '" + G_SERIA + "'," +
//                " G_PRICE = '" + G_PRICE + "'," +
//                " G_QUANT = '" + G_QUANT + "'," +
//                " G_EXP = '" + G_EXP + "'" +
//                " where g_id = '" + g_id + "'" +
//                "\n");
        Log.e(TAG, "changesQuery: " + changesQuery);
        //SELECT G_SERIA,G_EXP,G_QUANT,G_EXP,G_D_ID,G_MAN_ID,G_MAN_Q_ID,G_ZDN,G_TIME,G_BAR1,P_ID,P_NAME,P_FORM,P_ERT,P_LOC_ID FROM [A_PLUS].[dbo].[GET] left join A_PLUS.dbo.PRODUCT on P_ID=g_p_id where G_ZDN = '" + zednadebi_id + "'"
    }

    public static void SetTableViewAdapter(List<List<Migeba_Cell>> products_cache) {
//        mTableView.showColumn(5);
//        mTableView.showColumn(6);
//        mTableView.showColumn(7);
//        mTableView.showColumn(8);
        mTableView.showColumn(9);
        mTableView.showColumn(10);
        mTableView.getAdapter().setAllItems(getColumnHeaders(), getRowHeaders(), products_cache);

        mTableView.setColumnWidth(0, 500);
        mTableView.setColumnWidth(1, 250);
        mTableView.setColumnWidth(2, 170);
        mTableView.setColumnWidth(3, 170);
        mTableView.setColumnWidth(4, 265);
        mTableView.setColumnWidth(5, 170);
        mTableView.setColumnWidth(6, 230);
//      mTableView.setColumnWidth(7, 230);
        mTableView.setColumnWidth(8, 170);
        mTableView.setColumnWidth(9, 170);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                mTableView.hideColumn(9);
//                mTableView.hideColumn(10);
            }
        }, 400);

    }


//    public void SetTableViewAdapter(List<List<Migeba_Cell>> products) {
////        mTableView.showColumn(5);
////        mTableView.showColumn(6);
////        mTableView.showColumn(7);
//        mTableView.showColumn(8);
//        mTableView.showColumn(9);
//        mTableView.getAdapter().setAllItems(getColumnHeaders(), getRowHeaders(zednadebi_fab.getText().toString()), products);
//
//        mTableView.setColumnWidth(0, 500);
//        mTableView.setColumnWidth(1, 250);
//        mTableView.setColumnWidth(2, 270);
//        mTableView.setColumnWidth(3, 265);
//        mTableView.setColumnWidth(4, 265);
//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mTableView.hideColumn(8);
//                mTableView.hideColumn(9);
//            }
//        }, 400);
//
//    }


//    public void Edit_Card(Integer row) {
//        AlertDialog.Builder mb = new AlertDialog.Builder(context);
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View v = inflater.inflate(R.layout.card_edit_migeba, null, false);
//        v.setBackgroundColor(Color.TRANSPARENT);
//        v.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
//        mb.setView(v);
//        AlertDialog alertDialog = mb.create();
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        alertDialog.show();
//        TextInputLayout input1 = alertDialog.findViewById(R.id.card_edit_seria);
//        TextInputLayout input2 = alertDialog.findViewById(R.id.card_edit_vada);
//        TextInputLayout input3 = alertDialog.findViewById(R.id.card_edit_raodenoba);
//        TextInputLayout input4 = alertDialog.findViewById(R.id.card_edit_mwarmoebeli);
//        TextInputLayout input5 = alertDialog.findViewById(R.id.card_edit_raodenobayutshi);
//        TextInputLayout input6 = alertDialog.findViewById(R.id.card_edit_qveyana);
//        TextInputLayout input8 = alertDialog.findViewById(R.id.card_edit_sabechdiraodenoba);
//
//        new DateInputMask(input2.getEditText());
//
//        new Handler().postDelayed(new Runnable() {
//
//            public void run() {
////        ((EditText) findViewById(R.id.et_find)).requestFocus();
////
//
////        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
////        imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);
//
//                input1.getEditText().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0));
//                input1.getEditText().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0));
//                input1.getEditText().setSelection(input1.getEditText().length());
//            }
//        }, 400);
//
//
////        TextInputLayout input5 = alertDialog.findViewById(R.id.card_edit_5);
//        MaterialButton saveBtn = alertDialog.findViewById(R.id.edit_card_saveBtn);
//        MaterialButton edit_card_p_id = alertDialog.findViewById(R.id.edit_card_p_id);
//        MaterialButton edit_card_next = alertDialog.findViewById(R.id.edit_card_next);
//        MaterialButton edit_card_previous = alertDialog.findViewById(R.id.edit_card_previous);
//
//
//        ///print
//        MaterialButton edit_card_print = alertDialog.findViewById(R.id.edit_card_print);
//        edit_card_print.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    for (int i = 0; i < Math.min(Integer.parseInt(input8.getEditText().getText().toString()), 10); i++) {
//                        ConnectAndPrint(context, input1.getEditText().getText().toString());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        edit_card_next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Edit_Card(row + 1);
//                new Handler().postDelayed(new Runnable() {
//
//                    public void run() {
//                        try {
//                            mTableView.setSelectedCell(mTableView.getSelectedColumn(), row + 1);
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, 500);
//                alertDialog.cancel();
//            }
//        });
//
//        edit_card_previous.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Edit_Card(row - 1);
//                alertDialog.cancel();
//                try {
//                    mTableView.setSelectedCell(mTableView.getSelectedColumn(), row - 1);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        if (row == 0) {
//            edit_card_previous.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    alertDialog.cancel();
//                }
//            });
//
//
//        }
//        int rowindex = mTableView.getAdapter().getRowHeaderRecyclerViewAdapter().getItemCount() - 1;
//        if (row == rowindex) {
//            edit_card_next.setText("დასრულება");
//            edit_card_next.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    FINISH_MIGEBA();
//                    alertDialog.cancel();
//                }
//            });
//
//        }
//
//        TextView seria = alertDialog.findViewById(R.id.location_name);
//        TextView g_id = alertDialog.findViewById(R.id.g_id);
//        Migeba_Cell migebaCell_name = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
//        Migeba_Cell migebaCell1 = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, row);
//        Migeba_Cell migebaCell2 = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
//        Migeba_Cell migebaCell3 = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, row);
//        Migeba_Cell migebaCell4 = (Migeba_Cell) mTableView.getAdapter().getCellItem(4, row);
//        Migeba_Cell migebaCell5 = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
//        Migeba_Cell migebaCell6 = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
//        Migeba_Cell migebaCell7 = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
//        Migeba_Cell g_id_cell = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
////        Migeba_Cell g_id_Migeba_cell = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
//        Migeba_Cell g_p_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(9, row);
//        input1.getEditText().setText(String.valueOf(migebaCell1.getData()));
//        input2.getEditText().setText(String.valueOf(migebaCell2.getData()));
//        input3.getEditText().setText(String.valueOf(migebaCell3.getData()));
//        input4.getEditText().setText(String.valueOf(migebaCell4.getData()));
//        input5.getEditText().setText(String.valueOf(migebaCell5.getData()));
//        input6.getEditText().setText(String.valueOf(migebaCell6.getData()));
//
//
//        loca_tv = alertDialog.findViewById(R.id.location_autocomplete);
//        loca_tv.setText(String.valueOf(migebaCell7.getData()));
//
//        loca_tv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if (i == 0) {
//                    Intent intent = new Intent(context, LocationChooser.class);
//                    loca_tv.setText("");
//                    context.startActivity(intent);
//                }
//            }
//        });
//
//        locationsList.clear();
//
//        locationsList.add("ახალის შექმნა");
//
//
//        try {
//            ResultSet rs = MainActivity.SQL_ResultSet("SELECT distinct g_loc\n" +
//                    "  FROM [A_PLUS].[dbo].[GET] where G_P_ID='" + g_p_id.getData() + "'");
//
//            while (rs.next()) {
//                if (rs.getString("g_loc") != null) {
//                    locationsList.add(rs.getString("g_loc"));
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(
//                        context,
//                        R.layout.dropdown_menu_popup_item,
//                        locationsList);
//
//
//        try {
//            AutoCompleteTextView editTextFilledExposedDropdown =
//                    alertDialog.findViewById(R.id.location_autocomplete);
//            editTextFilledExposedDropdown.setAdapter(adapter);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Log.d(TAG, "onClick: " + row + 1);
//        edit_card_p_id.setText(String.valueOf(row + 1));
//        seria.setText(String.valueOf(migebaCell_name.getData()));
////        g_id.setText(String.valueOf(cell_name.getData()));
//
//        saveBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Migeba_Cell migebaCell00 = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
//                Migeba_Cell migebaCell11 = new Migeba_Cell(migebaCell1.getId(), input1.getEditText().getText().toString());
//                Migeba_Cell migebaCell22 = new Migeba_Cell(migebaCell2.getId(), input2.getEditText().getText().toString());
//                Migeba_Cell migebaCell33 = new Migeba_Cell(migebaCell3.getId(), input3.getEditText().getText().toString());
//                Migeba_Cell migebaCell44 = new Migeba_Cell(migebaCell4.getId(), input4.getEditText().getText().toString());
//                Migeba_Cell migebaCell55 = new Migeba_Cell(migebaCell5.getId(), input5.getEditText().getText().toString());
//                Migeba_Cell migebaCell66 = new Migeba_Cell(migebaCell6.getId(), input6.getEditText().getText().toString());
//                Migeba_Cell migebaCell77 = new Migeba_Cell(migebaCell7.getId(), loca_tv.getText().toString());
////                Migeba_Cell migebaCell88 = new Migeba_Cell(g_id_cell.getId(), input8.getEditText().getText().toString());
//                List<Migeba_Cell> rowItems = new ArrayList<>();
//                rowItems.add(migebaCell00);
//                rowItems.add(migebaCell11);
//                rowItems.add(migebaCell22);
//                rowItems.add(migebaCell33);
//                rowItems.add(migebaCell44);
//                rowItems.add(migebaCell55);
//                rowItems.add(migebaCell66);
//                rowItems.add(migebaCell77);
//                rowItems.add(g_id_cell);
////                rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(5, row));
////                rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(6, row));
////                rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(7, row));
////                rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(8, row));
//                rowItems.add((Migeba_Cell) mTableView.getAdapter().getCellItem(9, row));
//
//                final Handler handler = new Handler(Looper.getMainLooper());
//                saveBtn.setIcon(context.getDrawable(R.drawable.ic_baseline_check_24));
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        saveBtn.setIcon(null);
//                    }
//                }, 1500);
//                products_cache.set(row, rowItems);
//                SetTableViewAdapter(products_cache);
////                mTableView.getAdapter().removeRow(row);
////                mTableView.getAdapter().addRow(row, new RowHeader(String.valueOf(row + 1), String.valueOf(row + 1)), rowItems);
//                saveChanges(
//                        input1.getEditText().getText().toString(),
//                        input2.getEditText().getText().toString(),
//                        input3.getEditText().getText().toString(),
//                        input4.getEditText().getText().toString(),
//                        input5.getEditText().getText().toString(),
//                        input6.getEditText().getText().toString(),
//                        loca_tv.getText().toString(),
//                        g_id_cell.getData().toString());
//
//            }
//        });
//    }
//    private void Edit_Card(){
//
//    }

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
