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

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.example.migeba.R;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_Cell;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_ColumnHeader;
import com.evrencoskun.tableviewsample.migeba_tableview.model.Migeba_RowHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Migeba_TableViewModel {

    // Columns indexes
    public static final int MOOD_COLUMN_INDEX = 3;
    public static final int GENDER_COLUMN_INDEX = 4;

    // Constant values for icons
    public static final int SAD = 1;
    public static final int HAPPY = 2;
    public static final int BOY = 1;
    public static final int GIRL = 2;

    // Constant size for dummy data sets
    private static final int COLUMN_SIZE = 500;
    private static final int ROW_SIZE = 500;

    // Drawables
    @DrawableRes
    private final int mBoyDrawable;
    @DrawableRes
    private final int mGirlDrawable;
    @DrawableRes
    private final int mHappyDrawable;
    @DrawableRes
    private final int mSadDrawable;

    public Migeba_TableViewModel() {
        // initialize drawables
        mBoyDrawable = R.drawable.ic_male;
        mGirlDrawable = R.drawable.ic_female;
        mHappyDrawable = R.drawable.ic_happy;
        mSadDrawable = R.drawable.ic_sad;
    }

    @NonNull
    private List<Migeba_RowHeader> getSimpleRowHeaderList() {
        List<Migeba_RowHeader> list = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {
            Migeba_RowHeader header = new Migeba_RowHeader(String.valueOf(i), "row " + i);
            list.add(header);
        }

        return list;
    }

    /**
     * This is a dummy model list test some cases.
     */
    @NonNull
    private List<Migeba_ColumnHeader> getRandomColumnHeaderList() {
        List<Migeba_ColumnHeader> list = new ArrayList<>();

        for (int i = 0; i < COLUMN_SIZE; i++) {
            String title = "column " + i;
            int nRandom = new Random().nextInt();
            if (nRandom % 4 == 0 || nRandom % 3 == 0 || nRandom == i) {
                title = "large column " + i;
            }

            Migeba_ColumnHeader header = new Migeba_ColumnHeader(String.valueOf(i), title);
            list.add(header);
        }

        return list;
    }

    /**
     * This is a dummy model list test some cases.
     */
    @NonNull
    private List<List<Migeba_Cell>> getCellListForSortingTest() {
        List<List<Migeba_Cell>> list = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {
            List<Migeba_Cell> migebaCellList = new ArrayList<>();
            for (int j = 0; j < COLUMN_SIZE; j++) {
                Object text = "cell " + j + " " + i;

                final int random = new Random().nextInt();
                if (j == 0) {
                    text = i;
                } else if (j == 1) {
                    text = random;
                } else if (j == MOOD_COLUMN_INDEX) {
                    text = random % 2 == 0 ? HAPPY : SAD;
                } else if (j == GENDER_COLUMN_INDEX) {
                    text = random % 2 == 0 ? BOY : GIRL;
                }

                // Create dummy id.
                String id = j + "-" + i;

                Migeba_Cell migebaCell;
                if (j == 3) {
                    migebaCell = new Migeba_Cell(id, text);
                } else if (j == 4) {
                    // NOTE female and male keywords for filter will have conflict since "female"
                    // contains "male"
                    migebaCell = new Migeba_Cell(id, text);
                } else {
                    migebaCell = new Migeba_Cell(id, text);
                }
                migebaCellList.add(migebaCell);
            }
            list.add(migebaCellList);
        }

        return list;
    }

    @DrawableRes
    public int getDrawable(int value, boolean isGender) {
        if (isGender) {
            return value == BOY ? mBoyDrawable : mGirlDrawable;
        } else {
            return value == SAD ? mSadDrawable : mHappyDrawable;
        }
    }

    @NonNull
    public List<List<Migeba_Cell>> getCellList() {
        return getCellListForSortingTest();
    }

    @NonNull
    public List<Migeba_RowHeader> getRowHeaderList() {
        return getSimpleRowHeaderList();
    }

    @NonNull
    public List<Migeba_ColumnHeader> getColumnHeaderList() {
        return getRandomColumnHeaderList();
    }
}
