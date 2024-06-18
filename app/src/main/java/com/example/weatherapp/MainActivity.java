package com.example.weatherapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton;

    private HashMap<String, String> userDatabase = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#0e0506"));
        }

        // Initialize views
        usernameEditText = findViewById(R.id.etInlognaam);
        passwordEditText = findViewById(R.id.etWachtwoord);
        loginButton = findViewById(R.id.inloggen);
        registerButton = findViewById(R.id.registreren);

        // Set click listeners
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void registerUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Vul alle velden in", Toast.LENGTH_SHORT).show();
        } else if (userDatabase.containsKey(username)) {
            Toast.makeText(MainActivity.this, "Gebruikersnaam is al geregistreerd", Toast.LENGTH_SHORT).show();
        } else {
            userDatabase.put(username, password);
            Toast.makeText(MainActivity.this, "Registratie succesvol", Toast.LENGTH_SHORT).show();
            clearFields();
        }
    }

    private void loginUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Vul alle velden in", Toast.LENGTH_SHORT).show();
        } else if (!userDatabase.containsKey(username) || !userDatabase.get(username).equals(password)) {
            Toast.makeText(MainActivity.this, "Onjuiste gebruikersnaam of wachtwoord", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Login succesvol", Toast.LENGTH_SHORT).show();
            clearFields();
            openMainActivity2(); // Open MainActivity2 after successful login
        }
    }

    private void clearFields() {
        usernameEditText.setText("");
        passwordEditText.setText("");
    }

    private void openMainActivity2() {
        Intent intent = new Intent(MainActivity.this, MainActivity2.class);
        startActivity(intent);
        finish();
    }
}
