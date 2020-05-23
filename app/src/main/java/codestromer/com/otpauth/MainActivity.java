package codestromer.com.otpauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    TextView pName, pEmail, pPhone;
    Toolbar tbar;
    RecyclerView appList;
    FirestoreRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pName = findViewById(R.id.profileFullName);
        pEmail = findViewById(R.id.profileEmail);
        pPhone = findViewById(R.id.profilePhone);

        appList = findViewById(R.id.applist);
        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        tbar = findViewById(R.id.toolbar);
        setSupportActionBar(tbar);

        getSupportActionBar().setTitle("Profile");

        //Query
        Query query = fstore.collection("apps");

        //RecyclerOptions
        FirestoreRecyclerOptions<AppModel> options = new FirestoreRecyclerOptions.Builder<AppModel>()
                .setQuery(query, AppModel.class)
                .build();
        adapter = new FirestoreRecyclerAdapter<AppModel, AppViewHolder>(options) {
            @NonNull
            @Override
            public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_single,parent,false);
                return new AppViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AppViewHolder holder, int position, @NonNull AppModel model) {
                holder.app_name.setText(model.getAppName());
                holder.app_desc.setText(model.getDesc());
            }
        };

        //View Holder
        appList.setHasFixedSize(true);
        appList.setLayoutManager(new LinearLayoutManager(this));
        appList.setAdapter(adapter);

        DocumentReference docRef = fstore.collection("users").document(fAuth.getCurrentUser().getUid());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String fullName = documentSnapshot.getString("firstName")+" "+documentSnapshot.getString("lastName");
                    pName.setText(fullName);
                    pEmail.setText(documentSnapshot.getString("emailAddress"));
                    pPhone.setText(fAuth.getCurrentUser().getPhoneNumber());
                }
            }
        });
    }


    private class AppViewHolder extends RecyclerView.ViewHolder{
        private TextView app_name;
        private TextView app_desc;
        public AppViewHolder(@NonNull View itemView) {
            super(itemView);

            app_name = itemView.findViewById(R.id.appname);
            app_desc = itemView.findViewById(R.id.appdesc);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.logout){
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),Register.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop(){
        super.onStop();
        adapter.stopListening();
    }
    @Override
    protected void onStart(){
        super.onStart();
        adapter.startListening();
    }
}
