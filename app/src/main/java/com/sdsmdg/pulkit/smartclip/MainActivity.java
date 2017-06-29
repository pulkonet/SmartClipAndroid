package com.sdsmdg.pulkit.smartclip;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.firebase.ui.auth.ui.AcquireEmailHelper.RC_SIGN_IN;

public class MainActivity extends AppCompatActivity {

    private static final String ANONYMOUS="anonymous";

    private ProgressBar mProgressBar;
    private ListView mClippedTextListView;
    private TextAdapter mTextAdapter;
    private FloatingActionButton floatingActionButton;
    private RelativeLayout mAddClippedTextContainer, relativeLayout;
    private EditText mAddClippedText;
    private Button mClipButton;
    private Button mHideBtn;
    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference, db_node;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private RecyclerView rvClippedText;
    private  ArrayList<ClippedText> clippedTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsername = ANONYMOUS;

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mDatabaseReference=mFirebaseDatabase.getReference().child("clippedtext");
        mFirebaseAuth=FirebaseAuth.getInstance();


        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        rvClippedText = (RecyclerView) findViewById(R.id.clippedTextRecyclerView);
        floatingActionButton= (FloatingActionButton)findViewById(R.id.fab);
        mAddClippedTextContainer = (RelativeLayout)findViewById(R.id.addClippedTextContainer);

        mAddClippedText= (EditText) findViewById(R.id.addClippedText);
        mClipButton= (Button) findViewById(R.id.clipButton);
        mHideBtn =(Button)findViewById(R.id.hideAddClippedTextContainer);
        mClipButton.setEnabled(false);

        // Initialize message ListView and its adapter
        clippedTexts=new ArrayList<>();
        mTextAdapter=new TextAdapter(this,clippedTexts);
        rvClippedText.setAdapter(mTextAdapter);
        rvClippedText.setLayoutManager(new LinearLayoutManager(this));
        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is logged in
                    onSignedInInitialized(user.getEmail());
                } else {
                    //user is logged out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN ) ;
                }
            }
        };

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.setVisibility(View.GONE);
                mAddClippedTextContainer.setVisibility(View.VISIBLE);
            }
        });
        mHideBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddClippedText.setText("");
                floatingActionButton.setVisibility(View.VISIBLE);
                mAddClippedTextContainer.setVisibility(View.GONE);
            }
        });
        mAddClippedText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mClipButton.setEnabled(true);
                } else {
                    mClipButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mClipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                ClippedText clippedText = new ClippedText(mAddClippedText.getText().toString(), mUsername, getCurrentTime());
                mDatabaseReference.push().setValue(clippedText);
                // Clear input box
                mAddClippedText.setText("");
                floatingActionButton.setVisibility(View.VISIBLE);
                mAddClippedTextContainer.setVisibility(View.GONE);
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                clippedTexts.remove(viewHolder.getAdapterPosition());
                mTextAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                final Snackbar mySnackbar = Snackbar.make(findViewById(R.id.container),"The clip has been deleted!", Snackbar.LENGTH_LONG);
                mySnackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //bring back the view
                    Toast.makeText(getApplicationContext(),"Oh yeah",Toast.LENGTH_LONG).show();
                          }
                });
                mySnackbar.setActionTextColor(Color.RED);
                mySnackbar.show();
                }
                //kill the view completely some time after this snackbar goes down
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(rvClippedText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseReadListener();
//        mTextAdapter.clear();

    }


    public void onSignedInInitialized(String username){
        mUsername = username;
        attachDatabaseReadListener();

    }

    private void onSignedOutCleanup(){
        mUsername=ANONYMOUS;
//        mTextAdapter.clear();
        detachDatabaseReadListener();

    }

    private void attachDatabaseReadListener(){
        if(mChildEventListener==null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    ClippedText clippedText = dataSnapshot.getValue(ClippedText.class);
                    addNewClippedText(clippedText);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }

    private void detachDatabaseReadListener(){
        if(mChildEventListener!=null)
        {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener=null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed In!", Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "Sign In Canceled", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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
    public String getCurrentTime() {
        Date time = Calendar.getInstance().getTime();
        return time.toString().substring(0,19);
    }

    public void addNewClippedText(ClippedText clippedText){
        clippedTexts.add(0, clippedText);
        mTextAdapter.notifyItemInserted(0);
    }

}
