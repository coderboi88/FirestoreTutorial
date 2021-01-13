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
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "Second Activity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference textbookRef = db.collection("TextBook");

    private EditText second_title,second_description,second_priority;
    private TextView details_textview;

    private DocumentSnapshot lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        second_title = findViewById(R.id.second_title);
        second_description = findViewById(R.id.second_description);
        second_priority = findViewById(R.id.second_priority);
        details_textview = findViewById(R.id.second_textview_detail);

        //executeBatchWrite();
        executeTransaction();
    }


    /*@Override
    protected void onStart() {
        super.onStart();

        textbookRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(SecondActivity.this, "error!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG,error.toString());
                    return;
                }

                //This for loop code is used mainly when we have recycler view ,as during any change we dont want to load full list instead
                //we want only to load changes,this below code is useful for that purpose.
                for(DocumentChange dc : value.getDocumentChanges()){
                    DocumentSnapshot documentSnapshot = dc.getDocument();
                    String id = documentSnapshot.getId();
                    int oldIndex = dc.getOldIndex();
                    int newIndex = dc.getNewIndex(); //Here we get old index and new index so we know the position of data change which help in recycler biew

                    switch (dc.getType()){
                        case ADDED:
                            details_textview.append("\nAddedd:"+id+"\nOld Index: "+oldIndex+"\nNew Index: "+newIndex);
                            break;
                        case MODIFIED:
                            details_textview.append("\nModified:"+id+"\nOld Index: "+oldIndex+"\nNew Index: "+newIndex);
                            break;
                        case REMOVED:
                            details_textview.append("\nRemoved:"+id+"\nOld Index: "+oldIndex+"\nNew Index: "+newIndex);
                            break;

                    }
                }
            }
        });
    }*/

    public void addNote(View view) {
        String title = second_title.getText().toString();
        String description = second_description.getText().toString();

        if(second_priority.length()==0){
            second_priority.setText("0");
        }

        int priority = Integer.parseInt(second_priority.getText().toString());

        Text text = new Text(title,description,priority);

        textbookRef.add(text)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(SecondActivity.this, "Text Added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SecondActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG,e.toString());
                    }
                });
    }

    public void loadNotes(View view) {
        //It is used to load all the text present in collection
        //.whereEqual is a query in which how we want to see the data and there few more queries
        /*textbookRef.whereGreaterThanOrEqualTo("priority",2)
                .whereEqualTo("title","WC")
                .orderBy("priority")
                //.limit(3)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Text text = documentSnapshot.toObject(Text.class);
                            text.setDocumentId(documentSnapshot.getId());

                            String documentId = text.getDocumentId();
                            String title = text.getTitle();
                            String descp = text.getDescription();
                            int priority = text.getPriority();

                            data += "Document Id: "+documentId+"\nTitle: "+ title + "\nDescription: "+
                                    descp+"\nPriority: "+priority + "\n\n";
                        }
                        details_textview.setText(data);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG,e.toString());
            }
        });*/

        //This method is used to load the document from the last point where it has been left
        //{sometime it occus when we load only few data,and we want to load further from the last point}
        Query query;

        if(lastResult==null){
            query = textbookRef.orderBy("priority").limit(3);
        }else{
            query = textbookRef.orderBy("priority").startAfter(lastResult).limit(3);
        }

        query.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Text text = documentSnapshot.toObject(Text.class);
                            text.setDocumentId(documentSnapshot.getId());

                            String documentId = text.getDocumentId();
                            String title = text.getTitle();
                            String descp = text.getDescription();
                            int priority = text.getPriority();

                            data += "Document Id: " + documentId + "\nTitle: " + title + "\nDescription: " +
                                    descp + "\nPriority: " + priority + "\n\n";
                        }
                        if (queryDocumentSnapshots.size() > 0) {
                            data += "_____________\n\n";
                            details_textview.append(data);

                            lastResult = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                     @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void queryText(View view) {

        //in above loadNote method we have apply queries/multiple queries but in this
        //method we have merge to or task which is not directly available in Firestore

        Task task1 = textbookRef.whereLessThan("priority",2)
                .orderBy("priority")
                .get();
        Task task2 = textbookRef.whereGreaterThan("priority",2)
                .orderBy("priority")
                .get();

        Task<List<QuerySnapshot>> allTask = Tasks.whenAllSuccess(task1,task2);
        allTask.addOnSuccessListener(new OnSuccessListener<List<QuerySnapshot>>() {
            @Override
            public void onSuccess(List<QuerySnapshot> querySnapshots) {
                String data = "";
                for (QuerySnapshot queryDocumentSnapshots : querySnapshots) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Text text = documentSnapshot.toObject(Text.class);
                        text.setDocumentId(documentSnapshot.getId());

                        String documentId = text.getDocumentId();
                        String title = text.getTitle();
                        String descp = text.getDescription();
                        int priority = text.getPriority();

                        data += "Document Id: " + documentId + "\nTitle: " + title + "\nDescription: " +
                                descp + "\nPriority: " + priority + "\n\n";
                    }
                }
                details_textview.setText(data);

            }
        });

    }

    private void executeBatchWrite() {
        //This method is used to write multiple files at same time
        //But if there is eoor in writing any one document only then whole writting process will fail.
        WriteBatch batch = db.batch();
        DocumentReference doc1 = textbookRef.document("New Text");
        batch.set(doc1,new Text("New Note","New Note",1));

        DocumentReference doc2 = textbookRef.document("wE7kph8daLTEMkrVGyRR");
        batch.update(doc2,"title","Updated Text");

        DocumentReference doc3 = textbookRef.document("AKk4mTyZ96GSOGdwnDT4");
        batch.delete(doc3);


        DocumentReference doc4 = textbookRef.document();
        batch.set(doc4,new Text("Added Note","Adeed Note",1));

        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                details_textview.setText(e.toString());
            }
        });

    }

    private void executeTransaction() {
        db.runTransaction(new Transaction.Function<Long>() {

            @Override
            public Long apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                //Here we can define many read and write operation but only thing is first all read and then all write opeartion
                // have to be defined.
                DocumentReference exampleTextref = textbookRef.document("Example Note");
                DocumentSnapshot exampleSnapshot = transaction.get(exampleTextref);
                long newPriority = exampleSnapshot.getLong("priority")+1; //Until here it is our read operation
                transaction.update(exampleTextref,"priority",newPriority);
                return newPriority;
            }
        }).addOnSuccessListener(new OnSuccessListener<Long>() {
            @Override
            public void onSuccess(Long result) {
                Toast.makeText(SecondActivity.this, "New priority :" + result, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void nextActivity(View view) {
        Intent intent = new Intent(SecondActivity.this,ThirdActivity.class);
        startActivity(intent);
    }
}
