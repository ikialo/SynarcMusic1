package isaacsilas05.gmail.com.synarcmusic1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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


    private static final String TAG = "CHILD";
    private DatabaseReference mDatabase;
    private StorageReference mStoraage;
    private FirebaseAuth auth;
    private AdapterAudioDownload mAdapter;
    private RecyclerView mRecyclerView;

    private List<Upload> mDownloads;
    private List<String> child;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        mRecyclerView = findViewById(R.id.recyclerViewUpload);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mDownloads = new ArrayList<>();
        mAdapter = new AdapterAudioDownload(DownloadActivity.this, mDownloads);

        mRecyclerView.setAdapter(mAdapter);

        mDatabase = FirebaseDatabase.getInstance().getReference("Uploads");
        auth = FirebaseAuth.getInstance();


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
                                upload = new Upload(name,url);

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

}
