package com.example.skindiseasesapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button login_button, login_button_register;
    DBHelper dbHelper;
    ImageView login_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // EdgeToEdge padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.login_enter_username);
        etPassword = findViewById(R.id.login_enter_password);
        login_button = findViewById(R.id.login_button);
        login_button_register = findViewById(R.id.login_button_register);
        login_back = findViewById(R.id.login_back);

        dbHelper = new DBHelper(this);

        // Вход
        login_button.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean checkUserPass = dbHelper.checkUsernamePassword(username, password);
                if (checkUserPass) {
                    Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, uploader.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Login.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Переход на регистрацию
        login_button_register.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
            finish();
        });

        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
