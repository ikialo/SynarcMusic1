package isaacsilas05.gmail.com.synarcmusic1;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RegisterActivity extends AppCompatActivity {


    private EditText mName, mPhone, mEmail, mPassword, mConPass;
    private FirebaseAuth auth;
    private TextView dateofbirth;
    private Button btnSignUp, male, female;
    private String UID;
    private String gender;
    private boolean check;
    private ProgressDialog PD;
    private DatePickerDialog.OnDateSetListener mOndatesetlistener;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    @Override    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize variables
        gender = null;
        PD = new ProgressDialog(this);
        PD.setMessage("Loading...");
        PD.setCancelable(true);
        PD.setCanceledOnTouchOutside(false);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        mEmail = (EditText) findViewById(R.id.emailReg);
        mPassword = (EditText) findViewById(R.id.passwordReg);
        mPhone = (EditText) findViewById(R.id.phoneReg);
        dateofbirth = findViewById(R.id.dateofbirth);
        mConPass = (EditText) findViewById(R.id.confirmPasswordReg);
        mName = findViewById(R.id.nameReg);
        btnSignUp = (Button) findViewById(R.id.sign_up_buttonReg);
        male = (Button) findViewById(R.id.male);
        female = (Button) findViewById(R.id.female);


        // when male gender is clicked
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "male";
                male.setBackgroundColor(0xFFBDFB42);
                female.setBackgroundColor(0xFF05DB82);
            }
        });
        // when female gender is clicked
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gender = "female";
                female.setBackgroundColor(0xFFBDFB42);
                male.setBackgroundColor(0xFF05DB82);
            }
        });

        // date of birth clicked
        dateofbirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(RegisterActivity.this,
                        android.R.style.Theme_Black_NoTitleBar_Fullscreen,
                        mOndatesetlistener,2000,1,1);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        // set date of birth
        mOndatesetlistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 = i1+1;
                String date = i2 + "/"+i1+"/"+i;
                dateofbirth.setText(date);
            }
        };



        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpButtonCLicked();
            }
        });
    }

    private void SignUpButtonCLicked() {

        // check if fields are empty
        if (!TextUtils.isEmpty(mName.getText()) && !TextUtils.isEmpty(mPhone.getText())
                && gender != null && !TextUtils.isEmpty(mEmail.getText()) &&
                !TextUtils.isEmpty(mPassword.getText()) ) {


            final String email = mEmail.getText().toString();
            final String password = mPassword.getText().toString();
            final String conpassword = mConPass.getText().toString();

            // check if email already used
            auth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                @Override
                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                    // not check means there is an existin email
                    check  = task.getResult().getProviders().isEmpty();

                    if (!check){
                        mEmail.setHint("email already exist");
                    }
                }
            });



            try {
                // check if password correct length
                if (password.length() > 5 && email.length() > 0 && password.equals(conpassword) && check) {
                    PD.show();

                    //create email and password
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override

                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(
                                                RegisterActivity.this,
                                                "Authentication Failed",
                                                Toast.LENGTH_LONG).show();
                                        Log.v("error", task.getResult().toString());
                                    } else {
                                        //getting user id
                                        UID = auth.getCurrentUser().getUid().toString();

                                        //creating object
                                        UserProfile userProfile = new UserProfile(mName.getText().toString(),
                                                dateofbirth.getText().toString(), gender,
                                                mPhone.getText().toString(), email);

                                        String uploadId = mDatabaseRef.push().getKey();
                                        mDatabaseRef.child("Users").child(UID).child(uploadId).setValue(userProfile);

                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    PD.dismiss();
                                }
                            });
                } else {

                    Toast.makeText(
                            RegisterActivity.this,
                            "Password must be at least 6 characters and must match",
                            Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(
                    RegisterActivity.this,
                    "fields have to be filled",
                    Toast.LENGTH_LONG).show();
        }
    }
}
