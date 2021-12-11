package uet.oop.bomberman;

/**
 * import libraries, don't modify
 */
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import uet.oop.bomberman.entities.Item.Item;
import uet.oop.bomberman.entities.Item.Portal;
import uet.oop.bomberman.entities.character.Bomber;
import uet.oop.bomberman.entities.Entity;
import uet.oop.bomberman.entities.Unmovable.*;

import uet.oop.bomberman.entities.character.enemy.Balloom;
import uet.oop.bomberman.entities.character.enemy.Doll;
import uet.oop.bomberman.entities.character.enemy.Enemy;
import uet.oop.bomberman.graphics.Sprite;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static uet.oop.bomberman.graphics.Sprite.SCALED_SIZE;
import static uet.oop.bomberman.entities.character.Bomber.*;

public class BombermanGame extends Application {

    /**
     * local variables, don't modify
     */
    public static int WIDTH;
    public static int HEIGHT;
    public static final int FPS = 20;
    private GraphicsContext gc;
    private Canvas canvas;
    private int timeLoadImage;
    public static List<Enemy> enemies;
    public static List<Item> items;
    public static List<Entity> map;
    public static Bomber bomber;
    public static final int timeEachFrame = 1000 / FPS;
    public static int level;
    public static int point;
    public static Portal portal;

    public static void main(String[] args) {
        Application.launch(BombermanGame.class);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Bomberman Go!");
        level = 2;
        point = 0;
        portal = null;
        if (bomber != null) {
            bomber.clearBom();
        }
        bomber = null;
        timeLoadImage = FPS * 3;
        enemies = new ArrayList<>();
        map = new ArrayList<>();
        items = new ArrayList<>();
        reset();

        createMap();

        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long l) {
                long start = System.currentTimeMillis();
                // The update functions
                update();
                render();
                if (maxLife == 0) {
                    System.out.println("End game! You Lose!");
                    stop();
                }
                if (portal == null) {
                    System.out.println("End game! You win!");
                    stop();
                }
                long realTime = System.currentTimeMillis() - start;
                if (realTime < timeEachFrame) {
                    try {
                        Thread.sleep(timeEachFrame - realTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        canvas = new Canvas(Sprite.SCALED_SIZE * WIDTH, Sprite.SCALED_SIZE * HEIGHT);
        gc = canvas.getGraphicsContext2D();
        // Tao root container
        Group root = new Group();
        root.getChildren().add(canvas);
        // Tao scene
        Scene scene = new Scene(root);
        // Them scene vao stage
        stage.setScene(scene);
        stage.setResizable(false);


        scene.setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case UP: bomber.MoveUp();
                break;
                case DOWN:bomber.MoveDown(); break;
                case LEFT:bomber.MoveLeft(); break;
                case RIGHT:bomber.MoveRight(); break;
                case SPACE:bomber.plantBomb(); break;
                case Z:enemies.clear(); break;
                case ESCAPE: timer.stop(); break;
            }
        });
        scene.setOnKeyReleased((KeyEvent key) -> bomber.Standing());

        timer.start();
        stage.show();
    }

    public void createMap() {
        try {
            String path = "Resources/levels/Level" + level + ".txt";
            FileReader fileReader = new FileReader(path);
            Scanner sc = new Scanner(fileReader);
            sc.nextInt();
            HEIGHT = sc.nextInt();
            WIDTH = sc.nextInt();
            int I = sc.nextInt();
            sc.nextLine();

            // Load map
            for (int i = 0; i < HEIGHT; i++) {
                String temp = sc.nextLine();
                for (int j = 0; j < WIDTH; j++) {
                    Entity object;
                    Enemy enemy = null;
                    char p = temp.charAt(j);

                    switch (p) {
                        case '#' -> object = new Wall(j, i, Sprite.wall.getFxImage());
                        case '*' -> object = new Brick(j, i, Sprite.brick.getFxImage());
                        case 'p' -> {
                            bomber = new Bomber(j, i, Sprite.player_right.getFxImage());
                            object = new Grass(j, i, Sprite.grass.getFxImage());
                        }
                        case '1' -> {
                            enemy = new Balloom(j, i, Sprite.balloom_right1.getFxImage());
                            object = new Grass(j, i, Sprite.grass.getFxImage());
                        }
                        case '2' -> {
                            enemy = new Doll(j, i, Sprite.doll_right1.getFxImage());
                            object = new Grass(j, i, Sprite.grass.getFxImage());
                        }
                        // add new enemy

                        default -> object = new Grass(j, i, Sprite.grass.getFxImage());
                    }
                    if (enemy != null) enemies.add(enemy);
                    map.add(object);
                }
            }

            // Load item
            for (int i = 0; i < I; i++) {
                String temp = sc.nextLine();
                String[] s = temp.split(" ");
                if ("x".equals(s[0])) {
                    for (int j = 1; j < s.length; j += 2) {
                        int xUnit = Integer.parseInt(s[j + 1]);
                        int yUnit = Integer.parseInt(s[j]);
                        portal = new Portal(xUnit, yUnit, Sprite.portal.getFxImage());
                    }
                }
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void update() {
        bomber.update();
        updateEnemy();
        items.forEach(Entity::update);
        map.forEach(Entity::update);
        updateItem();
        if (portal != null) portal.update();
        updateMap();
    }

    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        map.forEach(g -> g.render(gc));
        items.forEach(g -> g.render(gc));
        if (portal != null) portal.render(gc);
        enemies.forEach(g -> g.render(gc));
        bomber.render(gc);
    }

    public int getFPS() {
        return FPS;
    }

    private void updateMap() {
        int n = map.size();
        for (int j = 0; j < n; j++) {
            Entity entity = map.get(j);
            if (entity instanceof Brick && ((Brick) entity).destroyed) {
                Entity obj = new Grass(entity.getX() / SCALED_SIZE,
                        entity.getY() / SCALED_SIZE, Sprite.grass.getFxImage());
                map.remove(j);
                map.add(j, obj);
            }
        }
    }

    private void updateItem() {
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).Lying) {
                items.remove(i);
                return;
            }
        }
    }

    private String gameOverInfo() {
        if (enemies.size() == 0 && portal == null) {
            return "win";
        } else
            return "lose";
    }

    private void updateEnemy() {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy enemy = enemies.get(i);
            if (enemy != null) {
                enemy.update();
                if (!enemy.alive) {
                    point += enemy.getPoint();
                    System.out.println("Point: "+ point);
                    enemies.remove(enemy);
                }
            }
        }
    }

}
