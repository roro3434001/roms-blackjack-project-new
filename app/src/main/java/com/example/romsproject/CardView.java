package com.example.romsproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

public class CardView extends View {
    private String rank;
    private String suit;
    private Drawable cardDrawable;

    // Constructors
    public CardView(Context context) {
        super(context);
    }

    public CardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    // Set the card's rank and suit
    public void setCard(String rank, String suit) {
        this.rank = rank.toLowerCase();
        this.suit = suit.toLowerCase();
        loadCardImage();
    }

    // Load the correct card image
    private void loadCardImage() {
        String imageName = rank + "_of_" + suit;  // Ex: "ace_of_spades"
        int resId = getResources().getIdentifier(imageName, "drawable", getContext().getPackageName());

        if (resId != 0) {
            cardDrawable = ContextCompat.getDrawable(getContext(), resId);
        } else {
            cardDrawable = ContextCompat.getDrawable(getContext(), R.drawable.card_back);  // Default to card back if not found
        }

        invalidate();  // Redraw the view with the new image
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = (int) (width * 1.5);  // Standard card ratio 2:3 (Width : Height)
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (cardDrawable != null) {
            cardDrawable.setBounds(0, 0, getWidth(), getHeight());
            cardDrawable.draw(canvas);
        }
    }
}
