package zhaos.spaceagegame.ui;

import android.view.View;
import android.widget.RelativeLayout;

import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by kodomazer on 9/25/2016.
 */
public class PositionedElementWrapper<E extends View>{

    protected RelativeLayout parent;
    protected E self;
    protected IntPoint position;
    protected IntPoint size;
    protected RelativeLayout.LayoutParams params;

    public PositionedElementWrapper(RelativeLayout parentView,
                                    E content,
                                    IntPoint position,
                                    IntPoint size){
        parent = parentView;
        self = content;
        this.position = position;
        this.size = size;

        params = new RelativeLayout.LayoutParams(size.x,size.y);

        params.leftMargin = position.x;
        params.topMargin = position.y;

        parent.addView(content, params);
    }

    public void setPosition(IntPoint newPosition){
        position = newPosition;
        params.leftMargin = position.x;
        params.topMargin = position.y;
        parent.refreshDrawableState();
    }


}
