package com.tripbuddy.callbacks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.tripbuddy.Adapter;
import com.tripbuddy.R;

import org.jetbrains.annotations.NotNull;

public class SwipeToEditCallback extends ItemTouchHelper.SimpleCallback {
    Adapter adapter;
    Drawable icon;
    Paint clearPaint;
    final int iconHeight;
    final int iconWidth;
    final ColorDrawable background;

    public SwipeToEditCallback(Adapter adapter) {
        super(0, ItemTouchHelper.RIGHT);
        this.adapter = adapter;
        icon = ContextCompat.getDrawable(adapter.getContext(), R.drawable.ic_pencil);
        iconHeight = iconWidth = 120;
        background = new ColorDrawable(ContextCompat.getColor(adapter.getContext(), R.color.quantum_teal));
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public boolean onMove(@NonNull @NotNull RecyclerView recyclerView,
                          @NonNull @NotNull RecyclerView.ViewHolder viewHolder,
                          @NonNull @NotNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        adapter.editItem(position);
    }

    @Override
    public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView,
                            @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        if (dX == 0 && !isCurrentlyActive) {
            clearCanvas(c, itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() - (int) dX, itemView.getBottom());
            return;
        }

        int backgroundCornerOffset = 20;

        if (dX > 0) { // swiping right
            int iconMargin = (itemHeight - iconHeight) / 2;
            int iconTop = itemView.getTop() + (itemHeight - iconHeight) / 2;
            int iconBottom = iconTop + iconHeight;
            int iconRight = itemView.getLeft() + iconMargin + iconWidth;
            int iconLeft = itemView.getLeft() + iconMargin;

            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            icon.setTint(Color.WHITE);

            background.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
        } else {
            background.setBounds(0, 0, 0, 0);
        }
        background.draw(c);
        icon.draw(c);
    }

    private void clearCanvas(Canvas c, int left, int top, int right, int bottom) {
        c.drawRect(left, top, right, bottom, clearPaint);
    }

    @Override
    public float getSwipeThreshold(@NonNull @NotNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }
}
