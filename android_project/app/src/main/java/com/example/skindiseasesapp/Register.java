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

public class Register extends AppCompatActivity {

    EditText etUsername, etPassword;
    Button register_button, register_button_login;
    DBHelper dbHelper;
    ImageView register_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // обработка системных отступов (EdgeToEdge)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // связываем элементы интерфейса
        etUsername = findViewById(R.id.register_enter_username);
        etPassword = findViewById(R.id.register_enter_password);
        register_button = findViewById(R.id.register_button);
        register_button_login = findViewById(R.id.register_button_login);
        register_back = findViewById(R.id.register_back);

        // создаем объект базы данных
        dbHelper = new DBHelper(this);

        // обработка регистрации
        register_button.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(Register.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                boolean inserted = dbHelper.insertUser(username, password);
                if (inserted) {
                    Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                    // переход на экран логина
                    Intent intent = new Intent(Register.this, Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(Register.this, "User already exists", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // кнопка "Уже есть аккаунт? Войти"
        register_button_login.setOnClickListener(v -> {
            Intent intent = new Intent(Register.this, Login.class);
            startActivity(intent);
            finish();
        });

        register_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
