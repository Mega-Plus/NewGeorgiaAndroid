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
import static com.example.migeba.migeba.SetFilter;
import static com.example.migeba.migeba.getColumnHeaders;
import static com.example.migeba.migeba.getRowHeaders;
import static com.example.migeba.migeba.zednadebi_fab;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.listener.ITableViewListener;
import com.evrencoskun.tableviewsample.migeba_tableview.holder.Migeba_ColumnHeaderViewHolder;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_Cell;
import com.evrencoskun.tableviewsample.migeba_tableview.popup.Migeba_ColumnHeaderLongPressPopup;
import com.evrencoskun.tableviewsample.migeba_tableview.popup.Migeba_RowHeaderLongPressPopup;
import com.example.migeba.migeba;

import java.util.ArrayList;
import java.util.List;


public class Migeba_TableViewListener implements ITableViewListener {
    @NonNull
    private final Context context;
    @NonNull
    private final TableView mTableView;

    public static String ChosenLocation = "";

    public static AutoCompleteTextView loca_tv;
    public static List<String> locationsList = new ArrayList<>();


    public Migeba_TableViewListener(@NonNull TableView tableView) {
        this.context = tableView.getContext();
        this.mTableView = tableView;
    }

    /**
     * Called when user click any cell item.
     *
     * @param cellView : Clicked Cell ViewHolder.
     * @param column   : X (Column) position of Clicked Cell item.
     * @param row      : Y (Row) position of Clicked Cell item.
     */
    @Override
    public void onCellClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {
        // Do what you want.
        showToast("Cell " + column + " " + row + " has been clicked.");
        Migeba_Cell selectedMigebaCell = null;
        if (column == 0)
            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(0, row);
        if (column == 1)
            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(1, row);
        if (column == 2)
            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(2, row);
        if (column == 3)
            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(3, row);
        if (column == 4)
            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(4, row);
        if (column == 5)
            selectedMigebaCell = (Migeba_Cell) mTableView.getAdapter().getCellItem(5, row);

//        String productName = selectedMigebaCell.getFilterableKeyword();
//        if (productName.length() > 0) {
        migeba.setProductEditButtonText();
//        } else {
//            migeba.setProductEditButtonText("", false);
//        }

    }

    /**
     * Called when user double click any cell item.
     *
     * @param cellView : Clicked Cell ViewHolder.
     * @param column   : X (Column) position of Clicked Cell item.
     * @param row      : Y (Row) position of Clicked Cell item.
     */
    @Override
    public void onCellDoubleClicked(@NonNull RecyclerView.ViewHolder cellView, int column, int row) {

        if (!migeba.filterString.isEmpty()) {
            final String tempraryString = migeba.filterString;
            Migeba_Cell ro = ((Migeba_Cell) migeba.mTableView.getAdapter().getCellRowItems(row).get(0));
            SetFilter("");
            int i = migeba.mTableView.getAdapter().getCellColumnItems(0).indexOf(ro);
            migeba.Edit_Card(i, context);
            SetFilter(tempraryString);
        }else{
            migeba.Edit_Card(row,context);
        }

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
