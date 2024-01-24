package com.example.migeba;

import static com.example.migeba.MainActivity.CONN_EXECUTE_SQL;
import static com.example.migeba.MainActivity.CONN_RESULTSET_SQL;
import static com.example.migeba.MainActivity.USER_ID;
import static com.example.migeba.MainActivity._parseTime;
import static com.example.migeba.MainActivity.getAppContext;
import static com.example.migeba.Utils.DebugTools.print;
import static com.example.migeba.Utils.DecodeShegrovebaIntentReceiver.ACTION_BROADCAST_RECEIVER;
import static com.example.migeba.Utils.DecodeShegrovebaIntentReceiver.CATEGORY_BROADCAST_RECEIVER;
import static com.example.migeba.Utils.geotranslate.TOGEO;
import static com.example.migeba.migeba.Qartulad;
import static com.example.migeba.migeba.changesQuery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.datalogic.decode.BarcodeManager;
import com.datalogic.decode.DecodeException;
import com.datalogic.decode.configuration.IntentDeliveryMode;
import com.datalogic.decode.configuration.ScannerProperties;
import com.datalogic.device.configuration.ConfigException;
import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.filter.Filter;
import com.evrencoskun.tableviewsample.migeba_tableview.Dalageba_TableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.Inventarizacia_TableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewAdapter;
import com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewModel;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_Cell;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_ColumnHeader;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_RowHeader;
import com.example.migeba.Utils.DebugTools;
import com.example.migeba.Utils.DecodeDalagebaIntentReceiver;
import com.example.migeba.Utils.DecodeShegrovebaIntentReceiver;
import com.example.migeba.Utils.geotranslate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.platform.MaterialContainerTransform;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Dalageba extends AppCompatActivity {

    Context context;
    public static TableView mTableView;
    public static ProgressBar progressbar;
    public static FrameLayout tableParentLayout;
    public static String zednadebi_id = "";
    public static Boolean dalageba = false;
    public boolean firstBackPress = false;
    CardView inventory_searchBox_cardView;
    TextInputLayout inventory_searchBox;
    MaterialButton inventory_search;
    public static Filter tableViewFilter;
    public static String ag_time;
    public static String ag_stat;

    public static Activity activity;

    private BroadcastReceiver receiver = null;
    private IntentFilter intentFilter = null;

    private BarcodeManager manager;

    private final String LOGTAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.activity_dalageba);
        initialize();
        manager = new BarcodeManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register dynamically decode wedge intent broadcast receiver.
        receiver = new DecodeDalagebaIntentReceiver();

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

    public void initialize() {
        context = this;
        mTableView = findViewById(R.id.tableview);
        progressbar = findViewById(R.id.accept_progress_bar);
        tableParentLayout = findViewById(R.id.tableParentLayout);
        inventory_searchBox = findViewById(R.id.inventory_searchBox);
        inventory_searchBox_cardView = findViewById(R.id.inventory_searchBox_cardView);
        inventory_search = findViewById(R.id.inventory_search);
        changesQuery = new ArrayList<>();

        tableViewFilter = new Filter(mTableView);
        dalageba = true;
        try {
            zednadebi_id = getIntent().getExtras().getString("zednadebi", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //////////DALAGEBA//////////
        DalagebaInit();


        inventory_search.setEnabled(true);
        inventory_search.setVisibility(View.VISIBLE);
        inventory_searchBox_cardView.setVisibility(View.GONE);

        MaterialContainerTransform transformOpen = new MaterialContainerTransform();
        transformOpen.setStartView(inventory_search);
        transformOpen.setEndView(inventory_searchBox_cardView);
        transformOpen.addTarget(inventory_searchBox_cardView);
        transformOpen.setScrimColor(Color.TRANSPARENT);

        MaterialContainerTransform transformClose = new MaterialContainerTransform();
        transformClose.setStartView(inventory_searchBox_cardView);
        transformClose.setEndView(inventory_search);
        transformClose.addTarget(inventory_search);
        transformClose.setScrimColor(Color.TRANSPARENT);


        View.OnClickListener search = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInventory(inventory_searchBox.getEditText().getText().toString());
            }
        };

        View.OnClickListener closeCard = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearFilter();
                TransitionManager.beginDelayedTransition((ViewGroup) getWindow().getDecorView().getRootView(), transformClose);
                inventory_search.setVisibility(View.VISIBLE);
                inventory_searchBox_cardView.setVisibility(View.GONE);
            }
        };
        MaterialButton searchButton = findViewById(R.id.inventory_searchBox_search);

        inventory_searchBox.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    ///CLOSE DIALOG
                    searchButton.setIcon(getDrawable(R.drawable.ic_baseline_close_24));
                    searchButton.setOnClickListener(closeCard);

                } else {
                    ///SEARCH DIALOG
                    searchButton.setIcon(getDrawable(R.drawable.ic_baseline_search_24));
                    searchButton.setOnClickListener(search);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        ///searchbox transitions

        inventory_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition((ViewGroup) getWindow().getDecorView().getRootView(), transformOpen);
                inventory_search.setVisibility(View.GONE);
                inventory_searchBox_cardView.setVisibility(View.VISIBLE);
                inventory_searchBox.requestFocus();

            }
        });
        searchButton.setOnClickListener(closeCard);


        Inventory_AsyncInitializeTableView();
    }

    public static void uploadDalagebaToServer() {
        if (changesQuery == null && changesQuery.isEmpty()) {
            return;
        }
        print(Dalageba_TableViewListener.changesQuery.toString());
        String finishQuery = "update [A_PLUS].[dbo].[GET] " +
                "SET G_ACT = 4 " +
                "where G_ZDN = '" + zednadebi_id + "'";
        CONN_EXECUTE_SQL(finishQuery);

        CONN_EXECUTE_SQL(Dalageba_TableViewListener.changesQuery.toString());
        activity.finish();
    }

    public static void uploadDalageba(View v) {
        //alert dialog with title that says "დარწმუნებული ხართ?"
        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(activity);
        mb.setTitle("დალაგების დასრულება");
        mb.setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //upload inventarizacia to server
                uploadDalagebaToServer();
            }
        });
        mb.setNegativeButton("არა", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        mb.show();
    }


    private void DalagebaInit() {
        TextView inventarizacia_title;
        inventarizacia_title = findViewById(R.id.inventarizacia_title);
        inventarizacia_title.setText("დალაგება");
        Snackbar.make(getWindow().getDecorView().getRootView(), "არჩეულია ზედნადები " + zednadebi_id, BaseTransientBottomBar.LENGTH_SHORT).show();

        inventory_search.setEnabled(true);
        inventory_search.setVisibility(View.VISIBLE);
        inventory_searchBox_cardView.setVisibility(View.GONE);
    }


    public void clearFilter() {
        inventory_searchBox.setEnabled(false);


        tableViewFilter.set("");


        inventory_searchBox.setEnabled(true);
        inventory_searchBox.getEditText().setText("");
    }

    public void searchDalageba(String filterText) {
        if (filterText.contains("\\ ")) {
            filterText = filterText.split("\\ ")[1];
        }
        print("cfsdf for " + filterText);
        tableViewFilter.set(filterText);
        inventory_searchBox.setEnabled(true);
        inventory_searchBox.getEditText().setText("");

    }

    public void searchInventory(String filterText) {
        inventory_searchBox.setEnabled(false);

        print("searching for " + filterText);
        tableViewFilter.set(filterText);


        inventory_searchBox.setEnabled(true);
        inventory_searchBox.getEditText().setText("");
    }


    @Override
    public void onBackPressed() {
        if (!firstBackPress) {
            MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(context);
            mb.setTitle("ნამდვილად გსურთ გასვლა?");
            mb.setPositiveButton("კი", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
//                    Inventarizacia.super.onBackPressed();
                }
            });
            mb.setNegativeButton("არა", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    firstBackPress = true;
                    onBackPressed();
                }
            });
            AlertDialog dialog = mb.create();
            dialog.show();
        } else {
            firstBackPress = false;
        }


    }

    public static void Inventory_AsyncInitializeTableView() {
        AsyncTask<Void, Void, List<List<Migeba_Cell>>> asyncTask = new AsyncTask<Void, Void, List<List<Migeba_Cell>>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<List<Migeba_Cell>> doInBackground(Void... params) {
                return getProducts();
            }


            @Override
            protected void onPostExecute(List<List<Migeba_Cell>> products) {
                super.onPostExecute(products);
                progressbar.setVisibility(View.GONE);
                Migeba_TableViewModel migebaTableViewModel = new Migeba_TableViewModel();
                final Handler handler = new Handler(Looper.getMainLooper());

                // Create TableView Adapter
                Migeba_TableViewAdapter migebaTableViewAdapter = new Migeba_TableViewAdapter(migebaTableViewModel);
                mTableView.setAdapter(migebaTableViewAdapter);

                if (dalageba) {
                    mTableView.setTableViewListener(new Dalageba_TableViewListener(mTableView));
                    Dalageba_TableViewListener.products_cache = products;
                } else {
                    mTableView.setTableViewListener(new Inventarizacia_TableViewListener(mTableView));
                    Inventarizacia_TableViewListener.products_cache = products;
                }


                migebaTableViewAdapter.setAllItems(getColumnHeaders(), getRowHeaders(), products);
                if (dalageba) {
//                    mTableView.hideColumn(0);
//                    mTableView.hideColumn(0);
//                    mTableView.hideColumn(3);
//                    mTableView.hideColumn(4);
//                    mTableView.hideColumn(5);
//                    mTableView.hideColumn(6);
//                    mTableView.hideColumn(7);
//                    mTableView.hideColumn(8);
//                    mTableView.hideColumn(9);
//                    mTableView.setColumnWidth(0, 250);
//                    mTableView.setColumnWidth(1, 370);
//                    mTableView.setColumnWidth(2, 350);
                } else {
//                    mTableView.hideColumn(5);
                    mTableView.setColumnWidth(0, 350);
                    mTableView.setColumnWidth(1, 120);
                    mTableView.setColumnWidth(2, 170);
                    mTableView.setColumnWidth(3, 170);
                    mTableView.setColumnWidth(4, 105);
                    mTableView.setColumnWidth(5, 170);
                }

                Dalageba_TableViewListener.Edit_Card_Location(0, getAppContext());


//                mTableView.setColumnWidth(1, 250);
//                mTableView.setColumnWidth(2, 500);
//                mTableView.setColumnWidth(3, 250);
//                mTableView.setColumnWidth(4, 250);
//                mTableView.setColumnWidth(5, 250);
//                mTableView.setColumnWidth(6, 250);
//                mTableView.setColumnWidth(7, 250);
//                mTableView.setColumnWidth(8, 250);
//                mTableView.setColumnWidth(9, 250);
            }
        };

        asyncTask.execute();
    }

    private static List<List<Migeba_Cell>> getProducts() {
        List<List<Migeba_Cell>> list = new ArrayList<>();
        if (dalageba) {


            ResultSet rs2 = CONN_RESULTSET_SQL("" +
                    " SELECT G_P_ID,G_ID,G_SERIA,G_EXP,G_QUANT,G_PRICE,G_D_ID,MAN_Q_name,\n" +
                    "    [MAN_name],G_MAN_ID,G_MAN_Q_ID,G_ZDN,G_TIME,G_BAR1,P_ID,P_NAME,P_FORM,P_ERT,L_NAME,P_LOC_ID\n" +
                    "    FROM [A_PLUS].[dbo].[GET]  as t1\n" +
                    "    left join A_PLUS.dbo.PRODUCT on P_ID=g_p_id \n" +
                    "    left join A_PLUS.dbo.manufacturer_Q on [G_MAN_Q_ID]=[MAN_Q_ID]\n" +
                    "     left join A_PLUS.dbo.manufacturer on [G_MAN_Q_ID] = MAN_ID\n" +
                    "     outer apply (SELECT TOP 1 [G_LOC]  FROM [A_PLUS].[dbo].[GET] where  G_SERIA=t1.G_SERIA and G_P_ID=t1.G_P_ID and G_ID!=t1.G_ID order by G_TIME desc) as t4\n" +
                    "    \n" +
                    "     left join A_PLUS.dbo.LOCATION\n" +
                    "     on L_ID = t4.G_LOC\n" +
                    "      where G_ZDN = '" + zednadebi_id + "' order by G_ID");
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
                    Migeba_Cell P_ERT = new Migeba_Cell(String.valueOf(i), rs2.getString("P_ERT"));
                    Migeba_Cell MAN_Q_name = new Migeba_Cell(String.valueOf(i), Qartulad(rs2.getString("MAN_Q_name")));
                    Migeba_Cell P_TEMP = new Migeba_Cell(String.valueOf(i), rs2.getString("P_LOC_ID"));
                    Migeba_Cell P_LOC_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("L_NAME"));
                    Migeba_Cell G_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("G_ID"));
                    Migeba_Cell G_P_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("G_P_ID"));
                    Migeba_Cell P_NUMERUS = new Migeba_Cell(String.valueOf(i), "");
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
                    migebaCellList.add(P_LOC_ID);
                    migebaCellList.add(G_ID);
                    migebaCellList.add(G_P_ID);
                    migebaCellList.add(P_NUMERUS);
                    list.add(migebaCellList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

//            String sqlQuery = "SELECT G_SERIA,P_NAME,P_LOC_ID FROM [A_PLUS].[dbo].[GET] left join A_PLUS.dbo.PRODUCT on P_ID=g_p_id left join A_PLUS.dbo.manufacturer_Q on [G_MAN_Q_ID]=[MAN_Q_ID] left join A_PLUS.dbo.manufacturer on [G_MAN_Q_ID] = MAN_ID where G_ZDN = '" + zednadebi_id + "' order by P_LOC_ID desc";
//
//            ResultSet rs2 = SQL_ResultSet(sqlQuery);
//            Log.e("tag", sqlQuery);
//            try {
//                int i = 0;
//                while (rs2.next()) {
//                    i++;
//                    List<Inventory_Cell> migebaCellList = new ArrayList<>();
//                    Inventory_Cell G_SERIA = new Inventory_Cell(String.valueOf(i), rs2.getString("G_SERIA"));
//                    Inventory_Cell P_NAME = new Inventory_Cell(String.valueOf(i), rs2.getString("P_NAME"));
//                    Inventory_Cell P_LOC_ID = new Inventory_Cell(String.valueOf(i), rs2.getString("P_LOC_ID"));
//                    migebaCellList.add(G_SERIA);
//                    migebaCellList.add(P_NAME);
//                    migebaCellList.add(P_LOC_ID);
//                    list.add(migebaCellList);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        } else {
            ///inventarizacia

            //InitializeScript
            CONN_EXECUTE_SQL(
                    "declare @TT datetime = getdate() insert into [A_PLUS].[dbo].[INVENT](\n" +
                            "  AG_GID, AG_PID, AG_QUANT, AG_GLOC, \n" +
                            "  AG_TIME, AG_USER\n" +
                            ") \n" +
                            "SELECT \n" +
                            "  G_ID, \n" +
                            "  G_p_id, \n" +
                            "  (\n" +
                            "    isnull(G_QUANT, 0)- isnull(GACERIL, 0)- isnull(CHAMOCERIL, 0)- isnull(gakiduli, 0)+ isnull(DB_QUANT_SUM, 0)\n" +
                            "  ) as nasht, \n" +
                            "  G_LOC, \n" +
                            "  @TT, \n" +
                            "  " + USER_ID + " \n" +
                            "FROM \n" +
                            "  [A_PLUS].[dbo].[GET] \n" +
                            "  left join (\n" +
                            "    SELECT \n" +
                            "      GACER_G_SERIA, \n" +
                            "      GACER_G_ZDN, \n" +
                            "      sum ([GACER_QUANT]) as 'GACERIL' \n" +
                            "    FROM \n" +
                            "      [A_PLUS].[dbo].GACER \n" +
                            "    GROUP BY \n" +
                            "      GACER_G_SERIA, \n" +
                            "      GACER_G_ZDN\n" +
                            "  ) AS GACER2 ON G_SERIA = GACER2.GACER_G_SERIA \n" +
                            "  and G_ZDN = GACER2.GACER_G_ZDN \n" +
                            "  left join (\n" +
                            "    SELECT \n" +
                            "      CHAMOCER_G_SERIA, \n" +
                            "      CHAMOCER_G_ZDN, \n" +
                            "      sum ([CHAMOCER_QUANT]) as 'CHAMOCERIL' \n" +
                            "    FROM \n" +
                            "      [A_PLUS].[dbo].CHAMOCER \n" +
                            "    GROUP BY \n" +
                            "      CHAMOCER_G_SERIA, \n" +
                            "      CHAMOCER_G_ZDN\n" +
                            "  ) AS CHAMOCER2 ON G_SERIA = CHAMOCER2.CHAMOCER_G_SERIA \n" +
                            "  and G_ZDN = CHAMOCER2.CHAMOCER_G_ZDN \n" +
                            "  left JOIN (\n" +
                            "    SELECT \n" +
                            "      SL_SERIA, \n" +
                            "      SL_ZEDNAD, \n" +
                            "      sum ([SL_RAOD]) as 'gakiduli' \n" +
                            "    FROM \n" +
                            "      [A_PLUS].[dbo].sell \n" +
                            "    GROUP BY \n" +
                            "      SL_SERIA, \n" +
                            "      SL_ZEDNAD\n" +
                            "  ) AS tsum ON G_SERIA = tsum.SL_SERIA \n" +
                            "  and G_ZDN = tsum.SL_ZEDNAD \n" +
                            "  left outer join (\n" +
                            "    SELECT \n" +
                            "      DB_G_SERIA, \n" +
                            "      DB_G_ZED, \n" +
                            "      sum (DB_QUANT) as 'DB_QUANT_SUM' \n" +
                            "    FROM \n" +
                            "      [A_PLUS].[dbo].DABRUNEBA \n" +
                            "    GROUP BY \n" +
                            "      DB_G_SERIA, \n" +
                            "      DB_G_ZED\n" +
                            "  ) AS DABRUNEBA ON G_SERIA = DABRUNEBA.DB_G_SERIA \n" +
                            "  and G_ZDN = DABRUNEBA.DB_G_ZED \n" +
                            "  left join [A_PLUS].dbo.DISTRIBUTOR ON G_D_ID = D_ID \n" +
                            "  left join [A_PLUS].dbo.manufacturer ON G_MAN_ID = MAN_ID \n" +
                            "  left join [A_PLUS].dbo.manufacturer_Q ON G_MAN_Q_ID = MAN_Q_ID \n" +
                            "  left join [A_PLUS].dbo.PRODUCT ON G_P_ID = P_ID \n" +
                            "  left join [A_PLUS].dbo.indication ON P_INDICATION = IND_ID \n" +
                            "  left join [A_PLUS].dbo.FORM ON P_FORM = F_ID \n" +
                            "  left join [A_PLUS].dbo.ERT ON P_ert = e_ID \n" +
                            "where \n" +
                            "  (\n" +
                            "    isnull(G_QUANT, 0)- isnull(GACERIL, 0)- isnull(CHAMOCERIL, 0)- isnull(gakiduli, 0)+ isnull(DB_QUANT_SUM, 0)\n" +
                            "  )> 0 \n" +
                            "order by \n" +
                            "  G_TIME\n"
            );

            String sqlQuery = "SELECT * FROM [A_PLUS].[dbo].[INVENT]" +
                    "  left join [A_PLUS].dbo.PRODUCT on AG_PID=P_ID " +
                    "  left join (select  G_SERIA,G_ZDN from [A_PLUS].dbo.GET)as tg2 on G_ID=AG_GID " +
                    " where AG_STAT = '" + ag_stat + "' AND AG_TIME = '" + ag_time + "'";

            ResultSet rs2 = CONN_RESULTSET_SQL(sqlQuery);
            Log.e("tag", sqlQuery);
            try {
                int i = 0;
                while (rs2.next()) {
                    i++;
                    List<Migeba_Cell> migebaCellList = new ArrayList<>();
                    Migeba_Cell P_NAME = new Migeba_Cell(String.valueOf(i), geotranslate.TOGEO(rs2.getString("P_NAME")));
                    Migeba_Cell P_LOC_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("P_LOC_ID"));
                    Migeba_Cell AG_QUANT = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_QUANT"));
                    Migeba_Cell AG_ANAGC = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_ANAGC"));
                    Migeba_Cell AG_GLOC = new Migeba_Cell(String.valueOf(i), "");
                    try {
                        AG_GLOC = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_GLOC"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Migeba_Cell AG_GID = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_GID"));
                    migebaCellList.add(P_NAME);
                    migebaCellList.add(P_LOC_ID);
                    migebaCellList.add(AG_QUANT);
                    migebaCellList.add(AG_ANAGC);
                    migebaCellList.add(AG_GLOC);
                    migebaCellList.add(AG_GID);
                    list.add(migebaCellList);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static List<Migeba_ColumnHeader> getColumnHeaders() {
        if (!dalageba) {
            //G_SERIA,G_EXP,G_QUANT,G_PRICE,G_BAR1,P_NAME,P_FORM,P_ERT,P_LOC_ID
            List<Migeba_ColumnHeader> migebaColumnHeaders = new ArrayList<>();
            Migeba_ColumnHeader P_NAME = new Migeba_ColumnHeader("0", "დასახელება");
            Migeba_ColumnHeader P_LOC_ID = new Migeba_ColumnHeader("1", "ლოკაცია");
            Migeba_ColumnHeader AG_QUANT = new Migeba_ColumnHeader("2", "რაოდენობა");
            Migeba_ColumnHeader AG_ANAGC = new Migeba_ColumnHeader("3", "ანაღწერი");
            Migeba_ColumnHeader AG_GLOC = new Migeba_ColumnHeader("4", "G_LOC");
            Migeba_ColumnHeader AG_GID = new Migeba_ColumnHeader("5", "AG_GID");
            migebaColumnHeaders.add(P_NAME);
            migebaColumnHeaders.add(P_LOC_ID);
            migebaColumnHeaders.add(AG_QUANT);
            migebaColumnHeaders.add(AG_ANAGC);
            migebaColumnHeaders.add(AG_GLOC);
            migebaColumnHeaders.add(AG_GID);
            return migebaColumnHeaders;
        } else {
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
            Migeba_ColumnHeader G_P_ID = new Migeba_ColumnHeader("10", "G_P_ID");
            Migeba_ColumnHeader P_NUMERUS = new Migeba_ColumnHeader("11", "ნუმერუსი");
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
            return migebaColumnHeaders;
        }

    }

    public static List<Migeba_RowHeader> getRowHeaders() {
        List<Migeba_RowHeader> migebaRowHeaderList = new ArrayList<>();
        int ROW_COUNT = 5;
        ResultSet rs2 = null;
        if (dalageba) {
            rs2 = CONN_RESULTSET_SQL("select COUNT (*) from (SELECT G_SERIA,P_NAME,P_LOC_ID FROM [A_PLUS].[dbo].[GET] left join A_PLUS.dbo.PRODUCT on P_ID=g_p_id left join A_PLUS.dbo.manufacturer_Q on [G_MAN_Q_ID]=[MAN_Q_ID] left join A_PLUS.dbo.manufacturer on [G_MAN_Q_ID] = MAN_ID where G_ZDN = '" + zednadebi_id + "') x");
        } else {
            rs2 = CONN_RESULTSET_SQL("select COUNT (*) from (SELECT AG_ANAGC, P_NAME, P_LOC_ID, AG_QUANT, AG_GLOC, AG_GID\n" +
                    "  FROM [A_PLUS].[dbo].[INVENT] \n" +
                    "  left join [A_PLUS].dbo.PRODUCT on [P_ID] = AG_PID\n" +
                    "  where AG_TIME = (Select top 1 AG_TIME from [A_PLUS].[dbo].[INVENT])) x");

        }
        try {
            while (rs2.next()) {
                ROW_COUNT = rs2.getInt(1) + 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        if (ROW_COUNT > 1000) ROW_COUNT = 1000;
        for (int i = 1; i < ROW_COUNT; i++) {
            Migeba_RowHeader header = new Migeba_RowHeader(String.valueOf(i), String.valueOf(i));
            migebaRowHeaderList.add(header);
        }


        return migebaRowHeaderList;
    }


}