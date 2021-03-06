package isaacsilas05.gmail.com.synarcmusic1;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity  implements AudioAdapterProducer.OnItemClickListener{

    private static final int PICK_IMAGE_REQUEST = 1;

    Button chooseFile;
    Button uploadSong;
    EditText songName;
    ProgressBar progressBar;

    private ProgressDialog PD;
    private Uri mAudioUri;
    private FirebaseAuth auth;
    private RecyclerView mRecyclerView;
    private AudioAdapterProducer mAdapter;
    private ProgressBar mProgressCircle;
    private Button logout;
    private SharedPreferences sp;
    private String UId;


    private FirebaseStorage mStorage;
    private DatabaseReference mDatabase;
    private ValueEventListener mDBL;
    private List<Upload> mUploads;

    private static final String LOGIN = "login";
    private static final String LOGGED = "logged";
    private static final String EMAIL = "email";

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        sp = getSharedPreferences(LOGIN, MODE_PRIVATE);

        //Recycler view set ups
        mRecyclerView = findViewById(R.id.recyclerViewUpload);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mProgressCircle = findViewById(R.id.progress_circle);

        mUploads = new ArrayList<>();
        mAdapter = new AudioAdapterProducer(UploadActivity.this, mUploads);



        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(UploadActivity.this);

        // firebase  initialisation
        mDatabase = FirebaseDatabase.getInstance().getReference("Uploads");
        mStorage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();

        // Progress Dialog set in
        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);



        chooseFile = findViewById(R.id.choose_file);
        songName = findViewById(R.id.songName);
        uploadSong = findViewById(R.id.uploadSong);
        progressBar = findViewById(R.id.progressBar);
        UId = auth.getCurrentUser().getUid();
        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Uploads");

        uploadSong.setEnabled(false);
        uploadSong.setBackgroundColor(0xFF9CCAB4);
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseFileMethod();
                uploadSong.setBackgroundColor(0xFF05db82);

            }
        });

        // stop onscreen keybooard from poping up
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        uploadSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
                uploadSong.setEnabled(false);
                uploadSong.setBackgroundColor(0xFF9CCAB4);
            }
        });

        mDBL = mDatabase.child(UId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload;
                    String name = postSnapshot.child("name").getValue().toString();
                    String url = postSnapshot.child("imageUrl").getValue().toString();
                    upload = new Upload(name,url, auth.getCurrentUser().getDisplayName());

                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }
                mAdapter.notifyDataSetChanged();

                //mProgressCircle.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UploadActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(UploadActivity.this, ArtistUploadDownloadActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

    private void chooseFileMethod() {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
        uploadSong.setEnabled(true);
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null
                && data.getData()!= null){
            mAudioUri = data.getData();


            //mImageView.setImageURI(mImageUri);
        }
    }
    private void uploadFile(){
        PD.show();
        if(mAudioUri != null){
            final StorageReference fileRef = mStorageRef.child(songName.getText().toString().trim()
                    + "."+ getFileExtension(mAudioUri));

            mUploadTask = fileRef.putFile(mAudioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler =  new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }
                    }, 500);

                    Toast.makeText(UploadActivity.this,"Upload Successful", Toast.LENGTH_SHORT).show();
                    Upload upload = new Upload(songName.getText().toString().trim(),
                            fileRef.getDownloadUrl().toString(), auth.getCurrentUser().getDisplayName());

                    String uploadId = mDatabaseRef.push().getKey();
                    mDatabaseRef.child(UId).child(uploadId).setValue(upload);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int)progress);
                }
            });

            PD.dismiss();
        }else{
            Toast.makeText(this,"no file", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWhateverClick(int position) {
        Toast.makeText(this, "Whatever Click at position: " + position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDeleteClick(int position) {
        Toast.makeText(this, "Delete click at position: " + position, Toast.LENGTH_SHORT).show();
        Upload SelectedItem = mUploads.get(position);

        final String selectedKey = SelectedItem.getKey();

        StorageReference imageref = mStorage.getReferenceFromUrl(SelectedItem.getImageUrl());
        imageref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabase.child(UId).child(selectedKey).removeValue();
                Toast.makeText(UploadActivity.this, "Item Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        mDatabase.removeEventListener(mDBL);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuupload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout_upload:
                AuthUI.getInstance()
                        .signOut(UploadActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            public void onComplete(@NonNull Task<Void> task) {
                                // ...

                                Toast.makeText(UploadActivity.this, "user logged OUT successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                startActivity(new Intent(UploadActivity.this, FrontPageActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}



