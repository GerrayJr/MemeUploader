package com.gerray.memeuploader;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ImageUploader extends Fragment {

    public ImageUploader() {
        // Required empty public constructor
    }

    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;
    private EditText edName;

    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;
    private StorageTask imUploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_uploader, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button btnSelect = view.findViewById(R.id.btn_imSelect);
        Button btnUpload = view.findViewById(R.id.btn_imUpload);
        edName = view.findViewById(R.id.ed_imUpload);
        imageView = view.findViewById(R.id.image_view);
        TextView viewUploads = view.findViewById(R.id.tv_viewImUploads);

        progressDialog = new ProgressDialog(getView().getContext());
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("Images");
        mStorageReference = FirebaseStorage.getInstance().getReference("Images");

        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imUploadTask != null && imUploadTask.isInProgress())
                {
                    Toast.makeText(getView().getContext(), "Currently Uploading!!", Toast.LENGTH_SHORT).show();
                }else {
                    uploadFile();
                }

            }
        });

        viewUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.with(getView().getContext()).load(imageUri).into(imageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getView().getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (imageView != null) {

            StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            imUploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.setMessage("Uploading...");
                                    progressDialog.setProgress(0);

                                }
                            }, 500);
                            Toast.makeText(getView().getContext(), "Upload Successfully", Toast.LENGTH_SHORT).show();

                            ImageUpload upload = new ImageUpload(edName.getText().toString().trim(), taskSnapshot.getStorage().getDownloadUrl().toString()); //.getMetadata().getReference().getDownloadUrl().toString()

                            String uploadId = mDatabaseReference.getKey();
                            mDatabaseReference.child(uploadId).setValue(upload);
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getView().getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploading Image");
                            progressDialog.setProgress((int) progress);

                        }
                    });

        } else {
            Toast.makeText(getView().getContext(), "No Meme selected", Toast.LENGTH_SHORT).show();
        }

    }
}
