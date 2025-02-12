package com.example.swipe_n_borrow;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterAdmin extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword, editTextID, editTextFullName, editTextPhoneNumber, editTextAddress, editTextLibrary;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    FirebaseFirestore fstore;
    String adminID;
    boolean acceptedTerms=false;

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent= new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    public void openActivityTermsAdmin(View view) {
        Intent intent = new Intent(this, ActivityTermsAdmin.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);
        mAuth= FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextID = findViewById(R.id.id);
        editTextFullName = findViewById(R.id.fullName);
        editTextPhoneNumber = findViewById(R.id.phoneNumber);
        editTextAddress = findViewById(R.id.address);
        editTextLibrary = findViewById(R.id.library);
        buttonReg =findViewById(R.id.BTN_Register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.LoginNow);
        fstore = FirebaseFirestore.getInstance();

        CheckBox checkBoxTerms = findViewById(R.id.checkBoxTermsAdmin);

        checkBoxTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update the acceptedTerms variable based on the CheckBox state
                acceptedTerms = isChecked;
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password, id, fullName, phoneNumber, address, library;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());
                id = String.valueOf(editTextID.getText());
                fullName = String.valueOf(editTextFullName.getText());
                phoneNumber = String.valueOf(editTextPhoneNumber.getText());
                address = String.valueOf(editTextAddress.getText());
                library = String.valueOf(editTextLibrary.getText());


                if(TextUtils.isEmpty(fullName)){
                    Toast.makeText(RegisterAdmin.this,"Enter Full Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(id)){
                    Toast.makeText(RegisterAdmin.this,"Enter ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isAllDigits(id)){
                    Toast.makeText(RegisterAdmin.this,"ID must be made of digits only", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (id.length()<4){
                    Toast.makeText(RegisterAdmin.this,"ID must be at least a 4 digit number", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(address)){
                    Toast.makeText(RegisterAdmin.this,"Enter Address", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterAdmin.this,"Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!email.contains("@")){
                    Toast.makeText(RegisterAdmin.this,"Email address is badly formatted", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(RegisterAdmin.this,"Enter Phone Number", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!isAllDigits(phoneNumber)||!(phoneNumber.length()==10)){
                    Toast.makeText(RegisterAdmin.this,"Phone Number must be a 10 digit number.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!(phoneNumber.startsWith("05"))){
                    Toast.makeText(RegisterAdmin.this,"Phone Number is not legal, must start with 05.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterAdmin.this,"Enter Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.length()<6){
                    Toast.makeText(RegisterAdmin.this,"Password must be at least 6 characters ", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(library)){
                    Toast.makeText(RegisterAdmin.this,"Enter Library", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!acceptedTerms) {
                    Toast.makeText(RegisterAdmin.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterAdmin.this, "Account created.",
                                            Toast.LENGTH_SHORT).show();
                                    adminID = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fstore.collection("Admins").document(adminID);
                                    Map<String,Object> admin =new HashMap<>();
                                    admin.put("email",email);
                                    admin.put("fullName",fullName);
                                    admin.put("id",id);
                                    admin.put("address",address);
                                    admin.put("phoneNumber",phoneNumber);
                                    admin.put("library",library);
                                    documentReference.set(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG,"onSuccess: user Profile is created for "+adminID);
                                        }
                                    });

                                    // Navigate to AdminHome activity after successful registration
                                    Intent intent2 = new Intent(getApplicationContext(), AdminHome.class);
                                    startActivity(intent2);
                                    finish();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(RegisterAdmin.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


            }
        });

    }
    static boolean isAllDigits(String s) {
        return s.matches("\\d+");
    }
}