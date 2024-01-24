package com.example.migeba;

import static com.evrencoskun.tableviewsample.migeba_tableview.Shegroveba_TableViewListener.Edit_Card_Shegroveba;
import static com.example.migeba.MainActivity.CONN_EXECUTE_SQL;
import static com.example.migeba.MainActivity.CONN_RESULTSET_SQL;
import static com.example.migeba.MainActivity.ConnectAndPrintPOS;
import static com.example.migeba.MainActivity.SELECTED_USER;
import static com.example.migeba.MainActivity._parseTime;
import static com.example.migeba.MainActivity.mainActivity;
import static com.example.migeba.Utils.DecodeShegrovebaIntentReceiver.ACTION_BROADCAST_RECEIVER;
import static com.example.migeba.Utils.DecodeShegrovebaIntentReceiver.CATEGORY_BROADCAST_RECEIVER;
import static com.example.migeba.Utils.geotranslate.TOGEO;
import static com.example.migeba.migeba.Qartulad;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.datalogic.decode.BarcodeManager;
import com.datalogic.decode.DecodeException;
import com.datalogic.decode.configuration.IntentDeliveryMode;
import com.datalogic.decode.configuration.ScannerProperties;
import com.datalogic.device.configuration.ConfigException;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.filter.Filter;
import com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewAdapter;
import com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewModel;
import com.evrencoskun.tableviewsample.migeba_tableview.Shegroveba_TableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_Cell;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_ColumnHeader;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_RowHeader;
import com.example.migeba.Utils.DebugTools;
import com.example.migeba.Utils.DecodeShegrovebaIntentReceiver;
import com.example.migeba.Utils.geotranslate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Shegroveba extends AppCompatActivity {

    private ListView lv_zednadebi;
    Context context;
    public static Activity activity;
    public static MaterialButton zednadebi_fab;
    public static String zednadebi_id;
    public static List<List<Migeba_Cell>> products_cache;
    static ExtendedFloatingActionButton finish_accept;
    public static MaterialButton productEditButton;
    public static StringBuilder changesQuery;
    private static TableView mTableView;
    public static FloatingActionButton scanner_button;

    private static String clientName;
    public static Filter filter;
    public static String filterString = new String("");

    public static ProgressBar progressbar;

    // Constants for Broadcast Receiver defined below.


    private final String LOGTAG = getClass().getName();

    private BroadcastReceiver receiver = null;
    private IntentFilter intentFilter = null;

    private BarcodeManager manager;

    private Boolean isEditing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shegroveba);

        Initialize();

        AsyncInitializeZednadebi();

        manager = new BarcodeManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register dynamically decode wedge intent broadcast receiver.
        receiver = new DecodeShegrovebaIntentReceiver();

        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_BROADCAST_RECEIVER);
        intentFilter.addCategory(CATEGORY_BROADCAST_RECEIVER);
        registerReceiver(receiver, intentFilter);

        try {
            // get the current settings from the BarcodeManager
            ScannerProperties configuration = ScannerProperties.edit(manager);
            // disables KeyboardWedge
            configuration.keyboardWedge.enable.set(false);
            // enable wedge intent
            configuration.intentWedge.enable.set(true);
            // set wedge intent action and category
            configuration.intentWedge.action.set(ACTION_BROADCAST_RECEIVER);
            configuration.intentWedge.category.set(CATEGORY_BROADCAST_RECEIVER);
            // set wedge intent delivery through broadcast
            configuration.intentWedge.deliveryMode.set(IntentDeliveryMode.BROADCAST);
            configuration.store(manager, false);
        } catch (Exception e) { // Any error?
            if (e instanceof ConfigException) {
                ConfigException ex = (ConfigException) e;
                Log.e(LOGTAG, "Error while retrieving/setting properties: " + ex.error_number, ex);
            } else if (e instanceof DecodeException) {
                DecodeException ex = (DecodeException) e;
                Log.e(LOGTAG, "Error while retrieving/setting properties: " + ex.error_number, ex);
            } else {
                Log.e(LOGTAG, "Error in onCheckedChanged", e);
            }
        }

    }

    @Override
    protected void onPause() {
        Log.i(this.getClass().getName(), "onPause");
        super.onPause();

        unregisterReceiver(receiver);
        receiver = null;
        intentFilter = null;
    }

    private void Initialize() {
        context = this;

        filterString = "";
        lv_zednadebi = findViewById(R.id.lv_zednadebi);
        zednadebi_fab = findViewById(R.id.zednadebi_fab);
        progressbar = findViewById(R.id.accept_progress_bar);
        progressbar.setVisibility(View.GONE);
        finish_accept = findViewById(R.id.finish_accept);
        finish_accept.setVisibility(View.GONE);
        zednadebi_fab.setVisibility(View.GONE);
        mTableView = findViewById(R.id.tableview);
        scanner_button = findViewById(R.id.scanner_button);
        productEditButton = findViewById(R.id.productEditButton);
        productEditButton.setVisibility(View.GONE);
        scanner_button.setVisibility(View.GONE);
        finish_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FINISH_SHEGROVEBA();
            }
        });

        productEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Edit_Card_Shegroveba(mTableView.getSelectedRow(), context);
            }
//        if (mTableView != null) AsyncInitializeTableView(findViewById(R.id.accept_progress_bar));
        });

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

        changesQuery = new StringBuilder("");
        activity = this;

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


    public static void FINISH_SHEGROVEBA() {

        System.out.println("savedProducts.size()");
        System.out.println(Shegroveba_TableViewListener.savedProducts.size());
        System.out.println("deletedProducts.size()");
        System.out.println(Shegroveba_TableViewListener.deletedProducts.size());
        System.out.println("mTableView.getAdapter().getCellColumnItems(0).size()");
        System.out.println(mTableView.getAdapter().getCellColumnItems(0).size());
        if (Shegroveba_TableViewListener.savedProducts.size() < mTableView.getAdapter().getCellColumnItems(0).size()) {
            Log.e("notfilled", String.valueOf(Shegroveba_TableViewListener.savedProducts.size()));
            Log.e("notfilled", String.valueOf(mTableView.getAdapter().getCellColumnItems(0).size()));
            Toast.makeText(mainActivity, "ყველა პროდუქტი არაა შესრულებული!", Toast.LENGTH_LONG).show();
            return;
        }

            MaterialAlertDialogBuilder mmb = new MaterialAlertDialogBuilder(finish_accept.getContext());
            View v = mainActivity.getLayoutInflater().inflate(R.layout.dialog_accept_finish, null);
            TextInputLayout parkebis_raodenoba = v.findViewById(R.id.parkebis_raodenoba);
            TextInputLayout yutebis_raodenoba = v.findViewById(R.id.vada);
            MaterialButton accept_finish = v.findViewById(R.id.edit_card_nextBtn);
            MaterialButton back = v.findViewById(R.id.button_previous);

            mmb.setView(v);
            AlertDialog dialog = mmb.create();

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            accept_finish.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ///SET FINISHED
//                      String deleteQuery = "DELETE FROM [A_PLUS].[dbo].[GET_EDIT] WHERE SL_ZEDNAD = '" + zednadebi_fab.getText().toString() + "'";
//                      CONN_EXECUTE_SQL(deleteQuery);
                    int amountStickers = 0;
                    try {
                        amountStickers = Integer.parseInt(parkebis_raodenoba.getEditText().getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        amountStickers += Integer.parseInt(yutebis_raodenoba.getEditText().getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    if (amountStickers > 0) {
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        String now = LocalDateTime.now().format(dtf);
                        for (int j = 0; j < amountStickers; j++) {
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    ConnectAndPrintPOS(mainActivity, "პარკი - " + parkebis_raodenoba.getEditText().getText().toString(), "ყუთი - " + yutebis_raodenoba.getEditText().getText().toString(), clientName, SELECTED_USER, now);
                                }
                            }, 750 * j);
                        }
                    }


//                        ConnectAndPrint(context, "", SELECTED_USER, geotranslate.FROMGEO(clientName), "", "");

                    String finishQuery = "update [A_PLUS].[dbo].[SELL] " +
                            "SET SL_ACT = 3 " +
                            "where [SL_DOC] = '" + zednadebi_fab.getText().toString() + "'";
                    CONN_EXECUTE_SQL(finishQuery);

                    CONN_EXECUTE_SQL(changesQuery.toString());
                    activity.finish();
                }
            });

            dialog.show();




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
                    SimpleAdapter simpleAdapter = new SimpleAdapter(context, list[0], R.layout.card_zednadebi, from, to);
                    lv_zednadebi.setAdapter(simpleAdapter);
                    lv_zednadebi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            if (i != 0) {
                                Toast.makeText(context, "აირჩიეთ პირველი ფაქტურა!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            TextView zednadebi_id = view.findViewById(R.id.L_NAME);
                            TextView zednadebi_clientname = view.findViewById(R.id.zednadebi_dist_name);
                            clientName = zednadebi_clientname.getText().toString();
//                            TextView zednadebi_g_id = view.findViewById(R.id.zednadebi_g_id);
//                            mimdinare_zednadebi_g_id = zednadebi_g_id.getText().toString();
                            lv_zednadebi.setVisibility(View.GONE);
                            lv_zednadebi = new ListView(context);
                            lv_zednadebi.setAdapter(null);
                            zednadebi_fab.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Snackbar.make(zednadebi_fab, "არჩეულია ზედნადები - " + zednadebi_id.getText().toString(), Snackbar.LENGTH_SHORT).show();
                                    Snackbar.make(zednadebi_fab, "კლიენტი - " + zednadebi_clientname.getText().toString(), Snackbar.LENGTH_SHORT).show();

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

    public void AsyncInitializeTableView(String zednadebi_id) {
        AsyncTask<Void, Void, List<List<Migeba_Cell>>> asyncTask = new AsyncTask<Void, Void, List<List<Migeba_Cell>>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<List<Migeba_Cell>> doInBackground(Void... params) {
                Shegroveba.zednadebi_id = zednadebi_id;
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
                scanner_button.setVisibility(View.VISIBLE);
                zednadebi_fab.setVisibility(View.VISIBLE);

                // Create TableView Adapter
                Migeba_TableViewAdapter migebaTableViewAdapter = new Migeba_TableViewAdapter(migebaTableViewModel);
                mTableView.setAdapter(migebaTableViewAdapter);
                mTableView.setTableViewListener(new Shegroveba_TableViewListener(mTableView));
                migebaTableViewAdapter.setAllItems(getColumnHeaders(), getRowHeaders(zednadebi_id), products);
                filter = new Filter(mTableView);
                filter.set(filterString);

                mTableView.hideColumn(9);
                mTableView.hideColumn(10);
                mTableView.setColumnWidth(0, 500);
                mTableView.setColumnWidth(1, 250);
                mTableView.setColumnWidth(2, 170);
                mTableView.setColumnWidth(3, 170);
                mTableView.setColumnWidth(4, 265);
                mTableView.setColumnWidth(5, 170);
                mTableView.setColumnWidth(6, 230);
//                mTableView.setColumnWidth(7, 230);
                mTableView.setColumnWidth(8, 170);
                mTableView.setColumnWidth(9, 170);

                String finishQuery = "update [A_PLUS].[dbo].[SELL] " +
                        "SET SL_ACT = 4 " +
                        "where [SL_DOC] = '" + zednadebi_fab.getText().toString() + "'";
                CONN_EXECUTE_SQL(finishQuery);

                Shegroveba_TableViewListener.Edit_Card_Shegroveba(0, context);
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
        Migeba_ColumnHeader SL_ID = new Migeba_ColumnHeader("9", "SL_ID");
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
        migebaColumnHeaders.add(SL_ID);
        return migebaColumnHeaders;
    }

    public static List<Migeba_RowHeader> getRowHeaders(String zednadebi_id) {
        List<Migeba_RowHeader> migebaRowHeaderList = new ArrayList<>();
        int ROW_COUNT = 5;
        ResultSet rs2 = CONN_RESULTSET_SQL("SELECT COUNT(*) FROM [A_PLUS].[dbo].[SELL] where [SL_DOC] = '" + zednadebi_id + "'");
        try {
            while (rs2.next()) {
                ROW_COUNT = rs2.getInt(1) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 1; i < ROW_COUNT; i++) {
            Migeba_RowHeader header = new Migeba_RowHeader(String.valueOf(i), String.valueOf(i));
            migebaRowHeaderList.add(header);
        }


        return migebaRowHeaderList;
    }

    private static List<List<Migeba_Cell>> getProducts(String zednadebi_id) {
        List<List<Migeba_Cell>> list = new ArrayList<>();

        ResultSet rs2 = CONN_RESULTSET_SQL(
                "SELECT \n" +
                        "      G_P_ID, \n" +
                        "      G_ID, \n" +
                        "      SL_SERIA as G_SERIA, \n" +
                        "      G_EXP, \n" +
                        "      SL_RAOD as G_QUANT, \n" +
                        "      SL_PR as G_PRICE, \n" +
                        "      G_D_ID, \n" +
                        "      MAN_Q_name, \n" +
                        "      [MAN_name], \n" +
                        "      G_MAN_ID, \n" +
                        "      G_MAN_Q_ID, \n" +
                        "      SL_DOC as G_ZDN, \n" +
                        "      G_TIME, \n" +
                        "      G_BAR1, \n" +
                        "      P_ID, \n" +
                        "      P_NAME, \n" +
                        "      P_FORM, \n" +
                        "      P_ERT, \n" +
                        "      SL_ID, \n" +
                        "      P_LOC_ID ,\n" +
                        "      L_NAME\n" +
                        "    FROM \n" +
                        "      (\n" +
                        "        select \n" +
                        "          * \n" +
                        "        from \n" +
                        "          [A_PLUS].[dbo].SELL \n" +
                        "        where \n" +
                        "          SL_DOC = '" + zednadebi_id + "'\n" +
                        "      ) as slleft \n" +
                        "      join [A_PLUS].[dbo].[GET] on G_ZDN = sl_zednad \n" +
                        "      and g_seria = sl_seria \n" +
                        "      left join A_PLUS.dbo.PRODUCT on P_ID = g_p_id \n" +
                        "      left join A_PLUS.dbo.manufacturer_Q on [G_MAN_Q_ID] = [MAN_Q_ID] \n" +
                        "      left join A_PLUS.dbo.manufacturer on [G_MAN_Q_ID] = MAN_ID \n" +
                        "      left join A_PLUS.dbo.LOCATION     on L_ID = G_LOC\n" +
                        "    order by \n" +
                        "      L_NAME asc"
        );
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
                Migeba_Cell P_TEMP = new Migeba_Cell(String.valueOf(i), rs2.getString("P_LOC_ID"));

                Migeba_Cell P_LOC_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("L_NAME"));
                Migeba_Cell G_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("G_ID"));
                Migeba_Cell G_P_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("G_P_ID"));
                Migeba_Cell sl_id = new Migeba_Cell(String.valueOf(i), rs2.getString("SL_ID"));
                Migeba_Cell P_NUMERUS = new Migeba_Cell(String.valueOf(i), "");
                try {
                    P_NUMERUS = new Migeba_Cell(String.valueOf(i), P_NAME.getData().toString().split(" N")[1]);
                } catch (Exception e) {
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
                migebaCellList.add(P_LOC_ID);
                migebaCellList.add(G_ID);
                migebaCellList.add(G_P_ID);
                migebaCellList.add(P_NUMERUS);
                migebaCellList.add(sl_id);
                list.add(migebaCellList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    private static ArrayList<HashMap<String, Object>> getZednadebebi() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        ResultSet rs2 = CONN_RESULTSET_SQL("SELECT * from(select [SL_CL_ID],[SL_DOC],[SL_TAR] ,cast (sum([SL_RAOD]*[SL_PR]) as decimal(18,2)) as jam FROM [A_PLUS].[dbo].[SELL] where SL_ACT=2 group by [SL_DOC],[SL_TAR] ,[SL_CL_ID])as t2 outer apply (select CL_NAME,CL_FIZIK from [A_PLUS].[dbo].CLIENTS where cl_ID=[SL_CL_ID])as t3");
        try {
            while (rs2.next()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("G_ZDN", rs2.getString("SL_DOC"));
                map.put("G_TIME", _parseTime(rs2.getString("SL_TAR")));
                map.put("jam", rs2.getString("jam") + "₾");
                map.put("d_name", TOGEO(rs2.getString("CL_NAME") + "⌠" + TOGEO(rs2.getString("CL_FIZIK"))));
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}