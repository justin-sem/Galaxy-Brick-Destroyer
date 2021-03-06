package GameMain;

import Ball.Ball;
import Brick.Brick;
import Console.DebugConsole;
import Component.Paddle;
import Component.Wall;
import Component.WriteToFile;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;


/**
 * This is the Game Board class
 * which is the gaming part
 */
public class GameBoard extends JComponent implements KeyListener,MouseListener,MouseMotionListener {

    private static final String CONTINUE = "CONTINUE";
    private static final String RESTART = "RESTART";
    private static final String EXIT = "EXIT";
    private static final String PAUSE = "PAUSE MENU";
    private static final int TEXT_SIZE = 30;
    private static final Color MENU_COLOR = new Color(166, 141, 16);

    private static final int DEF_WIDTH = 600;
    private static final int DEF_HEIGHT = 450;

    private static final Color BG_COLOR = new Color(15, 20, 66);

    private Timer gameTimer;
    private Wall wall;
    private String message;
    private boolean showPauseMenu;
    private Font menuFont;

    private Rectangle continueButtonRect;
    private Rectangle exitButtonRect;
    private Rectangle restartButtonRect;
    private int strLen;

    private DebugConsole debugConsole;
    public static int temp;


    /**
     * @param owner JFrame object
     * Game Board constructor which included the activities in game
     */
    public GameBoard(JFrame owner){

        super();
        strLen = 0;
        showPauseMenu = false;

        menuFont = new Font("Times New Roman",Font.BOLD,TEXT_SIZE);

        this.initialize();

        message = "PRESS SPACE TO START";
        wall = new Wall(new Rectangle(0,0,DEF_WIDTH,DEF_HEIGHT),30,3,6/2,new Point(300,430));

        debugConsole = new DebugConsole(owner,wall,this);

        //initialize the first level
        wall.nextLevel();

        gameTimer = new Timer(10,e ->{
            wall.move();
            wall.findImpacts();
            message = String.format("        Bricks: %d, Balls %d" ,wall.getBrickCount(),wall.getBallCount());
            temp = wall.getHighScore();
            if(wall.isBallLost()){
                if(wall.ballEnd()){
                    GameFrame enableHighScore = new GameFrame();
                    WriteToFile writeFile = new WriteToFile();          //when game over record the HS
                    writeFile.WriteToFile();
                    wall.wallReset();
                    wall.highScore = 0;                         // if lost the game, the score reset
                    message = "          GAME OVER";
                    enableHighScore.enableHighScore();
                }
                wall.ballReset();
                gameTimer.stop();
            }
            else if(wall.isDone()){
                if(wall.hasLevel()){
                    message = "GO TO NEXT LEVEL";
                    GameFrame enableHighScore = new GameFrame();
                    WriteToFile writeFile = new WriteToFile();          //when game end record the HS
                    writeFile.WriteToFile();
                    enableHighScore.enableHighScore();
                    gameTimer.stop();
                    wall.ballReset();
                    wall.wallReset();
                    wall.nextLevel();
                }
                else{
                    message = "ALL WALLS DESTROYED";
                    gameTimer.stop();
                }
            }

            repaint();
        });

    }


    /**
     * initialize method which build the Game Board frame
     */
    private void initialize(){
        this.setPreferredSize(new Dimension(DEF_WIDTH,DEF_HEIGHT));
        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(this);
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }


    /**
     * @param g
     * method which paint and draw the components in Game Board
     */
    public void paint(Graphics g){

        Graphics2D g2d = (Graphics2D) g;
        clear(g2d);

        g2d.setColor(Color.GREEN);
        g2d.drawString(message,230,225);

        drawBall(wall.ball,g2d);

        for(Brick b : wall.bricks)
            if(!b.isBroken())
                drawBrick(b,g2d);

        drawPaddle(wall.paddle,g2d);

        if(showPauseMenu)
            drawMenu(g2d);

        Toolkit.getDefaultToolkit().sync();
    }

    /**
     * @param g2d
     * method which set the background color in the game board
     */
    private void clear(Graphics2D g2d){
        Color tmp = g2d.getColor();
        g2d.setColor(BG_COLOR);
        g2d.fillRect(0,0,getWidth(),getHeight());
        g2d.setColor(tmp);
    }

    /**
     * @param brick
     * @param g2d
     * method which used to draw all the bricks in the game
     */
    private void drawBrick(Brick brick,Graphics2D g2d){

        Color tmp = g2d.getColor();
        g2d.setColor(brick.getInnerColor());
        g2d.fill(brick.getBrick());
        g2d.setColor(brick.getBorderColor());
        g2d.draw(brick.getBrick());
        g2d.setColor(tmp);
    }

    /**
     * @param ball
     * @param g2d
     * method which used to draw the ball in the game
     */
    private void drawBall(Ball ball, Graphics2D g2d){

        Color tmp = g2d.getColor();
        Shape s = ball.getBallFace();
        g2d.setColor(ball.getInnerColor());
        g2d.fill(s);
        g2d.setColor(ball.getBorderColor());
        g2d.draw(s);
        g2d.setColor(tmp);
    }

    /**
     * @param p
     * @param g2d
     * method which used to draw the paddle in game
     */
    private void drawPaddle(Paddle p, Graphics2D g2d){

        Color tmp = g2d.getColor();
        Shape s = p.getPaddleFace();
        g2d.setColor(Paddle.INNER_COLOR);
        g2d.fill(s);
        g2d.setColor(Paddle.BORDER_COLOR);
        g2d.draw(s);
        g2d.setColor(tmp);
    }

    /**
     * @param g2d
     * method used to draw the Pause Menu in game
     */
    private void drawMenu(Graphics2D g2d){

        obscureGameBoard(g2d);
        drawPauseMenu(g2d);
    }

    /**
     * @param g2d
     * method to design the components of the Pause Menu in game
     */
    private void obscureGameBoard(Graphics2D g2d){

        Composite tmp = g2d.getComposite();
        Color tmpColor = g2d.getColor();
        AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.55f);
        g2d.setComposite(ac);
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0,0,DEF_WIDTH,DEF_HEIGHT);
        g2d.setComposite(tmp);
        g2d.setColor(tmpColor);
    }

    /**
     * @param g2d
     * method which to draw the locations of components and designs in the Pause Menu
     */
    private void drawPauseMenu(Graphics2D g2d){

        Font tmpFont = g2d.getFont();
        Color tmpColor = g2d.getColor();
        g2d.setFont(menuFont);
        g2d.setColor(MENU_COLOR);

        if(strLen == 0){
            FontRenderContext frc = g2d.getFontRenderContext();
            strLen = menuFont.getStringBounds(PAUSE,frc).getBounds().width;
        }

        int x = (this.getWidth() - strLen) / 2;
        int y = this.getHeight() / 10;

        g2d.drawString(PAUSE,x,y);

        x = this.getWidth() / 8;
        y = this.getHeight() / 4;

        if(continueButtonRect == null){
            FontRenderContext frc = g2d.getFontRenderContext();
            continueButtonRect = menuFont.getStringBounds(CONTINUE,frc).getBounds();
            continueButtonRect.setLocation(x,y-continueButtonRect.height);
        }

        g2d.drawString(CONTINUE,x,y);

        y *= 2;

        if(restartButtonRect == null){
            restartButtonRect = (Rectangle) continueButtonRect.clone();
            restartButtonRect.setLocation(x,y-restartButtonRect.height);
        }

        g2d.drawString(RESTART,x,y);

        y *= 3.0/2;

        if(exitButtonRect == null){
            exitButtonRect = (Rectangle) continueButtonRect.clone();
            exitButtonRect.setLocation(x,y-exitButtonRect.height);
        }

        g2d.drawString(EXIT,x,y);
        g2d.setFont(tmpFont);
        g2d.setColor(tmpColor);
    }

    /**
     * @param keyEvent
     * all the methods below listen to key and mouse events
     * which function the paddle in game and buttons in Pause Menu
     */
    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        switch(keyEvent.getKeyCode()){
            case KeyEvent.VK_A:
            case KeyEvent.VK_LEFT:
                wall.paddle.moveLeft();
                break;
            case KeyEvent.VK_D:
            case KeyEvent.VK_RIGHT:
                wall.paddle.movRight();
                break;
            case KeyEvent.VK_ESCAPE:
                showPauseMenu = !showPauseMenu;
                repaint();
                gameTimer.stop();
                break;
            case KeyEvent.VK_SPACE:
                if(!showPauseMenu)
                    if(gameTimer.isRunning()) {
                        gameTimer.stop();
                        message = String.format("         GAME PAUSED");
                        repaint();
                    }
                    else
                        gameTimer.start();

                break;
            case KeyEvent.VK_F1:
                if(keyEvent.isAltDown() && keyEvent.isShiftDown())
                    debugConsole.setVisible(true);
            default:
                wall.paddle.stop();
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        wall.paddle.stop();
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        Point p = mouseEvent.getPoint();
        if(!showPauseMenu)
            return;
        if(continueButtonRect.contains(p)){
            showPauseMenu = false;
            repaint();
        }
        else if(restartButtonRect.contains(p)){
            message = "Restarting Game...";
            wall.ballReset();
            wall.wallReset();
            showPauseMenu = false;
            repaint();
        }
        else if(exitButtonRect.contains(p)){
            System.exit(0);
        }

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseMoved(MouseEvent mouseEvent) {
        Point p = mouseEvent.getPoint();
        if(exitButtonRect != null && showPauseMenu) {
            if (exitButtonRect.contains(p) || continueButtonRect.contains(p) || restartButtonRect.contains(p))
                this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            else
                this.setCursor(Cursor.getDefaultCursor());
        }
        else{
            this.setCursor(Cursor.getDefaultCursor());
        }
    }

    /**
     * method which used when the game lost focus
     */
    public void onLostFocus(){
        gameTimer.stop();
        message = "         GAME PAUSED";
        repaint();
    }

}
