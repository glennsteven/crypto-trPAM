package com.example.tugasrancang;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userId;
    private Button btn_logout;
    public TextView txt_fullname2;
    public TextView txt_email2;
    public String TAG;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

       user = FirebaseAuth.getInstance().getCurrentUser();
       reference = FirebaseDatabase.getInstance().getReference("Client");
       userId = user.getUid();

        Log.e(TAG, "onCreate: "+ userId );
       txt_fullname2 = findViewById(R.id.txt_fullname2);
       txt_email2 = findViewById(R.id.txt_email2);

       btn_logout = findViewById(R.id.btn_logout);

       btn_logout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FirebaseAuth.getInstance().signOut();
               Intent intent = new Intent(Profile.this, Profile.class);
               startActivity(intent);
               finish();
           }
       });

       reference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                Biodata biodataprofile = snapshot.getValue(Biodata.class);
                if (biodataprofile != null){
                    String fullnamee = biodataprofile.fullname;
                    String emaill = biodataprofile.email;

                    txt_fullname2.setText(fullnamee);
                    txt_email2.setText(emaill);
                }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
               Toast.makeText(Profile.this, "Ada yang Salah!", Toast.LENGTH_SHORT).show();
           }
       });
    }
}
