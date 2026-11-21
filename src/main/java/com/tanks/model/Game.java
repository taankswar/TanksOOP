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

    /**
     * Перемещает танк на указанное количество клеток в текущем направлении.
     * Движение атомарно: если на пути есть препятствие или другой танк,
     * движение не выполняется вообще.
     *
     * @param tankId идентификатор танка
     * @param cells количество клеток для перемещения
     * @return true, если танк успешно перемещен
     * @throws IllegalArgumentException если танк не найден или движение невозможно
     */
    public boolean moveTank(String tankId, int cells) {
        Tank tank = findTank(tankId);

        if (!tank.canMove(cells)) {
            throw new IllegalArgumentException(
                    "Танк " + tankId + " не может пройти " + cells + " клеток"
            );
        }

        Direction facing = tank.getFacing();
        Position currentPos = tank.getPosition();

        // Проверяем весь путь заранее
        for (int i = 1; i <= cells; i++) {
            Position nextPos = currentPos.plus(facing);

            // Проверка: клетка внутри поля
            if (!board.isInside(nextPos.x(), nextPos.y())) {
                throw new IllegalArgumentException(
                        "Танк " + tankId + " выйдет за пределы поля"
                );
            }

            // Проверка: клетка проходима
            if (!board.isPassable(nextPos.x(), nextPos.y())) {
                throw new IllegalArgumentException(
                        "Танк " + tankId + " не может пройти через препятствие"
                );
            }

            // Проверка: клетка не занята другим танком
            if (isTankAt(nextPos)) {
                throw new IllegalArgumentException(
                        "Танк " + tankId + " не может пройти через другой танк"
                );
            }

            currentPos = nextPos;
        }

        // Все проверки пройдены — двигаем танк
        tank.updateState(currentPos, facing);
        return true;
    }

    /**
     * Проверяет, есть ли танк в указанной позиции.
     * @param pos позиция
     * @return true, если в позиции есть танк
     */
    public boolean isTankAt(Position pos) {
        return tanks.stream()
                .anyMatch(t -> t.getPosition().x() == pos.x() && t.getPosition().y() == pos.y());
    }

    /**
     * Находит танк по идентификатору.
     * @param tankId идентификатор танка
     * @return найденный танк
     * @throws IllegalArgumentException если танк не найден
     */
    public Tank findTank(String tankId) {
        return tanks.stream()
                .filter(t -> t.getId().equals(tankId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Танк не найден: " + tankId));
    }

    /**
     * Проверяет линию огня от стрелка к цели.
     * Возвращает танка, который находится на линии огня (если есть), или null.
     *
     * @param shooter стреляющий танк
     * @return танк-цель или null, если на пути нет танков
     */
    public Tank checkLineOfSight(Tank shooter) {
        Position pos = shooter.getPosition();
        Direction facing = shooter.getFacing();

        // Идем по клеткам в направлении взгляда
        while (true) {
            pos = pos.plus(facing);

            // Вышли за поле
            if (!board.isInside(pos.x(), pos.y())) {
                return null;
            }

            // Встретили препятствие
            if (board.blocksShot(pos.x(), pos.y())) {
                return null;
            }

            // Встретили танк
            Tank target = findTankAt(pos);
            if (target != null) {
                return target;
            }
        }
    }

    /**
     * Находит танк в указанной позиции.
     * @param pos позиция
     * @return танк или null
     */
    public Tank findTankAt(Position pos) {
        return tanks.stream()
                .filter(t -> t.getPosition().x() == pos.x() && t.getPosition().y() == pos.y())
                .findFirst()
                .orElse(null);
    }

    /**
     * Выполняет выстрел танка.
     * Проверяет линию огня, наносит урон цели.
     *
     * @param shooterId идентификатор стреляющего танка
     * @return результат выстрела (урон, зона попадания, уничтожен ли цель) или null, если промах
     */
    public ShotResult shoot(String shooterId) {
        Tank shooter = findTank(shooterId);

        if (!shooter.canShoot()) {
            throw new IllegalStateException("Танк " + shooterId + " не может стрелять");
        }

        // Проверяем линию огня
        Tank target = checkLineOfSight(shooter);

        if (target == null) {
            return null; // Промах
        }

        // Расходуем боезапас
        shooter.spendAmmo();

        // Определяем зону попадания
        ArmorZone hitZone = calculateHitZone(shooter, target);

        double armorModifier = getArmorModifier(target);
        int damage = target.receiveShot(
                shooter.getStats().gunPenetration(),
                shooter.getStats().gunDamage(),
                hitZone,
                armorModifier
        );

        return new ShotResult(
                damage > 0,
                hitZone,
                damage,
                target.getCurrentHp(),
                target.isDestroyed()
        );
    }

    /**
     * Определяет зону попадания на основе относительного положения стрелка и цели.
     */
    private ArmorZone calculateHitZone(Tank shooter, Tank target) {
        Position shooterPos = shooter.getPosition();
        Position targetPos = target.getPosition();

        int dx = targetPos.x() - shooterPos.x();
        int dy = targetPos.y() - shooterPos.y();

        Direction directionToTarget;
        if (dx == 0 && dy < 0) directionToTarget = Direction.UP;
        else if (dx == 0 && dy > 0) directionToTarget = Direction.DOWN;
        else if (dx < 0 && dy == 0) directionToTarget = Direction.LEFT;
        else directionToTarget = Direction.RIGHT;

        Direction targetFacing = target.getFacing();

        if (directionToTarget == targetFacing) {
            return ArmorZone.FRONT;
        } else if (directionToTarget == targetFacing.opposite()) {
            return ArmorZone.REAR;
        } else {
            // Определяем левый или правый борт
            if (targetFacing == Direction.UP || targetFacing == Direction.DOWN) {
                return directionToTarget == Direction.LEFT ? ArmorZone.LEFT_SIDE : ArmorZone.RIGHT_SIDE;
            } else {
                return directionToTarget == Direction.UP ? ArmorZone.LEFT_SIDE : ArmorZone.RIGHT_SIDE;
            }
        }
    }

    /**
     * Проверяет, стоит ли танк на укрытии.
     * @param tank танк
     * @return true, если танк на укрытии
     */
    public boolean isTankOnCover(Tank tank) {
        Position pos = tank.getPosition();
        return board.getCell(pos) == Board.COVER;
    }

    /**
     * Возвращает модификатор брони для танка на укрытии.
     * @param tank танк
     * @return множитель брони (1.0 = обычный, 1.5 = на укрытии)
     */
    public double getArmorModifier(Tank tank) {
        return isTankOnCover(tank) ? 1.5 : 1.0;
    }
}