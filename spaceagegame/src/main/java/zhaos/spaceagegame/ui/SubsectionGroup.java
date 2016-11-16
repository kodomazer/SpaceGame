package zhaos.spaceagegame.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.v4.util.Pools;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import zhaos.spaceagegame.util.HHexDirection;

/**
 * Created by kodomazer on 10/15/2016.
 */

public class SubsectionGroup extends RelativeLayout {
    private static String TAG = "Subsection Group";
    private static Context context;

    protected SubsectionGUI[] subsections;

    protected LayoutParams layoutParams;
    protected HexGUI parentHex;
    protected boolean drawn;

     private static final Pools.SynchronizedPool<SubsectionGroup> sPool =
             new Pools.SynchronizedPool<SubsectionGroup>(50);
    private Point hexPosition;

    public static SubsectionGroup obtain() {
        SubsectionGroup instance = sPool.acquire();
         return (instance != null) ? instance : new SubsectionGroup(context);
    }

    public void recycle() {
          // Clear state if needed.
          sPool.release(this);
    }

    static void setContext(Context context){
        SubsectionGroup.context = context;
    }

    private SubsectionGroup(Context context) {
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

    public Point getHexPosition() {
        return hexPosition;
    }
}
