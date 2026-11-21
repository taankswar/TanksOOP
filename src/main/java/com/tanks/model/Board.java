package com.tanks.model;

/**
 * Игровое поле с препятствиями и укрытиями.
 * Поле хранится как двумерный массив целых чисел.
 */
public class Board {

    /** Пустая клетка — можно проезжать */
    public static final int EMPTY = 0;

    /** Укрытие — можно проезжать, даёт бонус к броне */
    public static final int COVER = 1;

    /** Непроходимое препятствие — блокирует движение и выстрелы */
    public static final int OBSTACLE = 2;

    private final int width;
    private final int height;
    private final int[][] grid;

    /**
     * Создаёт пустое поле заданного размера.
     * @param width ширина поля
     * @param height высота поля
     */
    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new int[height][width];
        // По умолчанию все клетки пустые (0)
    }

    /**
     * Возвращает тип клетки в указанной позиции.
     * @param pos позиция на поле
     * @return тип клетки (EMPTY, COVER, OBSTACLE)
     * @throws IndexOutOfBoundsException если позиция за пределами поля
     */
    public int getCell(Position pos) {
        return getCell(pos.x(), pos.y());
    }

    /**
     * Возвращает тип клетки по координатам.
     * @param x координата по горизонтали
     * @param y координата по вертикали
     * @return тип клетки
     */
    public int getCell(int x, int y) {
        if (!isInside(x, y)) {
            throw new IndexOutOfBoundsException("Позиция (" + x + ", " + y + ") за пределами поля");
        }
        return grid[y][x];
    }

    /**
     * Устанавливает тип клетки в указанной позиции.
     * @param pos позиция на поле
     * @param cellType тип клетки
     */
    public void setCell(Position pos, int cellType) {
        setCell(pos.x(), pos.y(), cellType);
    }

    /**
     * Устанавливает тип клетки по координатам.
     * @param x координата по горизонтали
     * @param y координата по вертикали
     * @param cellType тип клетки
     */
    public void setCell(int x, int y, int cellType) {
        if (!isInside(x, y)) {
            throw new IndexOutOfBoundsException("Позиция (" + x + ", " + y + ") за пределами поля");
        }
        grid[y][x] = cellType;
    }

    /**
     * Проверяет, находится ли позиция внутри поля.
     * @param x координата по горизонтали
     * @param y координата по вертикали
     * @return true, если позиция внутри поля
     */
    public boolean isInside(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Проверяет, можно ли проехать через клетку.
     * @param x координата по горизонтали
     * @param y координата по вертикали
     * @return true, если клетка пустая или укрытие
     */
    public boolean isPassable(int x, int y) {
        if (!isInside(x, y)) {
            return false;
        }
        int cell = grid[y][x];
        return cell == EMPTY || cell == COVER;
    }

    /**
     * Проверяет, блокирует ли клетка линию огня.
     * @param x координата по горизонтали
     * @param y координата по вертикали
     * @return true, если клетка блокирует выстрел
     */
    public boolean blocksShot(int x, int y) {
        if (!isInside(x, y)) {
            return true;
        }
        return grid[y][x] == OBSTACLE;
    }

    /**
     * Возвращает ширину поля.
     * @return ширина поля
     */
    public int getWidth() {
        return width;
    }

    /**
     * Возвращает высоту поля.
     * @return высота поля
     */
    public int getHeight() {
        return height;
    }
}