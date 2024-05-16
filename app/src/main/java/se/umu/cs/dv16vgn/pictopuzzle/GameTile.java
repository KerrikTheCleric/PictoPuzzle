package se.umu.cs.dv16vgn.pictopuzzle;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

/**
 * <h1>GameTile</h1>
 * Represents the positional data of a tile.
 * <p>
 * actualPosition - The position of the tile.
 * <p>
 * imageSourcePosition - The source position of the image on the tile.
 *
 */

public class GameTile implements Parcelable{
    private Position actualPosition;
    private Position imageSourcePosition;

    /**
     * Standard constructor.
     * @param row The row of the tile.
     * @param column The column of the tile.
     */

    public GameTile (int row, int column){
        this.actualPosition = new Position(row, column);
        this.imageSourcePosition = new Position(row, column);
    }

    /**
     * Parceable constructor.
     * @param in Parcel object containing data.
     */

    private GameTile(Parcel in) {
        this.actualPosition =
                in.readParcelable(Position.class.getClassLoader());
        this.imageSourcePosition =
                in.readParcelable(Position.class.getClassLoader());
    }

    /**
     * Creator constructor for Parceable.
     */

    public static final Creator<GameTile> CREATOR = new Creator<GameTile>(){
        @Override
        public GameTile createFromParcel(Parcel in) {
            return new GameTile(in);
        }

        @Override
        public GameTile[] newArray(int size) {
            return new GameTile[size];
        }
    };

    /**
     * Checks if the correct image is on the tile.
     * @return True if it's the correct image, false otherwise.
     */

    public Boolean isImageCorrect(){
        if(actualPosition.equals(imageSourcePosition)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Getter.
     * @return The image source position.
     */

    public Position getImageSourcePosition() {
        return imageSourcePosition;
    }

    /**
     * Setter.
     * @param imageSourcePosition The new The image source position.
     */

    public void setImageSourcePosition(Position imageSourcePosition) {
        this.imageSourcePosition = imageSourcePosition;
    }

    /**
     * Checks if two tiles are identical.
     * @param o The object to compare to.
     * @return True if given object is an equal tile.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameTile)) return false;
        GameTile gameTile = (GameTile) o;
        return Objects.equals(actualPosition, gameTile.actualPosition) &&
                Objects.equals(imageSourcePosition,
                        gameTile.imageSourcePosition);
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
        parcel.writeParcelable(actualPosition, i);
        parcel.writeParcelable(imageSourcePosition, i);
    }
}
