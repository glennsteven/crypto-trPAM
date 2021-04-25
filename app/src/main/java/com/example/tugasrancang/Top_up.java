package com.example.tugasrancang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Top_up extends Activity {
    private EditText nominal;
    private Button btn_topUp;
    private String userId;
    private DatabaseReference mFirebaseDatabase;
    public static final String TAG="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_up);

        nominal = findViewById(R.id.nominal);
        btn_topUp = findViewById(R.id.btn_topUp);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("Saldo");

        nominal.addTextChangedListener(onTextChangeListener());
        btn_topUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topUp = nominal.getText().toString();
                    if (TextUtils.isEmpty(userId)){
                        createTransaksi(topUp);
                    }else {
                        updateTransaksi(topUp);

                    }
            }
        });
//        mFirebaseDatabase.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Biodata biodata = snapshot.getValue(Biodata.class);
//                if (biodata != null){
//                    String dompet = biodata.topUp;
//                    if (dompet.contains(",")){
//                        dompet.replaceAll(","," ");
//                        Log.e(TAG, "onDataChange(Dompet): "+dompet );
//                        Double dompet2 = Double.parseDouble(dompet);
//
//                        Double masukkan = Double.parseDouble(nominal.getText().toString());
//                        Log.e(TAG, "onDataChange(Masukkan): "+masukkan );
//
//                        Double hasil = dompet2 + masukkan;
//                        String converthasil = Double.toString(hasil);
//                        if (!TextUtils.isEmpty(converthasil)){
//                            mFirebaseDatabase.child(userId).child("Saldo").setValue(converthasil);
//                        }
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        toggleButton();
    }
    private void toggleButton(){
        if (TextUtils.isEmpty(userId)){
            btn_topUp.setText("Save");
        }else {
            btn_topUp.setText("Update");
        }
    }

    private void createTransaksi(String topUp){
        if (TextUtils.isEmpty(userId)){
            userId = mFirebaseDatabase.push().getKey();
        }

        Biodata biodata = new Biodata(topUp);

        mFirebaseDatabase.child(userId).setValue(biodata);

        addUserChangeListener();
    }

    private void addUserChangeListener() {
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Biodata biodata = snapshot.getValue(Biodata.class);
                if (biodata == null){
                    Log.e(TAG, "Data user null");
                    return;
                }
                Log.e(TAG,"Data User berubah"+biodata.getTopUp());
                toggleButton();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,"Gagal membaca data");
            }
        });
    }

    private void updateTransaksi(String topUp){
        Biodata biodata = new Biodata(topUp);
        if (!TextUtils.isEmpty(topUp)){
            FirebaseDatabase.getInstance().getReference("Saldo")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(biodata).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(Top_up.this, "Transaksi Berhasil",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Top_up.this,DashboardActicity.class);
                    startActivity(intent);
                    onDestroy();
                }
            });
        }
        else {
            Toast.makeText(Top_up.this, "Masukkan Nominal Uang",Toast.LENGTH_SHORT).show();
        }
    }
    private TextWatcher onTextChangeListener(){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nominal.removeTextChangedListener(this);
                try{
                    Long longvalue;
                    String originalRupiahs = s.toString();

                    if (originalRupiahs.contains(",")){
                        originalRupiahs = originalRupiahs.replaceAll(",", "");
                    }
                    longvalue = Long.parseLong(originalRupiahs);

                    NumberFormat formater = new DecimalFormat("#,###,###,###,###");
                    String formattedNumber = formater.format(longvalue);
                    nominal.setText(formattedNumber);
                    nominal.setSelection(nominal.getText().length());
                }catch (NumberFormatException n){
                    n.printStackTrace();
                }
                nominal.addTextChangedListener(this);
            }
        };
    }
}
