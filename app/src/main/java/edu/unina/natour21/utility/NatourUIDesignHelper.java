package edu.unina.natour21.utility;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.text.TextPaint;
import android.widget.Button;
import android.widget.TextView;

public class NatourUIDesignHelper {

    public NatourUIDesignHelper() {

    }

    public void setTextGradient(TextView textView) {

        textView.setTextColor(Color.parseColor("#1A759F"));

        TextPaint textPaint = textView.getPaint();
        float textWidth = textPaint.measureText(textView.getText().toString());

        Shader textShader = new LinearGradient(0, 0, textWidth, textView.getTextSize(),
                new int[] {
                        Color.parseColor("#1A759F"),
                        Color.parseColor("#339DA1"),
                        Color.parseColor("#6EB988")
                }, null, Shader.TileMode.CLAMP);

        textView.getPaint().setShader(textShader);
    }

    public void setTextGradient(Button button) {

        button.setTextColor(Color.parseColor("#1A759F"));

        TextPaint textPaint = button.getTextMetricsParams().getTextPaint();
        float textWidth = textPaint.measureText(button.getText().toString());

        Shader textShader = new LinearGradient(0, 0, textWidth, button.getTextSize(),
                new int[] {
                        Color.parseColor("#1A759F"),
                        Color.parseColor("#339DA1"),
                        Color.parseColor("#6EB988")
                }, null, Shader.TileMode.CLAMP);

        button.getTextMetricsParams().getTextPaint().setShader(textShader);
    }

}
