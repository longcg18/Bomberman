package uet.oop.bomberman.entities.character.enemy;

import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.entities.character.MovingState;

import static uet.oop.bomberman.BombermanGame.bomber;
import static uet.oop.bomberman.BombermanGame.enemies;

import javafx.scene.image.Image;

public abstract class Enemy extends Character {
    protected int point;

    public Enemy(int x, int y, Image image) {
        super(x, y, image);
    }

    public abstract void changeMovingState();

    protected void checkCollisionEnemy() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = (Enemy) enemies.get(i);
            if (this != e && e.movable) {
                if (CollisionTest(this.getNextRectangle(), e.getNextRectangle())) {
                    this.movable = false;
                    e.movable = false;
                    this.reverseMovingState();
                    e.reverseMovingState();
                }
            }
        }
    }

    public int getPoint() {
        return point;
    }

    protected void randomMovingState() {
        if (!alive || deathLoading) return;
        movingState = MovingState.random();
        while (movingState == MovingState.STANDING) {
            movingState = MovingState.random();
        }
    }

    protected void reverseMovingState() {
        if (!alive || deathLoading) return;
        switch (movingState) {
            case UP:
                movingState = MovingState.DOWN;
                break;
            case DOWN:
                movingState = MovingState.UP;
                break;
            case LEFT:
                movingState = MovingState.RIGHT;
                break;
            case RIGHT:
                movingState = MovingState.LEFT;
                break;
        }
    }

    @Override
    public void update() {
        if (deathLoading) {
            loadDestroyImage();
            return;
        }
        changeMovingState();
        checkCollisionEnemy();
        moveChecking(bomber.bom);
        move();
        updateImg();
    }
}
