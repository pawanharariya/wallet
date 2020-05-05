package com.example.pro;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddBalance extends AppCompatActivity implements PaymentResultListener {

    private long amount, currentAmount = 0;
    private Button addMoney, proceed;
    private EditText amountText;
    private View addAmountDialog;
    private String firebaseId;
    private TextView userBalanceTextView;
    private ProgressDialog post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_banlance);
        loadUI();
        firebaseId = "#paone"; //TODO to be replaced by firebase id of the user obtained after firebase authentication

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("user_balance").document(firebaseId);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("firebase", "document doesn't exist");
                    return;
                }
                if(documentSnapshot.exists())
                    currentAmount = (long) documentSnapshot.get("userBalance");
                    Log.e("amount",""+currentAmount);
                userBalanceTextView.setText("\u20B9 " + currentAmount);
            }
        });


        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAmountDialog.setVisibility(View.VISIBLE);
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(amountText.getText().toString()))
                    Toast.makeText(AddBalance.this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                else {
                    amount = Integer.parseInt(amountText.getText().toString());
                    if (amount <= 0)
                        Toast.makeText(AddBalance.this, "Please enter valid amount", Toast.LENGTH_SHORT).show();
                    else
                        startPayment(amount);
                }
            }
        });
    }

    public void startPayment(long amountToAdd) {
        Checkout checkout = new Checkout();
//        checkout.setKeyID("rzp_test_sVrW5vtI3Nd2pZ\t");
        checkout.setImage(R.drawable.pay);
        final Activity activity = AddBalance.this;
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Merchant Name");
            options.put("description", "Reference No. #123456");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", amountToAdd + "00");
            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("error", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        addAmountDialog.setVisibility(View.GONE);
        Log.e("Payment Success", s);
        Toast.makeText(AddBalance.this, "Money added to your wallet", Toast.LENGTH_SHORT).show();
        Checkout.clearUserData(this);
        postData(s);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.e("payment error", s);
        if (i == Checkout.NETWORK_ERROR) {
            Log.e("error payment failed", "Checkout.NETWORK_ERROR");
            Toast.makeText(this, "Poor Network, Payment Failed", Toast.LENGTH_SHORT).show();
        }
        if (i == Checkout.INVALID_OPTIONS) {
            Log.e("error payment failed", "Checkout.INVALID_OPTIONS");
            Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();

        }
        if (i == Checkout.PAYMENT_CANCELED) {
            Log.e("error payment failed", "Checkout.PAYMENT_CANCELED");
            Toast.makeText(this, "Payment Canceled by user", Toast.LENGTH_SHORT).show();

        }
        if (i == Checkout.TLS_ERROR) {
            Log.e("error payment failed", "Checkout.TLS_ERROR");
            Toast.makeText(this, "Payment Not supported", Toast.LENGTH_SHORT).show();
        }
    }

    private void postData(final String s) {
        post = new ProgressDialog(this);
        post.setCancelable(false);
        post.setMessage("Adding amount");
        post.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("user_balance").document(firebaseId);
        Map<String, Object> data = new HashMap<>();
        data.put("userBalance", currentAmount + amount);
        docRef.set(data)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        postTransaction(s);
                    }
                });
    }

    public void backDialog(View view) {
        addAmountDialog.setVisibility(View.GONE);
    }

    private void loadUI() {
        addMoney = findViewById(R.id.add_money);
        amountText = findViewById(R.id.amount);
        proceed = findViewById(R.id.proceed);
        addAmountDialog = findViewById(R.id.add_amount_dialog);
        userBalanceTextView = findViewById(R.id.user_balance);
    }

    private void postTransaction(String s) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef2 = db.collection("transactions").document();
        Map<String, Object> data2 = new HashMap<>();
        data2.put("userId", firebaseId);
        data2.put("amount", amount);
        data2.put("transactionId", s);
        docRef2.set(data2)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AddBalance.this, "Money added successfully", Toast.LENGTH_SHORT).show();
                        post.dismiss();
                    }
                });
    }
}
