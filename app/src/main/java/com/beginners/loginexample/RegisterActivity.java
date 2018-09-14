package com.beginners.loginexample;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameRegister;
    private EditText email;
    private EditText passwordRegister;
    private EditText fullName;
    Button registerButton;
    ProgressBar progressBar;
    DatabaseReference databaseUsers;

//    byte[] salt = new byte[0];
//    byte[] saltSave = new byte[0];

    //for md5
    //String AES = "AES";
    //String key = "base";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        usernameRegister = (EditText) findViewById(R.id.editTextRegisterUsername);

        email = (EditText) findViewById(R.id.editTextEmail);

        passwordRegister = (EditText) findViewById(R.id.editTextRegisterPassword);

        fullName = (EditText) findViewById(R.id.editTextFullName);

        registerButton = (Button) findViewById(R.id.buttonRegisterNewUser);

        progressBar = (ProgressBar) findViewById(R.id.progressBarRegister);
        progressBar.setVisibility(View.GONE);

        //if everything is correct close register activity and return to log in activity
        //register user to database
        registerButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.buttonRegisterNewUser:
                        registerUser();
                        break;
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void registerUser() {

        final String username = usernameRegister.getText().toString().trim();
        final String emailR = email.getText().toString().trim();
        String password = passwordRegister.getText().toString().trim();
        final String name = fullName.getText().toString().trim();

        if(username.isEmpty()){
            usernameRegister.setError("Name required!");
            usernameRegister.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(emailR).matches()){
            email.setError("Enter a valid email address!");
            email.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordRegister.setError("Enter password.");
            passwordRegister.requestFocus();
            return;
        }
        if(password.length() < 6){
            passwordRegister.setError("Enter password that is longer than 6 characters.");
            passwordRegister.requestFocus();
            return;
        }
        if(name.isEmpty()){
            fullName.setError("Enter your full name.");
            fullName.requestFocus();
            return;
        }
        Runnable runnable3secs = new Runnable() {
            @Override
            public void run() {

                Toast.makeText(RegisterActivity.this, "Registration successful!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                finish();

            }
        };
        progressBar.setVisibility(View.VISIBLE);
        //change in object to encryptedPassword later
        String passwordToHash = password;

//        try {
//            salt = getSalt();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }

        String securePassword = sha256(passwordToHash);

        String id = databaseUsers.push().getKey(); //unique ID

        User user = new User(id, username, emailR, securePassword, name);

        databaseUsers.child(id).setValue(user);
        Handler myHandler = new Handler();
        myHandler.postDelayed(runnable3secs,3000);

    } //end register

//    private static String get_SHA_1_SecurePassword(String passwordToHash, byte[] salt)
//    {
//        String generatedPassword = null;
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            md.update(salt);
//            byte[] bytes = md.digest(passwordToHash.getBytes());
//            StringBuilder sb = new StringBuilder();
//            for(int i=0; i< bytes.length ;i++)
//            {
//                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
//            }
//            generatedPassword = sb.toString();
//        }
//        catch (NoSuchAlgorithmException e)
//        {
//            e.printStackTrace();
//        }
//        return generatedPassword;
//    }

    //Add salt
//    private static byte[] getSalt() throws NoSuchAlgorithmException
//    {
//        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//        byte[] salt = new byte[16];
//        sr.nextBytes(salt);
//        return salt;
//    }
    //md5 hash
//    private String encrypt(String data, String password) throws Exception{
//        SecretKeySpec key = generateKey(password);
//        Cipher c = Cipher.getInstance(AES);
//        c.init(Cipher.ENCRYPT_MODE, key);
//        byte[] encVal = c.doFinal(data.getBytes());
//        String encryptedValue = Base64.encodeToString(encVal, Base64.DEFAULT);
//        return encryptedValue;
//    }
//
//    private SecretKeySpec generateKey(String password) throws Exception {
//        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] bytes = password.getBytes("UTF-8");
//        digest.update(bytes, 0, bytes.length);
//        byte[] key = digest.digest();
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
//        return secretKeySpec;
//    }
//
//    private String decrypt(String outputString, String password) throws Exception{
//        SecretKeySpec key = generateKey(password);
//        Cipher c = Cipher.getInstance(AES);
//        c.init(Cipher.DECRYPT_MODE, key);
//        byte[] decodedValue = Base64.decode(outputString, Base64.DEFAULT);
//        byte[] decValue = c.doFinal(decodedValue);
//        String decryptedValue = new String(decValue);
//        return decryptedValue;
//    }

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
