package isaacsilas05.gmail.com.synarcmusic1;

import android.content.Intent;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class FrontPageActivity extends AppCompatActivity {

    Button mUpLoad;
    private static final int RC_SIGN_IN = 1 ;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    TextView name;
    final static private int UPLOAD_DECIDE =1;
    final static private int DOWNLOAD_DECIDE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);

        mDatabase = FirebaseDatabase.getInstance().getReference("Uploads");
        mUpLoad = findViewById(R.id.upload);
        mUpLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI();

            }
        });


    }

    void AuthUI (){
        auth = FirebaseAuth.getInstance();
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_launcher_background)      // Set logo drawable
                        .setTheme(R.style.Theme_AppCompat_DayNight_DarkActionBar)
                        .build(),
                RC_SIGN_IN);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                mDatabase.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String child;
                        int updowndecider = DOWNLOAD_DECIDE;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                            child = postSnapshot.getKey();
                            if(child.equals(user.getUid())){
                                Toast.makeText(
                                        FrontPageActivity.this,
                                        "Authentication Sucess",
                                        Toast.LENGTH_LONG).show();

                                updowndecider = UPLOAD_DECIDE;
                                Intent intent = new Intent(FrontPageActivity.this, ArtistUploadDownloadActivity.class);
                                startActivity(intent);
                            }
                        }
                        if (updowndecider ==DOWNLOAD_DECIDE) {
                            Intent intent = new Intent(FrontPageActivity.this, DownloadActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                // ...
            } else {

                Toast.makeText(this, "user log in Failed", Toast.LENGTH_SHORT).show();
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

}
