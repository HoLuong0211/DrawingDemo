package ominext.com.drawingapplication.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ominext.com.drawingapplication.R;
import ominext.com.drawingapplication.fragment.BrushSizeChooserFragment;
import ominext.com.drawingapplication.listeners.OnNewBrushSizeSelectedListener;
import ominext.com.drawingapplication.view.DrawingView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

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

    @OnClick({R.id.btn_color, R.id.btn_delete, R.id.btn_erase, R.id.btn_pencil, R.id.btn_redo, R.id.btn_change_brush_size, R.id.btn_undo, R.id.btn_image})
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
                break;
            default:
                break;
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
                if (mDrawingView.isDrawMode()) {
                    mBtnPencil.setBackgroundResource(R.drawable.radius_green_border);
                    mBtnErase.setBackgroundResource(R.color.transparent);
                } else {
                    mBtnPencil.setBackgroundResource(R.color.transparent);
                    mBtnErase.setBackgroundResource(R.drawable.radius_green_border);
                }
                mBtnChangeBrushSize.setBackgroundResource(R.color.transparent);
            }
        });
        brushDialog.show(getSupportFragmentManager(), "Dialog");
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
}
