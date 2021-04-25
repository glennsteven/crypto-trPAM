package com.example.tugasrancang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DashboardActicity extends AppCompatActivity {

    private FirebaseUser user;
    private ImageView btn_topUp;
    private TextView profile;
    private ImageView srcprofile;
    private DatabaseReference reference;
    private String userId;
    public TextView txt_saldo;
    public String TAG;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        btn_topUp = findViewById(R.id.btn_topUp);
        profile = findViewById(R.id.profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();

        Log.e(TAG, "onCreate: "+ userId );

        txt_saldo = findViewById(R.id.txt_saldo);
        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        reference = mFirebaseInstance.getReference("Saldo");

        Intent intent = getIntent();
        String name = intent.getStringExtra("sendemail");
        profile.setText(name);

        srcprofile = findViewById(R.id.scrprofile);
        srcprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(DashboardActicity.this, Profile.class);
                startActivity(intent1);
            }
        });


        btn_topUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActicity.this, Top_up.class);
                startActivity(intent);
                finish();
            }
        });

            reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Biodata biodata = snapshot.getValue(Biodata.class);
                if (biodata != null){
                    String Dompet = biodata.topUp;
                    txt_saldo.setText(Dompet);
                }else{
                    Toast.makeText(DashboardActicity.this, "Kesalahan Sistem!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
