package com.tanks.model;

import java.util.Random;

/**
 * Генератор игрового поля.
 * Создает карту с укрытиями и препятствиями на основе переданного генератора случайных чисел.
 */
public class MapGenerator {

    /**
     * Генерирует поле заданного размера.
     *
     * @param width  ширина поля
     * @param height высота поля
     * @param random единый генератор случайных чисел
     * @return сгенерированное игровое поле
     */
    public Board generate(int width, int height, Random random) {
        Board board = new Board(width, height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int chance = random.nextInt(100); // число от 0 до 99

                // 15% шанс на укрытие (1)
                if (chance < 15) {
                    board.setCell(x, y, Board.COVER);
                }
                // 10% шанс на непроходимое препятствие (2)
                else if (chance < 25) {
                    board.setCell(x, y, Board.OBSTACLE);
                }
                // Иначе остается EMPTY (0)
            }
        }
        return board;
    }
}