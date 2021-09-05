package com.example.qrscanner;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class QRGenerator extends AppCompatActivity {
    TextView tv;
    EditText et;
    String text;
    Button generateqrbt,saveqrimagebt;
    ImageView iv;
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_generator);
        generateqrbt=findViewById(R.id.GenerateQRButton);
        saveqrimagebt=findViewById(R.id.SaveQRImageButton);
        tv=findViewById(R.id.textView2);
        et=findViewById(R.id.InputText);
        iv=findViewById(R.id.imageView);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        saveqrimagebt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                if(text==null)
                {
                    Toast.makeText(QRGenerator.this,"QR is not created yet",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    saveQRToDevice();
                    Toast.makeText(QRGenerator.this,"QR saved succesfully",Toast.LENGTH_SHORT).show();
                }
            }
        });
        generateqrbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et.getText().length()<1)
                {
                    Toast.makeText(QRGenerator.this,"Please Enter Text",Toast.LENGTH_SHORT).show();
                }
                else {
                iv.setImageBitmap(genarteQRCode(et.getText().toString()));
                text=et.getText().toString();
                }
            }
        });
    }
    private void saveQRToDevice()
    {
        BitmapDrawable drawable=(BitmapDrawable) iv.getDrawable();
        Bitmap bitmap= drawable.getBitmap();
        FileOutputStream outputStream=null;
        String root=Environment.getExternalStorageDirectory().toString();
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
            Toast.makeText(QRGenerator.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        try
        {
         outputStream.flush();
        }
        catch (IOException e)
        {
            Toast.makeText(QRGenerator.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
        try
        {
            outputStream.close();;
        }
        catch (IOException e)
        {
            Toast.makeText(QRGenerator.this,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }
    private Bitmap genarteQRCode(String text)
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
                    bitmap.setPixel(x,y,Color.BLUE);
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
        return bitmap;
    }
}
