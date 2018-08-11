package isaacsilas05.gmail.com.synarcmusic1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class FrontPageActivity extends AppCompatActivity {

    Button mUpLoad, mDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

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
                Intent intent = new Intent(FrontPageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
