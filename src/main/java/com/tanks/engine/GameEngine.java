package com.tanks.engine;

import com.tanks.model.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Игровой движок. Управляет ходами и условиями победы.
 */
public class GameEngine {

    private final Game game;
    private int currentTurnIndex;

    public GameEngine(Game game) {
        this.game = game;
        this.currentTurnIndex = 0;
    }

    /**
     * Выполняет ход танка.
     * @param tankId идентификатор танка
     * @param action действие (move, rotate, shoot)
     * @return список событий, произошедших в результате хода
     */
    public List<GameEvent> executeTurn(String tankId, TurnAction action) {
        List<GameEvent> events = new ArrayList<>();
        Tank tank = game.findTank(tankId);

        if (tank.isDestroyed()) {
            return events; // Уничтоженный танк пропускает ход
        }

        if (action instanceof MoveAction moveAction) {
            Position oldPos = tank.getPosition();
            game.moveTank(tankId, moveAction.cells());
            events.add(new MoveEvent(tankId, oldPos, tank.getPosition()));

        } else if (action instanceof RotateAction rotateAction) {
            Direction oldFacing = tank.getFacing();
            tank.rotate(rotateAction.newDirection());
            events.add(new RotateEvent(tankId, oldFacing, tank.getFacing()));

        } else if (action instanceof ShootAction) {
            ShotResult result = game.shoot(tankId);
            if (result != null) {
                Tank target = findTarget(tank);
                events.add(new ShootEvent(
                        tankId,
                        target.getId(),
                        result.hitZone(),
                        result.damage(),
                        result.penetrated(),
                        result.destroyed()
                ));

                if (result.destroyed()) {
                    events.add(new TankDestroyedEvent(target.getId()));
                }
            }
        }

        // Проверяем условие победы
        GameEvent gameOver = checkGameOver();
        if (gameOver != null) {
            events.add(gameOver);
        }

        return events;
    }

    /**
     * Переходит к следующему танку.
     */
    public void nextTurn() {
        currentTurnIndex = (currentTurnIndex + 1) % game.getTanks().size();
    }

    /**
     * Возвращает танк, чей сейчас ход.
     */
    public Tank getCurrentTank() {
        return game.getTanks().get(currentTurnIndex);
    }

    /**
     * Проверяет, закончилась ли игра.
     * @return событие окончания игры или null
     */
    private GameEvent checkGameOver() {
        List<Tank> aliveTanks = game.getTanks().stream()
                .filter(t -> !t.isDestroyed())
                .toList();

        if (aliveTanks.size() <= 1) {
            String winner = aliveTanks.isEmpty() ? null : aliveTanks.get(0).getId();
            return new GameOverEvent(winner);
        }
        return null;
    }

    /**
     * Находит цель на линии огня стрелка.
     */
    private Tank findTarget(Tank shooter) {
        return game.checkLineOfSight(shooter);
    }

    public Game getGame() {
        return game;
    }
}