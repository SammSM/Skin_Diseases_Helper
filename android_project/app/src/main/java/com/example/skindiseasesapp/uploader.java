package com.example.skindiseasesapp;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class uploader extends AppCompatActivity {

    private Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private static final int PERMISSION_REQUEST = 200;

    TextView usernameText, uploadAlert;
    ImageView logoutButton, mainImage;
    Button buttonScan, buttonUpload;
    FrameLayout imageContainer;
    View scanLine;

    EditText addSymptoms;
    ImageView plusButton, historyButton;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_uploader);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usernameText = findViewById(R.id.username);
        logoutButton = findViewById(R.id.logout);
        mainImage = findViewById(R.id.main_image);
        buttonScan = findViewById(R.id.button_scan);
        buttonUpload = findViewById(R.id.button_upload);
        uploadAlert = findViewById(R.id.upload_alert);
        imageContainer = findViewById(R.id.image_container);
        scanLine = findViewById(R.id.scan_line);

        addSymptoms = findViewById(R.id.add_symptoms);
        plusButton = findViewById(R.id.plus);
        historyButton = findViewById(R.id.history);
        dbHelper = new DBHelper(this);


        String username = getIntent().getStringExtra("username");
        if (username != null) {
            usernameText.setText(username);
        }

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(uploader.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        loadAndCropImage(imageUri);
                    }
                }
        );

        buttonUpload.setOnClickListener(v -> {
            openGallery();
            uploadAlert.setText("");
        });

        buttonScan.setOnClickListener(v -> {
            if (imageUri != null) {
                startScanAnimation(() -> uploadImage(imageUri)); // ✅ запуск upload после 5 секунд
            } else {
//                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
                uploadAlert.setText("Please select an image!");
            }
        });

        plusButton.setOnClickListener(v -> {
            String prediction = uploadAlert.getText().toString();
            String symptoms = addSymptoms.getText().toString();
            String date = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm",
                    java.util.Locale.getDefault()).format(new java.util.Date());

            if (!prediction.isEmpty()) {
                boolean inserted = dbHelper.insertHistory(prediction, symptoms, date);
                if (inserted) {
                    Toast.makeText(this, "Saved to history", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No prediction to save", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void openGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_MEDIA_IMAGES}, PERMISSION_REQUEST);
            } else {
                launchGallery();
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST);
            } else {
                launchGallery();
            }
        }
    }

    private void launchGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                launchGallery();
            } else {
                Toast.makeText(this, "Permission required to select image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadAndCropImage(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newSize = Math.min(width, height);
            int x = (width - newSize) / 2;
            int y = (height - newSize) / 2;
            Bitmap cropped = Bitmap.createBitmap(bitmap, x, y, newSize, newSize);

            float scale = getResources().getDisplayMetrics().density;
            int targetSizePx = (int)(214 * scale);  // 214dp -> pixels

            Bitmap scaled = Bitmap.createScaledBitmap(cropped, targetSizePx, targetSizePx, true);
            mainImage.setImageBitmap(scaled);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void startScanAnimation(Runnable onAnimationEnd) {
        scanLine.setVisibility(View.VISIBLE);

        ObjectAnimator animator = ObjectAnimator.ofFloat(scanLine, "translationY", 0f, imageContainer.getHeight());
        animator.setDuration(3000); // 5 секунд
        animator.setRepeatCount(0); // один раз
        animator.start();

        animator.addListener(new android.animation.Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(android.animation.Animator animator) { }

            @Override
            public void onAnimationEnd(android.animation.Animator animator) {
                scanLine.setVisibility(View.GONE);
                onAnimationEnd.run(); // вызываем результат после анимации
            }

            @Override
            public void onAnimationCancel(android.animation.Animator animator) { }

            @Override
            public void onAnimationRepeat(android.animation.Animator animator) { }
        });
    }


    private void stopScanAnimation() {
        scanLine.setVisibility(View.GONE);
    }

    private void uploadImage(Uri imageUri) {
        new Thread(() -> {
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[16384];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                byte[] imageBytes = buffer.toByteArray();
                inputStream.close();

                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", "image.jpg",
                                RequestBody.create(imageBytes, MediaType.parse("image/jpeg")))
                        .build();

                Request request = new Request.Builder()
                        .url("http://10.0.2.2:8000/predict")
                        .post(requestBody)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.body() != null) {
                    String result = response.body().string();
                    runOnUiThread(() -> {
                        stopScanAnimation();
                        uploadAlert.setText(result);
                    });
                } else {
                    runOnUiThread(() -> {
                        stopScanAnimation();
                        Toast.makeText(this, "No response from server", Toast.LENGTH_SHORT).show();
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    stopScanAnimation();
                    Toast.makeText(this, "Error uploading image", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
