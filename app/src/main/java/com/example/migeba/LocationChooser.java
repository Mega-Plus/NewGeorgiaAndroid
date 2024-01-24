package com.example.migeba;

import static com.example.migeba.MainActivity.CONN_RESULTSET_SQL;
import static com.example.migeba.MainActivity.CONN_EXECUTE_SQL;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.evrencoskun.tableviewsample.migeba_tableview.Dalageba_TableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.Inventarizacia_TableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.Migeba_TableViewListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

public class LocationChooser extends AppCompatActivity {

    ListView lv;
    ExtendedFloatingActionButton createLocationFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_location);

        lv = findViewById(R.id.listview_locations);
        createLocationFab = findViewById(R.id.createLocationFab);
        listviewInit();
        createLocationFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createLocation();
            }
        });

    }

    public void initializeLocationsSearch(SimpleAdapter simpleAdapter) {

        TextInputEditText filterEditText = findViewById(R.id.filterEditText);

        filterEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                simpleAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void createLocation() {
        MaterialAlertDialogBuilder change = new MaterialAlertDialogBuilder(LocationChooser.this);
        LayoutInflater lf = getLayoutInflater();
        View v = lf.inflate(R.layout.card_edit_location, null, false);
        change.setView(v);
        TextInputLayout input1;
        TextInputLayout input2;
        MaterialButton back = v.findViewById(R.id.button_previous);
        MaterialButton save = v.findViewById(R.id.edit_card_nextBtn);
        input1 = v.findViewById(R.id.card_edit_anagceri);
        input2 = v.findViewById(R.id.vada);
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

                listviewInit();
            }
        });
    }

    public void listviewInit() {
        ArrayList<HashMap<String, Object>> list = new ArrayList<>();

        ResultSet rs2 = CONN_RESULTSET_SQL("SELECT * FROM [A_PLUS].[dbo].[LOCATION]");
        try {
            while (rs2.next()) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("L_ID", rs2.getString("L_ID"));
                map.put("L_NAME", rs2.getString("L_NAME"));
                map.put("L_INFO", rs2.getString("L_INFO"));
//                Log.e(TAG, "onClick: " + map.toString());
                list.add(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] from = {"L_ID", "L_NAME", "L_INFO"};
        int[] to = {R.id.L_ID, R.id.L_NAME, R.id.L_INFO};

        SimpleAdapter simpleAdapter = new SimpleAdapter(LocationChooser.this, list, R.layout.item_location, from, to);


        lv.setAdapter(simpleAdapter);
        initializeLocationsSearch(simpleAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                chooseLocation(view);
            }
        });

    }

    public void chooseLocation(View view) {

        MaterialAlertDialogBuilder mb = new MaterialAlertDialogBuilder(LocationChooser.this);
        TextView title = view.findViewById(R.id.L_NAME);
        TextView info = view.findViewById(R.id.L_INFO);
        TextView id = view.findViewById(R.id.L_ID);
        mb.setTitle(title.getText().toString());
        mb.setMessage(info.getText().toString());
        mb.setNeutralButton("უკან", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        mb.setPositiveButton("არჩევა", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    Migeba_TableViewListener.loca_tv.setText(title.getText().toString(), false);
                } catch (Exception e) {
                    e.printStackTrace();

                }
                try {
                    migeba.loca_tv.setText(title.getText().toString(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Dalageba_TableViewListener.loca_tv.setText(title.getText().toString(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    Inventarizacia_TableViewListener.loca_tv.setText(title.getText().toString(), false);
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                Toast.makeText(getApplicationContext(), title.getText().toString(), Toast.LENGTH_SHORT).show();


                finish();
            }
        });
        mb.setNegativeButton("რედაქტირება", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MaterialAlertDialogBuilder change = new MaterialAlertDialogBuilder(LocationChooser.this);
                LayoutInflater lf = getLayoutInflater();
                View v = lf.inflate(R.layout.card_edit_location, null, false);
                change.setView(v);
                TextInputLayout input1;
                TextInputLayout input2;
                MaterialButton back = v.findViewById(R.id.button_previous);
                MaterialButton save = v.findViewById(R.id.edit_card_nextBtn);
                input1 = v.findViewById(R.id.card_edit_anagceri);
                input2 = v.findViewById(R.id.vada);
                AlertDialog changeDialog = change.create();
                changeDialog.show();

                input1.getEditText().setText(title.getText().toString());
                input2.getEditText().setText(info.getText().toString());
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
                        CONN_EXECUTE_SQL("UPDATE A_PLUS.DBO.LOCATION " +
                                "SET L_NAME = '" + input1.getEditText().getText().toString() + "'," +
                                "L_INFO = '" + input2.getEditText().getText().toString() + "'" +
                                "WHERE " +
                                "L_NAME = '" + title.getText() + "' AND " +
                                "L_INFO = '" + info.getText() + "' AND " +
                                "L_ID   = '" + id.getText() + "'");

                        listviewInit();
                    }
                });
            }
        });
        mb.show();
    }

}