package cn.geoff.dailyrecite;

import android.content.Context;  
import android.graphics.Paint;  
import android.util.AttributeSet;  
import android.util.TypedValue;
import android.widget.TextView;  
  
public class AutoSizeTextView extends TextView{  
    private static float MIN_TEXT_SIZE = 1;  
    private static float MAX_TEXT_SIZE = 48;  
  
    // Attributes  
    private Paint testPaint; 
    private float minTextSize, maxTextSize;
  
    public AutoSizeTextView(Context context, AttributeSet attrs){  
        super(context, attrs);        
        
        testPaint = new Paint();  
        testPaint.set(this.getPaint());  
      
        maxTextSize = this.getTextSize();  
        if (maxTextSize <= MIN_TEXT_SIZE){  
            maxTextSize = MAX_TEXT_SIZE;  
        }
        minTextSize = MIN_TEXT_SIZE;
    } 

    private void adjustText(String text, int textWidth){  
        if (textWidth > 0){  
            int availableWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();  
            float trySize = maxTextSize;  
            testPaint.setTextSize(trySize);  
            while ((trySize > minTextSize) && (testPaint.measureText(text) > availableWidth)){  
                trySize -= 1;  
                if (trySize <= minTextSize){  
                    trySize = minTextSize;  
                    break;  
                }  
                testPaint.setTextSize(trySize);  
            }  
            setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
        }  
    };  
    @Override  
    protected void onTextChanged(CharSequence text, int start, int before,  
            int after) {  
        super.onTextChanged(text, start, before, after);  
        adjustText(text.toString(), this.getWidth());  
    }  
  
    @Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
        if (w != oldw) {  
            adjustText(this.getText().toString(), w);  
        }  
    }  
}