package zhaos.spaceagegame.ui;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import zhaos.spaceagegame.R;
import zhaos.spaceagegame.game.SpaceGameHexSubsection;
import zhaos.spaceagegame.util.HHexDirection;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by bzhao on 10/4/2016.
 */
public class SubsectionGUI extends ImageView implements View.OnClickListener{

    protected SubsectionGroup parentLayout;
    protected HHexDirection thisSubsection;


    public SubsectionGUI(SubsectionGroup parentLayout,HHexDirection direction) {
        super(parentLayout.getContext());
    int direction;

    protected ImageView imageView;


    public SubsectionGUI(RelativeLayout parentLayout){
        super(parentLayout.getContext());


    }

    public SubsectionGUI(RelativeLayout parentLayout,
                         HexGUI parent,
                         SpaceGameHexSubsection subsection,
                         IntPoint size,
                         IntPoint position) {
        super(parentLayout.getContext());
        this.parentLayout = parentLayout;
        thisSubsection = direction;

        setOnClickListener(this);
    }

        imageView = new ImageView(parentLayout.getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size.x,size.y);
        params.leftMargin = position.x;
        params.topMargin = position.y;

    public void setImage(int subsection){
        switch (subsection) {

        imageView.setOnClickListener(this);
        parentLayout.addView(imageView,params);



    }

    private void setImage(int i){
        switch (i) {

            case 0:
                setBackgroundResource(R.mipmap.empty_hex_border_0);
                break;
            case 1:
                setBackgroundResource(R.mipmap.empty_hex_border_1);
                break;
            case 2:
                setBackgroundResource(R.mipmap.empty_hex_border_2);
                break;
            case 3:
                setBackgroundResource(R.mipmap.empty_hex_border_3);
                break;
            case 4:
                setBackgroundResource(R.mipmap.empty_hex_border_4);
                break;
            case 5:
                setBackgroundResource(R.mipmap.empty_hex_border_5);
                break;
            default:
                setBackgroundResource(R.mipmap.empty_hex_white);
                break;
        }
    }

    public void updateDraw(){

    public void setSubsection(int subsection) {
        direction = subsection;
        setImage(subsection);
    }

    public void setAttachedHex(HexGUI hex){
        parentHex = hex;
        thisSubsection= parentHex.hexTile.getSubsection(HHexDirection.getDirection(direction));
        setAsMoveable();
        if(thisSubsection.getAffiliation()==-1)
            setAsBattle();
    }


    public void setAsMoveable() {
        setColorFilter(Color.rgb(100, 255, 100));
    }

    public void setAsBattle() {
        setColorFilter(Color.rgb(100, 255, 100));
    }

    public void resetBorder() {
        setColorFilter(Color.rgb(100, 255, 100));
    }

    @Override
    public void onClick(View v) {
        setColorFilter(Color.argb(255,255,50,50), PorterDuff.Mode.MULTIPLY);
    }
}
