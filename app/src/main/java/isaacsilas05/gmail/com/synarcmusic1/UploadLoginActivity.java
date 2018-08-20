package isaacsilas05.gmail.com.synarcmusic1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
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

public class UploadLoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private DatabaseReference mDatabase;
    private FirebaseAuth auth;
    private Button btnLogin;
    private ProgressDialog PD;
    private  SharedPreferences sp,sp2;
    private  SharedPreferences.Editor editor;

    private static final String LOGIN = "login";
    private static final String IDUSER= "user id";
    private static final String LOGGED = "logged";
    private static final String EMAIL = "email";



    @Override    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_login);

        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("Uploads");

        inputEmail =  findViewById(R.id.email);
        inputPassword =  findViewById(R.id.password);
        btnLogin = findViewById(R.id.sign_in_button);

        sp = getSharedPreferences(LOGIN, MODE_PRIVATE);
        sp2 = getSharedPreferences(LOGIN, MODE_PRIVATE);
        editor =sp.edit();

        String userid = sp2.getString(IDUSER,"");
        String email = sp2.getString(EMAIL,"");

        Toast.makeText(
                UploadLoginActivity.this,
                email,
                Toast.LENGTH_LONG).show();

        if (sp.getBoolean(LOGGED, false) &&  email.equals(auth.getCurrentUser().getEmail()) ){
            Toast.makeText(
                    UploadLoginActivity.this,
                    auth.getCurrentUser().getEmail(),
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(UploadLoginActivity.this, UploadActivity.class);
            intent.putExtra("uid", userid);
            startActivity(intent);
        }



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override            public void onClick(View view) {
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();



                try {

                    if (password.length() > 0 && email.length() > 0) {
                        PD.show();
                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(UploadLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override

                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            sp.edit().putBoolean(LOGGED,true).apply();

                                            mDatabase.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                                        String child = postSnapshot.getKey();
                                                        if(child.equals(auth.getCurrentUser().getUid())){
                                                            Toast.makeText(
                                                                    UploadLoginActivity.this,
                                                                    "Authentication Sucess",
                                                                    Toast.LENGTH_LONG).show();

                                                            editor.putString(IDUSER, auth.getCurrentUser().getUid() );
                                                            editor.putString(EMAIL, email);
                                                            editor.apply();

                                                            Intent intent = new Intent(UploadLoginActivity.this, UploadActivity.class);
                                                            intent.putExtra("uid", auth.getCurrentUser().getUid());
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
                                                    UploadLoginActivity.this,
                                                    "Authentication Fail",
                                                    Toast.LENGTH_LONG).show();


                                            //finish();
                                        }
                                        PD.dismiss();
                                    }
                                });
                    } else {
                        Toast.makeText(
                                UploadLoginActivity.this,
                                "Fill All Fields",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        findViewById(R.id.forget_password_button).setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), ForgetAndChangePasswordActivity.class).putExtra("Mode", 0));
            }
        });

    }

    @Override    protected void onResume() {
        if (auth.getCurrentUser() != null) {
            //startActivity(new Intent(LoginActivity.this, MainActivity.class));
            // finish();
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(UploadLoginActivity.this, FrontPageActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }

}
