package se.umu.cs.dv16vgn.pictopuzzle;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h1>Position</h1>
 * Position is a coordinate container.
 * <p>
 * x - The x coordinate.
 * <p>
 * y - The y coordinate.
 *
 */

public class Position implements Parcelable {
    private int y;
    private int x;


    /**
     * Generates a new Position with given x and y - coordinate.
     * @param y
     * @param x
     */
    public Position(int y, int x){
        this.y = y;
        this.x = x;
    }

    /**
     * Parceable constructor.
     * @param in Parcel object containing data.
     */

    private Position(Parcel in) {
        y = in.readInt();
        x = in.readInt();
    }

    /**
     * Creator constructor for Parceable.
     */

    public static final Creator<Position> CREATOR = new Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    /**
     * Getter for the x value.
     * @return The x value.
     */
    public int getX() {
        return x;
    }

    /**
     * Getter for the y value.
     * @return The y value.
     */
    public int getY() {
        return y;
    }

    /**
     * Setter for the x value
     * @param x The new x value.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Setter for the y value.
     * @param y The new y value.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Checks if two Positions have the same x and y values.
     * @param object The object to compare to.
     * @return True if given object is a position with equal x and y values.
     */

    @Override
    public boolean equals(Object object){
        return (((Position)object).getX() == x
                && ((Position)object).getY() == y);
    }

    /**
     * Converts the data in Position to a formatted string.
     * @return  A formatted string.
     */

    @Override
    public String toString() {
        return "Position[" + y + ", " + x  +"]";
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
        parcel.writeInt(y);
        parcel.writeInt(x);
    }
}
