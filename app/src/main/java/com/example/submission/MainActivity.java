package com.example.submission;

import static android.content.ContentValues.TAG;
import static java.lang.Math.abs;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private ImageView imageView;
    private ImageView imageView2;
    private Bitmap bitmap;
    private Button button;
    private Button cropButton;
    private Button rotateButton;
    private Button flipButton;
    private Button overlayButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView extBtn = findViewById(R.id.exitBtn);

        extBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory( Intent.CATEGORY_HOME );
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });

        imageView = findViewById(R.id.imageView);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showPngOverlayDialog();
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });
        cropButton = findViewById(R.id.cropBtn);
        cropButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap == null) {
                    Log.d(TAG, "processImage: bitmap empty");
                }
                cropImage();
                imageView.setImageBitmap(bitmap);
            }
        });
        flipButton = findViewById(R.id.flipBtn);
        flipButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmap == null) {
                    Log.d(TAG, "processImage: bitmap empty");
                }
                flipImage();
                imageView.setImageBitmap(bitmap);
            }
        });
        rotateButton = findViewById(R.id.rotatesBtn);
        rotateButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (bitmap == null) {
                    Log.d(TAG, "processImage: bitmap empty");
                    return;
                }
                rotateImage();
                imageView.setImageBitmap(bitmap);
            }
        });
        overlayButton = findViewById(R.id.overlayBtn);
        overlayButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                showPngOverlayDialog();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void rotateImage() {
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private void cropImage() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = 500;
        int newHeight = 500;
        int left = (width - newWidth) / 2;
        int top = (height - newHeight) / 2;
        int right = (width + newWidth) / 2;
        int bottom = (height + newHeight) / 2;
        bitmap = Bitmap.createBitmap(bitmap, left, top, abs(right - left), abs(bottom - top));
    }

    private void flipImage() {
        Matrix matrix = new Matrix();
        matrix.preScale(-1.0f, 1.0f);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    private void showPngOverlayDialog() {
        final String[] overlayNames = getResources().getStringArray(R.array.png_overlay_names);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.png_overlay_dialog, null);
        builder.setView(dialogView);
        ListView pngOverlayList = dialogView.findViewById(R.id.png_overlay_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, overlayNames);
        pngOverlayList.setAdapter(adapter);
        builder.setTitle("Select Overlay");
        final AlertDialog dialog = builder.create();
        pngOverlayList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String overlayName = overlayNames[position];
                applyPngFilter(overlayName);
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }



    private void applyPngFilter(@NonNull String overlayName) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap pngOverlayTemp = null;
        Bitmap pngOverlay = null;
        switch (overlayName) {
            case "user_image_frame_1":
                pngOverlayTemp = BitmapFactory.decodeResource(getResources(), R.drawable.user_image_frame_1);
                pngOverlay = pngOverlayTemp.copy(Bitmap.Config.ARGB_8888, true);
                break;
            case "user_image_frame_2":
                pngOverlayTemp = BitmapFactory.decodeResource(getResources(), R.drawable.user_image_frame_2);
                pngOverlay = pngOverlayTemp.copy(Bitmap.Config.ARGB_8888, true);
                break;
            case "user_image_frame_3":
                pngOverlayTemp = BitmapFactory.decodeResource(getResources(), R.drawable.user_image_frame_3);
                pngOverlay = pngOverlayTemp.copy(Bitmap.Config.ARGB_8888, true);
                break;
            case "user_image_frame_4":
                pngOverlayTemp = BitmapFactory.decodeResource(getResources(), R.drawable.user_image_frame_4);
                pngOverlay = pngOverlayTemp.copy(Bitmap.Config.ARGB_8888, true);
                break;
        }

        if (pngOverlay == null) {
            return;
        }

        try{
            pngOverlay = invertBitmap(pngOverlay);
        }
        catch (Exception e){
            Log.d(TAG, "applyPngFilter: "+e);
        }

        int overlayWidth = pngOverlay.getWidth();
        int overlayHeight = pngOverlay.getHeight();
        float scaleFactor = Math.min((float) width / overlayWidth, (float) height / overlayHeight);

        Bitmap scaledOverlay = Bitmap.createScaledBitmap(pngOverlay,
                (int) (overlayWidth * scaleFactor), (int) (overlayHeight * scaleFactor), false);

        // Create a new canvas to draw the overlay on top of the original image
        Bitmap mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutable);
        canvas.drawBitmap(scaledOverlay, 0, 0, null);
        imageView2 = findViewById(R.id.imageView2);
        imageView2.setImageBitmap(mutable);

        scaledOverlay.recycle();
        pngOverlay.recycle();
    }

    public static Bitmap invertBitmap(Bitmap bitmap) {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        for (int x = 0; x < bitmap.getWidth(); x++) {
            for (int y = 0; y < bitmap.getHeight(); y++) {
                int pixel = bitmap.getPixel(x, y);
                int alpha = Color.alpha(pixel);

                if (alpha == 255) {
                    pixel = Color.argb(0, Color.red(pixel), Color.green(pixel), Color.blue(pixel));
                }
                else {
                    pixel = Color.argb(255, Color.red(pixel), Color.green(pixel), Color.blue(pixel));
                }

                newBitmap.setPixel(x, y, pixel);
            }
        }

        return newBitmap;
    }

    }

