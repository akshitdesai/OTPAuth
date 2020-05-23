package codestromer.com.otpauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    EditText phone,codeEnter;
    Button nextBtn;
    ProgressBar progressBar;
    TextView state;
    CountryCodePicker countryCodePicker;
    PhoneAuthProvider.ForceResendingToken token;
    String verificationId;
    Boolean verificationOnProgress = false;

    /*
    PhoneAuthCredential credential;
    TextView resend;
    String phoneNumber = "+917451221244";
    String otpCode = "123456";
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phone = findViewById(R.id.phone);
        codeEnter = findViewById(R.id.codeEnter);
        countryCodePicker = findViewById(R.id.ccp);
        nextBtn = findViewById(R.id.nextBtn);
        progressBar = findViewById(R.id.progressBar);
        state = findViewById(R.id.state);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        /*
        resend = findViewById(R.id.resendOtpBtn);
        */
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {

                if(!verificationOnProgress){
                    if(!phone.getText().toString().isEmpty() && phone.getText().toString().length()==10){
                        String phoneNum = "+"+countryCodePicker.getSelectedCountryCode()+phone.getText().toString();
                        Log.d("TAG", "onClick: Phone NO ->"+phoneNum);
                        progressBar.setVisibility(View.VISIBLE);
                        state.setVisibility(View.VISIBLE);
                        state.setText("Sending OTP..");
                        requestOTP(phoneNum);
                    }
                    else {
                        phone.setError("Invalid Phone Number");
                    }
                }else {
                    String userOTP = codeEnter.getText().toString();
                    if(!userOTP.isEmpty() && userOTP.length()==6){
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,userOTP);
                        progressBar.setVisibility(View.VISIBLE);
                        verifyAuth(credential);
                    } else {
                        codeEnter.setError("Invalid OTP");
                    }
                }
            }
        });
    }


    @Override
    protected void onStart(){
        super.onStart();

        if(fAuth.getCurrentUser() != null){
            progressBar.setVisibility(View.VISIBLE);
            state.setText("Checking...");
            state.setVisibility(View.VISIBLE);
            checkUserProfile();
        }
    }

    private void verifyAuth(PhoneAuthCredential credential) {
        fAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    checkUserProfile();
                }else {
                    Toast.makeText(Register.this,"Authentication failed",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    private void checkUserProfile() {
        DocumentReference docRef = fStore.collection("users").document(fAuth.getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    startActivity(new Intent(getApplicationContext(),addDetails.class));
                    finish();
                }
            }
        });
    }

    private void requestOTP(String phoneNum) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L,TimeUnit.SECONDS,this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                progressBar.setVisibility(View.GONE);
                state.setVisibility(View.GONE);
                codeEnter.setVisibility(View.VISIBLE);
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;
                nextBtn.setText("Verify");
                verificationOnProgress = true;
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Toast.makeText(Register.this,"OTP-Expired, Re-request the otp",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                verifyAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Register.this,"Can't Create Account"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
