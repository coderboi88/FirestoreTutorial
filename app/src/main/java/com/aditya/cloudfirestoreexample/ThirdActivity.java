package com.aditya.cloudfirestoreexample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirdActivity extends AppCompatActivity {

    private static final String TAG = "Third Activity";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference bookShelfRef = db.collection("BookShelf");

    private EditText third_title,third_description,third_priority,third_tag;
    private TextView details_textview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        third_title = findViewById(R.id.third_title);
        third_description = findViewById(R.id.third_description);
        third_priority = findViewById(R.id.third_priority);
        third_tag = findViewById(R.id.third_tag);
        details_textview = findViewById(R.id.third_textview_detail);

        updateArray();
    }

    private void updateArray() {
        //bookShelfRef.document("").update("tags", FieldValue.arrayUnion("new tag"));
        //bookShelfRef.document("").update("tags", FieldValue.arrayRemove("new tag"));
        //bookShelfRef.document("").update("tags.tag1", true);
    }

    public void addBook(View view) {
        String title = third_title.getText().toString();
        String descp = third_description.getText().toString();

        if(third_priority.length()==0){
            third_priority.setText("0");
        }

        int priority = Integer.parseInt(third_priority.getText().toString());

        String tagInput = third_tag.getText().toString();
        String[] tagArray = tagInput.split("\\s*,\\s*");
        //List<String> tags = Arrays.asList(tagArray);
        Map<String ,Boolean> tags = new HashMap<>();

        for(String tag:tagArray){
            tags.put(tag,true);
        }

        Book book = new Book(title,descp,priority,tags);
        bookShelfRef.add(book);
    }

    public void loadBook(View view) {
        bookShelfRef.whereEqualTo("tags.tag1","true")
                //.whereArrayContains("tags","tag5")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String data="";
                for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots){
                    Book book = documentSnapshot.toObject(Book.class);
                    book.setDocumentId(documentSnapshot.getId());

                    String documentId = book.getDocumentId();

                    data+="Id: "+documentId;
                    for(String tag:book.getTags().keySet()){
                        data+="\n-"+tag;
                    }
                    data+="\n\n";
                }

                details_textview.setText(data);
            }
        });
    }
}
