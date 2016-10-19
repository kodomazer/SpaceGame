package zhaos.spaceagegame;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import zhaos.spaceagegame.ui.GameGUIActivity;

public class Games extends Activity {
    Games a = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_options);
        findViewById(R.id.start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(a,GameGUIActivity.class);
                //newIntent.setComponent(new ComponentName(".ui","GameGUIActivity"));
                newIntent.setAction("SPACE");
                newIntent.putExtra("EXTRA_BOARD_SIZE",
                        Integer.parseInt(((EditText)findViewById(R.id.size)).
                                getText().toString()));
                newIntent.putExtra("EXTRA_TEAM_COUNT",
                        Integer.parseInt(((EditText)findViewById(R.id.teams)).
                                getText().toString()));
                startActivity(newIntent);
            }
        });
    }

}
