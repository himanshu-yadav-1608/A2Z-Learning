package com.himanshu.a2zlearning.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.himanshu.a2zlearning.utils.InternetCheck;
import com.himanshu.a2zlearning.ui.activities.MainActivity;
import com.himanshu.a2zlearning.R;
import com.himanshu.a2zlearning.utils.Res;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    EditText email,password;
    TextView txtforgot,txtsignup;
    Button btnlogin;
    ProgressBar progressBar;
    SharedPreferences sp;
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        String DATA = Res.sp1;
        sp = getSharedPreferences(DATA, MODE_PRIVATE);
        email = findViewById(R.id.etemail);
        password = findViewById(R.id.etpassword);
        btnlogin = findViewById(R.id.btnlogin);
        txtforgot = findViewById(R.id.txtforgot);
        txtsignup = findViewById(R.id.txtsignup);
        progressBar = findViewById(R.id.progressBar);

        btnlogin.setOnClickListener(v -> userLogin());

        txtforgot.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgetActivity.class)));

        txtsignup.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,SignupActivity.class)));

    }

    private void userLogin() {
        btnlogin.setClickable(false);
        new InternetCheck(internet -> {
            if(internet) {
                final String semail = email.getText().toString().trim();
                final String spass = password.getText().toString().trim();
                if(validate(semail,spass)) {
                    progressBar.setVisibility(View.VISIBLE);
                    checkLogin(semail,spass);
                }
            } else {
                Toast.makeText(LoginActivity.this,"Allow Internet",Toast.LENGTH_LONG).show();
                btnlogin.setClickable(true);
            }
        });

    }

    private void checkLogin(String semail,String spass) {
        auth.signInWithEmailAndPassword(semail,spass).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                progressBar.setVisibility(View.INVISIBLE);
                String uid = auth.getUid();
                sp.edit().putString("UserID",uid).apply();
                assert uid != null;
                dbRef.child("Users").child(uid).runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        for(final MutableData childs : mutableData.getChildren()) {
                            if(Objects.equals(childs.getKey(), "UserName")) {
                                sp.edit().putString("UserName",childs.getValue(String.class)).apply();
                            } else if(Objects.equals(childs.getKey(), "UserEmail")) {
                                sp.edit().putString("UserEmail",childs.getValue(String.class)).apply();
                            } else if(Objects.equals(childs.getKey(), "UserPhone")) {
                                sp.edit().putString("UserPhone",childs.getValue(String.class)).apply();
                            }
                            sp.edit().putString("UserPassword",spass).apply();
                        }
                        return Transaction.success(mutableData);
                    }
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {btnlogin.setClickable(true);}
                });
                sp.edit().putBoolean("isLogged",true).apply();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.INVISIBLE);
            btnlogin.setClickable(true);
            if(e instanceof FirebaseAuthInvalidUserException) {
                Toast.makeText(LoginActivity.this,"Email not registered!\nSign Up Now :)",Toast.LENGTH_LONG).show();
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            } else if(e instanceof FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(LoginActivity.this,"Invalid Email or Password",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(LoginActivity.this,"Error : "+e,Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validate(String semail, String spass) {
        if(!isvalidEmail(semail)) {
            email.setError("Invalid Email Address");
            email.requestFocus();
            btnlogin.setClickable(true);
            return false;
        }
        if(!isValidPassword(spass)) {
            password.setError("Minimum length 6,minimum 1 uppercase,1 lowercase,1 symbol,1 digit");
            password.requestFocus();
            btnlogin.setClickable(true);
            return false;
        }
        return true;
    }

    private boolean isValidPassword(@NonNull String password) {
        Pattern pattern;
        Matcher matcher;
        String PASSWORD_PATTERN = getString(R.string.supp);
        pattern = compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean isvalidEmail(@NonNull String email) {
        if(TextUtils.isEmpty(email)) {
            return false;
        }
        String emailPatterning = getString(R.string.suep);
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return false;
        }
        if(!email.matches(emailPatterning)) {
            return false;
        }
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        String emailPatternnew = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
        String domain = email.substring(email.indexOf('@'));
        String last   = domain.substring(domain.indexOf('.'));
        if (!email.matches(emailPattern) || (last.length() ==3 || last.length() == 4)) {
            return true;
        }
        else return email.matches(emailPatternnew) && last.length() == 6 && email.charAt(email.length() - 3) == '.';
    }
}
