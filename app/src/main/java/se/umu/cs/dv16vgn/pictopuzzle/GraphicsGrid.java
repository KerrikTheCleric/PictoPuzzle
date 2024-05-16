package se.umu.cs.dv16vgn.pictopuzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * <h1>GraphicsGrid</h1>
 * Governs graphical elements.
 * <p>
 * buttonGrid - The grid of image buttons.
 * <p>
 * cameraButton - The camera button.
 * <p>
 * continueButton - The continue button.
 * <p>
 * roundText - The text displaying the current round or just "Free Play".
 * <p>
 * rows - The amount of rows in the grid.
 * <p>
 * columns - The amount of columns in the grid.
 * <p>
 * linearLayoutList - A list of LinearLayouts used to build up the
 * grid graphically.
 *
 */

public class GraphicsGrid {

    private ImageButton [][] buttonGrid;
    private ImageButton cameraButton;
    private Button continueButton;
    private TextView roundText;
    private int rows;
    private int columns;
    private LinearLayout linearLayoutList[];

    /**
     * Standard constructor.
     * @param rows The amount of rows in the grid.
     * @param columns The amount of columns in the grid.
     * @param context The Context needed to make graphical elements.
     */

    public GraphicsGrid(int rows, int columns, Context context){

        this.buttonGrid = new ImageButton[rows][columns];
        this.rows = rows;
        this.columns = columns;
        this.linearLayoutList = new LinearLayout[rows];

        for(int i = 0; i<rows; i++){
            for(int j = 0; j<columns; j++){
                buttonGrid[i][j] = new ImageButton(context);
                buttonGrid[i][j].setBackgroundColor(Color.TRANSPARENT);
                buttonGrid[i][j].setPadding(0, 0, 0, 0);
            }
        }
        for (int i = 0; i<rows; i++){
            linearLayoutList[i] = new LinearLayout(context);
            linearLayoutList[i].setLayoutParams(
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT
                            ,LinearLayout.LayoutParams.MATCH_PARENT));
            linearLayoutList[i].setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout.LayoutParams params =
                    (LinearLayout.LayoutParams)
                            linearLayoutList[i].getLayoutParams();
            linearLayoutList[i].setLayoutParams(params);
        }
    }

    /**
     * Prepares the button grid with new images.
     * @param topRow The LinearLayout that holds the grid.
     * @param bmMatrix A matrix of bitmaps with the images the grid needs.
     * @param gameGrid Contains information on tile sizes.
     * @param solved Says if the fresh grid should be drawn with tile
     *              markers or not.
     */

    public void setupNewImagesForButtonGrid(LinearLayout topRow,
                                            Bitmap[][] bmMatrix,
                                            GameGrid gameGrid,
                                            boolean solved){
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<columns; j++){
                buttonGrid[i][j].setImageBitmap(bmMatrix[j][i]);

                if(!solved){
                    markTile(buttonGrid[i][j], false, gameGrid);
                }
                linearLayoutList[i].addView(buttonGrid[i][j]);
            }
        }
        for(int i = 0; i<rows; i++){
            topRow.addView(linearLayoutList[i]);
        }
    }

    /**
     * Sets OnClickListeners on all image buttons in the grid.
     * @param listener The listener to set.
     */

    public void setOnClickListenersOnWholeButtonGrid(View.OnClickListener
                                                             listener){
        for(int i = 0; i<rows; i++){
            for(int j = 0; j<columns; j++){
                buttonGrid[i][j].setOnClickListener(listener);
            }
        }
    }

    /**
     * Marks an image button so it looks like a tile.
     * @param button The button to mark.
     * @param markAsSelected Decides what colour to use.
     * @param gameGrid Contains information on tile sizes.
     */

    public void markTile(ImageButton button, Boolean markAsSelected,
                         GameGrid gameGrid){

        Bitmap temp1 = ((BitmapDrawable) button.getDrawable()).getBitmap();
        Canvas canvas = new Canvas(temp1);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if(markAsSelected){
            paint.setColor(Color.GREEN);
        }else{
            paint.setColor(Color.BLACK);
        }

        paint.setStrokeWidth(10);
        canvas.drawLine(0, 0, 0, gameGrid.getTileHeight(), paint);
        canvas.drawLine(0, 0, gameGrid.getTileWidth(), 0, paint);
        canvas.drawLine(gameGrid.getTileWidth(), 0, gameGrid.getTileWidth(),
                gameGrid.getTileHeight(), paint);
        canvas.drawLine(0, gameGrid.getTileHeight(), gameGrid.getTileWidth(),
                gameGrid.getTileHeight(), paint);

        button.setImageBitmap(temp1);
    }

    /**
     * Swaps the images on two image buttons.
     * @param button1 A button to swap with.
     * @param button2 A button to swap with.
     */

    public void swapButtonBitmaps(Position button1, Position button2){
        Bitmap temp1 = ((BitmapDrawable) buttonGrid[button1.getY()]
                [button1.getX()].getDrawable()).getBitmap();
        Bitmap temp2 = ((BitmapDrawable) buttonGrid[button2.getY()]
                [button2.getX()].getDrawable()).getBitmap();

        buttonGrid[button1.getY()][button1.getX()].setImageBitmap(temp2);
        buttonGrid[button2.getY()][button2.getX()].setImageBitmap(temp1);
    }

    /**
     * Setter.
     * @param cameraButton New camera button.
     */

    public void setCameraButton(ImageButton cameraButton) {
        this.cameraButton = cameraButton;
    }

    /**
     * Setter.
     * @param continueButton New continue button.
     */

    public void setContinueButton(Button continueButton) {
        this.continueButton = continueButton;
    }

    /**
     * Setter.
     * @param roundText New round TextView.
     */

    public void setRoundText(TextView roundText) {
        this.roundText = roundText;
    }

    /**
     * Getter for specific image button in grid.
     * @param p The Position to return an image button from.
     * @return The desired image button.
     */

    public ImageButton getImageButtonAtPosition(Position p){
        return buttonGrid[p.getY()][p.getX()];

    }

    /**
     * Getter.
     * @return Camera button.
     */

    public ImageButton getCameraButton() {
        return cameraButton;
    }

    /**
     * Getter.
     * @return Continue button.
     */

    public Button getContinueButton() {
        return continueButton;
    }

    /**
     * Getter.
     * @return Round TextView.
     */

    public TextView getRoundText() {
        return roundText;
    }
}
