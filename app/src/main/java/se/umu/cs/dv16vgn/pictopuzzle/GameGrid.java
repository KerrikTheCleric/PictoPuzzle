package se.umu.cs.dv16vgn.pictopuzzle;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Random;

/**
 * <h1>GameGrid</h1>
 * Governs grid logic.
 * <p>
 * tileWidth - The width of tiles.
 * <p>
 * tileHeight - The height of tiles.
 * <p>
 * rows - The amount of rows in the grid.
 * <p>
 * columns - The amount of columns in the grid.
 * <p>
 * tiles - A matrix of GameTiles the build up the grid.
 * <p>
 * markedTile - The position of the currently marked tile.
 *
 */

public class GameGrid implements Parcelable{

    private int tileWidth;
    private int tileHeight;
    private int rows;
    private int columns;
    private GameTile tiles[][];
    private Position markedTile = null;

    /**
     * Standard constructor.
     * @param tileWidth The width of tiles.
     * @param tileHeight The height of tiles.
     * @param rows The amount of rows in the grid.
     * @param columns The amount of columns in the grid.
     */

    public GameGrid (int tileWidth, int tileHeight, int rows, int columns){
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.rows = rows;
        this.columns = columns;
        this.tiles = new GameTile[rows][columns];

        for(int i = 0; i<rows; i++){
            for (int j = 0; j<columns; j++){
                tiles[i][j] = new GameTile(i, j);
            }
        }
    }

    /**
     * Parceable constructor.
     * @param in Parcel object containing data.
     */

    private GameGrid(Parcel in) {
        this.tileWidth = in.readInt();
        this.tileHeight = in.readInt();
        this.rows = in.readInt();
        this.columns = in.readInt();

        this.tiles = new GameTile[rows][];
        for (int i = 0; i < rows; i++) {
            this.tiles[i] = in.createTypedArray(GameTile.CREATOR);
        }
        this.markedTile = in.readParcelable(Position.class.getClassLoader());
    }

    /**
     * Creator constructor for Parceable.
     */

    public static final Creator<GameGrid> CREATOR = new Creator<GameGrid>() {
        @Override
        public GameGrid createFromParcel(Parcel in) {
            return new GameGrid(in);
        }

        @Override
        public GameGrid[] newArray(int size) {
            return new GameGrid[size];
        }
    };

    /**
     * Swaps the image source positions of two tiles.
     * @param p1 A tile to swap with.
     * @param p2 A tile to swap with.
     */

    public void swapTileImageSourcePositions(Position p1, Position p2){

        GameTile tile1 = tiles[p1.getY()][p1.getX()];
        GameTile tile2 = tiles[p2.getY()][p2.getX()];

        Position temp = new Position(tile1.getImageSourcePosition().getY(),
                tile1.getImageSourcePosition().getX());
        tile1.setImageSourcePosition(tile2.getImageSourcePosition());
        tile2.setImageSourcePosition(temp);
    }

    /**
     * Checks if the puzzle is solved.
     * @return Returns true if the puzzle is solved, false otherwise.
     */

    public Boolean isPuzzleSolved(){
        for(int i = 0; i<rows; i++){
            for (int j = 0; j<columns; j++){
                if(!tiles[i][j].isImageCorrect()){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Calculates the position of a tile with its pixel position data.
     * @param y Y pixel position.
     * @param x X pixel position.
     * @return The calculated position.
     */

    public Position calculateTilePosition(int y, int x){
        return new Position(y/tileHeight, x/tileWidth);
    }

    /**
     * Gets a random position from the grid.
     * @param random The seed to randomize with.
     * @return A random position from the grid.
     */

    public Position getRandomPosition(Random random){
        return new Position(random.nextInt(rows), random.nextInt(columns));
    }

    /**
     * Getter.
     * @return The tile matrix.
     */

    public GameTile[][] getTiles() {
        return tiles;
    }

    /**
     * Getter.
     * @return The height of tiles.
     */

    public int getTileHeight() {
        return tileHeight;
    }

    /**
     * Getter.
     * @return The width of tiles.
     */

    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * Getter.
     * @return The position of the marked tile.
     */

    public Position getMarkedTile() {
        return markedTile;
    }

    /**
     * Getter.
     * @return The amount of rows.
     */

    public int getRows() {
        return rows;
    }

    /**
     * Getter.
     * @return The amount of columns.
     */

    public int getColumns() {
        return columns;
    }

    /**
     * Setter.
     * @param markedTile The position of the new marked tile.
     */

    public void setMarkedTile(Position markedTile) {
        this.markedTile = markedTile;
    }

    /**
     * Parceable method.
     * @return 0
     */

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes data to a parcel object.
     * @param parcel The Parcel to write to.
     * @param i Not used.
     */

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(tileWidth);
        parcel.writeInt(tileHeight);
        parcel.writeInt(rows);
        parcel.writeInt(columns);

        for (int j = 0; j < rows; j++) {
            parcel.writeTypedArray(tiles[j], i);
        }

        parcel.writeParcelable(markedTile, i);
    }
}

