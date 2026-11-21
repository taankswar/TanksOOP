package com.tanks.model;

/**
 * Позиция на игровом поле.
 * @param x координата по горизонтали (0 — слева, 19 — справа)
 * @param y координата по вертикали (0 — сверху, 19 — снизу)
 */
public record Position(int x, int y) {

    /**
     * Возвращает новую позицию, смещённую в указанном направлении.
     * @param direction направление смещения
     * @return новая позиция
     */
    public Position plus(Direction direction) {
        return new Position(x + direction.dx(), y + direction.dy());
    }

    /**
     * Проверяет, находится ли позиция внутри поля заданного размера.
     * @param width ширина поля
     * @param height высота поля
     * @return true, если позиция внутри поля
     */
    public boolean isInside(int width, int height) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}