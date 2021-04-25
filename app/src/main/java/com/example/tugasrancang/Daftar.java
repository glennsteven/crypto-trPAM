package com.example.tugasrancang;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class Daftar extends Activity {
    private EditText nameEt,emailEt,passwordEt1,passwordEt2;
    private Button SignUpButton;
    private TextView txt_signIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);

        firebaseAuth = FirebaseAuth.getInstance();
        nameEt = findViewById(R.id.fullname);
        emailEt = findViewById(R.id.email);
        passwordEt1 = findViewById(R.id.password1);
        passwordEt2 = findViewById(R.id.password2);
        SignUpButton = findViewById(R.id.register);
        progressDialog = new ProgressDialog(this);
        txt_signIn = findViewById(R.id.txt_signIn);
        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
        txt_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Daftar.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private void Register(){
        final String fullname = nameEt.getText().toString();
        final String email = emailEt.getText().toString();
        String password1 = passwordEt1.getText().toString();
        String password2 = passwordEt2.getText().toString();
        if (TextUtils.isEmpty(fullname)){
            nameEt.setError("Masukkan Nama Lengkap");
        }else if (TextUtils.isEmpty(email)){
            emailEt.setError("Masukkan Email kamu");
            return;
        }else if (TextUtils.isEmpty(password1)){
            passwordEt1.setError("Masukkan sandi kamu");
            return;
        }else if (TextUtils.isEmpty(password2)){
            passwordEt2.setError("Masukkan sandi konfirmasi");
            return;
        }else if (!password1.equals(password2)){
            passwordEt2.setError("Kata sandi tidak sama");
            return;
        }else if (password1.length()<4){
            passwordEt1.setError("Kata sandi haris lebih dari 4 huruf");
            return;
        }else if (!isValidEmail(email)){
            emailEt.setError("Email tidak valid");
            return;
        }
        progressDialog.setMessage("Mohon ditunggu...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
        firebaseAuth.createUserWithEmailAndPassword(email,password1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Biodata biodata = new Biodata(fullname,email);
                    FirebaseDatabase.getInstance().getReference("Client")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(biodata).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(Daftar.this, "Pendaftaran akun berhasil!",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Daftar.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(Daftar.this, "Pendaftaran akun gagal!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(Daftar.this, "Pendaftaran akun gagal!",Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }
    private Boolean isValidEmail(CharSequence target){
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
