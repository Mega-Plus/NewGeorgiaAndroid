package com.example.migeba;

import static android.content.ContentValues.TAG;
import static com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewListener.locationsList;
import static com.example.migeba.MainActivity.CONN_EXECUTE_SQL;
import static com.example.migeba.MainActivity.CONN_RESULTSET_SQL;
import static com.example.migeba.MainActivity.ConnectAndPrint;
import static com.example.migeba.MainActivity.SELECTED_USER;
import static com.example.migeba.MainActivity._parseTime;
import static com.example.migeba.MainActivity.getAppContext;
import static com.example.migeba.Utils.DebugTools.print;
import static com.example.migeba.Utils.UnicodeConvertorUtil.geoToEn;
import static com.example.migeba.Utils.geotranslate.TOGEO;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Icon;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.filter.Filter;
import com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewAdapter;
import com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewModel;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_Cell;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_ColumnHeader;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_RowHeader;
import com.example.migeba.Utils.DateInputMask;
import com.example.migeba.Utils.DebugTools;
import com.example.migeba.Utils.UnicodeConvertorUtil;
import com.example.migeba.Utils.geotranslate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class migeba extends AppCompatActivity {
    Context context;

    static MaterialButton productEditButton;

    static int refreshTabSizeCount = 0;
    public static List<String> savedProducts = new ArrayList<>();

    public static TableView mTableView;
    public static String Zednadebi;
    //    public static String mimdinare_zednadebi_g_id;
    public static List<String> changesQuery;
    public static List<String> changesGIds;
    public static List<String> printedGIds;
    public static AutoCompleteTextView loca_tv;


    public static List<List<Migeba_Cell>> products_cache;

    public static ProgressBar progressbar;
    private ListView lv_zednadebi;
    static ExtendedFloatingActionButton finish_accept;
    public static MaterialButton zednadebi_fab;
    static Activity activity;
    private static String clientName;
    public static Filter filter;
    public static String filterString = new String("");

    public static FloatingActionButton scanner_button;
    public static MaterialButton dialog_search_product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        config();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_migeba);
        finish_accept = findViewById(R.id.finish_accept);
        productEditButton = findViewById(R.id.productEditButton);
        progressbar = findViewById(R.id.accept_progress_bar);
        context = this;
        activity = this;
        savedProducts = new ArrayList<>();
        mTableView = findViewById(R.id.tableview);
        lv_zednadebi = findViewById(R.id.lv_zednadebi);
        zednadebi_fab = findViewById(R.id.zednadebi_fab);
        zednadebi_fab.setVisibility(View.GONE);

        scanner_button = findViewById(R.id.scanner_button);
        productEditButton.setVisibility(View.GONE);
        scanner_button.setVisibility(View.GONE);
        finish_accept.hide();
//        finish_accept.setVisibility(View.GONE);
        changesQuery = new ArrayList<String>();
        changesGIds = new ArrayList<String>();
        printedGIds = new ArrayList<String>();

        finish_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FINISH_MIGEBA();
            }
        });
        AsyncInitializeZednadebi();

        scanner_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!filterString.equals("")) {
                    Filter clearFilter = new Filter(mTableView);
                    clearFilter.set("");
                    filterString = "";
                    mTableView.filter(clearFilter);
                    scanner_button.setImageDrawable(getDrawable(R.drawable.ic_baseline_barcode_scanner));
                    return;
                }

                MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(context);
                mb.setTitle("დაასკანერეთ შტრიხკოდი");
                View v = getLayoutInflater().inflate(R.layout.card_scanner, null);
                mb.setView(v);
                TextInputEditText et_scanner = v.findViewById(R.id.et_scanner);
                et_scanner.requestFocus();
                mb.setPositiveButton("ძებნა", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!et_scanner.getText().toString().equals("")) {
                            SearchByScanner(et_scanner.getText().toString());
                            scanner_button.setImageDrawable(getDrawable(R.drawable.ic_item_delete));
                            Snackbar.make(getWindow().getDecorView().getRootView(), et_scanner.getText().toString(), BaseTransientBottomBar.LENGTH_SHORT).show();
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

                            if (!charSequence.toString().equals("")) {
                                SearchByScanner(charSequence.toString());
                                scanner_button.setImageDrawable(getDrawable(R.drawable.ic_item_delete));
                                Snackbar.make(getWindow().getDecorView().getRootView(), charSequence.toString(), BaseTransientBottomBar.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }

                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }
        });

        productEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (migeba.mTableView.getAdapter().getCellColumnItems(0).isEmpty()) {
                    return;
                }

                if (!migeba.filterString.isEmpty()) {
                    final String tempraryString = migeba.filterString;
                    String filter = migeba.filterString;
                    Migeba_Cell ro = ((Migeba_Cell) migeba.mTableView.getAdapter().getCellRowItems(mTableView.getSelectedRow()).get(0));
                    SetFilter("");

                    int i = migeba.mTableView.getAdapter().getCellColumnItems(0).indexOf(ro);
                    migeba.Edit_Card(i, context);
                    SetFilter(tempraryString);
                } else {
                    migeba.Edit_Card(mTableView.getSelectedRow(), context);
                }

            }


//        if (mTableView != null) AsyncInitializeTableView(findViewById(R.id.accept_progress_bar));
        });
    }

    public static void SearchByScanner(String filterText) {
        if (filterText.endsWith("\n")) {
            //ხელის სკანერით არის დასკანერებული
            String split = filterText.split(" ")[0];
            SetFilter(split);
        } else {
            SetFilter(filterText);
        }
    }

    public static void SetFilter(String filterText) {
        filterString = filterText;
        filter.set(filterText);
        mTableView.filter(filter);
    }

    public void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void Edit_Card(Integer row, Context mContext) throws NullPointerException {
//        Context mContext = mTableView.getContext();

        AlertDialog.Builder mb = new AlertDialog.Builder(mContext, android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.card_edit_migeba, null, false);
        v.setBackgroundColor(Color.WHITE);
        v.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
        mb.setView(v);
        AlertDialog alertDialog = mb.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        alertDialog.show();
        TextInputLayout inputseria = alertDialog.findViewById(R.id.card_edit_anagceri);
        TextInputLayout inputvada = alertDialog.findViewById(R.id.vada);
        TextInputLayout inputraodenoba = alertDialog.findViewById(R.id.card_edit_raodenoba);
        TextInputLayout inputmwarmoebeli = alertDialog.findViewById(R.id.card_edit_mwarmoebeli);
        TextInputLayout inputraodenobayutshi = alertDialog.findViewById(R.id.card_edit_raodenobayutshi);
        TextInputLayout inputqveyana = alertDialog.findViewById(R.id.card_edit_qveyana);
        TextInputLayout inputtemp = alertDialog.findViewById(R.id.card_edit_temp);
        TextInputLayout inputsabechidraodenoba = alertDialog.findViewById(R.id.card_edit_sabechdiraodenoba);
        TextInputLayout inputnumerusi = alertDialog.findViewById(R.id.card_edit_numerusi);
        TextView expectedText = alertDialog.findViewById(R.id.expectedText);


        TextView seria = alertDialog.findViewById(R.id.location_name);
        TextView g_id = alertDialog.findViewById(R.id.g_id);
        Migeba_Cell migebaCell_name = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
        Migeba_Cell migebaCell_seria = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, row);
        Migeba_Cell migebaCell_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
        Migeba_Cell migebaCell_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, row);
        Migeba_Cell migebaCell_mwarmoebeli = (Migeba_Cell) mTableView.getAdapter().getCellItem(4, row);
        Migeba_Cell migebaCell_temp = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);
        Migeba_Cell migebaCell_raodenobaYutshi = (Migeba_Cell) mTableView.getAdapter().getCellItem(6, row);
        Migeba_Cell migebaCell_qveyana = (Migeba_Cell) mTableView.getAdapter().getCellItem(7, row);
        Migeba_Cell migebaCell_location = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
//        Migeba_Cell migebaCell9 = (Migeba_Cell) mTableView.getAdapter().getCellItem(9, row); //// GAMOYENEBA AR QVS
//        Migeba_Cell migebaCell10 = (Migeba_Cell) mTableView.getAdapter().getCellItem(10, row); /// GAMOYENEBA AR QVS
        Migeba_Cell migebaCell_numerusi = (Migeba_Cell) mTableView.getAdapter().getCellItem(11, row);
        Migeba_Cell migebaCell_expectedQuant = (Migeba_Cell) mTableView.getAdapter().getCellItem(12, row);
        Migeba_Cell g_p_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(9, row);

//        Migeba_Cell g_id_Migeba_cell = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, row);
        inputseria.getEditText().setText(String.valueOf(migebaCell_seria.getData()));
        inputvada.getEditText().setText(String.valueOf(migebaCell_vada.getData()));
        inputraodenoba.getEditText().setText(String.valueOf(migebaCell_raodenoba.getData()));
        inputmwarmoebeli.getEditText().setText(String.valueOf(migebaCell_mwarmoebeli.getData()));
        inputtemp.getEditText().setText(String.valueOf(migebaCell_temp.getData()));
        inputraodenobayutshi.getEditText().setText(String.valueOf(migebaCell_raodenobaYutshi.getData()));
        inputqveyana.getEditText().setText(String.valueOf(migebaCell_qveyana.getData()));
//        inputqveyana.getEditText().setText(String.valueOf(migebaCell_location.getData()));
        inputnumerusi.getEditText().setText(String.valueOf(migebaCell_numerusi.getData()));
        //        inputsabechidraodenoba.getEditText().setText(String.valueOf(migebaCell_location.getData()));
//                input5.getEditText().setText(String.valueOf(cell5.getData()));
        loca_tv = alertDialog.findViewById(R.id.location_autocomplete);
        loca_tv.setText(String.valueOf(migebaCell_location.getData()));

        dialog_search_product = v.findViewById(R.id.dialog_search_product);
        dialog_search_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchProduct(mContext, alertDialog);
            }
        });

        int expectedQuantity = 0;

        try {
            expectedQuantity = Integer.parseInt(migebaCell_expectedQuant.getData().toString().split("\\.")[0]);
        } catch (Exception e) {
            expectedText.setText(migebaCell_expectedQuant.getData().toString());

            e.printStackTrace();
        }

        try {
            expectedText.setText(String.valueOf(expectedQuantity) + "  |  " + String.valueOf(Integer.parseInt(inputraodenoba.getEditText().getText().toString()) - expectedQuantity));

        } catch (Exception e) {
            e.printStackTrace();
        }

        int finalExpectedQuantity = expectedQuantity;
        inputraodenoba.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

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
                    expectedText.setText(String.valueOf(finalExpectedQuantity) + "  |  " + String.valueOf(x - finalExpectedQuantity));
                } else {
                    expectedText.setText(String.valueOf(finalExpectedQuantity));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
//        scanner_button_edit = v.findViewById(R.id.scanner_button_edit);
//        scanner_button_edit.setVisibility(View.GONE);

        new DateInputMask(inputvada.getEditText());

        MaterialButton saveBtn = alertDialog.findViewById(R.id.edit_card_nextBtn);
        MaterialButton edit_card_p_id = alertDialog.findViewById(R.id.edit_card_p_id);
        MaterialButton edit_card_next = alertDialog.findViewById(R.id.edit_card_nextAndSave);
        MaterialButton edit_card_previous = alertDialog.findViewById(R.id.button_previous);
        MaterialButton close_dialog = alertDialog.findViewById(R.id.dialog_close);


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
                                    ConnectAndPrint(mContext,
                                            inputseria.getEditText().getText().toString().replace(" ", "") + " " +
                                                    inputvada.getEditText().getText().toString().replace(" ", "") + " " +
                                                    g_p_id.getFilterableKeyword().replace(" ", ""),
                                            geotranslate.FROMGEO(migebaCell_name.getFilterableKeyword()),
                                            SELECTED_USER.toLowerCase() + " " + inputtemp.getEditText().getText().toString(),
                                            "",
                                            migebaCell_raodenoba.getFilterableKeyword());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, 750 * i);

                    }
                    printedGIds.add(g_p_id.getFilterableKeyword().replace(" ", ""));
                } catch (Exception e) {
                    Toast.makeText(mContext, "ვერ დაიბეჭდა!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        HashMap<Integer, String> location_map = new HashMap<>();
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

        close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        ///REZERVI


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
                        CONN_EXECUTE_SQL("update t1  set   [P_LOC_REZERVI]='" + rezervi_edittext.getText().toString() + "' from [A_PLUS].[dbo].[PRODUCT] as t1\n" +
                                "  left join A_PLUS.dbo.GET on P_ID = G_P_ID\n" +
                                "  where G_SERIA = '" + migebaCell_seria.getData().toString() + "'");
                        TransitionManager.beginDelayedTransition((ViewGroup) alertDialog.getWindow().getDecorView().getRootView(), transformCloseRezervi);
                        dialog_secondary_bg.setVisibility(View.INVISIBLE);
                        dialog_secondary.setVisibility(View.INVISIBLE);
                        button_rezervi.setVisibility(View.VISIBLE);

                    }
                });

                ResultSet query_rezervi = CONN_RESULTSET_SQL("SELECT DISTINCT top 1\n" +
                        "      [P_LOC_REZERVI]\n" +
                        "  FROM [A_PLUS].[dbo].[PRODUCT]\n" +
                        "  left join A_PLUS.dbo.GET on P_ID = G_P_ID\n" +
                        "  where G_SERIA = '" + migebaCell_seria.getData().toString() + "'");


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
                                    "  left join A_PLUS.dbo.GET on P_ID = G_P_ID\n" +
                                    "  where G_SERIA = '" + migebaCell_seria.getData().toString() + "'");
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
        int rowindex = mTableView.getAdapter().getRowHeaderRecyclerViewAdapter().getItemCount() - 1;


        edit_card_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int nextRow = row + 1;
                while (true) {
                    if (!savedProducts.contains(String.valueOf(nextRow + 1))) {
                        if (rowindex + 1 == nextRow) {
                            Edit_Card(0, mContext);
                        } else {
                            Edit_Card(nextRow, mContext);
                        }
                        break;
                    }
                    nextRow++;
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
                Edit_Card(row - 1, mContext);
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
        if (row == rowindex) {
            edit_card_next.setText("შემდეგი");
            edit_card_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Edit_Card(0, mContext);


                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            try {
                                mTableView.setSelectedCell(mTableView.getSelectedColumn(), 0);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 500);
                    alertDialog.cancel();


                }
            });

        }


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
//            ResultSet rs = CONN_RESULTSET_SQL("SELECT g_loc\n" +
//                    "  FROM [A_PLUS].[dbo].[GET] where G_P_ID='" + g_p_id.getData() + "' group by G_LOC");
            ResultSet rs = CONN_RESULTSET_SQL("SELECT g_loc\n" +
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
        if (savedProducts.contains(String.valueOf(row + 1))) {
            saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
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
                hideKeyboardFrom(mContext, view);
                Migeba_Cell migebaCell00 = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
                Migeba_Cell edited_seria = new Migeba_Cell(migebaCell_seria.getId(), inputseria.getEditText().getText().toString());
                Migeba_Cell edited_vada = new Migeba_Cell(migebaCell_vada.getId(), inputvada.getEditText().getText().toString());
                Migeba_Cell edited_raodenoba = new Migeba_Cell(migebaCell_raodenoba.getId(), inputraodenoba.getEditText().getText().toString());
                Migeba_Cell edited_mwarmoebeli = new Migeba_Cell(migebaCell_mwarmoebeli.getId(), inputmwarmoebeli.getEditText().getText().toString());
                Migeba_Cell edited_temp = new Migeba_Cell(migebaCell_mwarmoebeli.getId(), inputtemp.getEditText().getText().toString());
                Migeba_Cell edited_raodenobayutshi = new Migeba_Cell(migebaCell_temp.getId(), inputraodenobayutshi.getEditText().getText().toString());
                Migeba_Cell edited_qveyana = new Migeba_Cell(migebaCell_raodenobaYutshi.getId(), inputqveyana.getEditText().getText().toString());
                Migeba_Cell edited_lokacia = new Migeba_Cell(migebaCell_qveyana.getId(), loca_tv.getText().toString());
                Migeba_Cell edited_sabechdiRaodenoba = new Migeba_Cell(edited_seria.getId(), inputsabechidraodenoba.getEditText().getText().toString());
                Migeba_Cell edited_numerusi = new Migeba_Cell(edited_seria.getId(), inputnumerusi.getEditText().getText().toString());

                List<Migeba_Cell> rowItems = new ArrayList<>();
                rowItems.add(migebaCell00);
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
//                rowItems.add(edited_sabechdiRaodenoba);
                rowItems.add(edited_numerusi);
                rowItems.add(migebaCell_expectedQuant);

                final Handler handler = new Handler(Looper.getMainLooper());
                saveBtn.setIcon(mContext.getDrawable(R.drawable.ic_baseline_check_24));
                saveBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

                products_cache.set(row, rowItems);
                SetTableViewAdapter(products_cache);
//                Migeba_Cell g_id_cell = (Migeba_Cell) ((Migeba_Cell) mTableView.getAdapter().getCellItem(9, row)).getData();


                saveChanges(
                        String.valueOf(row + 1),
                        inputseria.getEditText().getText().toString(),
                        inputvada.getEditText().getText().toString(),
                        inputraodenoba.getEditText().getText().toString(),
                        inputmwarmoebeli.getEditText().getText().toString(),
                        inputraodenobayutshi.getEditText().getText().toString(),
                        inputqveyana.getEditText().getText().toString(),
                        loca_tv.getText().toString(),
                        g_p_id.getData().toString(),
                        inputtemp.getEditText().getText().toString(),
                        inputnumerusi.getEditText().getText().toString());

                if (row == rowindex) {
                    alertDialog.dismiss();
                } else {

                    Edit_Card(row + 1, mContext);
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
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void SetTableViewAdapter(List<List<Migeba_Cell>> products_cache) {
//        mTableView.showColumn(5);
//        mTableView.showColumn(6);
//        mTableView.showColumn(7);
//        mTableView.showColumn(8);
        mTableView.showColumn(9);
        mTableView.showColumn(10);
        mTableView.getAdapter().setAllItems(getColumnHeaders(), getRowHeaders(zednadebi_fab.getText().toString()), products_cache);

        ;
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
                mTableView.hideColumn(9);
                mTableView.hideColumn(10);
            }
        }, 400);

    }

    private void config() {
        findViewById(android.R.id.content).setTransitionName("go");
        setEnterSharedElementCallback(new MaterialContainerTransformSharedElementCallback());
        MaterialContainerTransform transform = new MaterialContainerTransform();
        transform.addTarget(android.R.id.content);
        transform.setDuration(300L);

        getWindow().setSharedElementEnterTransition(transform);
        getWindow().setSharedElementReturnTransition(transform);
    }

    public static void cardEditHelperInfo(View view) {
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG).setDuration(Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        TextView tv = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setMaxLines(3);
        snackbar.show();
    }

    public void FINISH_MIGEBA() {

        if (savedProducts.size() < mTableView.getAdapter().getCellColumnItems(0).size()) {
            Toast.makeText(context, "ყველა პროდუქტი არაა შესრულებული!", Toast.LENGTH_LONG).show();
            return;
        }

        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(finish_accept.getContext());
        mb.setMessage("ნამდვილად გსურთ მიღების დასრულება?");
        mb.setTitle("დასრულება");
        mb.setPositiveButton("კი", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ///SET FINISHED
                String deleteQuery = "DELETE FROM [A_PLUS].[dbo].[GET_EDIT] WHERE G_ZDN = '" + zednadebi_fab.getText().toString() + "'";


                CONN_EXECUTE_SQL(deleteQuery);
                String finishQuery = "update [A_PLUS].[dbo].[GET] " +
                        "SET G_ACT = 2 " +
                        "where G_ZDN = '" + zednadebi_fab.getText().toString() + "'";

                CONN_EXECUTE_SQL(finishQuery);
                String changequerylast = "";
                for (String str : changesQuery
                ) {
                    changequerylast += str;
                }
                CONN_EXECUTE_SQL(changequerylast);

                for (int j = 0; j < mTableView.getAdapter().getCellColumnItems(0).size(); j++) {
                    int finalJ = j;


                    new Handler().postDelayed(new Runnable() {


                        public void run() {
                            try {
                                Migeba_Cell g_p_id = (Migeba_Cell) mTableView.getAdapter().getCellItem(9, finalJ);
                                if (printedGIds.contains(g_p_id.getData().toString())) {
                                    Log.e(TAG, "ბეჭდვა - " + g_p_id.getData().toString() + " უკვე დაბეჭდილია");
                                    return;
                                }
                                Migeba_Cell migebaCell_name = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, finalJ);

                                Migeba_Cell migebaCell_seria = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, finalJ);
                                Migeba_Cell migebaCell_vada = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, finalJ);
//                    if (changesQuery.toString().contains(migebaCell_seria.getFilterableKeyword().toString())) {
//                        continue;
//                    }
                                Migeba_Cell migebaCell_raodenoba = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, finalJ);
                                Migeba_Cell migebaCell_temp = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, finalJ);
                                Migeba_Cell migebaCell_location = (Migeba_Cell) mTableView.getAdapter().getCellItem(8, finalJ);

                                ConnectAndPrint(migeba.this,
                                        String.valueOf(migebaCell_seria.getData()).replace(" ", "") + " " +
                                                String.valueOf(migebaCell_vada.getData()).replace(" ", "") + " " +
                                                g_p_id.getFilterableKeyword().replace(" ", ""),
                                        geotranslate.FROMGEO(migebaCell_name.getFilterableKeyword()),
                                        SELECTED_USER + " " + migebaCell_vada.getData().toString(),
                                        "",
                                        "");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 750 * j);

                }
                activity.finish();
            }
        });
        mb.setIcon(R.drawable.ic_baseline_receipt_long_24);
        mb.setNegativeButton("არა", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        mb.show();
    }

    public static void searchProduct(Context mContext, AlertDialog mainAlertDialog) {
        if (!filterString.equals("")) {
            SetFilter("");
            scanner_button.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_barcode_scanner));
            return;
        }

        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(mContext);
        mb.setTitle("დაასკანერეთ შტრიხკოდი");
        View v = activity.getLayoutInflater().inflate(R.layout.card_scanner, null);
        mb.setView(v);
        TextInputEditText et_scanner = v.findViewById(R.id.et_scanner);
        et_scanner.requestFocus();
        mb.setPositiveButton("ძებნა", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!et_scanner.getText().toString().equals("")) {
                    SearchByScanner(et_scanner.getText().toString());
                    mainAlertDialog.dismiss();
                    scanner_button.setImageDrawable(mContext.getDrawable(R.drawable.ic_item_delete));
                    Snackbar.make(activity.getWindow().getDecorView().getRootView(), et_scanner.getText().toString(), BaseTransientBottomBar.LENGTH_SHORT).show();
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

                    if (!charSequence.toString().equals("")) {
                        SearchByScanner(charSequence.toString());
                        scanner_button.setImageDrawable(mContext.getDrawable(R.drawable.ic_item_delete));
                        Snackbar.make(activity.getWindow().getDecorView().getRootView(), charSequence.toString(), BaseTransientBottomBar.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        mainAlertDialog.dismiss();
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

//    public static void searchProduct(Context mContext) {
//        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(mContext);
//        mb.setTitle("დაასკანერეთ შტრიხკოდი");
//        LayoutInflater inflater = LayoutInflater.from(mContext);
//        View v = inflater.inflate(R.layout.card_scanner, null, false);
//        mb.setView(v);
//        TextInputEditText et_scanner = v.findViewById(R.id.et_scanner);
//        et_scanner.requestFocus();
//        mb.setPositiveButton("ძებნა", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                String seria = et_scanner.getText().toString();
//
//                Filter fltr = new Filter(mTableView);
//                fltr.set(seria);
//                dialogInterface.cancel();
////                alertDialog.cancel();
//            }
//        });
//        mb.setNegativeButton("უკან", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.cancel();
//            }
//        });
//        AlertDialog alertDialogg = mb.create();
//        alertDialogg.show();
//
//        et_scanner.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().endsWith("\n")) {
//
//                    String seria = charSequence.toString().split("\n")[0];
//
//                    Filter fltr = new Filter(mTableView);
//                    fltr.set(seria);
//                    alertDialogg.cancel();
//
//
//                    if (mTableView.getAdapter().getCellColumnItems(0).size() == 1) {
//                        Edit_Card(0,mContext);
//                    }
//
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
//    }

    public static void saveChanges(String row, String G_SERIA, String G_EXP, String G_QUANT, String MAN_name, String P_ERT, String MAN_Q_name, String P_LOC_ID, String g_id, String P_TEMP, String P_NUMERUS) {

        String G_EXP_UNFORMATTED = G_EXP.replaceAll("\\/", "");


        if (!savedProducts.contains(row)) {
            savedProducts.add(row);
        }

        changesQuery.add("UPDATE A_PLUS.dbo.GET_EDIT set " +
                " G_SERIA = '" + G_SERIA + "'," +
                " G_EXP = '" + G_EXP + "'," +
                " G_QUANT = '" + G_QUANT + "'," +
                " MAN_name = '" + MAN_name + "'," +
                " P_ERT = '" + P_ERT + "'," +
                " MAN_Q_name = '" + geoToEn(MAN_Q_name) + "'," +
                " P_TEMP = '" + (P_TEMP) + "'," +
                " P_NUMERUS = '" + (P_NUMERUS) + "'," +
                " P_LOC_ID = '" + P_LOC_ID + "'" +
                " where g_id = '" + g_id + "' and G_ZDN = '" + zednadebi_fab.getText().toString() + "'" +
                "\n" +
                "if @@rowcount = 0 and @@error = 0\n" +
                "insert into A_PLUS.dbo.GET_EDIT (G_SERIA, P_TEMP, G_QUANT, G_EXP, g_id, G_ZDN) VALUES (NULL,NULL,NULL,NULL,'" + g_id + "' ,'" + zednadebi_fab.getText().toString() + "') \n" +
                "UPDATE A_PLUS.dbo.GET_EDIT set " +
                " G_SERIA = '" + G_SERIA + "'," +
                " G_EXP = '" + G_EXP + "'," +
                " G_QUANT = '" + G_QUANT + "'," +
                " MAN_name = '" + (MAN_name) + "'," +
                " P_TEMP = '" + (P_TEMP) + "'," +
                " P_NUMERUS = '" + (P_NUMERUS) + "'," +
                " P_ERT = '" + P_ERT + "'," +
                " MAN_Q_name = '" + geoToEn(MAN_Q_name) + "'," +
                " P_LOC_ID = '" + P_LOC_ID + "'" +
                " where g_id = '" + g_id + "'" +
                "\n");

        changesGIds.add(g_id);

        Log.e(TAG, "changesQuery: " + changesQuery);


    }

    public static void setProductEditButtonText() {
        productEditButton.setVisibility(View.VISIBLE);
    }

    public void AsyncInitializeTableView(String zednadebi_id) {
        AsyncTask<Void, Void, List<List<Migeba_Cell>>> asyncTask = new AsyncTask<Void, Void, List<List<Migeba_Cell>>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<List<Migeba_Cell>> doInBackground(Void... params) {
                Zednadebi = zednadebi_id;
                return getProducts(zednadebi_id);
            }


            @Override
            protected void onPostExecute(List<List<Migeba_Cell>> products) {
                super.onPostExecute(products);
                progressbar.setVisibility(View.GONE);
                products_cache = products;
                Migeba_TableViewModel migebaTableViewModel = new Migeba_TableViewModel();
                final Handler handler = new Handler(Looper.getMainLooper());
                zednadebi_fab.setText(zednadebi_id);
                finish_accept.setVisibility(View.VISIBLE);
                zednadebi_fab.setVisibility(View.VISIBLE);
                Migeba_TableViewAdapter migebaTableViewAdapter = new Migeba_TableViewAdapter(migebaTableViewModel);
                mTableView.setAdapter(migebaTableViewAdapter);
                mTableView.setTableViewListener(new Migeba_TableViewListener(mTableView));
                migebaTableViewAdapter.setAllItems(getColumnHeaders(), getRowHeaders(zednadebi_id), products);

                mTableView.hideColumn(9);
                mTableView.hideColumn(10);
                mTableView.setColumnWidth(0, 500);
                mTableView.setColumnWidth(1, 250);
                mTableView.setColumnWidth(2, 170);
                mTableView.setColumnWidth(3, 170);
                mTableView.setColumnWidth(4, 265);
                mTableView.setColumnWidth(5, 170);
                mTableView.setColumnWidth(6, 230);
                mTableView.setColumnWidth(8, 170);
                mTableView.setColumnWidth(9, 170);
                filter = new Filter(mTableView);
                filter.set(filterString);

                scanner_button.setVisibility(View.VISIBLE);

                Edit_Card(0, context);
            }
        };

        asyncTask.execute();
    }

    public void AsyncInitializeZednadebi() {
        String[] from = {"G_ZDN", "G_TIME", "jam", "d_name"};
        int[] to = {R.id.L_NAME, R.id.L_INFO, R.id.zednadebi_button, R.id.zednadebi_dist_name};
        final ArrayList<HashMap<String, Object>>[] list = new ArrayList[]{new ArrayList<>()};
        View progressbar = findViewById(R.id.accept_progress_bar);
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... params) {

                list[0] = getZednadebebi();
                return null;
            }

            @Override
            protected void onPostExecute(Void vvoid) {
                super.onPostExecute(vvoid);
                if (list[0].size() == 0) {
                    progressbar.setVisibility(View.GONE);
                    Snackbar.make(getWindow().getDecorView().getRootView(), "ზედნადებები ვერ მოიძებნა!", BaseTransientBottomBar.LENGTH_LONG).show();
                } else {
                    SimpleAdapter simpleAdapter = new SimpleAdapter(migeba.this, list[0], R.layout.card_zednadebi, from, to);
                    lv_zednadebi.setAdapter(simpleAdapter);
                    lv_zednadebi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            TextView zednadebi_id = view.findViewById(R.id.L_NAME);
                            lv_zednadebi.setVisibility(View.GONE);
                            lv_zednadebi = new ListView(context);
                            lv_zednadebi.setAdapter(null);
                            zednadebi_fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Snackbar.make(zednadebi_fab, "არჩეულია ზედნადები - " + zednadebi_id.getText().toString(), Snackbar.LENGTH_SHORT).show();

                                }
                            });
                            AsyncInitializeTableView(zednadebi_id.getText().toString());

                        }
                    });
                }

            }
        };

        asyncTask.execute();
    }

    public static List<Migeba_ColumnHeader> getColumnHeaders() {
        //G_SERIA,G_EXP,G_QUANT,G_PRICE,G_BAR1,P_NAME,P_FORM,P_ERT,P_LOC_ID
        List<Migeba_ColumnHeader> migebaColumnHeaders = new ArrayList<>();
        Migeba_ColumnHeader P_NAME = new Migeba_ColumnHeader("0", "დასახელება");
        Migeba_ColumnHeader G_SERIA = new Migeba_ColumnHeader("1", "სერია");
//        Migeba_ColumnHeader G_PRICE = new Migeba_ColumnHeader("2", "ფასი");
        Migeba_ColumnHeader G_EXP = new Migeba_ColumnHeader("2", "ვადა");
        Migeba_ColumnHeader G_QUANT = new Migeba_ColumnHeader("3", "რაოდენობა");
        Migeba_ColumnHeader G_PRINT_RAOD = new Migeba_ColumnHeader("4", "ბეჭდვის რაოდენობა");
        Migeba_ColumnHeader MAN_name = new Migeba_ColumnHeader("5", "მწარმოებელი");
        Migeba_ColumnHeader P_ERT = new Migeba_ColumnHeader("6", "ყუთში რაოდენობა");
        Migeba_ColumnHeader MAN_Q_name = new Migeba_ColumnHeader("7", "ქვეყანა");
        Migeba_ColumnHeader P_TEMP = new Migeba_ColumnHeader("7", "ტემპერატურა");
        Migeba_ColumnHeader P_LOC_ID = new Migeba_ColumnHeader("8", "ლოკაცია");
        Migeba_ColumnHeader G_ID = new Migeba_ColumnHeader("9", "ID");
        Migeba_ColumnHeader G_P_ID = new Migeba_ColumnHeader("9", "G_P_ID");
        Migeba_ColumnHeader P_NUMERUS = new Migeba_ColumnHeader("9", "ნუმერუსი");
        Migeba_ColumnHeader G_QUANT_EXPECT = new Migeba_ColumnHeader("9", "რაოდ2");
        migebaColumnHeaders.add(P_NAME);
        migebaColumnHeaders.add(G_SERIA);
        migebaColumnHeaders.add(G_EXP);
        migebaColumnHeaders.add(G_QUANT);
        migebaColumnHeaders.add(MAN_name);
        migebaColumnHeaders.add(P_TEMP);
        migebaColumnHeaders.add(P_ERT);
        migebaColumnHeaders.add(MAN_Q_name);
        migebaColumnHeaders.add(P_LOC_ID);
        migebaColumnHeaders.add(G_ID);
        migebaColumnHeaders.add(G_P_ID);
        migebaColumnHeaders.add(P_NUMERUS);
        migebaColumnHeaders.add(G_QUANT_EXPECT);
        return migebaColumnHeaders;
    }

    public static List<Migeba_RowHeader> getRowHeaders(String zednadebi_id) {
        List<Migeba_RowHeader> migebaRowHeaderList = new ArrayList<>();
        int ROW_COUNT = 5;
        ResultSet rs2 = CONN_RESULTSET_SQL("SELECT COUNT(*) FROM [A_PLUS].[dbo].[GET] where G_ZDN = '" + zednadebi_id + "' AND G_ACT = 1");
        try {
            while (rs2.next()) {
                ROW_COUNT = rs2.getInt(1) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ROW_COUNT > 1000) ROW_COUNT = 1000;
        for (int i = 1; i < ROW_COUNT; i++) {
            Migeba_RowHeader header = new Migeba_RowHeader(String.valueOf(i), String.valueOf(i));
            migebaRowHeaderList.add(header);
        }


        return migebaRowHeaderList;
    }

    private static ArrayList<HashMap<String, Object>> getZednadebebi() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        ResultSet rs2 = CONN_RESULTSET_SQL("SELECT * from(select  G_D_ID,[G_ZDN],[G_TIME] ,cast (sum([G_QUANT]*[G_PRICE]) as decimal(18,2)) as jam  \n" +
                "                FROM [A_PLUS].[dbo].[GET] where G_ACT=1 group by [G_ZDN],[G_TIME] ,G_D_ID)as t2\n" +
                "                outer apply  (select d_name from [A_PLUS].[dbo].[DISTRIBUTOR] where D_ID=G_D_ID)as t3");
        try {
            while (rs2.next()) {
                HashMap<String, Object> map = new HashMap<>();
//                map.put("G_ID", rs2.getString("G_ID"));
                map.put("G_ZDN", rs2.getString("G_ZDN"));
                map.put("G_TIME", _parseTime(rs2.getString("G_TIME")));
                map.put("jam", rs2.getString("jam") + "₾");
                map.put("d_name", TOGEO(rs2.getString("d_name")));


                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static List<List<Migeba_Cell>> getProducts(String zednadebi_id) {
        List<List<Migeba_Cell>> list = new ArrayList<>();

        ResultSet rs2 = CONN_RESULTSET_SQL("DELETE FROM [A_PLUS].[dbo].[GET_EDIT] where G_ZDN = '" + zednadebi_id + "'\nSELECT G_P_ID,G_ID,G_SERIA,G_EXP,G_QUANT,G_PRICE,G_D_ID,MAN_Q_name,\n" +
                "[MAN_name],G_MAN_ID,G_MAN_Q_ID,G_ZDN,G_TIME,G_BAR1,P_ID,P_NAME,P_FORM,P_ERT,L_NAME,P_LOC_ID\n" +
                "FROM [A_PLUS].[dbo].[GET]  as t1\n" +
                "left join A_PLUS.dbo.PRODUCT on P_ID=g_p_id \n" +
                "left join A_PLUS.dbo.manufacturer_Q on [G_MAN_Q_ID]=[MAN_Q_ID]\n" +
                " left join A_PLUS.dbo.manufacturer on [G_MAN_Q_ID] = MAN_ID\n" +
                " outer apply (SELECT TOP 1 [G_LOC]  FROM [A_PLUS].[dbo].[GET] where  G_SERIA=t1.G_SERIA and G_P_ID=t1.G_P_ID and G_ID!=t1.G_ID order by G_TIME desc) as t4\n" +
                "\n" +
                " left join A_PLUS.dbo.LOCATION\n" +
                " on L_ID = t4.G_LOC\n" +
                "  where G_ZDN = '" + zednadebi_id + "' AND G_ACT = 1 order by G_ID");

        try {
            int i = 0;
            while (rs2.next()) {
                i++;
                //G_SERIA,G_EXP,G_QUANT,G_PRICE,G_BAR1,P_NAME,P_FORM,P_ERT,P_LOC_ID
                List<Migeba_Cell> migebaCellList = new ArrayList<>();
                Migeba_Cell G_SERIA = new Migeba_Cell(String.valueOf(i), TOGEO(rs2.getString("G_SERIA")));
                Migeba_Cell G_EXP = new Migeba_Cell(String.valueOf(i), _parseTime(rs2.getString("G_EXP")));
                Migeba_Cell G_QUANT = new Migeba_Cell(String.valueOf(i), DebugTools.doubleStringToIntString(rs2.getString("G_QUANT")));
                Migeba_Cell G_PRICE = new Migeba_Cell(String.valueOf(i), rs2.getString("G_PRICE"));
                Migeba_Cell G_BAR1 = new Migeba_Cell(String.valueOf(i), rs2.getString("G_BAR1"));
                Migeba_Cell P_NAME = new Migeba_Cell(String.valueOf(i), Qartulad(rs2.getString("P_NAME")));
                Migeba_Cell MAN_name = new Migeba_Cell(String.valueOf(i), geotranslate.TOGEO2(rs2.getString("MAN_name")));
                Migeba_Cell P_ERT = new Migeba_Cell(String.valueOf(i), DebugTools.doubleStringToIntString(rs2.getString("P_ERT")));
                Migeba_Cell MAN_Q_name = new Migeba_Cell(String.valueOf(i), Qartulad(rs2.getString("MAN_Q_name")));

                Migeba_Cell L_NAME = new Migeba_Cell(String.valueOf(i), rs2.getString("L_NAME"));
                Migeba_Cell P_TEMP = new Migeba_Cell(String.valueOf(i), rs2.getString("P_LOC_ID"));
                Migeba_Cell G_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("G_ID"));
                Migeba_Cell G_P_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("G_P_ID"));
                Migeba_Cell P_NUMERUS = new Migeba_Cell(String.valueOf(i), "");
                Migeba_Cell G_EXPECTED_QUANT = new Migeba_Cell(String.valueOf(i), DebugTools.doubleStringToIntString(rs2.getString("G_QUANT")));
                try {
                    P_NUMERUS = new Migeba_Cell(String.valueOf(i), P_NAME.getData().toString().split(" N")[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                    P_NUMERUS = new Migeba_Cell(String.valueOf(i), "");
                }

                migebaCellList.add(P_NAME);
                migebaCellList.add(G_SERIA);
                migebaCellList.add(G_EXP);
                migebaCellList.add(G_QUANT);
                migebaCellList.add(MAN_name);
                migebaCellList.add(P_TEMP);
                migebaCellList.add(P_ERT);
                migebaCellList.add(MAN_Q_name);
                migebaCellList.add(L_NAME);
                migebaCellList.add(G_ID);
                migebaCellList.add(G_P_ID);
                migebaCellList.add(P_NUMERUS);
                migebaCellList.add(G_EXPECTED_QUANT);
                list.add(migebaCellList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String Qartulad(String input) {
        Log.d(TAG, "Qartulad: " + input);
        return UnicodeConvertorUtil.enToGeo(input);
    }


}