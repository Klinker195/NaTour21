package edu.unina.natour21.utility;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.view.WindowCompat;

import edu.unina.natour21.R;

public class NatourUIDesignHandler {

    public NatourUIDesignHandler() {
        super();
    }

    public void setFullscreen(Activity activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
    }

    public void setTextGradient(TextView textView) {

        textView.setTextColor(Color.parseColor("#1A759F"));

        TextPaint textPaint = textView.getPaint();
        Log.i("Text length text", String.valueOf(textPaint.measureText(textView.getText().toString())));
        Log.i("Text length text", String.valueOf(textView.getTextSize()));
        float textWidth = textPaint.measureText(textView.getText().toString());

        Shader textShader = new LinearGradient(0, 0, textWidth, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#1A759F"),
                        Color.parseColor("#339DA1"),
                        Color.parseColor("#6EB988")
                }, null, Shader.TileMode.CLAMP);

        textView.getPaint().setShader(textShader);
    }

    public void setTextGradient(Button button) {

        button.setTextColor(Color.parseColor("#1A759F"));

        TextPaint textPaint = button.getPaint();
        float textWidth = textPaint.measureText(button.getText().toString());

        Shader textShader = new LinearGradient(0, 0, textWidth, button.getTextSize(),
                new int[]{
                        Color.parseColor("#1A759F"),
                        Color.parseColor("#339DA1"),
                        Color.parseColor("#6EB988")
                }, null, Shader.TileMode.CLAMP);

        button.getPaint().setShader(textShader);
    }

    public void setTextGradientTextViewButton(TextView textView) {
        textView.setBackgroundResource(R.drawable.natour_button_background);

        textView.setTextColor(Color.parseColor("#1A759F"));

        TextPaint textPaint = textView.getPaint();
        Log.i("Text length text", String.valueOf(textPaint.measureText(textView.getText().toString())));
        Log.i("Text length text", String.valueOf(textView.getTextSize()));
        float textWidth = textPaint.measureText(textView.getText().toString());

        Shader textShader = new LinearGradient(0, 0, textWidth, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#1A759F"),
                        Color.parseColor("#339DA1"),
                        Color.parseColor("#6EB988")
                }, null, Shader.TileMode.CLAMP);

        textView.getPaint().setShader(textShader);
    }

    public void setTextGradientTextViewButtonInverse(TextView textView) {
        textView.setBackgroundResource(R.drawable.natour_gradient_button_background);
        textView.setTextColor(Color.WHITE);
        textView.getPaint().setShader(null);
    }

    public void setTextGradientTextViewLeftButton(TextView textView) {
        textView.setBackgroundResource(R.drawable.natour_half_button_background_left);

        textView.setTextColor(Color.parseColor("#1A759F"));

        TextPaint textPaint = textView.getPaint();
        Log.i("Text length text", String.valueOf(textPaint.measureText(textView.getText().toString())));
        Log.i("Text length text", String.valueOf(textView.getTextSize()));
        float textWidth = textPaint.measureText(textView.getText().toString());

        Shader textShader = new LinearGradient(0, 0, textWidth, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#1A759F"),
                        Color.parseColor("#339DA1"),
                        Color.parseColor("#6EB988")
                }, null, Shader.TileMode.CLAMP);

        textView.getPaint().setShader(textShader);
    }

    public void setTextGradientTextViewLeftButtonInverse(TextView textView) {
        textView.setBackgroundResource(R.drawable.natour_half_gradient_button_background_left);
        textView.setTextColor(Color.WHITE);
        textView.getPaint().setShader(null);
    }

    public void setTextGradientTextViewRightButton(TextView textView) {
        textView.setBackgroundResource(R.drawable.natour_half_button_background_right);

        textView.setTextColor(Color.parseColor("#1A759F"));

        TextPaint textPaint = textView.getPaint();
        Log.i("Text length text", String.valueOf(textPaint.measureText(textView.getText().toString())));
        Log.i("Text length text", String.valueOf(textView.getTextSize()));
        float textWidth = textPaint.measureText(textView.getText().toString());

        Shader textShader = new LinearGradient(0, 0, textWidth, textView.getTextSize(),
                new int[]{
                        Color.parseColor("#1A759F"),
                        Color.parseColor("#339DA1"),
                        Color.parseColor("#6EB988")
                }, null, Shader.TileMode.CLAMP);

        textView.getPaint().setShader(textShader);
    }

    public void setTextGradientTextViewRightButtonInverse(TextView textView) {
        textView.setBackgroundResource(R.drawable.natour_half_gradient_button_background_right);
        textView.setTextColor(Color.WHITE);
        textView.getPaint().setShader(null);
    }

}
