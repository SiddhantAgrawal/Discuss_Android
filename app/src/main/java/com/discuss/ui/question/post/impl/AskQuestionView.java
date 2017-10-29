package com.discuss.ui.question.post.impl;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.discuss.DiscussApplication;
import com.discuss.datatypes.Category;
import com.discuss.ui.question.post.QuestionPostPresenter;
import com.example.siddhantagrawal.check_discuss.R;

import java.io.IOException;

import javax.inject.Inject;

public class AskQuestionView extends AppCompatActivity {

    static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    static final int GALARY_CAPTURE_IMAGE_REQUEST_CODE = 200;

    private volatile Bitmap imageToUpload;
    Spinner dropdownSpinner;
    @Inject
    QuestionPostPresenter questionPostPresenter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((DiscussApplication) getApplication()).getMainComponent().inject(this);
        setContentView(R.layout.ask_question);
        FloatingActionButton addQuestion = (FloatingActionButton) findViewById(R.id.ask_question_add);
        Button addImageButton = (Button) findViewById(R.id.ask_question_choose_image);
        questionPostPresenter.
                getCategories().
                first().
                doOnNext(categories -> {
                    final Spinner categoryDropDown = (Spinner) findViewById(R.id.ask_question_tag_button);
                    categoryDropDown.setAdapter(new ArrayAdapter<Category>(AskQuestionView.this, android.R.layout.simple_list_item_1, categories));
                    addImageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            selectImage();
                        }
                    });
                    addQuestion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (imageToUpload != null)
                            Toast.makeText(AskQuestionView.this, "your question has been added successfully for the category : " + dropdownSpinner.getSelectedItem().toString(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                });
    }

    void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Add photo");
        alertDialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result = Utility.checkPermission(AskQuestionView.this);
                if (items[item].equals("Take Photo")) {
                    if (result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    if (result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        alertDialogBuilder.show();
    }

    void cameraIntent() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALARY_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALARY_CAPTURE_IMAGE_REQUEST_CODE)
                this.imageToUpload = onSelectFromGalleryResult(data);
            else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE)
                this.imageToUpload = onCaptureImageResult(data);
        }
    }

    public void reSize(Bitmap bitmap) {
        /* TODO(Deepak):  need to make all images uniform */
    }

    private Bitmap onCaptureImageResult(Intent data) {
        Bundle extras = data.getExtras();
        return (Bitmap) extras.get("data");
        //imageBitmap.compress()
    }

    private Bitmap onSelectFromGalleryResult(Intent data) {
        try {
            return MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    static class Utility {
        public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

        public static boolean checkPermission(final Context context) {
            int currentAPIVersion = Build.VERSION.SDK_INT;
            if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                        alertBuilder.setCancelable(true);
                        alertBuilder.setTitle("Permission necessary");
                        alertBuilder.setMessage("External storage permission is necessary");
                        alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
                        AlertDialog alert = alertBuilder.create();
                        alert.show();
                    } else {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
    }
}
