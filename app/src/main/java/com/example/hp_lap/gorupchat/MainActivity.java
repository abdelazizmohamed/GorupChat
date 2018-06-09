package com.example.hp_lap.gorupchat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * Created by hp-lap on 2/24/2018.
 */

public class MainActivity extends AppCompatActivity {

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    public static final int SIGN_IN = 1;
    private static final int PHOTO = 2;

    private ListView listView;
    private MessageAdapter massageadapter;
    private ProgressBar progressbar;
    private ImageButton imageButton;
    private EditText editText;
    private Button button;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private ChildEventListener childEventListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private String mUsername;
    ArrayList<MessageModel> messageModelList;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsername = ANONYMOUS;

        imageView=findViewById(R.id.image_view);
        showImage(imageView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        databaseReference = firebaseDatabase.getReference().child("messages");

        progressbar = findViewById(R.id.progress_bar);
        listView = findViewById(R.id.listview_message);
        editText = findViewById(R.id.edit_text);
        button = findViewById(R.id.send_button);

        if(savedInstanceState == null || !savedInstanceState.containsKey("Key"))
        { messageModelList = new ArrayList<>();}
        else
        {  messageModelList= savedInstanceState.getParcelableArrayList("Key");}


        massageadapter = new MessageAdapter(this, R.layout.item, messageModelList);
        listView.setAdapter(massageadapter);

        progressbar.setVisibility(ProgressBar.INVISIBLE);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length() >0){
                    button.setEnabled(true);
                }else{
                    button.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageModel messageModel = new MessageModel(editText.getText().toString(),mUsername,null);

                databaseReference.push().setValue(messageModel);
                editText.setText("");
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if(firebaseUser != null){
                    signedIn(firebaseUser.getDisplayName());
                }
                else{

                    signedOut();
                    List <AuthUI.IdpConfig> Auth = Arrays.asList(
                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(), new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
                    );

                    startActivityForResult(
                            AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setProviders(Auth)
                            .build(),SIGN_IN);
                }
            }
        };
    }
    public void showImage(View v){
        String url = "http://findicons.com/files/icons/2101/ciceronian/59/photos.png";
        new LoadBitmap(imageView).execute(url);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN){
            if(requestCode == RESULT_OK){
                Toast.makeText(this, R.string.signed_in,Toast.LENGTH_LONG).show();
            }
            else if (requestCode == RESULT_CANCELED){
                Toast.makeText(this, R.string.sign_in_canceled,Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        if(childEventListener != null){
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
        massageadapter.clear();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void signedIn(String username){
        mUsername = username;
        if(childEventListener == null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    massageadapter.add(messageModel);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            databaseReference.addChildEventListener(childEventListener);
        }
    }

    private void signedOut(){
        mUsername = ANONYMOUS;
        massageadapter.clear();
        if(childEventListener == null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                    massageadapter.add(messageModel);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
            databaseReference.addChildEventListener(childEventListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("Key",  messageModelList);
        super.onSaveInstanceState(outState);
    }

    class LoadBitmap extends AsyncTask<String,Void,Bitmap> {
        private ImageView views;

        LoadBitmap(ImageView views){
            this.views = views;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap=null;
            try {
                URL url=new URL(strings[0]);
                bitmap=BitmapFactory.decodeStream((InputStream)url.getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            views.setImageBitmap(bitmap);
        }
    }

}
