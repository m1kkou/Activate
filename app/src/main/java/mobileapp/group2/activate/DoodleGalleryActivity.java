package mobileapp.group2.activate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import mobileapp.group2.activate.R;


public class DoodleGalleryActivity extends AppCompatActivity {
    private StorageReference mStorageReference;
    private int lengthOfArray;

    private ArrayList<String> mDoodleURIs;
    private RecyclerView mRecyclerView;

    private File mGalleryFolder;
    private String GALLERY_LOCATION = "image gallery";

    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doodle_gallery);

        createImageGallery();

        Intent intent = getIntent();
        mDoodleURIs = intent.getStringArrayListExtra("ListOfDoodleURIs");
        lengthOfArray = mDoodleURIs.size();

        for(int i = 0; i < lengthOfArray; i += 1) {
            mStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(mDoodleURIs.get(i));
            String imageFileName = "DOODLE_" + i;
            try {
                final File image = File.createTempFile(imageFileName, ".png", mGalleryFolder);
                mStorageReference.getFile(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.galleryRecyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1);
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerView.Adapter imageAdapter = new harjoitustyo.projekti.doodlingnotepad.ImageAdapter(mGalleryFolder);
        mRecyclerView.setAdapter(imageAdapter);

        button = (Button) findViewById(R.id.buttonContinueDoodling);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), harjoitustyo.projekti.doodlingnotepad.MainActivity.class);
                startActivity(intent);
            }
        });
    }
    private void createImageGallery() {
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        mGalleryFolder = new File(storageDirectory, GALLERY_LOCATION);
        if (!mGalleryFolder.exists()) {
            mGalleryFolder.mkdirs();
            mGalleryFolder.setExecutable(true);
            mGalleryFolder.setReadable(true);
            mGalleryFolder.setWritable(true);
        }
    }

}


