package com.example.my;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.my.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        sharedPreferences = getSharedPreferences("session",MODE_PRIVATE);

        if(sharedPreferences.getBoolean("session",false) &&
                !sharedPreferences.getString("email","").isEmpty()){
            startActivity(new Intent(LoginActivity.this,NavigationDrawerActivity.class));
            finish();
        }

            mBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyOrNot();
            }
        });

        mBinding.cbRememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mBinding.cbRememberMe.isChecked()){

                    sharedPreferences.edit().putString("email",mBinding.etEmail.getText().toString()).apply();
                    sharedPreferences.edit().putBoolean("session",true).apply();

                }
            }
        });
    }

    private void emptyOrNot() {
        String email,password;
        email = mBinding.etEmail.getText().toString();
        password = mBinding.etPassword.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password) || password.length() <8){
            Toast.makeText(this, "You must have 8 characters in your password", Toast.LENGTH_LONG).show();
        }
        else{
            checkCredentials(email);
        }
    }

    private void checkCredentials(String email) {

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


            if(!email.matches(emailPattern)){
                Toast.makeText(this, "Invalid email address!!!", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, "Successfully Login", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,NavigationDrawerActivity.class));
                finish();
        }
}
}