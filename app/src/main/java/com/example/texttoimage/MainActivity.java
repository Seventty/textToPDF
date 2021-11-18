package com.example.texttoimage;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button button;
    int pageHeight = 1120;
    int pageWidth = 792;

    Bitmap bmp, scaledbmp;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.idBtnGeneratePDF);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.marlogo);
        scaledbmp = Bitmap.createScaledBitmap(bmp, 700, 600, false);

        if (checkPermission()) {
            Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }
        button.setOnClickListener(v -> generatePDF());
    }

    private void generatePDF() {
        //Document and page configuration
        PdfDocument pdfDocument = new PdfDocument();

        Paint paint = new Paint();
        Paint title = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(pageInfo);

        //Image banner
        Canvas canvas = myPage.getCanvas();
        canvas.drawBitmap(scaledbmp, 56, 40, paint);

        //Title text settings
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(25);
        title.setColor(ContextCompat.getColor(this, android.R.color.holo_blue_dark));
        canvas.drawText("MAR PRINTING SYSTEM", 229, 100, title);

        //Subtitle text settings
        title.setTextSize(20);
        title.setColor(ContextCompat.getColor(this, android.R.color.black));
        canvas.drawText("Ticket de impresion", 229, 70, title);

        //Body text setting
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        title.setColor(ContextCompat.getColor(this, R.color.black));
        title.setTextSize(15);
        title.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Este documento es un ticket de prueba de MAR nuevo", 396, 560, title);

        //Writing finished
        pdfDocument.finishPage(myPage);

        //File creation
        File file = new File(Environment.getExternalStorageDirectory(), "ticket.pdf");
        try {
            //End processor
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(MainActivity.this, "PDF file generated successfully.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Close pdf thread
        pdfDocument.close();
    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permisos denegados.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}