package com.rizki.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rizki.blogapp.R;

public class RegisterActivity extends AppCompatActivity {

    ImageView imgUserPhoto;
    static int preqCode = 1;
    static int ReqCode = 1;
    Uri pickedImage;
    private EditText userName, userMail, userPassword, userRePassword;
    private ProgressBar regProses;
    private Button regBtn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgUserPhoto    = findViewById(R.id.regUserPhoto);
        userName        = findViewById(R.id.regName);
        userMail        = findViewById(R.id.regMail);
        userPassword    = findViewById(R.id.regPassword);
        userRePassword  = findViewById(R.id.regPassword2);
        regProses       = findViewById(R.id.regprogressBar);
        regBtn          = findViewById(R.id.regButton);
        mAuth           = FirebaseAuth.getInstance();

        regProses.setVisibility(View.INVISIBLE);

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regBtn.setVisibility(View.INVISIBLE);
                regProses.setVisibility(View.VISIBLE);

                final String nama = userName.getText().toString();
                final String email = userMail.getText().toString();
                final String pass = userPassword.getText().toString();
                final String pass2 = userRePassword.getText().toString();

                if ( nama.isEmpty() || email.isEmpty() || pass.isEmpty() || pass2.isEmpty() || !pass2.equals(pass) ){
                    //TODO: Menampilkan pesan error untuk melengkapi data
                    showMessage("Tolong isi semua data");
                    regBtn.setVisibility(View.VISIBLE);
                    regProses.setVisibility(View.INVISIBLE);
                }
                else{
                    //TODO: Semua data oke, lanjut proses registrasi
                    CreateUserAccount(nama, email, pass);
                }
            }
        });

        imgUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= 22){
                    CheckAndRequestPermission();
                }
                else{
                    openGallery();
                }
            }
        });
    }

    private void CreateUserAccount(final String nama, String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            showMessage("Akun Berhasil Dibuat :)");

                            //TODO: Setelah akun dibuat, update profil picture dan info lainnya
                            updateInformasi(nama, pickedImage, mAuth.getCurrentUser());
                        }
                        else{
                            showMessage("Yhaa akun nya gagal dibuat karena " + task.getException().getMessage());
                            regProses.setVisibility(View.INVISIBLE);
                            regBtn.setVisibility(View.VISIBLE);
                        }
                    }
                });

    }

    private void updateInformasi(final String nama, Uri pickedImage, final FirebaseUser currentUser) {
        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("users-photo");
        final StorageReference imageFilePath = mStorage.child(pickedImage.getLastPathSegment());
        imageFilePath.putFile(pickedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        UserProfileChangeRequest profilUpdate = new UserProfileChangeRequest.Builder()
                                .setDisplayName(nama)
                                .setPhotoUri(uri)
                                .build();

                        currentUser.updateProfile(profilUpdate)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            showMessage("Registrasi Berhasil");
                                            updateUI();
                                        }

                                    }
                                });
                    }
                });
            }
        });
    }

    private void updateUI() {
        //TODO: Intent ke Home Activity
        Intent i = new Intent(RegisterActivity.this, CobaActivity.class);
        startActivity(i);
        finish();
    }

    private void showMessage(String message) {
        //TODO: Menampilkan Toast Message
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void openGallery() {
        //TODO: Buka galeri untuk ambil foto sesuai yang diinginkan user
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, ReqCode);

    }

    private void CheckAndRequestPermission() {
        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(RegisterActivity.this, "Tolong berikan izin untuk akses galeri", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, preqCode);
            }
        }
        else{
            openGallery();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == ReqCode && data != null){
            //TODO: Menyimpan gambar yang sudah dipilih user dengan Uri
            pickedImage = data.getData();
            imgUserPhoto.setImageURI(pickedImage);
        }
    }
}
