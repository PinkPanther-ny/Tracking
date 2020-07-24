import bagel.*;
import bagel.util.Point;

import java.util.Random;

import static bagel.Window.close;

public class Game extends AbstractGame {
    private final Image house = new Image("res/images/bg2.png");
    private final Image player = new Image("res/images/player.png");
    private final Image apple = new Image("res/images/cake.png");
    private final Image arrow = new Image("res/images/arrow.png");

    private final MusicPlayer eat = new MusicPlayer("res/Music/eat.wav");
    private final MusicPlayer bounce = new MusicPlayer("res/Music/bounce2.wav");
    private final MusicPlayer background = new MusicPlayer("res/Music/background.wav");

    private final Font myName = new Font("res/Font/BillionDreams_PERSONAL.ttf",52);
    private final Font myScore = new Font("res/Font/Hysteria.ttf",45);
    private final Font myInfo = new Font("res/Font/conformable.otf",30);

    private final String  title1 = "Rainko With a Cake";
    private final Point titleLocation1 = new Point(20, 50);
    private final String  title2 = "--Designed By Alvin";
    private final Point titleLocation2 = new Point(60, 100);


    private final int damage = 10, heal = 5;
    private final double gravity = 0.6, groundLevel = 624;
    private final double moveDamping = 0.99, bumpDamping = 0.85, radius = 35;
    private final double minimumBounceSpeed = 8;
    private final double xMaxSpeed = 3, yMaxSpeed = 8;

    double walkAcceleration, jumpAcceleration;
    private double x, y;
    private double xSpeed, ySpeed;
    private int health, score;
    private boolean isGameOver;

    private  double initialScrollPosition = 0;

    private double xApple;
    private double yApple;

    private final double trackingSpeedScale = 0.10;

    public Game() {
        reset();
    }

    public void reset(){

        this.x = 220;
        this.y = groundLevel;
        this.xSpeed = 0;
        this.ySpeed = 0;
        this.xApple = Window.getWidth() * Math.random();
        this.yApple = groundLevel * Math.random();
        this.health = 100;
        this.score = 0;
        this.isGameOver = false;

    }


    public void gameOver(){
        health=0;
        isGameOver = true;
        Font GG = new Font("res/Font/Hysteria.ttf",85);
        GG.drawString("Game Over!", Window.getWidth()/2.0-160, Window.getHeight()/2.0-200);
        GG = new Font("res/Font/Hysteria.ttf",40);
        GG.drawString("Press ENTER to play again...", Window.getWidth()/2.0-160, Window.getHeight()/2.0-160);
        GG.drawString("              ESC to exit...", Window.getWidth()/2.0-160, Window.getHeight()/2.0-120);
        //System.out.println("Score: " + score);
    }

    /**
     * Entry point for Bagel game
     *
     * Explore the capabilities of Bagel: https://people.eng.unimelb.edu.au/mcmurtrye/bagel-doc/
     */
    public static void main(String[] args) {
        // Create new instance of game and run it
        Game game = new Game();
        game.background.start(true, false);
        game.run();
        game.background.stop();

    }

    /**
     * Updates the game state approximately 60 times a second, potentially reading from input.
     * @param input The input instance which provides access to keyboard/mouse state information.
     */
    @Override
    protected void update(Input input) {

        if (initialScrollPosition == Window.getWidth()){initialScrollPosition=0;}
        DrawOptions loopImage = new DrawOptions();
        loopImage.setSection(initialScrollPosition, 0, Window.getWidth() - initialScrollPosition, Window.getHeight());
        house.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0, loopImage);

        DrawOptions loopImage2 = new DrawOptions();
        loopImage2.setSection(0, 0, initialScrollPosition, Window.getHeight());
        house.draw(1.5*Window.getWidth() - initialScrollPosition, Window.getHeight() / 2.0, loopImage2);
        initialScrollPosition+=1;

        double randomCoefficient = 0.44 * score * score * 0.0001;
        walkAcceleration = 0.2 + 0.1 * Math.random() * randomCoefficient;
        jumpAcceleration = 2 + Math.random() * randomCoefficient;
        myInfo.drawString("Location x: " + (int)x + ", y: " + (int)y, 20,685);
        myInfo.drawString("Speed (px/sec) x: " + (int)(xSpeed * 60) + ", y: " + (int)(ySpeed * 60), 20,705);
        myInfo.drawString("Acceleration (px/sec^2) x: " + (int)(walkAcceleration* 60) + ", y: " + (int)(jumpAcceleration * 60), 20,725);
        myInfo.drawString("Gravity (px/sec^2): " + (int)(gravity * 60), 20, 745);
        myInfo.drawString("Random Coefficient: " + (double)Math.round(0.44 * score * score * 0.0001 * 1000)/1000, 20, 765);

        if(!isGameOver) {
            //if ((x>200)&&(input.isDown(Keys.LEFT) || input.isDown(Keys.A))) {
            if (x>xApple || xSpeed>xMaxSpeed) {
                arrow.draw(762,700, new DrawOptions().setRotation(-3.14/2));
                xSpeed -= trackingSpeedScale * walkAcceleration;
            }
            //if ((x<Window.getWidth()-200)&&(input.isDown(Keys.RIGHT) || input.isDown(Keys.D))) {
            if (x<xApple || xSpeed<-xMaxSpeed) {
                arrow.draw(900,700, new DrawOptions().setRotation(3.14/2));
                xSpeed += trackingSpeedScale * walkAcceleration;
            }
            //if (input.wasPressed(MouseButtons.LEFT) || input.wasPressed(Keys.UP) || input.wasPressed(Keys.W) || input.wasPressed(Keys.SPACE) || input.wasPressed(Keys.L)) {
            if (y>yApple || ySpeed>yMaxSpeed) {
                arrow.draw(831,700-arrow.getHeight()/2.0, new DrawOptions().setRotation(0));
                ySpeed -= trackingSpeedScale * (4 * jumpAcceleration + 1 * Math.random());
            }

        }else{
            if (input.isDown(Keys.ENTER)) {
                reset();
                // reset
            }
        }
        if (input.wasPressed(Keys.ESCAPE)) {
            close();
        }
        x += xSpeed;
        y += ySpeed;
        if (y<Window.getHeight()) {
            ySpeed += gravity;
        }
        xSpeed *= moveDamping;

        if (y < 0) {
            y = 0;
            ySpeed = 0;
            bounce.start(false, false);
            health -= damage;
        } // SKY

        if (y > groundLevel) {
            y = groundLevel;
            ySpeed *= -bumpDamping;
            if (Math.abs(ySpeed) < minimumBounceSpeed) {
                ySpeed = 0;
            } else {
                bounce.start(false, false);
                health -= (damage-5);
            }
        } //GROUND


        if (x < 0) {
            x = 0;
            xSpeed *= -bumpDamping;
            bounce.start(false, false);
        } else if(x > Window.getWidth()){
            x = Window.getWidth();
            xSpeed *= -bumpDamping;
            bounce.start(false, false);

        }// LEFT OR RIGHT

        if (Math.abs(x - xApple) < radius && Math.abs(y - yApple) < radius && (!isGameOver)) {
            // Collision

            eat.start(false, false);

            Random generator = new Random();
            int maxi=9,min=1;
            double range = 0.1 * (generator.nextInt(maxi + 1 -min) + min);
            xApple = Window.getWidth() * range;
            yApple = groundLevel * range;
            score += 10;
            health += heal;
        }

        DrawOptions bigger = new DrawOptions();
        bigger.setScale(1.3,1.3);

        DrawOptions scale = new DrawOptions();
        scale.setScale(0.2,0.2);
        apple.draw(xApple, yApple, scale);

        
        scale.setScale(1.3,1.3);
        player.draw(x, y, scale);
        //player.draw(x, y, color);

        
        myName.drawString(title1, titleLocation1.x, titleLocation1.y);
        myName.drawString(title2, titleLocation2.x, titleLocation2.y);
        

        if (health > 100) {health = 100;}
        if (health <= 0) {
            gameOver();

        }
        myScore.drawString("Health: " + health +"\nScore: " + score,850, 50);

    }
}
