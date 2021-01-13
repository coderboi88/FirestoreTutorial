package com.aditya.cloudfirestoreexample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCP = "description";
    private static final String TAG = "MainActivity";

    private EditText editTextTitle,editTextDescription;
    private TextView textviewDetail;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference noteRef = db.document("NoteBook/My First Note");
    private CollectionReference noteBookRef = db.collection("TextBook");
    //private ListenerRegistration noteListener;
    //private DocumentReference notesRef = db.collection("NoteBook").document("My First note");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextTitle = findViewById(R.id.edit_text_title);
        editTextDescription = findViewById(R.id.edit_text_description);
        textviewDetail = findViewById(R.id.textview_detail);
    }
    //---------------------NOTE = Earlier we use hashmap,later we are usinf java object for data insertion and updation---------------------------
    @Override
    protected void onStart() {
        super.onStart();
        //noteListener =
        noteRef.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(MainActivity.this, "Error While Loading", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,error.toString());
                    return;
                }

                if(documentSnapshot.exists()){
                    /*String title = documentSnapshot.getString(KEY_TITLE);
                    String descp = documentSnapshot.getString(KEY_DESCP);

                    textviewDetail.setText("Title: "+title+"\n" + "Description: "+descp);*/

                    Note note = documentSnapshot.toObject(Note.class);
                    String title = note.getTitle();
                    String descp = note.getDescription();

                    textviewDetail.setText("Title: "+title+"\n" + "Description: "+descp);

                }
                else{
                    //This is done so whennwe delete the note nothing will show in the screen
                    textviewDetail.setText("");
                }
            }
        });
    }

    //Here are two ways to stop snapshot Listener first by manually stop it in the onStop method by Listener Registration and another option is
    //just putting this in addSnapshot method in parameter .It will stop automatically when controls flow from mainactivity
    //@Override
    /*protected void onStop() {
        super.onStop();
        noteListener.remove();
    }*/

    public void saveNote(View view) {
        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();

        /*Map<String,Object> note = new HashMap<>();
        note.put(KEY_TITLE,title);
        note.put(KEY_DESCP,description);*/

        //Earlier we are usinf hashmap but now we are using java object to do the d=same work so we can have wide variety in datatypes;
        Note note = new Note(title,description);

        //Here we ca us enoteref.set() directly as we have defined direct path for it
        db.collection("NoteBook").document("My First Note").set(note)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MainActivity.this, "Note Saved", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,e.toString());
                    }
                });
    }

    public void loadNote(View view) {
        noteRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            /*String title = documentSnapshot.getString(KEY_TITLE);
                            String descp = documentSnapshot.getString(KEY_DESCP);*/

                            //Another Method to get the data
                            //Map<String,Object> notes = documentSnapshot.getData();

                            Note note = documentSnapshot.toObject(Note.class);
                            String title = note.getTitle();
                            String descp = note.getDescription();

                            textviewDetail.setText("Title: "+title+"\n" + "Description: "+descp);
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Document Not Exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,e.toString());
                    }
                });
    }

    public void updateDescription(View view) {
        String description = editTextDescription.getText().toString();

        Map<String ,Object> note = new HashMap<>();
        note.put(KEY_DESCP,description);

        //noteRef.set(note, SetOptions.merge()); //Setoptions .merge will updated the omly field in which we have change the data and left untouched
                                                // rest of the field

        //noteRef.update(note);
        noteRef.update(KEY_DESCP,description);      //This method will not create new document if no document is present but above method will do
    }

    public void deleteDescription(View view) {
        //This method is used to delete the particular field in the Document

        //Map<String,Object> note = new HashMap<>();
        //note.put(KEY_DESCP, FieldValue.delete());
        //noteRef.update(note);

        noteRef.update(KEY_DESCP,FieldValue.delete());
    }

    public void deleteNote(View view) {
        noteRef.delete();
    }

    public void nextActivity(View view) {
        Intent intent = new Intent(MainActivity.this,SecondActivity.class);
        startActivity(intent);
    }
}

