package zhaos.spaceagegame.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 10/15/2016.
 */

public class SubsectionGroup extends RelativeLayout {
    protected SubsectionGUI[] subsections;

    protected LayoutParams layoutParams;
    protected HexGUI parentHex;
    protected boolean drawn;


    public SubsectionGroup(Context context) {
        super(context);

        subsections = new SubsectionGUI[7];
        for(int i =0;i<7;i++){
            subsections[i] = new SubsectionGUI(this, HHexDirection.getDirection(i));
        }

        drawn = false;
        parentHex = null;


    }

    public void addToView(LayoutParams layout,HexGUI parentHex){
       this.parentHex = parentHex;
        layoutParams=new LayoutParams(layout);
        layoutParams.leftMargin = 0;
        layoutParams.topMargin = 0;

        for(SubsectionGUI subsection:subsections){

            addView(subsection,layoutParams);
        }

        drawn=true;
    }

    public void clearState(){
        for(SubsectionGUI subsection:subsections){
            subsection.resetBorder();
        }
    }
    public void removed(){
        drawn = false;
        removeAllViews();
    }
}
