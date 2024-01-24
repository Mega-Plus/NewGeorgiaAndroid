package com.evrencoskun.tableviewsample.migeba_tableview;

import static android.content.ContentValues.TAG;
import static com.example.migeba.Inventarizacia.ag_time;
import static com.example.migeba.Inventarizacia.getColumnHeaders;
import static com.example.migeba.Inventarizacia.getRowHeaders;
import static com.example.migeba.Inventarizacia.tableViewFilter;
import static com.example.migeba.MainActivity.CONN_EXECUTE_SQL;
import static com.example.migeba.MainActivity.CONN_RESULTSET_SQL;
import static com.example.migeba.MainActivity.ConnectAndPrint;
import static com.example.migeba.MainActivity.SELECTED_USER;
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
import android.text.InputType;
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
import com.example.migeba.Inventarizacia;
import com.example.migeba.LocationChooser;
import com.example.migeba.MainActivity;
import com.example.migeba.R;
import com.example.migeba.Utils.DateInputMask;
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


public class Inventarizacia_TableViewListener implements ITableViewListener {
    @NonNull
    private final Context context;
    @NonNull
    public static TableView mTableView;
    public static List<List<Migeba_Cell>> products_cache;
    public static StringBuilder changesQuery;
    public static List<String> savedProducts = new ArrayList<>();

    public static AlertDialog alertDialog;

    public static AutoCompleteTextView loca_tv;
    public static List<String> locationsList = new ArrayList<>();


    public Inventarizacia_TableViewListener(@NonNull TableView tableView) {
        this.context = tableView.getContext();
        this.mTableView = tableView;
        savedProducts = new ArrayList<>();
        changesQuery = new StringBuilder("");
    }


    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

    }


    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        if (!tableViewFilter.getFilterItems().isEmpty()) {
            String filter = tableViewFilter.getFilterItems().get(0).getFilter();
            Migeba_Cell ro = ((Migeba_Cell) Inventarizacia.mTableView.getAdapter().getCellRowItems(row).get(0));
            tableViewFilter.set("");
            int i = Inventarizacia.mTableView.getAdapter().getCellColumnItems(0).indexOf(ro);
            tableViewFilter.set(filter);
            System.out.println(i);
            Edit_Card_Location(i, context);
        } else {
            Edit_Card_Location(row, context);
        }
    }

    public static void Edit_Card_Location(Integer row, Context context) {
        if (Inventarizacia.mTableView == null) {
            return;
        } else if (Inventarizacia.mTableView.getAdapter().getCellColumnItems(1).size() <= 0) {
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
        View v = inflater.inflate(R.layout.card_edit_inventarizacia, null, false);
        v.setBackgroundColor(Color.TRANSPARENT);
        v.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        mb.setView(v);
        alertDialog = mb.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        alertDialog.show();
        TextInputLayout inputAnagceri = alertDialog.findViewById(R.id.card_edit_anagceri);
        TextInputLayout inputvada = alertDialog.findViewById(R.id.vada);
        TextInputLayout inputRaodenoba = alertDialog.findViewById(R.id.card_edit_raodenoba);
        TextInputLayout inputmwarmoebeli = alertDialog.findViewById(R.id.card_edit_mwarmoebeli);
        TextInputLayout inputraodenobayutshi = alertDialog.findViewById(R.id.card_edit_raodenobayutshi);
        TextInputLayout inputqveyana = alertDialog.findViewById(R.id.card_edit_qveyana);
        TextInputLayout inputtemp = alertDialog.findViewById(R.id.card_edit_temp);
        TextInputLayout inputsabechidraodenoba = alertDialog.findViewById(R.id.card_edit_sabechdiraodenoba);
        TextInputLayout seriaText = alertDialog.findViewById(R.id.card_edit_numerusi);
        TextInputLayout inputseria = alertDialog.findViewById(R.id.card_edit_anagceri);


//        inputvada.setVisibility(View.GONE);
        inputmwarmoebeli.setVisibility(View.GONE);
        inputraodenobayutshi.setVisibility(View.GONE);
        inputqveyana.setVisibility(View.GONE);
        inputtemp.setVisibility(View.GONE);
        seriaText.setVisibility(View.VISIBLE);


        new DateInputMask(inputvada.getEditText());
        new Handler().postDelayed(new Runnable() {

            public void run() {
                inputAnagceri.getEditText().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0f, 0f, 0));
                inputAnagceri.getEditText().dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0f, 0f, 0));
                inputAnagceri.getEditText().setSelection(inputAnagceri.getEditText().length());
            }
        }, 400);

        MaterialButton saveBtn = alertDialog.findViewById(R.id.edit_card_nextBtn);
        MaterialButton edit_card_p_id = alertDialog.findViewById(R.id.edit_card_p_id);
        MaterialButton edit_card_next = alertDialog.findViewById(R.id.edit_card_nextAndSave);
        MaterialButton edit_card_previous = alertDialog.findViewById(R.id.button_previous);

        TextView seria = alertDialog.findViewById(R.id.location_name);
        TextView g_id = alertDialog.findViewById(R.id.g_id);
        Migeba_Cell migebaCell_name = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
        Migeba_Cell migebaCell_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
        Migeba_Cell migebaCell_location = (Migeba_Cell) mTableView.getAdapter().getCellItem(4, row);
        Migeba_Cell g_p_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
        Migeba_Cell seriaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
        Migeba_Cell anagceriCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, row);
        Migeba_Cell vadaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
        Migeba_Cell g_id_cell = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);

///REZERVI

        try {
            inputvada.getEditText().setText(vadaCell.getData().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        inputvada.setEnabled(false);

        seriaText.getEditText().setText(seriaCell.getData().toString());
        seriaText.setHint("სერია");
        seriaText.getEditText().setTextColor(Color.BLACK);
        seriaText.getEditText().setTextSize(20);
        seriaText.setEnabled(false);

        MaterialButton button_rezervi = alertDialog.findViewById(R.id.button_rezervi);
        FrameLayout dialog_secondary_bg = alertDialog.findViewById(R.id.dialog_secondary_bg);
        FrameLayout dialog_secondary = alertDialog.findViewById(R.id.dialog_secondary);

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
                        TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformCloseRezervi);
                        dialog_secondary_bg.setVisibility(View.INVISIBLE);
                        dialog_secondary.setVisibility(View.INVISIBLE);
                        button_rezervi.setVisibility(View.VISIBLE);
                    }
                });
                TextInputEditText rezervi_edittext = (TextInputEditText) dialog_secondaryEditText.getEditText();

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CONN_EXECUTE_SQL("update [a_PLUS].dbo.product set P_LOC_REZERVI = '" + rezervi_edittext.getText().toString() + "' where P_ID = '" + g_p_id.getFilterableKeyword() + "'");
                        TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformCloseRezervi);
                        dialog_secondary_bg.setVisibility(View.INVISIBLE);
                        dialog_secondary.setVisibility(View.INVISIBLE);
                        button_rezervi.setVisibility(View.VISIBLE);

                    }
                });

                ResultSet query_rezervi = CONN_RESULTSET_SQL("select top 1 * FROM [A_PLUS].[dbo].[PRODUCT] where P_ID = '" + g_p_id.getFilterableKeyword() + "'");


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
                                    "  where SL_SERIA = '" + seriaCell.getData().toString() + "'");
                            TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformCloseRezervi);
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
                TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformOpenRezervi);

            }
        });

        if (savedProducts.contains(String.valueOf(row + 1))) {
            saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        }

        try {
            inputseria.getEditText().setText(anagceriCell.getData().toString());
        } catch (Exception e) {
            e.printStackTrace();
            try {
                inputseria.getEditText().setText(anagceriCell.getData().toString());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        ///print
        MaterialButton edit_card_print = alertDialog.findViewById(R.id.button_seriebi_raod);
        edit_card_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    for (int i = 0; i < Integer.parseInt(inputsabechidraodenoba.getEditText().getText().toString()); i++) {
                        new Handler().postDelayed(new Runnable() {

                            public void run() {
                                try {
                                    ConnectAndPrint(context,

                                            seriaText.getEditText().getText().toString().replace(" ", "") + " " +
                                                    vadaCell.getData().toString().replace(" ", "") + " " +
                                                    g_id_cell.getFilterableKeyword().replace(" ", "")
                                            ,
                                            geotranslate.FROMGEO(migebaCell_name.getFilterableKeyword()),
                                            SELECTED_USER.toLowerCase() + " " + inputvada.getEditText().getText(),
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
        int nashti = Integer.parseInt(migebaCell_raodenoba.getData().toString().split("\\.")[0]);

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

                        List<Migeba_Cell> series_list = mTableView.getAdapter().getCellColumnItems(6);
                        for (int j = 0; j < series_list.size(); j++) {
                            if (series_list.get(j).getFilterableKeyword().equals(seria)) {
                                alertDialog.dismiss();
                                dialogInterface.dismiss();
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


                            List<Migeba_Cell> series_list = mTableView.getAdapter().getCellColumnItems(6);
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

        HashMap<Integer, String> location_map = new HashMap<>();
        edit_card_next.setOnClickListener(new View.OnClickListener() {
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
                Migeba_Cell edited_lokacia = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
                Migeba_Cell edited_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
                Migeba_Cell edited_anagceri = new Migeba_Cell(dasaxeleba.getId(), inputAnagceri.getEditText().getText().toString());
                Migeba_Cell edited_gloc = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
                Migeba_Cell edited_gpid = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
                Migeba_Cell edited_gseria = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
                Migeba_Cell edited_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
                Migeba_Cell edited_g_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
                List<Migeba_Cell> rowItems = new ArrayList<>();
                rowItems.add(dasaxeleba);
                rowItems.add(edited_lokacia);
                rowItems.add(edited_raodenoba);
                rowItems.add(edited_anagceri);
                rowItems.add(edited_gloc);
                rowItems.add(edited_gpid);
                rowItems.add(edited_gseria);
                rowItems.add(edited_vada);
                rowItems.add(edited_g_id);

                final Handler handler = new Handler(Looper.getMainLooper());
                saveBtn.setIcon(mContext.getDrawable(R.drawable.ic_baseline_check_24));

                saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                products_cache.set(row, rowItems);
                SetTableViewAdapter(products_cache);

//                inputRaodenoba.getEditText().setText(String.valueOf(nashti) + "  |  " + String.valueOf(nashti - Integer.parseInt(charSequence.toString())));

                ResultSet rs = CONN_RESULTSET_SQL("SELECT [L_ID]\n" +
                        "  FROM [A_PLUS].[dbo].[LOCATION] where L_NAME='" + loca_tv.getText().toString() + "'");

                String loca_id = "";
                try {
                    while (rs.next()) {
                        loca_id = rs.getString("L_ID");
                        print(loca_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ResultSet rs2 = CONN_RESULTSET_SQL("SELECT [L_ID]\n" +
                        "  FROM [A_PLUS].[dbo].[LOCATION] where L_NAME='" + migebaCell_location.getFilterableKeyword() + "'");

                String loca_id_old = "";
                try {
                    while (rs.next()) {
                        loca_id_old = rs.getString("L_ID");
                        print(loca_id_old);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    //notify tableview changes
                    Integer diff = Integer.parseInt(inputAnagceri.getEditText().getText().toString()) - nashti;

                    if (diff < 0) {
                        //ჩამოწერა
                        saveNaklebi(
                                String.valueOf(row + 1),
                                loca_id,
                                edited_gpid.getFilterableKeyword(), diff, loca_id_old, ((Migeba_Cell) mTableView.getAdapter().getCellItem(8, row)).getFilterableKeyword());
                    } else {
                        //ნამატი
                        saveNamati(
                                String.valueOf(row + 1),
                                loca_id,
                                edited_gpid.getFilterableKeyword(), diff, loca_id_old, ((Migeba_Cell) mTableView.getAdapter().getCellItem(8, row)).getFilterableKeyword());
                    }

                    Edit_Card_Location(row + 1, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        try {
                            mTableView.setSelectedCell(mTableView.getSelectedColumn(), row + 1);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 500);
                alertDialog.cancel();
            }
        });

        edit_card_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Edit_Card_Location(row - 1, context);
                alertDialog.cancel();
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
                    alertDialog.cancel();
                }
            });
        }
        int rowindex = mTableView.getAdapter().getRowHeaderRecyclerViewAdapter().getItemCount() - 1;
        if (row == rowindex) {
            edit_card_next.setText("დასრულება");
            edit_card_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    FINISH_MIGEBA();
                    if (!location_map.containsValue(loca_tv.getText().toString())) {
                        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(mContext);
                        mb.setTitle("ლოკაცია");
                        mb.setMessage("ლოკაცია " + loca_tv.getText().toString() + " არ არსებობს. გსურთ დამატება?");
                        mb.setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MaterialAlertDialogBuilder change = new MaterialAlertDialogBuilder(context);
                                LayoutInflater lf = LayoutInflater.from(context);
                                View v = lf.inflate(R.layout.card_edit_location, null, false);
                                change.setView(v);
                                TextInputLayout input1;
                                TextInputLayout input2;
                                MaterialButton back = v.findViewById(R.id.button_previous);
                                MaterialButton save = v.findViewById(R.id.edit_card_nextBtn);
                                input1 = v.findViewById(R.id.card_edit_anagceri);
                                input2 = v.findViewById(R.id.vada);
                                input1.getEditText().setText(loca_tv.getText().toString());
                                input1.setEnabled(false);
                                AlertDialog changeDialog = change.create();
                                changeDialog.show();
                                back.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changeDialog.dismiss();
                                    }
                                });
                                save.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        changeDialog.dismiss();
                                        CONN_EXECUTE_SQL("insert into [A_PLUS].[dbo].[LOCATION] (L_NAME,L_INFO) values " +
                                                "('" + input1.getEditText().getText().toString() + "','" + input2.getEditText().getText().toString() + "')");
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

                                    }
                                });
                            }
                        });
                        mb.setNegativeButton("არა", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        mb.setNeutralButton("დამატება და შენახვა", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Migeba_Cell dasaxeleba = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
                                Migeba_Cell edited_lokacia = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
                                Migeba_Cell edited_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
                                Migeba_Cell edited_anagceri = new Migeba_Cell(dasaxeleba.getId(), inputAnagceri.getEditText().getText().toString());
                                Migeba_Cell edited_gloc = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
                                Migeba_Cell edited_gpid = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
                                Migeba_Cell edited_gseria = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
                                Migeba_Cell edited_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
                                Migeba_Cell edited_g_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
                                List<Migeba_Cell> rowItems = new ArrayList<>();
                                rowItems.add(dasaxeleba);
                                rowItems.add(edited_lokacia);
                                rowItems.add(edited_raodenoba);
                                rowItems.add(edited_anagceri);
                                rowItems.add(edited_gloc);
                                rowItems.add(edited_gpid);
                                rowItems.add(edited_gseria);
                                rowItems.add(edited_vada);
                                rowItems.add(edited_g_id);

                                final Handler handler = new Handler(Looper.getMainLooper());
                                saveBtn.setIcon(mContext.getDrawable(R.drawable.ic_baseline_check_24));

                                saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                                products_cache.set(row, rowItems);
                                SetTableViewAdapter(products_cache);

//                inputRaodenoba.getEditText().setText(String.valueOf(nashti) + "  |  " + String.valueOf(nashti - Integer.parseInt(charSequence.toString())));

                                ResultSet rs = CONN_RESULTSET_SQL("SELECT [L_ID]\n" +
                                        "  FROM [A_PLUS].[dbo].[LOCATION] where L_NAME='" + loca_tv.getText().toString() + "'");

                                String loca_id = "";
                                try {
                                    while (rs.next()) {
                                        loca_id = rs.getString("L_ID");
                                        print(loca_id);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                ResultSet rs2 = CONN_RESULTSET_SQL("SELECT [L_ID]\n" +
                                        "  FROM [A_PLUS].[dbo].[LOCATION] where L_NAME='" + migebaCell_location.getFilterableKeyword() + "'");

                                String loca_id_old = "";
                                try {
                                    while (rs.next()) {
                                        loca_id_old = rs.getString("L_ID");
                                        print(loca_id_old);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Integer diff = Integer.parseInt(inputAnagceri.getEditText().getText().toString()) - nashti;
                                if (diff < 0) {
                                    //ჩამოწერა
                                    saveNaklebi(
                                            String.valueOf(row + 1),

                                            loca_id,
                                            edited_gpid.getFilterableKeyword(), diff, loca_id_old, ((Migeba_Cell) mTableView.getAdapter().getCellItem(8, row)).getFilterableKeyword());
                                } else {
                                    //ნამატი
                                    saveNamati(
                                            String.valueOf(row + 1),

                                            loca_id,
                                            edited_gpid.getFilterableKeyword(), diff, loca_id_old, ((Migeba_Cell) mTableView.getAdapter().getCellItem(8, row)).getFilterableKeyword());
                                }


                            }
                        });
                        mb.show();
                    } else {

                        Migeba_Cell dasaxeleba = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
                        Migeba_Cell edited_lokacia = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
                        Migeba_Cell edited_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
                        Migeba_Cell edited_anagceri = new Migeba_Cell(dasaxeleba.getId(), inputAnagceri.getEditText().getText().toString());
                        Migeba_Cell edited_gloc = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
                        Migeba_Cell edited_gpid = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
                        Migeba_Cell edited_gseria = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
                        Migeba_Cell edited_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
                        Migeba_Cell edited_g_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
                        List<Migeba_Cell> rowItems = new ArrayList<>();
                        rowItems.add(dasaxeleba);
                        rowItems.add(edited_lokacia);
                        rowItems.add(edited_raodenoba);
                        rowItems.add(edited_anagceri);
                        rowItems.add(edited_gloc);
                        rowItems.add(edited_gpid);
                        rowItems.add(edited_gseria);
                        rowItems.add(edited_vada);
                        rowItems.add(edited_g_id);

                        final Handler handler = new Handler(Looper.getMainLooper());
                        saveBtn.setIcon(mContext.getDrawable(R.drawable.ic_baseline_check_24));

                        saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                        products_cache.set(row, rowItems);
                        SetTableViewAdapter(products_cache);

//                inputRaodenoba.getEditText().setText(String.valueOf(nashti) + "  |  " + String.valueOf(nashti - Integer.parseInt(charSequence.toString())));

                        ResultSet rs = CONN_RESULTSET_SQL("SELECT [L_ID]\n" +
                                "  FROM [A_PLUS].[dbo].[LOCATION] where L_NAME='" + loca_tv.getText().toString() + "'");

                        String loca_id = "";
                        try {
                            while (rs.next()) {
                                loca_id = rs.getString("L_ID");
                                print(loca_id);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        ResultSet rs2 = CONN_RESULTSET_SQL("SELECT [L_ID]\n" +
                                "  FROM [A_PLUS].[dbo].[LOCATION] where L_NAME='" + migebaCell_location.getFilterableKeyword() + "'");

                        String loca_id_old = "";
                        try {
                            while (rs.next()) {
                                loca_id_old = rs.getString("L_ID");
                                print(loca_id_old);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            //notify tableview changes
                            Integer diff = Integer.parseInt(inputAnagceri.getEditText().getText().toString()) - nashti;
                            if (diff < 0) {
                                //ჩამოწერა
                                saveNaklebi(
                                        String.valueOf(row + 1),
                                        loca_id,
                                        edited_gpid.getFilterableKeyword(), diff, loca_id_old, ((Migeba_Cell) mTableView.getAdapter().getCellItem(8, row)).getFilterableKeyword());
                            } else {
                                //ნამატი
                                saveNamati(
                                        String.valueOf(row + 1),
                                        loca_id,
                                        edited_gpid.getFilterableKeyword(), diff, loca_id_old, ((Migeba_Cell) mTableView.getAdapter().getCellItem(8, row)).getFilterableKeyword());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    alertDialog.cancel();
                }
            });

        }

        MaterialButton close_dialog = alertDialog.findViewById(R.id.dialog_close);


        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });


        inputAnagceri.setHint("ანაღწერი");
        inputAnagceri.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);

        inputRaodenoba.setHint("ნაშთი");
        inputRaodenoba.setEnabled(false);

        inputRaodenoba.getEditText().setText(String.valueOf(nashti));


        inputAnagceri.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int x = 0;
                try {
                    x = Integer.parseInt(charSequence.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (charSequence.length() > 0) {
                    inputRaodenoba.getEditText().setText(String.valueOf(nashti) + "  |  " + String.valueOf(x - nashti));
                } else {
                    inputRaodenoba.getEditText().setText(String.valueOf(nashti));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
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


        loca_tv = alertDialog.findViewById(R.id.location_autocomplete);
        loca_tv.setText(String.valueOf(migebaCell_location.getData()));

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


        //ლოკაციები
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
                    alertDialog.findViewById(R.id.location_autocomplete);
            editTextFilledExposedDropdown.setAdapter(adapter);

        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.d(TAG, "onClick: " + row + 1);
        edit_card_p_id.setText(String.valueOf(row + 1));
        seria.setText(String.valueOf(migebaCell_name.getData()));

        saveBtn.setOnClickListener(new View.OnClickListener() {
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
                Migeba_Cell edited_lokacia = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
                Migeba_Cell edited_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
                Migeba_Cell edited_anagceri = new Migeba_Cell(dasaxeleba.getId(), inputAnagceri.getEditText().getText().toString());
                Migeba_Cell edited_gloc = new Migeba_Cell(dasaxeleba.getId(), loca_tv.getText().toString());
                Migeba_Cell edited_gpid = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
                Migeba_Cell edited_gseria = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
                Migeba_Cell edited_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
                Migeba_Cell edited_g_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
                List<Migeba_Cell> rowItems = new ArrayList<>();
                rowItems.add(dasaxeleba);
                rowItems.add(edited_lokacia);
                rowItems.add(edited_raodenoba);
                rowItems.add(edited_anagceri);
                rowItems.add(edited_gloc);
                rowItems.add(edited_gpid);
                rowItems.add(edited_gseria);
                rowItems.add(edited_vada);
                rowItems.add(edited_g_id);

                final Handler handler = new Handler(Looper.getMainLooper());
                saveBtn.setIcon(mContext.getDrawable(R.drawable.ic_baseline_check_24));

                saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                products_cache.set(row, rowItems);
                SetTableViewAdapter(products_cache);

//                inputRaodenoba.getEditText().setText(String.valueOf(nashti) + "  |  " + String.valueOf(nashti - Integer.parseInt(charSequence.toString())));

                ResultSet rs = CONN_RESULTSET_SQL("SELECT [L_ID]\n" +
                        "  FROM [A_PLUS].[dbo].[LOCATION] where L_NAME='" + loca_tv.getText().toString() + "'");

                String loca_id = "";
                try {
                    while (rs.next()) {
                        loca_id = rs.getString("L_ID");
                        print(loca_id);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ResultSet rs2 = CONN_RESULTSET_SQL("SELECT [L_ID]\n" +
                        "  FROM [A_PLUS].[dbo].[LOCATION] where L_NAME='" + migebaCell_location.getFilterableKeyword() + "'");

                String loca_id_old = "";
                try {
                    while (rs.next()) {
                        loca_id_old = rs.getString("L_ID");
                        print(loca_id_old);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    //notify tableview changes
                    Integer diff = Integer.parseInt(inputAnagceri.getEditText().getText().toString()) - nashti;
                    if (diff < 0) {
                        //ჩამოწერა
                        saveNaklebi(
                                String.valueOf(row + 1),
                                loca_id,
                                edited_gpid.getFilterableKeyword(), diff, loca_id_old, ((Migeba_Cell) mTableView.getAdapter().getCellItem(8, row)).getFilterableKeyword());
                    } else {
                        //ნამატი
                        saveNamati(
                                String.valueOf(row + 1),
                                loca_id,
                                edited_gpid.getFilterableKeyword(), diff, loca_id_old, ((Migeba_Cell) mTableView.getAdapter().getCellItem(8, row)).getFilterableKeyword());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


        });
    }

    public static void saveNamati(String row, String P_LOC_ID, String ag_pid, Integer diff, String oldLocation, String ag_gid) {

        if (!savedProducts.contains(row)) {
            savedProducts.add(row);
        }

        changesQuery.append("UPDATE A_PLUS.dbo.INVENT set " +
                " AG_GLOCNEW = '" + P_LOC_ID + "'," +
                " AG_GLOC = '" + oldLocation + "'," +
                " AG_STAT = 1," +
                " AG_ANAGC = '" + diff + "'," +
                " AG_USER = '" + MainActivity.USER_ID + "'" +
                " where AG_PID = '" + ag_pid + "' AND AG_TIME = '" + ag_time + "' \n");

        saveProductLocation(ag_gid, P_LOC_ID);

        Log.e(TAG, "changesQuery: " + changesQuery);
    }

    public static void saveProductLocation(String G_ID, String G_LOC) {
        changesQuery.append("UPDATE A_PLUS.dbo.GET set " +
                " G_LOC = '" + G_LOC + "'" +
                " where G_ID = '" + G_ID + "' \n");

        Log.e(TAG, "changesQuery: " + changesQuery);

    }


    public static void saveNaklebi(String row, String P_LOC_ID, String ag_pid, Integer diff, String oldLocation, String ag_gid) {

        if (!savedProducts.contains(row)) {
            savedProducts.add(row);
        }

        changesQuery.append("UPDATE A_PLUS.dbo.INVENT set " +
                " AG_GLOCNEW = '" + P_LOC_ID + "'," +
                " AG_GLOC = '" + oldLocation + "'," +
                " AG_STAT = 1," +
                " AG_ANAGC = '" + diff + "'," +
                " AG_USER = '" + MainActivity.USER_ID + "'" +
                " where AG_GID = '" + ag_pid + "' AND AG_TIME = '" + ag_time + "' \n");

        saveProductLocation(ag_gid, P_LOC_ID);
        Log.e(TAG, "changesQuery: " + changesQuery);

    }

    public static void SetTableViewAdapter(List<List<Migeba_Cell>> products_cache) {
        mTableView.showColumn(5);
        mTableView.getAdapter().setAllItems(getColumnHeaders(), getRowHeaders(), products_cache);

        mTableView.setColumnWidth(0, 350);
        mTableView.setColumnWidth(1, 200);
        mTableView.setColumnWidth(2, 170);
        mTableView.setColumnWidth(3, 170);
        mTableView.setColumnWidth(4, 125);
        mTableView.setColumnWidth(5, 170);


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
    public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellView,
                                  final int column,
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
    public void onColumnHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder
                                                    columnHeaderView, int column) {
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
    public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder columnHeaderView,
                                          int
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
    public void onRowHeaderDoubleClicked(@NonNull RecyclerView.ViewHolder rowHeaderView,
                                         int row) {
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
    public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder rowHeaderView,
                                       int row) {

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
