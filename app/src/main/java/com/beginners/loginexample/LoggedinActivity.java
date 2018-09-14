package com.beginners.loginexample;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoggedinActivity extends AppCompatActivity {


    private Button update;
    private Button delete;

    private EditText editTextUpdateFullName;
    private EditText editTextUpdateEmail;
    private TextView textViewUsername;
    private ProgressBar progressBarUpdate;

    private String fullName;
    private String email;
    private String user_id;
    private String user_name;
    private String password;
    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggedin);

        update = (Button) findViewById(R.id.buttonUpdate);
        delete = (Button) findViewById(R.id.buttonDelete);

        editTextUpdateFullName = (EditText) findViewById(R.id.editTextUpdateFullName);
        editTextUpdateEmail = (EditText) findViewById(R.id.editTextUpdateEmail);
        textViewUsername = findViewById(R.id.textViewShowUsername);
        progressBarUpdate = findViewById(R.id.progressBarUpdate);



        Intent intent = getIntent();
        user_id = intent.getStringExtra(MainActivity.USER_ID);
        user_name = intent.getStringExtra(MainActivity.USER_NAME);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        textViewUsername.setText("Welcome "+user_name+". Here you can edit your email and full name.");

        databaseUsers.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fullName = dataSnapshot.getValue(User.class).getFullName();
                email = dataSnapshot.getValue(User.class).getEmail();
                editTextUpdateFullName.setText(fullName);
                editTextUpdateEmail.setText(email);
                password = dataSnapshot.getValue(User.class).getPassword();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(LoggedinActivity.this, "Error ", Toast.LENGTH_LONG).show();
            }
        });

        //implement update user, check if fields are empty
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!Patterns.EMAIL_ADDRESS.matcher(editTextUpdateEmail.getText().toString()).matches()){
                    editTextUpdateEmail.setError("Enter a valid email address!");
                    editTextUpdateEmail.requestFocus();
                    return;
                }
                if(editTextUpdateFullName.getText().toString().isEmpty()){
                    editTextUpdateFullName.setError("Full name required.");
                    editTextUpdateFullName.requestFocus();
                    return;
                }

                databaseUsers = FirebaseDatabase.getInstance().getReference("users").child(user_id);

                email = editTextUpdateEmail.getText().toString();

                fullName = editTextUpdateFullName.getText().toString();

                User user = new User(user_id, user_name, email, password, fullName);

                databaseUsers.setValue(user);

                Runnable runnable3secs = new Runnable() {
                    @Override
                    public void run() {

                        Toast.makeText(LoggedinActivity.this, "Successfully updated info.", Toast.LENGTH_LONG).show();
                        progressBarUpdate.setVisibility(View.GONE);
                        finish();

                    }
                };

                progressBarUpdate.setVisibility(View.VISIBLE);

                Handler myHandler = new Handler();
                myHandler.postDelayed(runnable3secs,2000);

            }
        });

    }

}
