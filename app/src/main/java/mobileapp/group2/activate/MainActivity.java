package mobileapp.group2.activate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import mobileapp.group2.activate.DoodleGalleryActivity;
import mobileapp.group2.activate.R;


public class MainActivity extends AppCompatActivity {

    private mobileapp.group2.activate.PaintView paintView;
    private mobileapp.group2.activate.PaintView mImageContainer;
    private Button mSaveDoodleButton;
    private Button mMoveToGalleryButton;
    private ProgressBar mUploadProgressBar;
    private Date mThisDate;

    private ArrayList <String> mDoodleURIs;

    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = (mobileapp.group2.activate.PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

        mUploadProgressBar = findViewById(R.id.upload_progress_bar);
        mImageContainer = findViewById(R.id.paintView);

        //mTextView = findViewById(R.id.textView);
        mThisDate = Calendar.getInstance().getTime();
        mDoodleURIs = new ArrayList<>();

        mSaveDoodleButton = (Button) findViewById(R.id.button);
        mMoveToGalleryButton = (Button) findViewById(R.id.button_to_gallery);

        mSaveDoodleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap capture = Bitmap.createBitmap(
                        mImageContainer.getWidth(),
                        mImageContainer.getHeight(),
                        Bitmap.Config.ARGB_8888);
                Canvas captureCanvas = new Canvas(capture);
                mImageContainer.draw(captureCanvas);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                capture.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                byte[] data = outputStream.toByteArray();

                String path = "myDoodles/" + UUID.randomUUID() + ".png";
                final StorageReference doodlesRef = storage.getReference(path);

                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setCustomMetadata("date-time", mThisDate.toString())
                        .build();

                UploadTask uploadTask = doodlesRef.putBytes(data, metadata);

                mUploadProgressBar.setVisibility(View.VISIBLE);
                mSaveDoodleButton.setEnabled(false);

                uploadTask.addOnCompleteListener(MainActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Log.i("MA", "Upload task complete!");
                        mUploadProgressBar.setVisibility(View.GONE);
                        paintView.clear();
                        mSaveDoodleButton.setEnabled(true);
                    }
                });

                Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(
                        new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful()){
                                    throw task.getException();
                                }
                                return doodlesRef.getDownloadUrl();
                            }
                        }
                );

                getDownloadUriTask.addOnCompleteListener(MainActivity.this, new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if(task.isSuccessful()){
                            Uri downloadUri = task.getResult();

                            mDoodleURIs.add(downloadUri.toString());


                        }
                    }
                });
            }
        });


    }
    public void onClick(View view) {
        Intent intent = new Intent(this, DoodleGalleryActivity.class);
        intent.putStringArrayListExtra("ListOfDoodleURIs", mDoodleURIs);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.normal:
            paintView.normal();
            return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}