package com.example.migeba;

import static android.content.ContentValues.TAG;
import static com.example.migeba.Utils.DebugTools.print;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.datalogic.android.sdk.BuildConfig;
import com.example.migeba.Utils.PrinterHelper;
import com.example.migeba.Utils.geotranslate;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {


    public static String USER_ID = "";
    public static String conn_ip = "192.168.1.140";
//    public static String conn_ip = "192.168.1.1";
    public static String port = "1433";
    public static String Classes = "net.sourceforge.jtds.jdbc.Driver";
    public static String username = "sa";
    public static String password = "@Irakli24";
//    public static String password = "12Niangi";
    public static String url = "jdbc:jtds:sqlserver://" + conn_ip + ":" + port + ";databaseName=A_PLUS;user=" + username + ";password=" + password;

    private static final String API_URL = "https://vercel-api-hazel.vercel.app/modulesUpdates.json";

    public static java.sql.Connection connection = null;

    Context context;

    public static Context appContext;

    @SuppressLint("StaticFieldLeak")
    public static Activity mainActivity;

    public static String SELECTED_USER = "";

    MaterialCardView inventarizacia_cardview;
    MaterialCardView migeba_cardview;
    MaterialCardView dalageba_cardview;
    MaterialCardView shegroveba_cardview;
    TextView shared_element_migheba;
    public static FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appContext = getApplicationContext();
        context = this;
        mainActivity = MainActivity.this;


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        LOAD_SQL();

        SetOnClickListeners();


    }

    private void SetOnClickListeners() {
        migeba_cardview = findViewById(R.id.migeba_cardview);
        shared_element_migheba = findViewById(R.id.shared_element_migheba);
        inventarizacia_cardview = findViewById(R.id.inventory_cardview);
        dalageba_cardview = findViewById(R.id.dalageba_cardview);
        shegroveba_cardview = findViewById(R.id.shegroveba_cardview);
        shegroveba_cardview.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Shegroveba.class);
            startActivity(intent);
        });
        migeba_cardview.setOnClickListener(view -> {
            Intent intent = new Intent(context, migeba.class);
            startActivity(intent);
        });
        dalageba_cardview.setOnClickListener(view -> {

            MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(MainActivity.this);
            LayoutInflater lf = getLayoutInflater();
            View v = lf.inflate(R.layout.card_choose_dalageba, null, false);
            ListView lv = v.findViewById(R.id.dalageba_listview);
            //SET ADAPTER

            ArrayList<HashMap<String, Object>> list = new ArrayList<>();

            ResultSet rs2 = CONN_RESULTSET_SQL("SELECT * from(select  G_D_ID,[G_ZDN],[G_TIME] ,cast (sum([G_QUANT]*[G_PRICE]) as decimal(18,2)) as jam  \n" +
                    "                FROM [A_PLUS].[dbo].[GET] where G_ACT=3 group by [G_ZDN],[G_TIME] ,G_D_ID)as t2\n" +
                    "                outer apply  (select d_name from [A_PLUS].[dbo].[DISTRIBUTOR] where D_ID=G_D_ID)as t3");
            try {
                while (rs2.next()) {
                    HashMap<String, Object> map = new HashMap<>();
//                map.put("G_ID", rs2.getString("G_ID"));
                    map.put("G_ZDN", rs2.getString("G_ZDN"));
                    map.put("G_TIME", _parseTime(rs2.getString("G_TIME")));
                    map.put("jam", rs2.getString("jam") + "₾");
                    map.put("d_name", geotranslate.TOGEO(rs2.getString("d_name")));
                    Log.e(TAG, "onClick: " + map.toString());
                    list.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] from = {"G_ZDN", "G_TIME", "jam", "d_name"};
            int[] to = {R.id.L_NAME, R.id.L_INFO, R.id.zednadebi_button, R.id.zednadebi_dist_name};
//                lv.setDividerHeight(0);

            SimpleAdapter simpleAdapter = new SimpleAdapter(MainActivity.this, list, R.layout.card_zednadebi_alertdialog, from, to);

            lv.setAdapter(simpleAdapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Intent dalageba = new Intent(MainActivity.this, Dalageba.class);
                    Bundle bundle = new Bundle();
                    TextView zednadebi_tv = view.findViewById(R.id.L_NAME);
                    String zednadebi_id = zednadebi_tv.getText().toString();
                    bundle.putString("zednadebi", zednadebi_id);
                    dalageba.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    dalageba.putExtras(bundle);
                    startActivity(dalageba);
                }
            });
            //Controlling width and height.
            mb.setView(v);
            AlertDialog alertDialog = mb.create();
//                alertDialog.getWindow().setLayout(480, ViewGroup.LayoutParams.WRAP_CONTENT); //Controlling width and height.
            alertDialog.getWindow().getDecorView().getBackground().setTint(Color.WHITE);
            alertDialog.show();
//                startActivity(inventarizacia);
        });
        inventarizacia_cardview.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, Inventarizacia.class);
            Bundle bundle = new Bundle();
            bundle.putInt("type", 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtras(bundle);
            startActivity(intent);
        });
    }

    private void SetThreadPolicies() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .build());
    }

    private void GetCachedServerSettings() {
        SharedPreferences userDetails = context.getSharedPreferences("settings", MODE_PRIVATE);
        conn_ip = userDetails.getString("ip", "");
        username = userDetails.getString("name", "");
        password = userDetails.getString("password", "");
        url = "jdbc:jtds:sqlserver://" + conn_ip + ":" + port + ";databaseName=A_PLUS;user=" + username + ";password=" + password;
    }


    public static Context getAppContext() {
        return MainActivity.appContext;
    }

    public static void ConnectAndPrint(Context context, String barcode, String headerText, String bottomLeft1, String bottomLeft2, String bottomRightText) {
        try {
            PrinterHelper.ConnectPrinter(barcode, headerText, bottomLeft1, bottomLeft2, bottomRightText, "192.168.1.70", context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void ConnectAndPrintPOS(Context context, String barcode, String headerText, String bottomLeft1, String bottomLeft2, String bottomRightText) {
        try {
            PrinterHelper.ConnectPrinter(barcode, headerText, bottomLeft1, bottomLeft2, bottomRightText, "192.168.1.72", context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void VibrateForMs(int ms) {
        try {
            Vibrator v = (Vibrator) getAppContext().getSystemService(Context.VIBRATOR_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(ms);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void LOAD_SQL() {
        Log.d(TAG, "LOAD_SQL: Loading");

        SetThreadPolicies();
        GetCachedServerSettings(); //from SharedPreferences
        connection = CONNECTION_MAIN(username, password);
    }

    public static void CONN_EXECUTE_SQL(String query) {
        Connection conn;
        LogOnline("pre_execute", query, "CONN_EXECUTE_SQL");


        try {

            connection.close();


            Class.forName(Classes);
            conn = DriverManager.getConnection(url, username, password);

            connection = conn;

            if (connection != null && !connection.isClosed()) {
                _EXECUTE_SQL(query);
            } else {
                CONN_EXECUTE_SQL(query);
            }

        } catch (Exception ex) {

            ex.printStackTrace();
            /// NO INTERNET ///
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(appContext);
            materialAlertDialogBuilder.setTitle("ბაზასთან კავშირი არ არის");
            materialAlertDialogBuilder.setMessage("შეამოწმეთ წვდომა ინტერნეტთან\n" + conn_ip + ":" + port);
            materialAlertDialogBuilder.setNeutralButton("პარამეტრები", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    OpenSettings(null);
                }
            });
            materialAlertDialogBuilder.setPositiveButton("გადატვირთვა", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent mStartActivity = new Intent(appContext, MainActivity.class);
                    int mPendingIntentId = 2389;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(appContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager mgr = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);
                }
            });
            materialAlertDialogBuilder.setCancelable(false);
            materialAlertDialogBuilder.show();

            /// NO INTERNET ///

        }
    }

    public static ResultSet CONN_RESULTSET_SQL(String query) {

        LogOnline("pre_execute", query, "CONN_RESULTSET_SQL");
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Connection conn = null;
        ResultSet rs;
        try {
            if (connection.isClosed() || connection == null) {
                connection.close();
                Class.forName(Classes);
                try {
                    connection = DriverManager.getConnection(url, username, password);

                    if (connection != null && !connection.isClosed()) {
                        rs = _RESULTSET_SQL(query);
                        return rs;
                    } else {
                        CONN_RESULTSET_SQL(query);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    /// NO INTERNET ///
                    MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(appContext);
                    materialAlertDialogBuilder.setTitle("ბაზასთან კავშირი არ არის");
                    materialAlertDialogBuilder.setMessage("შეამოწმეთ წვდომა ინტერნეტთან\n" + conn_ip + ":" + port);
                    materialAlertDialogBuilder.setNeutralButton("პარამეტრები", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            OpenSettings(null);
                        }
                    });
                    materialAlertDialogBuilder.setPositiveButton("გამორთვა", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent mStartActivity = new Intent(appContext, MainActivity.class);
                            int mPendingIntentId = 2389;
                            PendingIntent mPendingIntent = PendingIntent.getActivity(appContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                            AlarmManager mgr = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
                            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                            System.exit(0);
                        }
                    });
                    materialAlertDialogBuilder.setCancelable(false);
                    materialAlertDialogBuilder.show();
                }
                print("connection is null");
            } else {
                rs = _RESULTSET_SQL(query);
                return rs;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static public void OpenSettings(View view) {
        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(mainActivity);
        mb.setTitle("პარამეტრები");
        mb.setPositiveButton("IP", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(mainActivity);
                View settings = mainActivity.getLayoutInflater().inflate(R.layout.alertdialog_change_settings, null);


                MaterialButton option_save = settings.findViewById(R.id.option_save);
                MaterialButton button_previous = settings.findViewById(R.id.button_previous);
                TextInputLayout options_password = settings.findViewById(R.id.options_password);
                TextInputLayout options_ip = settings.findViewById(R.id.options_ip);
                TextInputLayout options_name = settings.findViewById(R.id.options_name);
                options_ip.getEditText().setText(conn_ip);
                options_name.getEditText().setText(username);
                options_password.getEditText().setText(password);
                dialog.setView(settings);

                AlertDialog alertDialog = dialog.create();

                option_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //save contents of textinputlayouts into sharedpreferences
                        SharedPreferences sharedPreferences = mainActivity.getSharedPreferences("settings", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ip", options_ip.getEditText().getText().toString());
                        editor.putString("name", options_name.getEditText().getText().toString());
                        editor.putString("password", options_password.getEditText().getText().toString());
                        editor.apply();
                        url = "jdbc:jtds:sqlserver://" + conn_ip + ":" + port + ";databaseName=A_PLUS;user=" + username + ";password=" + password;
                        alertDialog.dismiss();
                    }
                });

                button_previous.setOnClickListener(view1 -> alertDialog.dismiss());


                alertDialog.show();

            }
        });
        mb.setNegativeButton("განახლება", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showUpdater();
            }
        });
        mb.setNeutralButton("უკან", (dialogInterface, i) -> dialogInterface.dismiss());
        mb.show();
    }

    public static void showUpdater() {
        RequestQueue requestQueue = Volley.newRequestQueue(mainActivity);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String apk = response.getString("apk");
                            JSONObject data = response.getJSONObject("data");
                            String title = data.getString("title");
                            int version = data.getInt("version");
                            String versionName = data.getString("versionName");

                            MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(mainActivity);

                            if (BuildConfig.VERSION_CODE < version) {
                                mb.setTitle("განახლება მოიძებნა");
                                mb.setMessage("ვერსია: " + version + "\nთარიღი " + versionName);
                                mb.setPositiveButton("გადმოწერა", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //download and install apk
                                        downloadApk(apk);
                                    }
                                });
                            } else {
                                mb.setTitle("განახლება ვერ მოიძებნა");
                            }
                            mb.setNegativeButton("უკან", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            mb.show();
                        } catch (JSONException e) {
                            Log.e("JSON Parsing Error", e.getMessage());
                        }
                    }
                },
                error -> Log.e("API Error", error.getMessage())
        );
        requestQueue.add(jsonObjectRequest);
    }

    private static void downloadApk(String apkUrl) {
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "modules.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //get url of app on server
        String url = apkUrl;

        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("განახლება");
        request.setTitle(mainActivity.getString(R.string.app_name));

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) mainActivity.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(uri,manager.getMimeTypeForDownloadedFile(downloadId));
                mainActivity.startActivity(install);

                mainActivity.unregisterReceiver(this);
                mainActivity.finish();
            }
        };
        //register receiver for when .apk download is compete
        mainActivity.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }


    public Connection CONNECTION_MAIN(String username, String password) {
        Connection conn = null;
        try {
            Class.forName(Classes);
            conn = DriverManager.getConnection(url, username, password);
            connection = conn;

            //SELECT USER
            try {
                SELECT_USER();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {

            ex.printStackTrace();
            /// NO INTERNET ///
            MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
            materialAlertDialogBuilder.setTitle("ბაზასთან კავშირი არ არის");
            materialAlertDialogBuilder.setMessage("შეამოწმეთ წვდომა ინტერნეტთან\n" + conn_ip + ":" + port);
            materialAlertDialogBuilder.setNeutralButton("პარამეტრები", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    OpenSettings(null);
                }
            });
            materialAlertDialogBuilder.setPositiveButton("გამორთვა", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent mStartActivity = new Intent(context, MainActivity.class);
                    int mPendingIntentId = 123456;
                    PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                    AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                    System.exit(0);
                }
            });
            materialAlertDialogBuilder.setCancelable(false);
            materialAlertDialogBuilder.show();

            /// NO INTERNET ///

        }
        return conn;
    }

    public void SELECT_USER() throws InterruptedException {
        ArrayList<String> U_ID = new ArrayList<String>();
        ArrayList<String> Passwords = new ArrayList<String>();
        Passwords.add("432");
        Passwords.add("321");
        Passwords.add("136");
        Passwords.add("718");
        Passwords.add("654");
        Passwords.add("889");
        Passwords.add("532");
        Passwords.add("123");
        Passwords.add("876");
        Passwords.add("435");
        Passwords.add("414");
        Passwords.add("223");
        Passwords.add("754");
        Passwords.add("877");
        Passwords.add("332");
        ArrayList<String> CLIENTS = new ArrayList<String>();
        Log.e("SELECTING", "USER");
        if (connection == null) Log.e("connn", "nnull");
        ResultSet rs2 = CONN_RESULTSET_SQL("SELECT TOP 15 [U_ID]\n" +
                "      ,[U_NAME]\n" +
                "      ,[U_STAT]\n" +
                "      ,[U_PASS]\n" +
                "  FROM [A_PLUS].[dbo].[USERS] order by U_ID asc");
        int i = 0;
        try {
            while (rs2.next()) {
                i++;
                String UID = rs2.getString("U_ID");

                CLIENTS.add("User " + UID);
                U_ID.add(UID);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int[] checkedItem = {0};
        String[] items = CLIENTS.toArray(new String[CLIENTS.size()]);
        //შეადგინოს სია

        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(context);
        mb.setCancelable(false);
        mb.setIcon(R.drawable.ic_baseline_groups_3_24);
        mb.setTitle("აირჩიეთ მომხმარებელი");
        mb.setSingleChoiceItems(items, checkedItem[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                checkedItem[0] = i;
            }
        });

        mb.setPositiveButton("არჩევა", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                USER_ID = U_ID.get(checkedItem[0]);

                MaterialAlertDialogBuilder pass = new MaterialAlertDialogBuilder(context);
                pass.setCancelable(false);
                LayoutInflater lf = getLayoutInflater();
                View v = lf.inflate(R.layout.card_user_pass, null, false);
                pass.setView(v);
                pass.setIcon(R.drawable.ic_baseline_key_24);
                pass.setTitle("შეიყვანეთ პაროლი");
                TextInputLayout tx;
                tx = v.findViewById(R.id.passwordField);
                pass.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean x = false;
                        if (tx.getEditText().getText().toString().equals(Passwords.get(checkedItem[0]))) {
                            //SUCCESS
                            Snackbar.make(getWindow().getDecorView().getRootView(), "არჩეულია " + CLIENTS.get(checkedItem[0]), Snackbar.LENGTH_SHORT).show();
                            SELECTED_USER = CLIENTS.get(checkedItem[0]);
                        } else {
                            //PASS WRONG
                            //TRY AGAIN
                            Snackbar.make(getWindow().getDecorView().getRootView(), "პაროლი არასწორია", Snackbar.LENGTH_SHORT).show();

                            try {
                                SELECT_USER();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                pass.show();

            }
        });
        mb.show();


    }

    public static ResultSet _RESULTSET_SQL(String QUERY) {
        Log.e(TAG, "SQL_ResultSet:" + QUERY);
        ResultSet RST2 = null;
        try {
            Statement stmt = connection.createStatement();
            RST2 = stmt.executeQuery(QUERY);

            LogOnline("executed", QUERY, "_RESULTSET_SQL");


        } catch (Exception das) {
            das.printStackTrace();
        }
        return RST2;
    }

    public static void _EXECUTE_SQL(String QUERY) {
        if (!QUERY.equals("")) {
            try {
                print(QUERY);
                Statement stmt = connection.createStatement();
                stmt.executeUpdate(QUERY);
                stmt.close();

                LogOnline("executed", QUERY, "_EXECUTE_SQL");
            } catch (Exception das) {
                das.printStackTrace();
            }
        }
    }


    public static void LogOnline(String id, String name, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, type);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }


    public static String _parseTime(String time) {
        String year = time.substring(0, 4);
        String month = time.substring(4, 6);
        String day = time.substring(6, 8);

        return year + "." + month + "." + day;
    }


}
