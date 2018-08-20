package isaacsilas05.gmail.com.synarcmusic1;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class FrontPageActivity extends AppCompatActivity {

    Button mUpLoad, mDownload;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads");
        mUpLoad = findViewById(R.id.upload);
        mDownload = findViewById(R.id.download);
        mUpLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FrontPageActivity.this, UploadLoginActivity.class);
                startActivity(intent);
            }
        });

        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(FrontPageActivity.this,DownloadActivity.class);
                startActivity(intent);
            }
        });
    }





}
