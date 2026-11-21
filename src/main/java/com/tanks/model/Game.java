package com.tanks.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Состояние игры (партии).
 * Хранит поле, список танков и единый генератор случайных чисел.
 */
public class Game {

    private final Board board;
    private final List<Tank> tanks;
    private final Random random;

    /**
     * Инициализирует новую игру.
     *
     * @param seed   зерно генератора случайных чисел (для воспроизводимости)
     * @param width  ширина поля
     * @param height высота поля
     */
    public Game(long seed, int width, int height) {
        // ЕДИНЫЙ генератор на всю игру
        this.random = new Random(seed);

        MapGenerator generator = new MapGenerator();
        this.board = generator.generate(width, height, this.random);
        this.tanks = new ArrayList<>();
    }

    public Board getBoard() {
        return board;
    }

    public List<Tank> getTanks() {
        return tanks;
    }

    /**
     * Добавляет танк на поле.
     * Танк не может быть размещен на препятствии или поверх другого танка.
     *
     * @param tank танк для размещения
     * @return true, если танк успешно добавлен
     */
    public boolean addTank(Tank tank) {
        Position pos = tank.getPosition();

        // 1. Проверка: клетка должна быть пустой (не укрытие и не препятствие)
        if (board.getCell(pos) != Board.EMPTY) {
            return false;
        }

        // 2. Проверка: клетка не должна быть занята другим танком
        boolean isOccupied = tanks.stream()
                .anyMatch(t -> t.getPosition().x() == pos.x() && t.getPosition().y() == pos.y());

        if (isOccupied) {
            return false;
        }

        tanks.add(tank);
        return true;
    }
}