package com.rizki.blogapp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rizki.blogapp.Fragments.ProfileFragment;
import com.rizki.blogapp.Fragments.RumahFragment;
import com.rizki.blogapp.Fragments.SettingsFragment;
import com.rizki.blogapp.Models.Post;
import com.rizki.blogapp.R;
import com.rizki.blogapp.ui.home.HomeFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class CobaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private static final int preqCode = 2;
    private static final int ReqCode = 2;

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Dialog popAddPost;
    ImageView popupUserImage, popupPostImage, popupAddBtn;
    TextView popupTitle, popupDescription;
    ProgressBar popupProgressBar;
    private Uri pickedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coba);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        updateNavHeader();

        popUpforPost();

        setupPopupImageClick();

        //TODO: Membuat Rumah Fragment menjadi default ketika dijalankan
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new RumahFragment()).commit();
    }

    private void setupPopupImageClick() {
        popupPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Open galeri untuk pilih gambar
                CheckAndRequestPermission();
            }
        });
    }

    private void CheckAndRequestPermission() {
        if(ContextCompat.checkSelfPermission(CobaActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(CobaActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(CobaActivity.this, "Tolong berikan izin untuk akses galeri", Toast.LENGTH_SHORT).show();
            }
            else{
                ActivityCompat.requestPermissions(CobaActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, preqCode);
            }
        }
        else{
            openGallery();
        }
    }

    private void openGallery() {
        //TODO: Buka galeri untuk ambil foto sesuai yang diinginkan user
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(i, ReqCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK && requestCode == ReqCode && data != null){
            //TODO: Menyimpan gambar yang sudah dipilih user dengan Uri
            pickedImage = data.getData();
            popupPostImage.setImageURI(pickedImage);
        }
    }

    private void popUpforPost() {
        //TODO: Membuat popup
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;

        //TODO: Init Popup
        popupUserImage = popAddPost.findViewById(R.id.popup_iv_user);
        popupPostImage = popAddPost.findViewById(R.id.popup_img);
        popupTitle = popAddPost.findViewById(R.id.popup_title);
        popupDescription = popAddPost.findViewById(R.id.popup_description);
        popupAddBtn = popAddPost.findViewById(R.id.popup_add);
        popupProgressBar = popAddPost.findViewById(R.id.popupProgressBar);

        //TODO: Add Post Click Listener
        Glide.with(CobaActivity.this).load(currentUser.getPhotoUrl()).into(popupUserImage);
        popupAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupAddBtn.setVisibility(View.INVISIBLE);
                popupProgressBar.setVisibility(View.VISIBLE);

                if (!popupTitle.getText().toString().isEmpty() && !popupDescription.toString().isEmpty() && pickedImage != null){
                    //TODO: Semua field sudah terisi, tinggal post ke firebase
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Blog-Images");
                    final StorageReference imageFilaPath = storageReference.child(pickedImage.getLastPathSegment());
                    imageFilaPath.putFile(pickedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilaPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageDownloadLink = uri.toString();

                                    //TODO: Membuat Objek Post baru dari class Post yang ada di Model Path
                                    Post post = new Post(
                                            popupTitle.getText().toString(),
                                            popupDescription.getText().toString(),
                                            imageDownloadLink,
                                            currentUser.getUid(),
                                            currentUser.getPhotoUrl().toString()
                                    );

                                    addPost(post);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //TODO: Handling Error Exception
                                    showMessage(e.getMessage());
                                    popupProgressBar.setVisibility(View.INVISIBLE);
                                    popupAddBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });


                }
                else{
                    showMessage("Masukkan data dengan benar dan pilih gambar yang diinginkan!");
                    popupAddBtn.setVisibility(View.VISIBLE);
                    popupProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    private void addPost(Post post) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Posts").push();

        //TODO: Get post unique ID
        String key = myRef.getKey();
        post.setPostKey(key);

        //TODO: Add Post to Firebase Database
        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessage("Post berhasil dibuat :)");
                popupAddBtn.setVisibility(View.VISIBLE);
                popupProgressBar.setVisibility(View.INVISIBLE);
                popAddPost.dismiss();
            }
        });

    }

    private void showMessage(String message) {
        //TODO: Menampilkan toast message
        Toast.makeText(CobaActivity.this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.coba, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.nav_home){
            getSupportActionBar().setTitle("Halaman");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new RumahFragment()).commit();
        }
        else if(id == R.id.nav_profile){
            getSupportActionBar().setTitle("Profil");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
        }
        else if(id == R.id.nav_settings){
            getSupportActionBar().setTitle("Pengaturan");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
        }
        else if(id == R.id.nav_signout){
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(CobaActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void updateNavHeader(){
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView txtUserName = headerView.findViewById(R.id.nav_user_name);
        TextView txtUserMail = headerView.findViewById(R.id.nav_user_mail);
        ImageView navUserPhoto = headerView.findViewById(R.id.nav_user_photo);

        txtUserMail.setText(currentUser.getEmail());
        txtUserName.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);
    }
}