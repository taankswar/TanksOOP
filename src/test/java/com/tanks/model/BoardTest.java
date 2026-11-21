package com.tanks.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void testCreateEmptyBoard() {
        Board board = new Board(20, 20);

        assertEquals(20, board.getWidth());
        assertEquals(20, board.getHeight());

        // Все клетки должны быть пустыми
        for (int x = 0; x < 20; x++) {
            for (int y = 0; y < 20; y++) {
                assertEquals(Board.EMPTY, board.getCell(x, y));
            }
        }
    }

    @Test
    void testSetAndGetCell() {
        Board board = new Board(20, 20);

        board.setCell(5, 5, Board.OBSTACLE);
        assertEquals(Board.OBSTACLE, board.getCell(5, 5));

        board.setCell(10, 10, Board.COVER);
        assertEquals(Board.COVER, board.getCell(10, 10));
    }

    @Test
    void testIsInside() {
        Board board = new Board(20, 20);

        assertTrue(board.isInside(0, 0));
        assertTrue(board.isInside(19, 19));
        assertTrue(board.isInside(10, 10));

        assertFalse(board.isInside(-1, 0));
        assertFalse(board.isInside(0, -1));
        assertFalse(board.isInside(20, 0));
        assertFalse(board.isInside(0, 20));
    }

    @Test
    void testIsPassable() {
        Board board = new Board(20, 20);

        // Пустая клетка — проходима
        assertTrue(board.isPassable(5, 5));

        // Укрытие — проходимо
        board.setCell(5, 5, Board.COVER);
        assertTrue(board.isPassable(5, 5));

        // Препятствие — непроходимо
        board.setCell(5, 5, Board.OBSTACLE);
        assertFalse(board.isPassable(5, 5));

        // За пределами поля — непроходимо
        assertFalse(board.isPassable(-1, 0));
        assertFalse(board.isPassable(20, 0));
    }

    @Test
    void testBlocksShot() {
        Board board = new Board(20, 20);

        // Пустая клетка — не блокирует
        assertFalse(board.blocksShot(5, 5));

        // Укрытие — не блокирует
        board.setCell(5, 5, Board.COVER);
        assertFalse(board.blocksShot(5, 5));

        // Препятствие — блокирует
        board.setCell(5, 5, Board.OBSTACLE);
        assertTrue(board.blocksShot(5, 5));

        // За пределами поля — блокирует
        assertTrue(board.blocksShot(-1, 0));
    }

    @Test
    void testOutOfBoundsThrowsException() {
        Board board = new Board(20, 20);

        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> board.getCell(0, 20));
        assertThrows(IndexOutOfBoundsException.class, () -> board.setCell(-1, 0, Board.OBSTACLE));
    }

    @Test
    void testPositionPlusDirection() {
        Position pos = new Position(10, 10);

        assertEquals(new Position(10, 9), pos.plus(Direction.UP));
        assertEquals(new Position(10, 11), pos.plus(Direction.DOWN));
        assertEquals(new Position(9, 10), pos.plus(Direction.LEFT));
        assertEquals(new Position(11, 10), pos.plus(Direction.RIGHT));
    }

    @Test
    void testPositionIsInside() {
        Position pos = new Position(10, 10);

        assertTrue(pos.isInside(20, 20));
        assertFalse(pos.isInside(10, 10));
        assertFalse(pos.isInside(5, 5));
    }

    @Test
    void testDirectionOpposite() {
        assertEquals(Direction.DOWN, Direction.UP.opposite());
        assertEquals(Direction.UP, Direction.DOWN.opposite());
        assertEquals(Direction.RIGHT, Direction.LEFT.opposite());
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite());
    }
}