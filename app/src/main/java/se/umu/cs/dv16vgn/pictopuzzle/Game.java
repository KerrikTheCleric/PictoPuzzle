package se.umu.cs.dv16vgn.pictopuzzle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * <h1>Game</h1>
 * Class for the game activity. Controls the game.
 * <p>
 * gameGrid - The GameGrid in use.
 * <p>
 * graphicsGrid - The GraphicsGrid in use.
 * <p>
 * photoHandler - The PhotoHandler in use.
 * <p>
 * mPopupWindow - A popup window used to display help text.
 * <p>
 * helpIsVisible - Says if the help window is visible.
 * <p>
 * alertDialog - A single selection dialog window used to select
 * difficulty in Free Play.
 * <p>
 * alertIsVisible - Says if the dialog window is visible or not.
 * <p>
 * gameType - The type of game in play, either (0) Free Play
 * or (1) Arcade Mode.
 * <p>
 * screenHeightOffset - Used to get the proper height of the activity.
 * <p>
 * selectedGridType - The currently selected grid.
 * <p>
 * random - Used to randomize stuff.
 * <p>
 * REQUEST_TAKE_PHOTO - Constant for taking photos.
 * <p>
 * photoPath - The path to the photo.
 * <p>
 * gridSizes - An array containing the different difficulties.
 * <p>
 * arcadeRounds - The current round in Arcade Mode.
 * <p>
 * state - The current state of the game. 0 for pre-photo, 1 for puzzle,
 * 2 for solved puzzle and 3 for completed Arcade Mode.
 *
 */

public class Game extends AppCompatActivity {

    private GameGrid gameGrid;
    private GraphicsGrid graphicsGrid;
    private PhotoHandler photoHandler = new PhotoHandler();

    private PopupWindow mPopupWindow;
    private Boolean helpIsVisible = false;
    private AlertDialog alertDialog;
    private Boolean alertIsVisible = false;

    private int gameType;
    private int screenHeightOffset = 0;
    private int selectedGridType = 0;
    private Random random = new Random();
    static final int REQUEST_TAKE_PHOTO = 1;
    private String photoPath;
    static final String[] gridSizes = {"3x2", "3x3", "4x3",
                                        "4x4", "5x4", "5x5"};
    private static final int arcadeRounds = 3;
    private int state = 0;

    /**
     * Standard Android method.
     * @param savedInstanceState Bundle containing data saved
     *                          from previous life cycle.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().getDecorView().setBackgroundColor(Color.parseColor(
                "#a6ca8f"));

        findViewById(R.id.continueButton).setVisibility(View.INVISIBLE);
        findViewById(R.id.continueButton).setOnClickListener(
                new ContinueListener());
        findViewById(R.id.cameraButton).setOnClickListener(
                new CameraListener());

        if(savedInstanceState != null){
            photoPath = savedInstanceState.getString("path");
            selectedGridType = savedInstanceState.getInt("gridType");
            state = savedInstanceState.getInt("state");
            rebuildState((GameGrid) savedInstanceState.getParcelable(
                    "gameGrid"));
        }else{
            photoHandler.deleteExcessPhotos(getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES));
        }

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayShowTitleEnabled(false);
        }

        Intent intent = getIntent();
        gameType = intent.getIntExtra("type", -1);
        TextView t = findViewById(R.id.round_text);

        if(gameType == 0){
            t.setText(R.string.free_play_title);
        }else if(gameType == 1 && state != 3){
            t.setText(getString(R.string.round, selectedGridType + 1));
        }
    }

    /**
     * Standard Android method.
     * @param menu Menu to inflate.
     * @return True.
     */

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(gameType == 0){
            inflater.inflate(R.menu.free_play_menu, menu);
        }else if(gameType == 1){
            inflater.inflate(R.menu.arcade_mode_menu, menu);
        }
        return true;
    }

    /**
     * Standard Android method.
     * @param item Selected item.
     * @return True or false.
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.options:
                displaySingleSelectionDialog();
                return true;
            case R.id.help:
                if(!helpIsVisible){
                    setUpHelpWindow();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Standard Android method.
     */

    @Override
    public void onPause() {
        super.onPause();
        if (alertIsVisible){
            alertIsVisible = false;
            alertDialog.dismiss();
        }
        if(helpIsVisible){
            helpIsVisible = false;
            mPopupWindow.dismiss();
        }
    }

    /**
     * Standard Android method.
     * @param savedInstanceState Bundle to save data to.
     */

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if(gameGrid != null){
            savedInstanceState.putParcelable("gameGrid", gameGrid);
        }

        switch (state){
            case 0:
                savedInstanceState.putInt("state", 0);
                break;
            case 1:
                savedInstanceState.putInt("state", 1);
                break;
            case 2:
                savedInstanceState.putInt("state", 2);
                break;
            case 3:
                savedInstanceState.putInt("state", 3);
                break;
            default:
                break;
        }

        savedInstanceState.putInt("gridType", selectedGridType);
        savedInstanceState.putString("path", photoPath);
    }

    /**
     * Standard Android method.
     * @param requestCode Request code.
     * @param resultCode Result code.
     * @param data Data.
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data){
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            state = 1;
            Bitmap b = photoHandler.produceBitmapFromPath(photoPath);
            int y = Character.getNumericValue(gridSizes[
                    selectedGridType].charAt(0));
            int x = Character.getNumericValue(gridSizes[
                    selectedGridType].charAt(2));

            prepareNewButtonGrid(y, x,b,false);
            graphicsGrid.getCameraButton().setVisibility(View.INVISIBLE);

        } else if(requestCode == REQUEST_TAKE_PHOTO
                && resultCode == RESULT_CANCELED){
            photoHandler.deleteExcessPhotos(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        }
    }

    /**
     * Rebuilds the state of the game.
     * @param gameGrid Gamegrid from previous life cycle.
     */

    private void rebuildState(GameGrid gameGrid){

        switch (state){
            //Base state, already restored.
            case 0:
                break;
            //Restore puzzle
            case 1:
                findViewById(R.id.cameraButton).setVisibility(View.INVISIBLE);
                this.gameGrid = gameGrid;
                gameGrid.setMarkedTile(null);
                int rows = gameGrid.getRows();
                int columns = gameGrid.getColumns();
                LinearLayout topRow = findViewById(R.id.toprow);
                graphicsGrid = new GraphicsGrid(rows, columns, this);

                setGraphicsGridComponents();

                Bitmap[][] pieces = photoHandler.splitAndResizeBitmap(
                        photoHandler.produceBitmapFromPath(photoPath),
                        gameGrid);
                graphicsGrid.setupNewImagesForButtonGrid(topRow,
                        pieces, gameGrid, false);
                graphicsGrid.setOnClickListenersOnWholeButtonGrid(
                        new TileListener());
                unsolvePuzzle();
                break;
            //Puzzle is solved.
            case 2:
                findViewById(R.id.continueButton).setVisibility(View.VISIBLE);
                findViewById(R.id.cameraButton).setVisibility(View.INVISIBLE);
                Bitmap b = photoHandler.produceBitmapFromPath(photoPath);
                prepareNewButtonGrid(2, 2, b,true);
                break;
            //Arcade Mode has been won.
            case 3:
                findViewById(R.id.cameraButton).setVisibility(View.INVISIBLE);
                TextView t = findViewById(R.id.round_text);
                t.setText(R.string.game_over_text);
                break;
            default:
                break;
        }
    }

    /**
     * Sets the components of the GraphicsGrid.
     */

    public void setGraphicsGridComponents(){
        graphicsGrid.setCameraButton((ImageButton)
                findViewById(R.id.cameraButton));
        graphicsGrid.setContinueButton((Button)
                findViewById(R.id.continueButton));
        graphicsGrid.setRoundText((TextView)
                findViewById(R.id.round_text));
    }

    /**
     * Goes through the grid and collects all the positions without
     * the correct image.
     * @return A list of wrong positions.
     */

    public ArrayList<Position> collectWrongPositions(){
        ArrayList <Position> wrongPositions = new ArrayList<>();
        for(int i = 0; i<gameGrid.getRows(); i++){
            for(int j = 0; j<gameGrid.getColumns(); j++){
                if(!gameGrid.getTiles()[i][j].isImageCorrect()){
                    wrongPositions.add(new Position(i, j));
                }
            }
        }
        return wrongPositions;
    }

    /**
     * Restores the puzzle to the previous unsolved state.
     */

    public void unsolvePuzzle(){
        ArrayList <Position> wrongPositions = collectWrongPositions();
        GameGrid tempGrid = new GameGrid(0, 0, gameGrid.getRows(),
                gameGrid.getColumns());
        GameTile[][] tempTiles = tempGrid.getTiles();
        GameTile[][] oldTiles = gameGrid.getTiles();
        Boolean pos1Match;
        Boolean pos2Match;

        while(!wrongPositions.isEmpty()){
            Position pos1 = wrongPositions.get(0);
            int y1 = pos1.getY();
            int x1 = pos1.getX();
            for(int i = 1; i<wrongPositions.size(); i++){
                Position pos2 = wrongPositions.get(i);
                int y2 = pos2.getY();
                int x2 = pos2.getX();
                graphicsGrid.swapButtonBitmaps(pos1, pos2);
                tempGrid.swapTileImageSourcePositions(pos1, pos2);

                pos1Match = tempTiles[y1][x1].equals(oldTiles[y1][x1]);
                pos2Match = tempTiles[y2][x2].equals(oldTiles[y2][x2]);

                if(!pos1Match && !pos2Match){
                    graphicsGrid.swapButtonBitmaps(pos1, pos2);
                    tempGrid.swapTileImageSourcePositions(pos1, pos2);
                }else if(pos1Match && pos2Match){
                    wrongPositions.remove(pos1);
                    wrongPositions.remove(pos2);
                    break;
                }else if(pos1Match){
                    wrongPositions.remove(pos1);
                    break;
                }else {
                    wrongPositions.remove(pos2);
                    break;
                }
            }
        }
    }

    /**
     * Prepares a new grid, both locical and graphical.
     * @param rows The amount of rows in the grid.
     * @param columns The amount of columns in the grid.
     * @param b The bitmap to make the tiles out of.
     * @param solved Says if the grid should be solved or scrambled.
     */

    public void prepareNewButtonGrid(int rows, int columns, Bitmap b,
                                     boolean solved){
        graphicsGrid = new GraphicsGrid(rows, columns, this);
        LinearLayout topRow = findViewById(R.id.toprow);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        setGraphicsGridComponents();

        display.getSize(size);
        int width = size.x;
        screenHeightOffset = getActionBarHeight(this);
        screenHeightOffset = screenHeightOffset + getStatusBarHeight();
        int height = size.y - screenHeightOffset;

        gameGrid = new GameGrid(width/columns, height/rows, rows, columns);

        Bitmap[][] pieces = photoHandler.splitAndResizeBitmap(b, gameGrid);

        graphicsGrid.setupNewImagesForButtonGrid(topRow, pieces,
                gameGrid, solved);
        if(!solved){
            graphicsGrid.setOnClickListenersOnWholeButtonGrid(
                    new TileListener());
            scrambleTiles(rows, columns);
        }

    }

    /**
     * Gets the height of the status bar.
     * @return  The height of the status bar.
     */

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * Gets the height of the action bar.
     * @param context Context to use.
     * @return The height of the action bar.
     */

    public static int getActionBarHeight(Context context) {
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        TypedArray a = context.obtainStyledAttributes(new TypedValue().data,
                textSizeAttr);
        int height = a.getDimensionPixelSize(0, 0);
        a.recycle();
        return height;
    }

    /**
     * OnClickListener for the camera button. Starts the camera.
     */

    class CameraListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            dispatchTakePictureIntent();
        }
    }

    /**
     * OnClickListener for the continue button. Changes the state of the game.
     */

    class ContinueListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            clearGrid();
            state = 0;
            photoHandler.deleteExcessPhotos(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            graphicsGrid.getContinueButton().setVisibility(View.INVISIBLE);
            graphicsGrid.getCameraButton().setVisibility(View.VISIBLE);

            if(gameType==1 && (selectedGridType + 1) < arcadeRounds){
                selectedGridType++;
                graphicsGrid.getRoundText().setText(
                        getString(R.string.round, selectedGridType + 1));
            }else if(gameType==1){
                graphicsGrid.getCameraButton().setVisibility(View.INVISIBLE);
                graphicsGrid.getRoundText().setText(R.string.game_over_text);
                state = 3;
            }
        }
    }

    /**
     * OnClickListener for the tiles. Allows them to be selected and swapped.
     * Also checks if the puzzle is solved.
     */

    class TileListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int[] pixelPosition = new int[2];
            view.getLocationOnScreen(pixelPosition);
            pixelPosition[1] = pixelPosition[1] - screenHeightOffset;
            Position p = gameGrid.calculateTilePosition(pixelPosition[1],
                    pixelPosition[0]);

            if(gameGrid.getMarkedTile() == null){
                graphicsGrid.markTile((ImageButton) view, true, gameGrid);
                gameGrid.setMarkedTile(p);
            }else if(gameGrid.getMarkedTile().equals(p)){
                graphicsGrid.markTile((ImageButton) view, false, gameGrid);
                gameGrid.setMarkedTile(null);
            } else {
                graphicsGrid.swapButtonBitmaps(gameGrid.getMarkedTile(), p);
                graphicsGrid.markTile(graphicsGrid.getImageButtonAtPosition(p),
                        false, gameGrid);
                gameGrid.swapTileImageSourcePositions(gameGrid.getMarkedTile(), p);
                gameGrid.setMarkedTile(null);

                if(gameGrid.isPuzzleSolved()){
                    Toast.makeText(getApplicationContext(),
                            "Puzzle Solved!",Toast.LENGTH_SHORT).show();
                    clearGrid();
                    graphicsGrid.getContinueButton().setVisibility(View.VISIBLE);
                    Bitmap b = photoHandler.produceBitmapFromPath(photoPath);
                    prepareNewButtonGrid(2, 2, b,true);
                    state = 2;
                }

            }
        }
    }

    /**
     * Clears out the visible grid.
     */

    public void clearGrid(){
        LinearLayout topRow = findViewById(R.id.toprow);
        topRow.removeAllViews();
    }

    /**
     * Scrambles the tiles in a puzzle rows*columns times.
     * @param rows The amount of rows in the puzzle.
     * @param columns The amount of columns in the puzzle.
     */

    public void scrambleTiles(int rows, int columns){
        Position pos1;
        Position pos2;
        int i = 0;

        while(i<(rows*columns)){
            pos1 = gameGrid.getRandomPosition(random);
            pos2 = gameGrid.getRandomPosition(random);

            if(!pos1.equals(pos2)){
                graphicsGrid.swapButtonBitmaps(pos1, pos2);
                gameGrid.swapTileImageSourcePositions(pos1, pos2);
                i++;
            }
        }
    }

    /**
     * Dispatches an intent to start the camera
     */

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = photoHandler.createImageFile(
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            } catch (IOException ex) {
                Toast.makeText(getApplicationContext(),
                        "Error: Failed to create photo file",
                        Toast.LENGTH_SHORT).show();
            }
            if (photoFile != null) {
                photoPath = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    /**
     * Constructs the help-window.
     */

    private void setUpHelpWindow(){
        helpIsVisible = true;
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View helpView;
        if(gameType == 0){
            helpView = inflater.inflate(R.layout.help_popup_free_play,null);
        }else {
            helpView = inflater.inflate(R.layout.help_popup_arcade_mode,null);
        }

        mPopupWindow = new PopupWindow(
                helpView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        ImageButton closeButton = helpView.findViewById(R.id.ib_close);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpIsVisible = false;
                mPopupWindow.dismiss();
            }
        });
        mPopupWindow.showAtLocation( findViewById(R.id.game),
                Gravity.CENTER,0,0);
    }

    /**
     * Displays the selection dialog.
     */

    private void displaySingleSelectionDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Grid");
        dialogBuilder.setSingleChoiceItems(gridSizes, selectedGridType, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                selectedGridType = i;
            }
        });

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                clearGrid();
                findViewById(R.id.continueButton).setVisibility(View.INVISIBLE);
                findViewById(R.id.cameraButton).setVisibility(View.VISIBLE);
                state = 0;
            }
        });


        alertIsVisible = true;
        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
}
