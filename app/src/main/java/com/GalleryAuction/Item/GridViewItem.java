package com.GalleryAuction.Item;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.GalleryAuction.UI.BidderMainActivity;

public class GridViewItem extends GridView {
    boolean expanded = false;
    public GridViewItem(Context context) {
        super(context);
    }

public GridViewItem(Context context, AttributeSet attrs) {
    super(context, attrs);
}
public GridViewItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
}
public boolean isExpanded() {
        return expanded;
}
@Override
public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

    if (isExpanded()) {
    int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    } else {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
public void setExpanded(boolean expanded) {
    this.expanded = expanded;
}


}