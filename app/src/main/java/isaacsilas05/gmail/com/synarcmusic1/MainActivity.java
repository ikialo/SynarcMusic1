package isaacsilas05.gmail.com.synarcmusic1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button mRegister, signin;
    EditText inputEmail, inputPassword;

    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private ProgressDialog PD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRegister = findViewById(R.id.sign_up_button);
        signin = findViewById( R.id.sign_in_button);
        inputEmail = findViewById(R.id.emailuser);
        inputPassword = findViewById(R.id.passworduser);

        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });


        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserLogin();
            }
        });


    }

    private void UserLogin() {
        final String email = inputEmail.getText().toString();
        final String password = inputPassword.getText().toString();

        try {

            if (password.length() > 0 && email.length() > 0) {
                PD.show();
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override

                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(
                                            MainActivity.this,
                                            "Authentication Sucess",
                                            Toast.LENGTH_LONG).show();
                                    mDatabase.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                                String child = postSnapshot.getKey();
                                                if(child.equals(auth.getCurrentUser().getUid())){
                                                    Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
                                                    //intent.putExtra("uid", auth.getCurrentUser().getUid());
                                                    startActivity(intent);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    Log.v("error", task.getResult().toString());
                                } else {
                                    Toast.makeText(
                                            MainActivity.this,
                                            "Authentication Fail",
                                            Toast.LENGTH_LONG).show();


                                    //finish();
                                }
                               PD.dismiss();
                            }
                        });
            } else {
                Toast.makeText(
                        MainActivity.this,
                        "Fill All Fields",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
