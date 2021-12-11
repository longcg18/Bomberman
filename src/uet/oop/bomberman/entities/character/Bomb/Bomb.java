package uet.oop.bomberman.entities.character.Bomb;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import uet.oop.bomberman.entities.Unmovable.Brick;
import uet.oop.bomberman.entities.Unmovable.Grass;
import uet.oop.bomberman.entities.Unmovable.Wall;
import uet.oop.bomberman.entities.character.*;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.character.MovingState;
import uet.oop.bomberman.entities.character.Character;
import uet.oop.bomberman.graphics.Sprite;

import java.awt.*;

import static uet.oop.bomberman.Sound.Sound.bombExplosionSound;
import static uet.oop.bomberman.Sound.Sound.playMedia;

import static uet.oop.bomberman.BombermanGame.*;
import static uet.oop.bomberman.graphics.Sprite.SCALED_SIZE;

public class Bomb extends Entity implements Destroy {
    public boolean exploded;
    public boolean isExploding;
    private int timeExist = FPS * 3;
    private int timeExplode = FPS;

    // Current max length if not stuck with in wall
    private int flameLength = 1;
    public static int presentFlameLength = 1;
    private final Flame flameUp;
    private final Flame flameDown;
    private final Flame flameLeft;
    private final Flame flameRight;

    // Actual length
    private int upLength;
    private int downLength;
    private int leftLength;
    private int rightLength;

    public Bomb(int xUnit, int yUnit, Image img) {
        super(xUnit, yUnit, img);
        exploded = false;
        isExploding = false;
        flameLength = presentFlameLength;
        upLength = setLengthFlame(MovingState.UP);
        downLength = setLengthFlame(MovingState.DOWN);
        leftLength = setLengthFlame(MovingState.LEFT);
        rightLength = setLengthFlame(MovingState.RIGHT);
        flameUp = new Flame(xUnit, yUnit - 1, Sprite.explosion_vertical_top_last.getFxImage(), upLength, MovingState.UP);
        flameDown = new Flame(xUnit, yUnit + 1, Sprite.explosion_vertical_down_last.getFxImage(), downLength, MovingState.DOWN);
        flameLeft = new Flame(xUnit - 1, yUnit, Sprite.explosion_horizontal_left_last.getFxImage(), leftLength, MovingState.LEFT);
        flameRight = new Flame(xUnit + 1, yUnit, Sprite.explosion_horizontal_right_last.getFxImage(), rightLength, MovingState.RIGHT);
    }

    public static void increaseLength() {
        if (presentFlameLength <= Flame.maxLength) presentFlameLength++;
    }

    @Override
    public void update() {

        if (!exploded) {
            image = Sprite.movingSprite(Sprite.bomb, Sprite.bomb_1, Sprite.bomb_2, timeExist--, FPS / 4).getFxImage();
        }
        //trước khi nổ
        if (timeExist == 0) {
            isExploding = true;
            playMedia(bombExplosionSound);
        }
        //bomb nổ
        if (isExploding) {
            loadDestroyImage();
            destroyObject();
            flameUp.update();
            flameDown.update();
            flameLeft.update();
            flameRight.update();
        }

        //sau khi nổ
        if (timeExplode == 0) {
            exploded = true;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        super.render(gc);
        if (isExploding) {
            flameUp.render(gc, flameLength);
            flameDown.render(gc, flameLength);
            flameLeft.render(gc, flameLength);
            flameRight.render(gc, flameLength);
        }
    }

    private int setLengthFlame(MovingState movingState) {
        int pos = getPosition();
        int length = 0;
        switch (movingState) {
            case UP:
                for (int i = 1; i <= flameLength; i++) {
                    if (!(map.get(pos - i * WIDTH) instanceof Grass)) {
                        break;
                    }
                    length++;
                }
                break;
            case DOWN:
                for (int i = 1; i <= flameLength; i++) {
                    if (!(map.get(pos + i * WIDTH) instanceof Grass)) {
                        break;
                    }
                    length++;
                }
                break;
            case LEFT:
                for (int i = 1; i <= flameLength; i++) {
                    if (!(map.get(pos - i) instanceof Grass)) {
                        break;
                    }
                    length++;
                }
                break;
            case RIGHT:
                for (int i = 1; i <= flameLength; i++) {
                    if (!(map.get(pos + i) instanceof Grass)) {
                        break;
                    }
                    length++;
                }
                break;
        }
        return length;
    }

    private void destroyObject() {
        int pos = getPosition();
        if (upLength < flameLength) {
            upLength++;
        }
        for (int i = 1; i <= upLength; i++) {
            int j = pos - i * WIDTH;
            if (collisionWithMap(j)) {
                upLength = i;
            }
        }
        if (downLength < flameLength) {
            downLength++;
        }
        for (int i = 1; i <= downLength; i++) {
            int j = pos + i * WIDTH;
            if (collisionWithMap(j)) {
                downLength = i;
            }
        }
        if (leftLength < flameLength) {
            leftLength++;
        }
        for (int i = 1; i <= leftLength; i++) {
            int j = pos - i;
            if (collisionWithMap(j)) {
                leftLength = i;
            }
        }
        if (rightLength < flameLength) {
            rightLength++;
        }
        for (int i = 1; i <= rightLength; i++) {
            int j = pos + i;
            if (collisionWithMap(j)) {
                rightLength = i;
            }
        }
    }

    public boolean collisionWithCharacter(Character character) {
        if (character instanceof Bomber) {
            Rectangle a = new Rectangle(character.getX() + 5, character.getY() + 5, SCALED_SIZE - 15, SCALED_SIZE - 15);
            //Rectangle a = character.getRec();
            return CollisionTest(a, getVerticalRec()) || CollisionTest(a, getHorizontalRec());
        }
        Rectangle a = new Rectangle(character.getX(), character.getY(), SCALED_SIZE - 10, SCALED_SIZE);
        return CollisionTest(a, getVerticalRec()) || CollisionTest(a, getHorizontalRec());
    }

    /**
     * check collision between flame and map.
     *
     * @param pos position of entity in map
     * @return return true if flame impact wall, brick.
     */
    private boolean collisionWithMap(int pos) {
        if (pos < 0 || pos > map.size()) {
            return true;
        }
        Entity temp = map.get(pos);
        if (temp instanceof Wall) {
            return true;
        }
        if (temp instanceof Brick) {
            ((Brick) temp).destroy();
            return true;
        }
        return false;
    }

    private void setBombExplode(int timeExplode) {
        this.timeExplode = timeExplode;
        isExploding = true;
        timeExist = 0;
        flameUp.setExplodingTime(timeExplode);
        flameRight.setExplodingTime(timeExplode);
        flameLeft.setExplodingTime(timeExplode);
        flameDown.setExplodingTime(timeExplode);
    }

    private int getVerticalLength() {
        return downLength + upLength + 1;
    }

    private int getHorizontalLength() {
        return leftLength + rightLength + 1;
    }

    private java.awt.Rectangle getHorizontalRec() {
        return new java.awt.Rectangle(x - leftLength * SCALED_SIZE, y, getHorizontalLength() * SCALED_SIZE, SCALED_SIZE);
    }

    private java.awt.Rectangle getVerticalRec() {
        return new java.awt.Rectangle(x, y - upLength * SCALED_SIZE, SCALED_SIZE, getVerticalLength() * SCALED_SIZE);
    }


    public void collisionWithBomb(Bomb bomb) {
        if (CollisionTest(getRectangle(), bomb.getHorizontalRec()) ||
                CollisionTest(getRectangle(), bomb.getVerticalRec()) ||
                CollisionTest(getVerticalRec(), bomb.getRectangle()) ||
                CollisionTest(getHorizontalRec(), bomb.getRectangle())) {
            if ((this.isExploding && !bomb.isExploding)) {
                //int min = Integer.min(bomb.getTimeExplode(),timeExplode);
                bomb.setBombExplode(timeExplode);
            }
        }
    }

    @Override
    public void loadDestroyImage() {
        image = Sprite.movingSprite(Sprite.bomb_exploded, Sprite.bomb_exploded1,
                Sprite.bomb_exploded2, timeExplode--, FPS).getFxImage();
    }
}

