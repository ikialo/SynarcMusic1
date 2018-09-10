package isaacsilas05.gmail.com.synarcmusic1;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 1;
    private static final String TAG = "CHILD";
    private DatabaseReference mDatabase;
    private StorageReference mStoraage;
    private FirebaseAuth auth;
    private AdapterAudioDownload mAdapter;
    private RecyclerView mRecyclerView;
    private TextView username;
    int extraSend;

    private List<Upload> mDownloads;
    private List<String> child;
    final static private String ArtistUpDownToken = "artist token";

    private SharedPreferences sp;
    private static final String LOGIN = "downloadsignedin";
    private static final String EMAIL= "email";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        mRecyclerView = findViewById(R.id.recyclerViewUpload);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        username = findViewById(R.id.usernamer);

        Intent intent = getIntent();
        extraSend = intent.getIntExtra(ArtistUpDownToken,0);
        mDownloads = new ArrayList<>();
        mAdapter = new AdapterAudioDownload(DownloadActivity.this, mDownloads);

        mRecyclerView.setAdapter(mAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("Uploads");
        auth = FirebaseAuth.getInstance();

        username.setText(auth.getCurrentUser().getDisplayName());

        sp = getSharedPreferences(LOGIN, MODE_PRIVATE);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You have already granted this permission!",
                    Toast.LENGTH_SHORT).show();
        } else {
            requestStoragePermission();
        }


        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDownloads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    String UID = postSnapshot.getKey();

                    mDatabase.child(UID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                Upload upload;
                                String name = postSnapshot.child("name").getValue().toString();
                                String url = postSnapshot.child("imageUrl").getValue().toString();
                                String username = postSnapshot.child("username").getValue().toString();
                                upload = new Upload(name,url,username);

                                upload.setKey(postSnapshot.getKey());
                                mDownloads.add(upload);
                            }
                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if ( extraSend == 1) {
            Intent i = new Intent(DownloadActivity.this, ArtistUploadDownloadActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }else {
            Intent intent = new Intent(DownloadActivity.this,FrontPageActivity.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.downloadmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.search_download:
                Toast.makeText(DownloadActivity.this,
                        " serach button pressed", Toast.LENGTH_SHORT).show();
            case R.id.logout_download:
                AuthUI.getInstance()
                        .signOut(DownloadActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...

                                Toast.makeText(DownloadActivity.this, "user logged OUT successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                startActivity(new Intent(DownloadActivity.this, FrontPageActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed to allow you to save songs to your phone")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(DownloadActivity.this,
                                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


