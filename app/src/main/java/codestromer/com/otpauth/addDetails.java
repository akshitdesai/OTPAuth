package codestromer.com.otpauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class addDetails extends AppCompatActivity {
    EditText firstName, lastName, email;
    Button saveBtn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_details);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.emailAddress);
        saveBtn = findViewById(R.id.saveBtn);

        firebaseAuth = firebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        userid = firebaseAuth.getCurrentUser().getUid();

        final DocumentReference docRef = fstore.collection("users").document(userid);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstName.getText().toString().isEmpty()){
                    firstName.setError("Required");
                    return;
                }
                if(lastName.getText().toString().isEmpty()){
                    lastName.setError("Required");
                    return;
                }
                if(email.getText().toString().isEmpty()){
                    email.setError("Required");
                    return;
                }
                String first = firstName.getText().toString();
                String last = lastName.getText().toString();
                String userEmail = email.getText().toString();

                Map<String,Object> user = new HashMap<>();

                user.put("firstName",first);
                user.put("lastName",last);
                user.put("emailAddress",userEmail);

                docRef.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(addDetails.this,"Data can't be inserted",Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }
}
