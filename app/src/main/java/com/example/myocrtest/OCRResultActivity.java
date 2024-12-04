package com.example.myocrtest;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.text.Text;

import java.util.ArrayList;

public class OCRResultActivity extends AppCompatActivity {
    public static final String EXTRA_TEXT_BLOCKS = "extra_text_blocks";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocrresult);

        ImageView imageView = findViewById(R.id.resultImageView);

        // データを受け取る
        ArrayList<CustomTextBlock> textBlocks = getIntent().getParcelableArrayListExtra(EXTRA_TEXT_BLOCKS);

        // 結果を描画
        if (textBlocks != null) {
            imageView.setImageBitmap(drawBoundingBoxes(textBlocks));
        }
    }

    private Bitmap drawBoundingBoxes(ArrayList<CustomTextBlock> textBlocks) {
        int canvasWidth = 1080 * 2;
        int canvasHeight = 1920 * 2;
        Bitmap bitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);

        Paint boxPaint = new Paint();
        //boxPaint.setColor(Color.RED);
        boxPaint.setStyle(Paint.Style.STROKE);
        //boxPaint.setStrokeWidth(5);

        int min_top  = Integer.MAX_VALUE;
        int min_left = Integer.MAX_VALUE;
        for (CustomTextBlock block : textBlocks) {
            Rect bbox = block.getBoundingBox();
            if (bbox.top < min_top) {
                min_top = bbox.top;
            }
            if (bbox.left < min_left) {
                min_left = bbox.left;
            }
        }

        for (CustomTextBlock block : textBlocks) {
            Rect boundingBox = block.getBoundingBox();
            Rect shiftedBox = new Rect(boundingBox.left - min_left + 10, boundingBox.top - min_top,
                    boundingBox.right - min_left + 10, boundingBox.bottom - min_top);
            if (boundingBox != null) {
                switch (block.getText()) {
                    case "<block>":
                        boxPaint.setColor(Color.GREEN);
                        boxPaint.setStrokeWidth(7);
                        canvas.drawRect(shiftedBox, boxPaint);
                        break;
                    case "<line>":
                        boxPaint.setColor(Color.BLUE);
                        boxPaint.setStrokeWidth(5);
                        canvas.drawRect(shiftedBox, boxPaint);
                        break;
                    default:
                        canvas.drawText(block.getText(), shiftedBox.left + 10, shiftedBox.top + 40, textPaint);
                        boxPaint.setColor(Color.RED);
                        boxPaint.setStrokeWidth(3);
                        canvas.drawRect(shiftedBox, boxPaint);
                }
            }
        }

        return bitmap;
    }
}
