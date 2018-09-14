package com.beginners.loginexample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    public static  final String USER_NAME = "username";
    public static  final String USER_ID = "userid";

    Button buttonRegister;
    Button buttonLogin;
    EditText usernameLogin;
    EditText passwordLogin;
    DatabaseReference databaseUsers;

    /// /for md5
    //String AES = "AES";
    //String key = "base";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent myIntent = new Intent(this, SplashActivity.class);
        startActivity(myIntent);

        databaseUsers = FirebaseDatabase.getInstance().getReference("users");

        usernameLogin = (EditText) findViewById(R.id.editTextUsername);

        passwordLogin = (EditText) findViewById(R.id.editTextPassword);

        buttonLogin = (Button) findViewById(R.id.buttonLogin);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToRegisterActivity();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLoginDetails();
                //move to main screen
            }
        });
    }

    private void checkLoginDetails() {
        //if username and password exist in database
        //finish(close) login activity
        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot userSnapshot : dataSnapshot.getChildren()){
                    User user = userSnapshot.getValue(User.class);

                    String u =  usernameLogin.getText().toString();

                    String passwordToHash = passwordLogin.getText().toString();

                    String userrr = user.getUsername().toString();

                    String pw = user.getPassword().toString();

                    //byte[] salt = user.getSa();
                    String securePassword = sha256(passwordToHash);

                    //if found
                    if(u.equals(userrr) && securePassword.equals(pw)){

                        Toast.makeText(MainActivity.this, "Username found: "+userrr, Toast.LENGTH_LONG).show();

                        //moveToLoggedin();

                        Intent intent = new Intent(getApplicationContext(), LoggedinActivity.class);

                        intent.putExtra(USER_ID, user.getUserID());
                        intent.putExtra(USER_NAME, user.getUsername());

                        startActivity(intent);

                        finish(); //close login

                        break;
                    }else{

                        //if not found
                        Toast.makeText(MainActivity.this, "Username or password not found: " + usernameLogin.getText().toString() + " salt: ", Toast.LENGTH_LONG).show();
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void moveToRegisterActivity() {
        Intent myIntent = new Intent(this,RegisterActivity.class);
        startActivity(myIntent);
    }

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

    // md5 hash
//    private String decrypt(String outputString, String password) throws Exception{
//        SecretKeySpec key = generateKey(password);
//        Cipher c = Cipher.getInstance(AES);
//        c.init(Cipher.DECRYPT_MODE, key);
//        byte[] decodedValue = Base64.decode(outputString, Base64.DEFAULT);
//        byte[] decValue = c.doFinal(decodedValue);
//        String decryptedValue = new String(decValue);
//        return decryptedValue;
//    }
//    private SecretKeySpec generateKey(String password) throws Exception {
//        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
//        byte[] bytes = password.getBytes("UTF-8");
//        digest.update(bytes, 0, bytes.length);
//        byte[] key = digest.digest();
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
//        return secretKeySpec;
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


