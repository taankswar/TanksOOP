package com.tanks.view;

import com.tanks.engine.GameEngine;
import com.tanks.model.*;
import java.util.List;
import java.util.Scanner;

/**
 * Консольный интерфейс игры.
 */
public class ConsoleView {

    private final GameEngine engine;
    private final Scanner scanner;

    public ConsoleView(GameEngine engine) {
        this.engine = engine;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Запускает игровой цикл.
     */
    /**
     * Запускает игровой цикл.
     */
    public void start() {
        System.out.println("=== ТАНКИ ===");
        System.out.println();

        while (true) {
            Tank currentTank = engine.getCurrentTank();

            if (currentTank.isDestroyed()) {
                System.out.println("Танк " + currentTank.getId() + " уничтожен. Пропуск хода.");
                engine.nextTurn();
                continue;
            }

            System.out.println("--- Ход танка " + currentTank.getId() + " ---");
            System.out.println("HP: " + currentTank.getCurrentHp() + "/" + currentTank.getStats().maxHp() +
                    " | Боезапас: " + currentTank.getCurrentAmmo() + "/" + currentTank.getStats().maxAmmo() +
                    " | Направление: " + currentTank.getFacing());
            System.out.println();

            printBoard();

            System.out.println("Команды: m <1-5> - движение, r <UP|DOWN|LEFT|RIGHT> - поворот, s - выстрел, p - пропуск");
            System.out.print("Ваш ход: ");
            String command = scanner.nextLine().trim().toLowerCase();

            TurnAction action = parseCommand(command);
            if (action == null) {
                System.out.println("❌ Неверная команда. Попробуйте снова.");
                System.out.println("Примеры: m 3, r UP, s, p");
                continue;
            }

            List<GameEvent> events = engine.executeTurn(currentTank.getId(), action);
            processEvents(events);

            // Проверяем, не закончилась ли игра
            if (events.stream().anyMatch(e -> e instanceof GameOverEvent)) {
                break;
            }

            engine.nextTurn();
            System.out.println();
            System.out.println("=".repeat(50));
            System.out.println();
        }

        System.out.println("Игра окончена!");
    }

    /**
     * Парсит команду пользователя.
     */
    private TurnAction parseCommand(String command) {
        if (command.equals("p")) {
            return new PassAction();
        }

        if (command.equals("s")) {
            return new ShootAction();
        }

        if (command.startsWith("m ")) {
            try {
                int cells = Integer.parseInt(command.substring(2).trim());
                return new MoveAction(cells);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        if (command.startsWith("r ")) {
            try {
                Direction dir = Direction.valueOf(command.substring(2).trim().toUpperCase());
                return new RotateAction(dir);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Обрабатывает события и выводит их в консоль.
     */
    private void processEvents(List<GameEvent> events) {
        for (GameEvent event : events) {
            if (event instanceof MoveEvent e) {
                System.out.println("Танк " + e.tankId() + " переместился " + e.from() + " -> " + e.to());
            } else if (event instanceof RotateEvent e) {
                System.out.println("Танк " + e.tankId() + " повернулся " + e.oldFacing() + " -> " + e.newFacing());
            } else if (event instanceof ShootEvent e) {
                if (e.penetrated()) {
                    System.out.println("Танк " + e.shooterId() + " выстрелил в " + e.targetId() +
                            " -> ПОПАДАНИЕ в " + e.hitZone() + ", урон " + e.damage());
                } else {
                    System.out.println("Танк " + e.shooterId() + " выстрелил в " + e.targetId() +
                            " -> НЕ ПРОБИЛ броню");
                }
            } else if (event instanceof TankDestroyedEvent e) {
                System.out.println("Танк " + e.tankId() + " УНИЧТОЖЕН!");
            } else if (event instanceof GameOverEvent e) {
                System.out.println("ПОБЕДИТЕЛЬ: " + (e.winnerId() != null ? e.winnerId() : "Ничья"));
            }
        }
    }

    /**
     * Отрисовывает поле в консоли.
     */
    private void printBoard() {
        Board board = engine.getGame().getBoard();
        int width = board.getWidth();
        int height = board.getHeight();

        System.out.println("  " + "-".repeat(width * 2 + 1));

        for (int y = 0; y < height; y++) {
            System.out.print(y + "|");
            for (int x = 0; x < width; x++) {
                Position pos = new Position(x, y);
                Tank tank = engine.getGame().findTankAt(pos);

                if (tank != null) {
                    // Отрисовка танка
                    char symbol = switch (tank.getFacing()) {
                        case UP -> '^';
                        case DOWN -> 'v';
                        case LEFT -> '<';
                        case RIGHT -> '>';
                    };
                    System.out.print(symbol + " ");
                } else {
                    // Отрисовка клетки
                    int cell = board.getCell(x, y);
                    char symbol = switch (cell) {
                        case Board.EMPTY -> '.';
                        case Board.COVER -> '#';
                        case Board.OBSTACLE -> 'X';
                        default -> '?';
                    };
                    System.out.print(symbol + " ");
                }
            }
            System.out.println("|");
        }

        System.out.println("  " + "-".repeat(width * 2 + 1));

        // Легенда
        System.out.println("Легенда: . - пусто, # - укрытие, X - препятствие, ^v<> - танки");
    }
}