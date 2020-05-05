package com.example.pro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.razorpay.Checkout;

public class MainActivity extends AppCompatActivity {

    ImageButton pay;
    Button code;
    Button discount;
    Button mycash;
    Button cancel;
    Button alltrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pay = findViewById(R.id.imageButton2);
        code = findViewById(R.id.button);
        discount = findViewById(R.id.button2);
        mycash = findViewById(R.id.button3);
        cancel = findViewById(R.id.button4);
        alltrans = findViewById(R.id.button5);
        Checkout.preload(MainActivity.this);  //for loading payment activity faster
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "here phonepe,googlepay,paytm is accesed", Toast.LENGTH_SHORT).show();
            }
        });

        code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "here qr code is opened", Toast.LENGTH_SHORT).show();
            }
        });

        discount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "here discount will be displayed for present day", Toast.LENGTH_SHORT).show();
            }
        });

        mycash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddBalance.class);
                startActivity(i);
            }
        });

        alltrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "here all transactions with transaction id and date&time is shown", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), transactions.class);
                startActivity(i);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "here it will return to homepage", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
