package com.example.qrscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextView tv;
    Button scanbt,generatorbt,googlebt,webpagebt,saveqrbt,showoptionsbt;
    Intent GeneratorIntent;
    String searchgooglest="https://www.google.com/search?q=";
    LinearLayout optionslt;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.item1:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.item2:
                startActivity(new Intent(this,QRGenerator.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        scanbt=findViewById(R.id.scanbutton);
        generatorbt=findViewById(R.id.GeneratorButton);
        googlebt=findViewById(R.id.GoogleButton);
        webpagebt=findViewById(R.id.WebpageButton);
        saveqrbt=findViewById(R.id.SaveQRButton);
        tv=findViewById(R.id.textView);
        optionslt=findViewById(R.id.OptionsLayout);
        showoptionsbt=findViewById(R.id.ShowOptionsButton);
        if(showoptionsbt.getVisibility()==View.GONE)
        {
            showoptionsbt.setVisibility(View.VISIBLE);
        }
        if(optionslt.getVisibility()==View.VISIBLE)
        {
            optionslt.setVisibility(View.GONE);
        }
        saveqrbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv.length()<1)
                {
                    Toast.makeText(MainActivity.this,"QR Not scanned yet",Toast.LENGTH_SHORT).show();
                }
                else
                {
                SaveScannedQR(tv.getText().toString());
                    Toast.makeText(MainActivity.this,"QR Saved succesfully",Toast.LENGTH_SHORT).show();
                }
            }
        });
        showoptionsbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(optionslt.getVisibility()==View.GONE)
                {
                    optionslt.setVisibility(View.VISIBLE);
                }
                showoptionsbt.setVisibility(View.GONE);
            }
        });
        webpagebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(tv.length()<1)
                {
                    Toast.makeText(MainActivity.this,"QR Not scanned yet",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    try{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(tv.getText().toString())));
                    showoptionsbt.setVisibility(View.VISIBLE);
                    optionslt.setVisibility(View.GONE);
                    tv.setText(null);
                    }
                    catch (Exception e)
                    {

                    }
                }
            }
        });
        googlebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tv.length()<1)
                {
                    Toast.makeText(MainActivity.this,"QR Not scanned yet",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    searchgooglest+=tv.getText().toString();
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(searchgooglest)));
                    showoptionsbt.setVisibility(View.VISIBLE);
                    optionslt.setVisibility(View.GONE);
                    tv.setText(null);
                }
            }
        });
        scanbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator=new IntentIntegrator(MainActivity.this);
                intentIntegrator.setPrompt("For flash use volume up key");
                intentIntegrator.setBeepEnabled(false);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(QRScannerActivity.class);
                intentIntegrator.initiateScan();
            }
        });
        generatorbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeneratorIntent=new Intent(MainActivity.this,QRGenerator.class);
                startActivity(GeneratorIntent);
            }
        });
}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        IntentResult intentResult=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult!=null)
        {
            if(intentResult.getContents()!=null)
            {
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Result");
                builder.setMessage(intentResult.getContents());
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                tv.setText(intentResult.getContents());
            }
            else
                {
                    Toast.makeText(getApplicationContext(),"Not scanned",Toast.LENGTH_SHORT).show();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void SaveScannedQR(String text)
    {
        int width=500;
        int height=500;
        Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        MultiFormatWriter codeWriter=new MultiFormatWriter();
        try
        {
            BitMatrix bitMatrix=codeWriter.encode(text, BarcodeFormat.QR_CODE,width,height);
            for(int x=0;x<width;x++)
            {
                for(int y=0;y<width;y++)
                {
                    if(bitMatrix.get(x,y))
                    {
                        bitmap.setPixel(x,y, Color.BLUE);
                    }
                    else
                    {
                        bitmap.setPixel(x,y,Color.WHITE);
                    }
                }
            }
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
        FileOutputStream outputStream=null;
        String root= Environment.getExternalStorageDirectory().toString();
        File dir=new File(root+"/DCIM");
        dir.mkdirs();
        String filename=text+".png";
        File outfile=new File(dir,filename);
        try
        {
            outputStream=new FileOutputStream(outfile);
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        try
        {
            outputStream.flush();
        }
        catch (IOException e)
        {
            Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
        try
        {
            outputStream.close();;
        }
        catch (IOException e)
        {
            Toast.makeText(MainActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
}