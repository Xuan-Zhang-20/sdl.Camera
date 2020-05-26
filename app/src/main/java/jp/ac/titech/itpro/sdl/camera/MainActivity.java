package jp.ac.titech.itpro.sdl.camera;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final static int REQ_PHOTO = 1234;
    private Bitmap photoImage = null;
    private String photoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button photoButton = findViewById(R.id.photo_button);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // TODO: You should setup appropriate parameters for the intent

                PackageManager manager = getPackageManager();
                List activities = manager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                if (!activities.isEmpty()) {
                    File photoFile=null;
                    try {
                        photoFile=createPhotoFile();
                    }
                    catch (IOException ex) {
                        Toast.makeText(MainActivity.this, "Cannot create photo file",Toast.LENGTH_LONG).show();
                        return;
                    }
                    if(photoFile != null) {
                        Uri photoUri= FileProvider.getUriForFile(MainActivity.this,"jp.ac.titech.itpro.sdl.fileprovider",photoFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                        startActivityForResult(intent, REQ_PHOTO);
                }
                } else {
                    Toast.makeText(MainActivity.this, R.string.toast_no_activities, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private File createPhotoFile() throws IOException {
        String timeStamp=new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        String photoFileName=""+timeStamp;
        File storageDir=getExternalFilesDir((Environment.DIRECTORY_DCIM));
        File photo=File.createTempFile(photoFileName,".jpg",storageDir);
        photoPath=photo.getAbsolutePath();
        return photo;
    }

    private void showPhoto() {
        if (photoImage == null) {
            return;
        }
        ImageView photoView = findViewById(R.id.photo_view);
        photoView.setImageBitmap(photoImage);
    }

    @Override
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        super.onActivityResult(reqCode, resCode, data);
        if (reqCode == REQ_PHOTO) {
            if (resCode == RESULT_OK) {
                // TODO: You should implement the code that retrieve a bitmap image
                File photo=new File(photoPath);
                Uri photoUri=Uri.fromFile(photo);
                try {
                    photoImage=MediaStore.Images.Media.getBitmap(getContentResolver(),photoUri);
                }
                catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPhoto();
    }
}