package isaacsilas05.gmail.com.synarcmusic1;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;

public class ArtistUploadDownloadActivity extends Activity {

    final static private String ArtistUpDownToken = "artist token";
    private int sendExtra = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_upload_download);

        Button upload = findViewById(R.id.artistupload);
        Button download = findViewById(R.id.artistdownload);


        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArtistUploadDownloadActivity.this, UploadActivity.class);
                startActivity(intent);
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ArtistUploadDownloadActivity.this, DownloadActivity.class);
                intent.putExtra(ArtistUpDownToken,sendExtra);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ArtistUploadDownloadActivity.this,FrontPageActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}
