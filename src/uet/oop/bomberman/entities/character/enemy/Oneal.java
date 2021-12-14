package uet.oop.bomberman.entities.character.enemy;

import javafx.scene.image.Image;
import uet.oop.bomberman.entities.character.MovingState;
import uet.oop.bomberman.graphics.Sprite;

import static uet.oop.bomberman.BombermanGame.FPS;
import static uet.oop.bomberman.graphics.Sprite.SCALED_SIZE;

public class Oneal extends Enemy {

    private static final int timeToChangeMovingState = FPS * 3;

    private int timeToChange = timeToChangeMovingState;

    public Oneal(int x, int y, Image image) {
        super(x, y, image);
        velocity = SCALED_SIZE / 8;
        movingState = MovingState.RIGHT;
        point = 70;
    }

    @Override
    protected void updateImg() {
        switch (movingState) {
            case LEFT:
            case UP:
                image = animate(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3);
                break;
            case RIGHT:
            case DOWN:
                image = animate(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3);
                break;
            default:
                break;
        }
    }

    @Override
    public void loadDestroyImage() {
        if (timeDeathLoading == 0 || !movable) {
            alive = false;
        }
        image = Sprite.oneal_dead.getFxImage();
        timeDeathLoading--;
    }

    @Override
    public void changeMovingState() {
        timeToChange--;
        if (timeToChange == 0) {
            randomMovingState();
            timeToChange = timeToChangeMovingState;
        }
    }
}
