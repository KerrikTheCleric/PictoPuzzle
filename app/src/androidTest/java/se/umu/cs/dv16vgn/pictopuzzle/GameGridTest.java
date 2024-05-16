package se.umu.cs.dv16vgn.pictopuzzle;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameGridTest {

    private GameGrid gg;

    @Before
    public void setUp() {
        gg = new GameGrid(50, 50, 5, 5);
    }

    @Test
    public void testForSwappedTileImageSourcePositions(){
        gg.swapTileImageSourcePositions(new Position(0, 0), new Position(4, 0));

        Position p = new Position(4, 0);

        assertEquals(true, p.equals(gg.getTiles()[0][0].getImageSourcePosition()));

    }

    @Test
    public void testForTripleSwappedTileImageSourcePositions(){
        gg.swapTileImageSourcePositions(new Position(0, 0), new Position(0, 4));
        gg.swapTileImageSourcePositions(new Position(0, 0), new Position(4, 0));

        Position p = new Position(0, 4);

        assertEquals(true, p.equals(gg.getTiles()[4][0].getImageSourcePosition()));

    }

    @Test
    public void testCalculatingPositionOfButton(){
        Position p = new Position(0, 2);
        assertEquals(true, p.equals(gg.calculateTilePosition(0, 100)));
    }

    @Test
    public void testThatNewGridIsCorrect(){
        assertEquals(true, gg.isPuzzleSolved());
    }

    @Test
    public void testThatSwitchedGridIsIncorrect(){
        gg.swapTileImageSourcePositions(new Position(0, 0), new Position(0, 4));

        assertEquals(false, gg.isPuzzleSolved());
    }

    @Test
    public void testThatSwitchedBackGridIsCorrect(){
        gg.swapTileImageSourcePositions(new Position(0, 0), new Position(0, 4));
        gg.swapTileImageSourcePositions(new Position(0, 4), new Position(0, 0));

        assertEquals(true, gg.isPuzzleSolved());
    }


}