package com.example.migeba;

import static com.example.migeba.MainActivity.CONN_EXECUTE_SQL;
import static com.example.migeba.MainActivity.CONN_RESULTSET_SQL;
import static com.example.migeba.MainActivity.USER_ID;
import static com.example.migeba.MainActivity.getAppContext;
import static com.example.migeba.Utils.DebugTools.print;
import static com.example.migeba.Utils.DecodeShegrovebaIntentReceiver.ACTION_BROADCAST_RECEIVER;
import static com.example.migeba.Utils.DecodeShegrovebaIntentReceiver.CATEGORY_BROADCAST_RECEIVER;
import static com.example.migeba.Utils.geotranslate.TOGEO;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.evrencoskun.tableviewsample.migeba_tableview.Inventarizacia_TableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewAdapter;
import com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewModel;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_Cell;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_ColumnHeader;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_RowHeader;
import com.example.migeba.Utils.DebugTools;
import com.example.migeba.Utils.DecodeInventarizaciaIntentReceiver;
import com.example.migeba.Utils.DecodeShegrovebaIntentReceiver;
import com.example.migeba.Utils.geotranslate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.transition.platform.MaterialContainerTransform;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Inventarizacia extends AppCompatActivity {

    Context context;
    public static TableView mTableView;
    public static ProgressBar progressbar;
    public static FrameLayout tableParentLayout;
    public static String zednadebi_id = "";
    final public static Boolean dalageba = false;
    public boolean firstBackPress = false;
    CardView inventory_searchBox_cardView;
    TextInputLayout inventory_searchBox;
    MaterialButton inventory_search;
    public static Filter tableViewFilter;
    public static String ag_time;
    public static String ag_stat;
    public static Integer invent_type;
    public static Migeba_TableViewAdapter migebaTableViewAdapter;
    public static Migeba_TableViewModel migebaTableViewModel;

    public static String barcode = "";
    private BroadcastReceiver receiver = null;
    private IntentFilter intentFilter = null;

    private BarcodeManager manager;

    private final String LOGTAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventarizacia);
        initialize();

        manager = new BarcodeManager();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register dynamically decode wedge intent broadcast receiver.
        receiver = new DecodeInventarizaciaIntentReceiver();

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
        tableViewFilter = new Filter(mTableView);

        migebaTableViewModel = new Migeba_TableViewModel();

        migebaTableViewAdapter = new Migeba_TableViewAdapter(migebaTableViewModel);

        getDataFromBundle();

        InvantarizaciaInit();

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
                if (charSequence.length() > 0 && charSequence.charAt(charSequence.length() - 1) == '\n') {
                    print("yes");
                    searchInventory(inventory_searchBox.getEditText().getText().toString());
                }
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


        Inventory_AsyncInitializeTableView(invent_type);
    }

    private void getDataFromBundle() {
        try {
            invent_type = getIntent().getExtras().getInt("type", 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ag_time = getIntent().getExtras().getString("time", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ag_stat = getIntent().getExtras().getString("stat", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            zednadebi_id = getIntent().getExtras().getString("zednadebi", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void uploadInventarizaciaToServer() {
        print(Inventarizacia_TableViewListener.changesQuery.toString());
        CONN_EXECUTE_SQL(Inventarizacia_TableViewListener.changesQuery.toString());
        finish();
    }

    public void addProductItem(View view) {
        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(context);
        View v = getLayoutInflater().inflate(R.layout.card_add_product, null);
        TextInputEditText seria = v.findViewById(R.id.seria_damateba);
        seria.requestFocus();
        mb.setView(v);
        mb.setPositiveButton("დამატება", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addProduct(seria);
                dialogInterface.dismiss();
            }
        });
        AlertDialog alert = mb.create();
        alert.show();
        seria.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //check if last char is enter
                if (charSequence.length() > 0) {
                    if (charSequence.charAt(charSequence.length() - 1) == '\n') {
                        seria.setText(seria.getText().toString().substring(0, seria.getText().toString().length() - 1));
                        seria.setSelection(seria.getText().toString().length());
                        addProduct(seria);
                        alert.dismiss();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void addProduct(TextInputEditText seria) {
        String seria_text = seria.getText().toString();
        print(seria.getText().toString());
        //insert data first into invent
        //take data from invent

        boolean b = false;
        for (int i = 0; i < Inventarizacia_TableViewListener.products_cache.size(); i++) {
            if (Inventarizacia_TableViewListener.products_cache.get(i).get(6).getData().equals(seria)) {
                Toast.makeText(getAppContext(), "ასეთი სერია უკვე არსებობს", Toast.LENGTH_SHORT).show();
                b = true;
            }
        }

        if (!b) {
            addItemToInventoryDBAndShow(seria_text, ag_time);


        }

    }

    public static void updateCacheOfProducts() {
        List<List<Migeba_Cell>> list = new ArrayList<>();

        for (int i = 0; i < migebaTableViewAdapter.getRowHeaderRecyclerViewAdapter().getItemCount(); i++) {
            list.add(migebaTableViewAdapter.getCellRowItems(i));
        }

        Inventarizacia_TableViewListener.products_cache = list;
        Inventarizacia_TableViewListener.SetTableViewAdapter(list);
    }

    public void uploadInventarizacia(View v) {
        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(Inventarizacia.this);
        mb.setTitle("დარწმუნებული ხართ?");
        mb.setPositiveButton("დიახ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //upload inventarizacia to server
                uploadInventarizaciaToServer();
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

    private void InvantarizaciaInit() {

        TextView inventarizacia_title;
        inventarizacia_title = findViewById(R.id.inventarizacia_title);
        inventarizacia_title.setText("ინვეტარიზაცია");

    }


    public void clearFilter() {
        inventory_searchBox.setEnabled(false);


        tableViewFilter.set("");


        inventory_searchBox.setEnabled(true);
        inventory_searchBox.getEditText().setText("");
    }

    public void searchInventory(String filterText) {

        if (filterText.split(" ").length == 3) {
            //დასკანერებულია ბარკოდი სტიკერიდან.
            filterText = filterText.split(" ")[1];
        }
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

    public static void Inventory_AsyncInitializeTableView(Integer type) {
        AsyncTask<Void, Void, List<List<Migeba_Cell>>> asyncTask = new AsyncTask<Void, Void, List<List<Migeba_Cell>>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            protected List<List<Migeba_Cell>> doInBackground(Void... params) {

                //TYPES
                //1 - სრული ინვენტარიზაცია
                //2 - სიით ინვენტარიზაცია
                //3 - არსებული (time, stat)

                switch (type) {
                    case 1:
                        getNewDateTime();
                        return getAllProducts();
                    case 2:
                        getNewDateTime();
                        return getEmptyInventory();
                    case 3:
                        return getOldInventory();
                    default:
                        getNewDateTime();
                        return getEmptyInventory();
                }
            }


            @Override
            protected void onPostExecute(List<List<Migeba_Cell>> products) {
                super.onPostExecute(products);
                progressbar.setVisibility(View.GONE);
                Migeba_TableViewModel migebaTableViewModel = new Migeba_TableViewModel();

                // Create TableView Adapter
                mTableView.setAdapter(migebaTableViewAdapter);

                mTableView.setTableViewListener(new Inventarizacia_TableViewListener(mTableView));
                Inventarizacia_TableViewListener.products_cache = products;


                migebaTableViewAdapter.setAllItems(getColumnHeaders(), getRowHeaders(), products);


                mTableView.setColumnWidth(0, 350);
                mTableView.setColumnWidth(1, 200);
                mTableView.setColumnWidth(2, 170);
                mTableView.setColumnWidth(3, 170);
                mTableView.setColumnWidth(4, 125);
                mTableView.setColumnWidth(5, 170);

                try {
                    Inventarizacia_TableViewListener.Edit_Card_Location(0, getAppContext());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        asyncTask.execute();
    }

    private static void getNewDateTime() {
        ResultSet rs = CONN_RESULTSET_SQL("select GETDATE()");
        try {
            while (rs.next()) {
                ag_time = rs.getString(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<List<Migeba_Cell>> getAllProducts() {
        List<List<Migeba_Cell>> list = new ArrayList<>();
        //InitializeScript
        addItemToInventoryDB("", ag_time);

        String sqlQuery = "SELECT * FROM [A_PLUS].[dbo].[INVENT] left join A_PLUS.dbo.PRODUCT on AG_PID=P_ID left join A_PLUS.dbo.LOCATION on AG_GLOC = L_ID left join  [A_PLUS].dbo.GET as tg2 on G_ID=AG_GID where AG_TIME = (SELECT top 1 AG_TIME from A_PLUS.dbo.INVENT order by AG_TIME desc)";


        ResultSet rs2 = CONN_RESULTSET_SQL(sqlQuery);
        Log.e("tag", sqlQuery);
        try {
            int i = 0;
            while (rs2.next()) {
                i++;
                List<Migeba_Cell> migebaCellList = new ArrayList<>();
                Migeba_Cell P_NAME = new Migeba_Cell(String.valueOf(i), TOGEO(rs2.getString("P_NAME")));
                Migeba_Cell P_LOC_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("P_LOC_ID"));
                Migeba_Cell AG_QUANT = new Migeba_Cell(String.valueOf(i), DebugTools.doubleStringToIntString(rs2.getString("AG_QUANT")));
                Migeba_Cell AG_ANAGC = new Migeba_Cell(String.valueOf(i), DebugTools.doubleStringToIntString(rs2.getString("AG_ANAGC")));
                Migeba_Cell AG_GLOC = new Migeba_Cell(String.valueOf(i), "");
                Migeba_Cell G_SERIA = new Migeba_Cell(String.valueOf(i), TOGEO(rs2.getString("G_SERIA")));
                try {
                    AG_GLOC = new Migeba_Cell(String.valueOf(i), rs2.getString("L_NAME"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Migeba_Cell AG_PID = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_PID"));
                Migeba_Cell AG_GID = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_GID"));
                Migeba_Cell G_EXP = new Migeba_Cell(String.valueOf(i), rs2.getString("G_EXP"));
                migebaCellList.add(P_NAME);
                migebaCellList.add(P_LOC_ID);
                migebaCellList.add(AG_QUANT);
                migebaCellList.add(AG_ANAGC);
                migebaCellList.add(AG_GLOC);
                migebaCellList.add(AG_PID);
                migebaCellList.add(G_SERIA);
                migebaCellList.add(G_EXP);
                migebaCellList.add(AG_GID);
                list.add(migebaCellList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addItemToInventoryDB(String seria, String time) {


        print("SERIA = " + seria);
        String seria1 = " [A_PLUS].[dbo].[GET]";
        String seria2 = time;
        if (!seria.equals("")) {
            seria1 = " ( select * from [A_PLUS].[dbo].[GET] where G_SERIA='" + seria + "' ) as gg";
        }

        CONN_EXECUTE_SQL(
                "declare @TT datetime = '" + seria2 + "' insert into [A_PLUS].[dbo].[INVENT](\n" +
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
                        seria1 + "  \n" +
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

        return true;
    }

    public static void addItemToInventoryDBAndShow(String seria, String time) {


        print("SERIA = " + seria);
        String seria1 = " [A_PLUS].[dbo].[GET]";
        String seria2 = time;
        if (!seria.equals("")) {
            seria1 = " ( select * from [A_PLUS].[dbo].[GET] where G_SERIA='" + seria + "' ) as gg";
        }

        for (int i = 0; i < Inventarizacia_TableViewListener.products_cache.size(); i++) {
            if (Inventarizacia_TableViewListener.products_cache.get(i).get(6).getData().equals(seria)) {
                Toast.makeText(getAppContext(), "ასეთი სერია უკვე არსებობს", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        CONN_EXECUTE_SQL("declare @TT datetime = '" + seria2 + "' insert into [A_PLUS].[dbo].[INVENT](\n" +
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
                seria1 + "  \n" +
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

        List<List<Migeba_Cell>> list = new ArrayList<>();
        //InitializeScript

        String sqlQuery = "SELECT * FROM [A_PLUS].[dbo].[INVENT]" +
                "  left join [A_PLUS].dbo.PRODUCT on AG_PID=P_ID " +
                "  left join [A_PLUS].dbo.GET as tg2 on G_ID=AG_GID " +
                " where G_SERIA = '" + seria + "' AND AG_TIME = '" + ag_time + "'";

        ResultSet rs2 = CONN_RESULTSET_SQL(sqlQuery);
        Log.e("tag", sqlQuery);
        try {
            int i = 0;
            while (rs2.next()) {
                i++;
                List<Migeba_Cell> migebaCellList = new ArrayList<>();
                Migeba_Cell P_NAME = new Migeba_Cell(String.valueOf(i), TOGEO(rs2.getString("P_NAME")));
                Migeba_Cell P_LOC_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("P_LOC_ID"));
                Migeba_Cell AG_QUANT = new Migeba_Cell(String.valueOf(i), DebugTools.doubleStringToIntString(rs2.getString("AG_QUANT")));
                Migeba_Cell AG_ANAGC = new Migeba_Cell(String.valueOf(i), DebugTools.doubleStringToIntString(rs2.getString("AG_ANAGC")));
                Migeba_Cell AG_GLOC = new Migeba_Cell(String.valueOf(i), "");
                try {
                    AG_GLOC = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_GLOC"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Migeba_Cell AG_PID = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_PID"));
                Migeba_Cell AG_GID = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_GID"));
                Migeba_Cell G_SERIA = new Migeba_Cell(String.valueOf(i), TOGEO(rs2.getString("G_SERIA")));
                Migeba_Cell G_EXP = new Migeba_Cell(String.valueOf(i), rs2.getString("G_EXP"));
                migebaCellList.add(P_NAME);
                migebaCellList.add(P_LOC_ID);
                migebaCellList.add(AG_QUANT);
                migebaCellList.add(AG_ANAGC);
                migebaCellList.add(AG_GLOC);
                migebaCellList.add(AG_PID);
                migebaCellList.add(G_SERIA);
                migebaCellList.add(G_EXP);
                migebaCellList.add(AG_GID);
                print(rs2.getString("P_NAME"));
                list.add(migebaCellList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int rowindex = mTableView.getAdapter().getRowHeaderRecyclerViewAdapter().getItemCount();

        for (int i = 0; i < list.size(); i++) {
            migebaTableViewAdapter.addRow(rowindex, new Migeba_RowHeader(String.valueOf(rowindex), String.valueOf(rowindex)), list.get(i));
            rowindex++;
        }
        updateCacheOfProducts();

    }


    private static List<List<Migeba_Cell>> getOldInventory() {
        List<List<Migeba_Cell>> list = new ArrayList<>();
        //InitializeScript

        String status = "0";
        if (ag_stat.equals("დაუსრულებელი")) {
            status = "0";
        } else if (ag_stat.equals("დასრულებული")) {
            status = "1";
        } else {
            status = "2";
        }

        String sqlQuery = "SELECT * FROM [A_PLUS].[dbo].[INVENT]" +
                "  left join [A_PLUS].dbo.PRODUCT on AG_PID=P_ID " +
                "  left join [A_PLUS].dbo.GET as tg2 on G_ID=AG_GID " +
                " where AG_STAT = '" + status + "' AND AG_TIME = '" + ag_time + "'";

        ResultSet rs2 = CONN_RESULTSET_SQL(sqlQuery);
        Log.e("tag", sqlQuery);
        try {
            int i = 0;
            while (rs2.next()) {
                i++;
                List<Migeba_Cell> migebaCellList = new ArrayList<>();
                Migeba_Cell P_NAME = new Migeba_Cell(String.valueOf(i), TOGEO(rs2.getString("P_NAME")));
                Migeba_Cell P_LOC_ID = new Migeba_Cell(String.valueOf(i), rs2.getString("P_LOC_ID"));
                Migeba_Cell AG_QUANT = new Migeba_Cell(String.valueOf(i), DebugTools.doubleStringToIntString(rs2.getString("AG_QUANT")));
                Migeba_Cell AG_ANAGC = new Migeba_Cell(String.valueOf(i), DebugTools.doubleStringToIntString(rs2.getString("AG_ANAGC")));
                Migeba_Cell AG_GLOC = new Migeba_Cell(String.valueOf(i), "");
                try {
                    AG_GLOC = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_GLOC"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Migeba_Cell AG_PID = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_PID"));
                Migeba_Cell AG_GID = new Migeba_Cell(String.valueOf(i), rs2.getString("AG_GID"));
                Migeba_Cell G_SERIA = new Migeba_Cell(String.valueOf(i), TOGEO(rs2.getString("G_SERIA")));
                Migeba_Cell G_EXP = new Migeba_Cell(String.valueOf(i), rs2.getString("G_EXP"));
                migebaCellList.add(P_NAME);
                migebaCellList.add(P_LOC_ID);
                migebaCellList.add(AG_QUANT);
                migebaCellList.add(AG_ANAGC);
                migebaCellList.add(AG_GLOC);
                migebaCellList.add(AG_PID);
                migebaCellList.add(G_SERIA);
                migebaCellList.add(G_EXP);
                migebaCellList.add(AG_GID);
                list.add(migebaCellList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static List<List<Migeba_Cell>> getEmptyInventory() {
        List<List<Migeba_Cell>> list = new ArrayList<>();
        return list;
    }

    public static List<Migeba_ColumnHeader> getColumnHeaders() {

        //G_SERIA,G_EXP,G_QUANT,G_PRICE,G_BAR1,P_NAME,P_FORM,P_ERT,P_LOC_ID
        List<Migeba_ColumnHeader> migebaColumnHeaders = new ArrayList<>();
        Migeba_ColumnHeader P_NAME = new Migeba_ColumnHeader("0", "დასახელება");
        Migeba_ColumnHeader P_LOC_ID = new Migeba_ColumnHeader("1", "ტემპერატურა");
        Migeba_ColumnHeader AG_QUANT = new Migeba_ColumnHeader("2", "რაოდენობა");
        Migeba_ColumnHeader AG_ANAGC = new Migeba_ColumnHeader("3", "ანაღწერი");
        Migeba_ColumnHeader AG_GLOC = new Migeba_ColumnHeader("4", "ლოკაცია");
        Migeba_ColumnHeader AG_PID = new Migeba_ColumnHeader("5", "AG_PID");
        Migeba_ColumnHeader G_SERIA = new Migeba_ColumnHeader("6", "სერია");
        Migeba_ColumnHeader G_EXP = new Migeba_ColumnHeader("7", "ვადა");
        Migeba_ColumnHeader G_ID = new Migeba_ColumnHeader("8", "G_ID");
        migebaColumnHeaders.add(P_NAME);
        migebaColumnHeaders.add(P_LOC_ID);
        migebaColumnHeaders.add(AG_QUANT);
        migebaColumnHeaders.add(AG_ANAGC);
        migebaColumnHeaders.add(AG_GLOC);
        migebaColumnHeaders.add(AG_PID);
        migebaColumnHeaders.add(G_SERIA);
        migebaColumnHeaders.add(G_EXP);
        migebaColumnHeaders.add(G_ID);
        return migebaColumnHeaders;

    }

    public static List<Migeba_RowHeader> getRowHeaders() {
        List<Migeba_RowHeader> migebaRowHeaderList = new ArrayList<>();
        int ROW_COUNT = 0;
//        //TYPES
//        //1 - სრული ინვენტარიზაცია
//        //2 - სიით ინვენტარიზაცია
//        //3 - არსებული (time, stat)
//        ResultSet rs2 = null;
//        print("getRowHeaders" + invent_type);
//        switch (invent_type) {
//            case 1:
//                rs2 = CONN_RESULTSET_SQL("select COUNT (*) from (SELECT AG_ANAGC, P_NAME, P_LOC_ID, AG_QUANT, AG_GLOC, AG_GID\n" +
//                        "  FROM [A_PLUS].[dbo].[INVENT] \n" +
//                        "  left join [A_PLUS].dbo.PRODUCT on [P_ID] = AG_PID\n" +
//                        "  where AG_TIME = (Select top 1 AG_TIME from [A_PLUS].[dbo].[INVENT])) x");
//                try {
//                    while (rs2.next()) {
//                        ROW_COUNT = rs2.getInt(1) + 1;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                break;
//            case 2:
//
//                ROW_COUNT = Inventarizacia_TableViewListener.products_cache.size() + 1;
//                break;
//            case 3:
//                rs2 = CONN_RESULTSET_SQL("select COUNT (*) from (SELECT AG_ANAGC, P_NAME, P_LOC_ID, AG_QUANT, AG_GLOC, AG_GID\n" +
//                        "  FROM [A_PLUS].[dbo].[INVENT] \n" +
//                        "  left join [A_PLUS].dbo.PRODUCT on [P_ID] = AG_PID\n" +
//                        "  where AG_TIME = '" + ag_time + "') x");
//                try {
//                    while (rs2.next()) {
//                        ROW_COUNT = rs2.getInt(1) + 1;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                break;
//            default:
//                ROW_COUNT = Inventarizacia_TableViewListener.products_cache.size() + 1;
//
//        }

        ROW_COUNT = mTableView.getAdapter().getCellColumnItems(2).size();
        ROW_COUNT = (Inventarizacia_TableViewListener.products_cache.size()) + 1;


        for (int i = 1; i < ROW_COUNT; i++) {
            Migeba_RowHeader header = new Migeba_RowHeader(String.valueOf(i), String.valueOf(i));
            migebaRowHeaderList.add(header);
        }


        return migebaRowHeaderList;
    }


}