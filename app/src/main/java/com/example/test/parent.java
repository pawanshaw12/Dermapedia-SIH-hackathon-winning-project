package com.example.test;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class parent extends Fragment {
    EditText name, email, pass, address,phone,checkPassword;ImageView imageView;Button signup,uploadpic;
    View view;
    FirebaseFirestore db;public FirebaseAuth mAuth;private StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
    public static final int RESULT_LOAD_IMAGE = 1;Uri selectedImage;int k=0;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null ){
            selectedImage=data.getData();
            imageView.setImageURI(selectedImage);k++;
            }
            else Toast.makeText(getActivity(), "PLEASE SELECT AN IMAGE", Toast.LENGTH_LONG).show();
    }
    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.parent,container,false);
        mAuth = FirebaseAuth.getInstance();
        name=view.findViewById(R.id.name);email=view.findViewById(R.id.email);pass=view.findViewById(R.id.pass);
        address=view.findViewById(R.id.address);phone=view.findViewById(R.id.phone);checkPassword=view.findViewById(R.id.checkPassword);
        imageView=view.findViewById(R.id.imageView);uploadpic=view.findViewById(R.id.uploadpic);signup=view.findViewById(R.id.signup);
        final FragmentActivity activity=getActivity();
        name.addTextChangedListener(new TextWatcher() {
            int k=0;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if(TextUtils.isEmpty(name.getText()))name.setError("EMPTY NAME FIELD");
                else{
                    String nameip = name.getText().toString().trim();
                    for (int i=0;i<nameip.length();i++){
                        if(nameip.charAt(i)<65&&nameip.charAt(i)>90&&nameip.charAt(i)<97&&nameip.charAt(i)>122&&nameip.charAt(i)!=' ')
                            name.setError("INVALID NAME");
                        if(nameip.charAt(i)==' ')k++;
                    }
                    if(k==0)name.setError("ENTER LAST NAME");
                }
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if(TextUtils.isEmpty(email.getText()))email.setError("EMPTY EMAIL FIELD");
            }
        });
        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(pass.getText()))pass.setError("EMPTY PASSWORD FIELD");
                else if (pass.getText().toString().length()<6) pass.setError("INPUT PASSWORD ATLEAST 6 CHARACTERS LONG");
            }
        });
        address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(address.getText()))address.setError("EMPTY ADDRESS FIELD");
            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(phone.getText()))phone.setError("EMPTY PHONE NUMBER FIELD");
                else if(phone.getText().toString().length()!=10) phone.setError("INVALID NAME");
            }
        });
        checkPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(checkPassword.getText()))checkPassword.setError("EMPTY PASSWORD FIELD");
                else if (!checkPassword.getText().toString().equals(pass.getText().toString())) checkPassword.setError("RE-TYPED WRONG PASSWORD");
            }
        });
        uploadpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,RESULT_LOAD_IMAGE);
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getError()==null&&address.getError()==null&&phone.getError()==null&&email.getError()==null&&pass.getError()==null&&checkPassword.getError()==null&&k!=0) {
                    mAuth=FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                    Toast.makeText(getActivity(), "PASSWORD NOT STRONG", Toast.LENGTH_LONG).show();
                                } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                    Toast.makeText(getActivity(), "EMAIL ID MALFUNCTIONED", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                db = FirebaseFirestore.getInstance();
                                String token = FirebaseInstanceId.getInstance().getToken();
                                objectParent current = new objectParent(name.getText().toString().trim(), phone.getText().toString().trim(), email.getText().toString().trim(), address.getText().toString().trim(),"0000","0",token);
                                db.collection("Email").document("parent " + email.getText().toString().trim()).set(current)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                StorageReference imgeref = mStorageRef.child(email.getText().toString()+".jpg");
                                                if (selectedImage != null) {
                                                    imgeref.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        }
                                                    });
                                                }
                                                Toast.makeText(getActivity(), "ACCOUNT CREATED", Toast.LENGTH_SHORT).show();
                                                mAuth.signInWithEmailAndPassword(email.getText().toString().trim(), pass.getText().toString().trim()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                                        if(task.isSuccessful()){
                                                            mAuth= FirebaseAuth.getInstance();
                                                            final FirebaseUser user = mAuth.getCurrentUser();assert user != null;
                                                            user.sendEmailVerification().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) Toast.makeText(activity, "VERIFICATION EMAIL SENT TO " + user.getEmail(), Toast.LENGTH_LONG).show();
                                                                    else Toast.makeText(activity,"FAILED TO SEND VERIFICATION EMAIL.", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                });
                                                Intent intent = new Intent(activity, login.class);startActivity(intent);activity.finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "CANNOT ADD USER", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });

                }
                else Toast.makeText(getActivity(), "INCOMPLETE PROFILE DETAILS", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
