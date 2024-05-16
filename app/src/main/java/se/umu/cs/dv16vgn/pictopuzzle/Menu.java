package se.umu.cs.dv16vgn.pictopuzzle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/*
* Menu (Decide game type) -> Game scene with camera button -> Camera -> Puzzle View -> Loop if free mode/Result if arcade
*
* -Help in menu for arcade mode.
* -Put text in string resources.
* -Make icon.
* -Add options in action bar?
* -Fix button icons
* -Write help text for freeplay and arcade modes.
* -Keep empty action bar in menu?
* -Swap image work to new thread!!!!
* -Code limit 80
* -Make methods private.
* -Rewrite meta data tag.
* -Reload state after pause?
* -Use don't keep activity to test remaking the grid
* -rotate landscape pics (stretching averted, nämn som begränsning)
* -Add reset warning for Free Play.
* -Disable menu button during work!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
* -Refactor excessive double code.
* -Delete photo before new one is saved.
* -Nämn begränsningar och utvecklingsmöjligheter(felaktig rotation på två sorters bilder, ingen tidsgräns och high score.)
* -Dialog glitch!
*
*
*
* */

/**
 * <h1>Menu</h1>
 * Class for the menu activity. Let's you pick game mode.
 */

public class Menu extends AppCompatActivity {

    /**
     * Standard Android method.
     * @param savedInstanceState Bundle containing data saved
     *                          from previous life cycle.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        prepareButtons();
    }

    /**
     * Sets up the two buttons of the menu.
     */

    private void prepareButtons(){
        Button b = findViewById(R.id.free_button);
        b.setOnClickListener(new FreeListener());

        b = findViewById(R.id.arcade_button);
        b.setOnClickListener(new ArcadeListener());
    }

    /**
     * OnClickListener for the Free Play button. Starts Free Play mode.
     */
    class FreeListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent myIntent = new Intent(Menu.this, Game.class);
            myIntent.putExtra("type", 0);
            Menu.this.startActivity(myIntent);
        }
    }

    /**
     * OnClickListener for the Arcade Mode button. Starts Arcade Mode.
     */
    class ArcadeListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            Intent myIntent = new Intent(Menu.this, Game.class);
            myIntent.putExtra("type", 1);
            Menu.this.startActivity(myIntent);
        }
    }
}
