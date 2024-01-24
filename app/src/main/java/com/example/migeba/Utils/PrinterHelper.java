package com.example.migeba.Utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.text.TextPaint;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import print.Print;

public class PrinterHelper {

    public static void ConnectPrinter(String barcode, String headerText, String bottomLeft1, String bottomLeft2, String bottomRightText, String strIPAddress, Context context) {
        try {
            String strPort = "9100";

            if (strIPAddress.equals("192.168.1.72")) {
                if (!Print.IsOpened()) {
                    Print.PortOpen(context, ("WiFi," + strIPAddress + "," + strPort));
                }
                PrintOnPOS(barcode, headerText, bottomLeft1, bottomLeft2, bottomRightText);
            } else {

                PrintBarcode(barcode, headerText, bottomLeft1);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void PrintBarcode(String barcode, String headerText, String bottomLeft1) throws IOException {
        Socket s = new Socket("192.168.1.70", 9100);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        dout.write(("SIZE 500mm,50mm\r\n" +
                "CLS\r\n" +
                "TEXT 85,50,\"12.TTF\",0,41,41,\"" + headerText + "\"\r\n" +
                "BARCODE 85,130,\"EAN128\",100,0,0,2,2,\"" + barcode + "\"\r\n" +
                "TEXT 85,250,\"0\",0,18,18,\"" + barcode + "\"\r\n" +
                "TEXT 85,310,\"12.TTF\",0,33,33,\"" + bottomLeft1 + "\"\r\n" +
                "PRINT 1,1\r\n").getBytes());


        dout.flush();
        dout.close();
        s.close();
    }

    public static void PrintOnPOS(String barcode, String headerText, String bottomLeft1, String bottomLeft2, String bottomRightText) {

        Bitmap bitmap = Bitmap.createBitmap(600, 270, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        TextPaint paint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);
        paint.setTextSize(40);

        String text = barcode + "  " + /*ყუთების რაოდენობა*/ headerText + "\n" + // პარკების რაოდენობა
                bottomLeft1.split("⌠")[0] + "\n" + //კლიენტის/მაღაზიის სახელი
                bottomLeft1.split("⌠")[1] + "\n" +//მისამართი
                bottomLeft2 + "\n" + //იუზერის სახელი
                bottomRightText //თარიღი/დრო
                ;


        List<Float> sizes = new ArrayList<>();

        sizes.add(40f); //ყუთების რაოდენობა
        sizes.add(40f); //პარკების რაოდენობა
        sizes.add(40f); //კლიენტის/მაღაზიის სახელი
        sizes.add(35f); //იუზერის სახელი
        sizes.add(35f); //თარიღი/დრო

//        for (String line : text.split("\n")) {
//            canvas.drawText(line, x, y, paint);
//            y += paint.descent() - paint.ascent();
//        }

        int x = 20, y = 40;
        for (int i = 0; i < text.split("\n").length; i++) {
            paint.setTextSize(sizes.get(i));
            canvas.drawText(text.split("\n")[i], x, y, paint);
            y += paint.descent() - paint.ascent();
        }

        try {
            Print.PrintBitmap(bitmap, 0, 0);
            Print.PrintAndLineFeed();
            Print.CutPaper(1, 10);

        } catch (Exception e) {
            Log.e("PRINTER", "Error POS PRINTER - >" + e.getMessage());
        }

    }

}
