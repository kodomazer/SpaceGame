package zhaos.spaceagegame.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import zhaos.spaceagegame.R;
import zhaos.spaceagegame.game.GameHexTile;
import zhaos.spaceagegame.game.HexSubsection;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by bzhao on 10/4/2016.
 */
public class HexGUI implements View.OnClickListener {

    protected RelativeLayout parent;
    protected GameHexTile hexTile;
    protected IntPoint position;
    protected IntPoint size;
    protected RelativeLayout.LayoutParams params;

    protected RelativeLayout hexLayout;
    protected ImageView imageView;
    protected SubsectionGUI[] subsections;

    public HexGUI(RelativeLayout parentView,
                  GameHexTile content,
                  IntPoint position,
                  IntPoint size) {
        //copy pertinent information over
        parent = parentView;
        hexTile = content;
        this.position = position;
        this.size = size;
        //increase the size a little to remove gaps
        this.size.translate(1, 1);

        //define layout params for absolute positioning on the canvas
        params = new RelativeLayout.LayoutParams(size.x, size.y);
        params.leftMargin = position.x;
        params.topMargin = position.y;

        //make a new canvas for the subsections to live in
        hexLayout = new RelativeLayout(parentView.getContext());
        hexLayout.setBackgroundResource(R.mipmap.empty_hex);

        //add the canvas for the hex to the main view
        parentView.addView(hexLayout, params);

        /*
        //initialize subsection GameGUIActivity array
        HexSubsection[] subsections = hexTile.getSubsections();
        this.subsections = new SubsectionGUI[subsections.length];
        for (HexSubsection s : subsections) {
            this.subsections[s.getPosition().i()]
                    = new SubsectionGUI(hexLayout,
                    this,
                    s,
                    this.size,
                    new IntPoint());
        }//*/

    }

    public void setPosition(IntPoint newPosition) {
        position = newPosition;
        params.leftMargin = position.x;
        params.topMargin = position.y;
        parent.refreshDrawableState();
    }

    @Override
    public void onClick(View v) {
        GameGUIActivity.getInstance().setInfoText(hexTile);
    }

    public void updateScale(float newScale) {
        //update our copy of the parameters
        params.leftMargin = (int)(position.x*newScale);
        params.topMargin = (int)(position.y*newScale);
        params.width = (int)(size.x*newScale)+1;
        params.height = (int)(size.y*newScale)+1;
        //update parameters in the layout
        parent.updateViewLayout(hexLayout,params);
    }

}
