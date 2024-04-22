package my.edu.utar.task_andassignment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ShowTimetable extends AppCompatActivity {

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final String IMAGE_DIRECTORY = "/timetable_images";

    private Spinner spinner;
    private ImageView imageView;
    private Button uploadButton;
    private Button deleteButton;
    private String selectedPhotoName;
    private List<String> photoNames = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_timetable);

        spinner = findViewById(R.id.spinner);
        imageView = findViewById(R.id.imageView);
        uploadButton = findViewById(R.id.uploadButton);
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setEnabled(false);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, photoNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPhotoName = photoNames.get(position);
                if (!selectedPhotoName.equals("Select Timetable")) {
                    loadPhoto(selectedPhotoName);
                } else {
                    imageView.setImageResource(android.R.color.transparent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Set a default image to the ImageView
        imageView.setImageResource(R.drawable.upload_timetable);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            showNameInputDialog(imageUri);
        }
    }

    private void showNameInputDialog(final Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Year and Month");

        // Inflate the layout containing the dropdown spinners
        View view = getLayoutInflater().inflate(R.layout.dialog_select_year_month, null);
        builder.setView(view);

        final Spinner yearSpinner = view.findViewById(R.id.yearSpinner);
        final Spinner monthSpinner = view.findViewById(R.id.monthSpinner);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String year = yearSpinner.getSelectedItem().toString();
                String month = monthSpinner.getSelectedItem().toString();
                String photoName = year + " " + month;
                processImage(imageUri, photoName);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void processImage(Uri imageUri, final String photoName) {
//        if (isImageNameExists(photoName)) {
//            showToast("Image name already exists. Please choose a different month or year.");
//            return;
//        }

        saveImageToLocal(imageUri, photoName);

        showToast("Upload successful");

        // Update the spinner with the new photo name
        photoNames.add(photoName);
        spinnerAdapter.notifyDataSetChanged();

        // Set the currently displayed image to the newly uploaded one
        loadPhoto(photoName);

        // Set the spinner selection to the newly uploaded image
        spinner.setSelection(photoNames.indexOf(photoName));
        updateDeleteButtonEnabled();
    }

//    private boolean isImageNameExists(String photoName) {
//        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + IMAGE_DIRECTORY);
//        File imageFile = new File(directory, photoName + ".jpg");
//        return imageFile.exists();
//    }

    private void saveImageToLocal(Uri imageUri, String photoName) {
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + IMAGE_DIRECTORY);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File file = new File(directory, photoName + ".jpg");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fos != null) {
                        fos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadPhoto(String photoName) {
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + IMAGE_DIRECTORY);
        File imageFile = new File(directory, photoName + ".jpg");
        if (imageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(android.R.color.transparent);
            showToast("Failed to load image");
        }
    }
    public void onDeleteButtonClick(View view) {
        if (!photoNames.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Image");
            builder.setMessage("Are you sure you want to delete this image?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int selectedIndex = spinner.getSelectedItemPosition();
                    if (selectedIndex != AdapterView.INVALID_POSITION) {
                        String selectedPhotoName = photoNames.get(selectedIndex);
                        //boolean deleted = deleteImage(selectedPhotoName);
                        if (deleteImage(selectedPhotoName)) {
                            photoNames.remove(selectedIndex);
                            if (!photoNames.isEmpty()) {
                                int lastImageIndex = photoNames.size() - 1;
                                spinner.setSelection(lastImageIndex);
                                String lastPhotoName = photoNames.get(lastImageIndex);
                                loadPhoto(lastPhotoName);
                            } else {
                                imageView.setImageDrawable(null);
                            }
                            spinnerAdapter.notifyDataSetChanged();
                            Toast.makeText(ShowTimetable.this,
                                    "Image deleted successfully", Toast.LENGTH_SHORT).show();
                            updateDeleteButtonEnabled();
                            if (photoNames.size() == 0) {
                                // Enable the delete button
                                imageView.setImageResource(R.drawable.upload_timetable);
                            }

                        } else {
                            Toast.makeText(ShowTimetable.this,
                                    "Failed to delete image", Toast.LENGTH_SHORT).show();
                            updateDeleteButtonEnabled();
                        }
                    }
                }
            });
            builder.setNegativeButton("No", null);
            builder.show();

        } else {
            Toast.makeText(this, "No image to delete", Toast.LENGTH_SHORT).show();
            if (photoNames.size() == 0) {
                // Enable the delete button
                imageView.setImageResource(R.drawable.upload_timetable);
            }
        }
    }

    private boolean deleteImage(String photoName) {
        // Define the directory using the correct path
        File directory = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + IMAGE_DIRECTORY);
        // Construct the file path of the image to be deleted
        File imageFile = new File(directory, photoName + ".jpg");
        // Attempt to delete the image file
        return imageFile.delete();
    }

    private void updateDeleteButtonEnabled() {
        // Check if there are any images
        if (photoNames.size() > 0) {
            // Enable the delete button
            deleteButton.setEnabled(true);
        } else {
            // Disable the delete button
            deleteButton.setEnabled(false);
        }
    }


    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
