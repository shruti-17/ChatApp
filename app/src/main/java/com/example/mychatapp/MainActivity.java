package com.example.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public final int SIGN_IN_REQUEST_CODE=10;
    private FirebaseListAdapter<ChatMessage> adapter;
    private static String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load chat room contents
            displayChatMessages();
        }

        FloatingActionButton fab =
                (FloatingActionButton)findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText input = (TextInputEditText)findViewById(R.id.input);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Message added");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessage("fail to add comment : "+e.getMessage());
                                    Log.d(TAG, e.getMessage());
                                }
                        });

                Log.d(TAG,input.getText().toString());
                // Clear the input
                input.setText("");
            }
        });
    }

    private void showMessage(String message) {

        Toast.makeText(this,message, Toast.LENGTH_LONG).show();

    }

    public void displayChatMessages(){

        FirebaseDatabase.getInstance().getReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ChatMessage> MessageList = new ArrayList<>();
                for (DataSnapshot snap:dataSnapshot.getChildren()) {

                    ChatMessage message = snap.getValue(ChatMessage.class);
                    MessageList.add(message);
//                    Log.d(TAG,"Loaded messages "+message.getMessageText());
                }
                RecyclerView listOfMessages = (RecyclerView)findViewById(R.id.list_of_messages);
                MessageAdapter messageAdapter = new MessageAdapter(getApplicationContext(),MessageList);
                listOfMessages.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


//        Log.d(TAG,"Displaying Mesaages");
//        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
//        Query query = FirebaseDatabase.getInstance().getReference();
//        FirebaseListOptions<ChatMessage> options =
//                new FirebaseListOptions.Builder<ChatMessage>()
//                        .setQuery(query, ChatMessage.class)
//                        .setLayout(R.layout.message)
//                        .build();
//        adapter = new FirebaseListAdapter<ChatMessage>(options) {
//            @Override
//            protected void populateView(View v, ChatMessage model, int position) {
//                // Get references to the views of message.xml
//                TextView messageText = (TextView)v.findViewById(R.id.message_text);
//                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
//                TextView messageTime = (TextView)v.findViewById(R.id.message_time);
//
//                // Set their text
//                messageText.setText(model.getMessageText());
//                messageUser.setText(model.getMessageUser());
//                Log.d(TAG,model.getMessageText());
//                // Format the date before showing it
//                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
//                        model.getMessageTime()));
//            }
//        };
//
//        listOfMessages.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                displayChatMessages();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }

    }
}