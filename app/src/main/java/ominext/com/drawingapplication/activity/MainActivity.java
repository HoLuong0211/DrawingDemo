package ominext.com.drawingapplication.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ominext.com.drawingapplication.R;
import ominext.com.drawingapplication.fragment.BrushSizeChooserFragment;
import ominext.com.drawingapplication.listeners.OnNewBrushSizeSelectedListener;
import ominext.com.drawingapplication.util.ImageUtils;
import ominext.com.drawingapplication.view.DrawingView;
import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_IMAGE = 100;
    public static final String MANUFACTURE_XIAOMI = "Xiaomi";

    @BindView(R.id.drawing_view)
    DrawingView mDrawingView;
    @BindView(R.id.btn_undo)
    FrameLayout mBtnUndo;
    @BindView(R.id.btn_pencil)
    FrameLayout mBtnPencil;
    @BindView(R.id.btn_redo)
    FrameLayout mBtnRedo;
    @BindView(R.id.btn_erase)
    FrameLayout mBtnErase;
    @BindView(R.id.btn_color)
    FrameLayout mBtnColor;
    @BindView(R.id.btn_delete)
    FrameLayout mBtnDelete;
    @BindView(R.id.btn_image)
    FrameLayout mBtnImage;
    @BindView(R.id.btn_change_brush_size)
    FrameLayout mBtnChangeBrushSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_color, R.id.btn_delete, R.id.btn_erase, R.id.btn_pencil, R.id.btn_redo,
            R.id.btn_change_brush_size, R.id.btn_undo, R.id.btn_image, R.id.btn_save, R.id.btn_share})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_pencil:
                resetMenuBackground();
                mBtnPencil.setBackgroundResource(R.drawable.radius_green_border);
                mDrawingView.setDrawMode(true);
                break;
            case R.id.btn_erase:
                resetMenuBackground();
                mBtnErase.setBackgroundResource(R.drawable.radius_green_border);
                mDrawingView.setDrawMode(false);
                break;
            case R.id.btn_color:
                resetMenuBackground();
                mBtnColor.setBackgroundResource(R.drawable.radius_green_border);
                pickColor();
                break;
            case R.id.btn_delete:
                resetMenuBackground();
                mBtnDelete.setBackgroundResource(R.drawable.radius_green_border);
                deleteDialog();
                break;
            case R.id.btn_undo:
                resetMenuBackground();
                mDrawingView.undo();
                mBtnPencil.setBackgroundResource(R.drawable.radius_green_border);
                break;
            case R.id.btn_redo:
                resetMenuBackground();
                mDrawingView.redo();
                mBtnPencil.setBackgroundResource(R.drawable.radius_green_border);
                break;
            case R.id.btn_change_brush_size:
                resetMenuBackground();
                mBtnChangeBrushSize.setBackgroundResource(R.drawable.radius_green_border);
                brushSizePicker();
                break;
            case R.id.btn_image:
                resetMenuBackground();
                mBtnImage.setBackgroundResource(R.drawable.radius_green_border);
                chooseImageFromGallery();
                break;
            case R.id.btn_save:
                break;
            case R.id.btn_share:
                shareDrawing();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_IMAGE) {
            if (data != null && data.getData() != null) {
                String imagePath = ImageUtils.getPath(this, data.getData());
                Bitmap myBitmap = BitmapFactory.decodeFile(imagePath);
                Bitmap rotatedBitmap = null;
                try {
                    rotatedBitmap = ImageUtils.modifyOrientation(myBitmap, imagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (rotatedBitmap != null) {
                    if (rotatedBitmap.getWidth() >= rotatedBitmap.getHeight()) {
                        rotatedBitmap = Bitmap.createBitmap(
                                rotatedBitmap,
                                rotatedBitmap.getWidth() / 2 - rotatedBitmap.getHeight() / 2,
                                0,
                                rotatedBitmap.getHeight(),
                                rotatedBitmap.getHeight()
                        );
                    } else {
                        rotatedBitmap = Bitmap.createBitmap(
                                rotatedBitmap,
                                0,
                                rotatedBitmap.getHeight() / 2 - rotatedBitmap.getWidth() / 2,
                                rotatedBitmap.getWidth(),
                                rotatedBitmap.getWidth()
                        );
                    }
                    mDrawingView.setBackgroundImage(rotatedBitmap);
                } else {
                    if (myBitmap != null) {
                        mDrawingView.setBackgroundImage(myBitmap);
                    }
                }
            }
            setDrawModeBackground();
            mBtnImage.setBackgroundResource(R.color.transparent);
        }
    }

    private void deleteDialog() {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle("Delete drawing");
        deleteDialog.setMessage("New Drawing?");
        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mDrawingView.eraseAll();
                dialog.dismiss();
            }
        });
        deleteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        deleteDialog.show();
    }

    private void brushSizePicker() {
        //Implement get/set brush size
        BrushSizeChooserFragment brushDialog = BrushSizeChooserFragment.newInstance((int) mDrawingView.getLastBrushSize());
        brushDialog.setOnNewBrushSizeSelectedListener(new OnNewBrushSizeSelectedListener() {
            @Override
            public void onNewBrushSizeSelected(float newBrushSize) {
                mDrawingView.setBrushSize(newBrushSize);
                mDrawingView.setLastBrushSize(newBrushSize);
                setDrawModeBackground();
                mBtnChangeBrushSize.setBackgroundResource(R.color.transparent);
            }
        });
        brushDialog.show(getSupportFragmentManager(), "Dialog");
    }

    private void pickColor() {
        AmbilWarnaDialog colorPickerDialog = new AmbilWarnaDialog(this, mDrawingView.getPaintColor(), true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                setDrawModeBackground();
                mBtnColor.setBackgroundResource(R.color.transparent);
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                mDrawingView.setPaintColor(color);
                setDrawModeBackground();
                mBtnColor.setBackgroundResource(R.color.transparent);
            }
        });
        colorPickerDialog.show();
    }

    private void chooseImageFromGallery() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
        } else {
            String manufacturer = Build.MANUFACTURER;
            Log.i(TAG, "====>manufacturer:" + manufacturer);
            if (MANUFACTURE_XIAOMI.equals(manufacturer)) {
                Intent intent = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*"});
                startActivityForResult(
                        Intent.createChooser(intent, getString(R.string.choose_picture)),
                        REQUEST_CODE_CHOOSE_IMAGE);
            } else {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*",});
                startActivityForResult(intent, REQUEST_CODE_CHOOSE_IMAGE);
            }
        }
    }

    private void shareDrawing() {
        mDrawingView.setDrawingCacheEnabled(true);
        mDrawingView.invalidate();
        String path = Environment.getExternalStorageDirectory().toString();
        OutputStream fOut = null;
        File file = new File(path,
                "android_drawing_app.png");
        file.getParentFile().mkdirs();

        try {
            file.createNewFile();
        } catch (Exception e) {
            Log.e(TAG, e.getCause() + e.getMessage());
        }

        try {
            fOut = new FileOutputStream(file);
        } catch (Exception e) {
            Log.e(TAG, e.getCause() + e.getMessage());
        }

        if (mDrawingView.getDrawingCache() == null) {
            Log.e(TAG, "Unable to get drawing cache ");
        }

        mDrawingView.getDrawingCache()
                .compress(Bitmap.CompressFormat.JPEG, 85, fOut);

        try {
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e(TAG, e.getCause() + e.getMessage());
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "Share image"));
    }


    private void resetMenuBackground() {
        mBtnColor.setBackgroundResource(R.color.transparent);
        mBtnDelete.setBackgroundResource(R.color.transparent);
        mBtnErase.setBackgroundResource(R.color.transparent);
        mBtnPencil.setBackgroundResource(R.color.transparent);
        mBtnRedo.setBackgroundResource(R.color.transparent);
        mBtnUndo.setBackgroundResource(R.color.transparent);
        mBtnChangeBrushSize.setBackgroundResource(R.color.transparent);
        mBtnImage.setBackgroundResource(R.color.transparent);
    }

    private void setDrawModeBackground() {
        if (mDrawingView.isDrawMode()) {
            mBtnPencil.setBackgroundResource(R.drawable.radius_green_border);
            mBtnErase.setBackgroundResource(R.color.transparent);
        } else {
            mBtnPencil.setBackgroundResource(R.color.transparent);
            mBtnErase.setBackgroundResource(R.drawable.radius_green_border);
        }
    }
}
