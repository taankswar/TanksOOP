package com.tanks;

import com.tanks.engine.GameEngine;
import com.tanks.model.*;
import com.tanks.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        // Создаем игру с seed=42
        Game game = new Game(42L, 20, 20);

        // Очищаем клетки для танков (гарантируем пустое место)
        game.getBoard().setCell(2, 2, Board.EMPTY);
        game.getBoard().setCell(17, 17, Board.EMPTY);

        // Добавляем танки
        game.addTank(new LightTank("T1", new Position(2, 2), Direction.RIGHT));
        game.addTank(new HeavyTank("T2", new Position(17, 17), Direction.LEFT));

        // Создаем движок и запускаем игру
        GameEngine engine = new GameEngine(game);
        ConsoleView view = new ConsoleView(engine);
        view.start();
    }
}