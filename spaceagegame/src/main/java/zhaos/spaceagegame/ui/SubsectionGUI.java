package zhaos.spaceagegame.ui;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import zhaos.spaceagegame.R;
import zhaos.spaceagegame.game.HexSubsection;
import zhaos.spaceagegame.util.IntPoint;

/**
 * Created by bzhao on 10/4/2016.
 */
public class SubsectionGUI implements View.OnClickListener{

    protected RelativeLayout parentLayout;
    protected HexGUI parentHex;
    protected HexSubsection thisSubsection;

    protected IntPoint size;
    protected IntPoint position;

    protected ImageView imageView;



    public SubsectionGUI(RelativeLayout parentLayout,
                         HexGUI parent,
                         HexSubsection subsection,
                         IntPoint size,
                         IntPoint position) {
        this.parentLayout = parentLayout;
        this.parentHex = parent;
        this.thisSubsection = subsection;
        this.size = size;
        this.position = position;

        imageView = new ImageView(parentLayout.getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size.x,size.y);
        params.leftMargin = position.x;
        params.topMargin = position.y;

        switch (thisSubsection.getPosition().i()) {

            case 0:
                imageView.setBackgroundResource(R.mipmap.empty_hex_border_0);
                break;
            case 1:
                imageView.setBackgroundResource(R.mipmap.empty_hex_border_1);
                break;
            case 2:
                imageView.setBackgroundResource(R.mipmap.empty_hex_border_2);
                break;
            case 3:
                imageView.setBackgroundResource(R.mipmap.empty_hex_border_3);
                break;
            case 4:
                imageView.setBackgroundResource(R.mipmap.empty_hex_border_4);
                break;
            case 5:
                imageView.setBackgroundResource(R.mipmap.empty_hex_border_5);
                break;
            default:
                imageView.setBackgroundResource(R.mipmap.empty_hex_white);
                break;
        }
        imageView.setOnClickListener(this);
        parentLayout.addView(imageView,params);



    }

    public void setAsMoveable() {
        imageView.setColorFilter(Color.rgb(100, 255, 100));
    }

    public void setAsBattle() {
        imageView.setColorFilter(Color.rgb(100, 255, 100));
    }

    public void resetBorder() {
        imageView.setColorFilter(Color.rgb(100, 255, 100));
    }

    @Override
    public void onClick(View v) {
        imageView.setColorFilter(Color.argb(255,255,50,50), PorterDuff.Mode.MULTIPLY);
        Log.i("Subsection", "onClick: "+thisSubsection.getPosition().i());
    }
}
