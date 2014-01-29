/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package defenduvic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.*;
import java.net.URL;
import javax.sound.sampled.*;
import java.applet.*;
import java.net.*;

/**
 *
 * @author Nick
 */
public class DefendUvic extends Canvas implements MouseListener {

    private BufferStrategy strategy;	// take advantage of accelerated graphics
    final int screenWidth = 800;
    final int screenHeight = 720;
    final int levelMessageTime = 2000;      // how long the wave end message is on the screen
    final int schoolWall = 500;             // number of pixels from left side of screen to wall
    private boolean gameRunning = true;
    private long startTime = 0;
    private int clicked = 0;
    private int updateInterval = 10;	// milliseconds
    private int level = 1;
    private int wave = 2;			// number of guys to spawn this level
    private long lastSpawn = 0;			// last time an enemy spawned
    final long spawnDelay = 250;		// how long to wait until spawning another enemy
    int kills = 0;
    int damage = 0;
    boolean usingEnemies = false;
    Font smallFont;
    Font bigFont;
    BufferedImage[] images;			// all images used
    final int bunnyRace = 13;			// white/brown/grey
    int[] firstBunnyImage = new int[bunnyRace];
    int bunnyImageCount;
    int bunnyW;
    int bunnyH;      
    Clip[] sounds;
    Clip clip2;
    boolean gamePause = true;
    boolean dead = false;
    boolean win = false;
    boolean pauseScreen = false;
    long levelCompleteTime = 0;
    
    public class Enemy {

        int x;
        int y;
        int w;
        int h;
        int imageCount;
        int firstImage;
        int currentImage;			// what animation the enemy is currently on
        public int health = 1;

        Enemy(int X, int Y, int W, int H, int ImageCount, int FirstImage) {
            x = X;
            y = Y;
            w = W;
            h = H;
            imageCount = ImageCount;
            firstImage = FirstImage;
            currentImage = firstImage;
        }
    }
    // create enemy array
    Enemy[] enemies = new Enemy[0];

    public DefendUvic() {
        // create a frame to contain game
        JFrame container = new JFrame("Game");

        // get hold the content of the frame
        JPanel panel = (JPanel) container.getContentPane();

        // set up the resolution of the game
        panel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        panel.setLayout(null);

        // set up canvas size (this) and add to frame
        setBounds(0, 0, 800, 720);
        panel.add(this);

        // Tell AWT not to bother repainting canvas since that will
        // be done using graphics acceleration
        setIgnoreRepaint(true);

        // make the window visible
        container.pack();
        container.setResizable(false);
        container.setVisible(true);

        // if user closes window, shutdown game and jre
        container.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            } // windowClosing
        });

        // add key listener to this canvas
        addKeyListener(new KeyInputHandler());

        // add mouse listener to this canvas
        addMouseListener(this);

        // request focus so key events are handled by this canvas
        requestFocus();

        // create buffer strategy to take advantage of accelerated graphics
        createBufferStrategy(2);
        strategy = getBufferStrategy();

        // create font
        smallFont = new Font("Helvetica", Font.PLAIN, 24);
        bigFont = new Font("Helvetica", Font.PLAIN, 150);


        // load images
        try {
            images = new BufferedImage[34];

            images[0] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/background.png"));	// background

            firstBunnyImage[0] = 1;
            images[1] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny1a.png"));		// white bunny 1
            images[2] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny1b.png"));		// white bunny 2

            firstBunnyImage[1] = 3;
            images[3] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny2a.png"));		// brown bunny 1
            images[4] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny2b.png"));		// brown bunny 2

            firstBunnyImage[2] = 5;
            images[5] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny3a.png"));		// grey bunny 1
            images[6] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny3b.png"));		// grey bunny 2

            firstBunnyImage[3] = 7;
            images[7] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny4a.png"));		
            images[8] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny4b.png"));
            
            firstBunnyImage[4] = 9;
            images[9] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny5a.png"));		
            images[10] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny5b.png"));
            
            firstBunnyImage[5] = 11;
            images[11] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny6a.png"));		
            images[12] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny6b.png"));
            
            firstBunnyImage[6] = 13;
            images[13] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny7a.png"));		
            images[14] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny7b.png"));
            
            firstBunnyImage[7] = 15;
            images[15] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny8a.png"));		
            images[16] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny8b.png"));         
            
            firstBunnyImage[8] = 17;
            images[17] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny9a.png"));		
            images[18] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny9b.png"));
            
            firstBunnyImage[9] = 19;
            images[19] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny10a.png"));		
            images[20] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny10b.png"));
            
            firstBunnyImage[10] = 21;
            images[21] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny11a.png"));		
            images[22] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny11b.png"));             

            firstBunnyImage[11] = 23;
            images[23] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny12a.png"));		
            images[24] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/bunny12b.png"));  
            
            firstBunnyImage[12] = 25;
            images[25] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/boss1a.png"));
            images[26] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/boss1b.png"));
            
            images[27] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/wave3.jpg"));
            images[28] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/wave6.jpg"));
            images[29] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/wave9.jpg"));
            images[30] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/wave12.jpg"));  
            images[31] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/defeat.jpg"));
            images[32] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/intro.jpg"));
            images[33] = ImageIO.read(DefendUvic.class.getResourceAsStream("/img/victory.jpg"));           
            
            bunnyImageCount = 2;

            bunnyW = images[1].getWidth();
            bunnyH = images[1].getHeight();
        } catch (IOException e) {
            System.out.println("Image Error");
            System.exit(-1);
        }
        
        try {                                        
            // recommended
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(DefendUvic.class.getResourceAsStream("calm1.wav"));
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.loop(Clip.LOOP_CONTINUOUSLY);      
            
            AudioInputStream audioIn2 = AudioSystem.getAudioInputStream(DefendUvic.class.getResourceAsStream("gun.wav")); 
            clip2 = AudioSystem.getClip();
            clip2.open(audioIn2);
            
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }      
        
        // start the game
        gameLoop();
    } // constructor

    /*
     * gameLoop input: none output: none purpose: Main game loop. Runs
     * throughout game play. Responsible for the following activities: -
     * calculates speed of the game loop to update moves - moves the game
     * entities - draws the screen contents (entities, text) - updates game
     * events - checks input
     */
    public void gameLoop() {
        long lastLoopTime = System.currentTimeMillis();
                
        // keep loop running until game ends
        while (gameRunning) {
            // calc. time since last update, will be used to calculate movement
            long delta = System.currentTimeMillis() - lastLoopTime;             
            lastLoopTime = System.currentTimeMillis();            
           
            if (!gamePause) {
                addWave(lastLoopTime);

                // get graphics context for the accelerated surface and make it black
                Graphics2D g = (Graphics2D) strategy.getDrawGraphics();                
                
                g.drawImage(images[0], 0, 0, null);

                while (usingEnemies) {
                    pause(1);    
                }

                usingEnemies = true;         
                moveEnemies(delta);
                drawEnemies(g);
                usingEnemies = false;

                g.setFont(smallFont);
                g.drawString(Integer.toString(kills), 20, 30);
                g.drawString("Wave: " + level, 350, 30);
     
                if ((levelCompleteTime + levelMessageTime) > System.currentTimeMillis()) {
                    int time = ((int)(System.currentTimeMillis() - levelCompleteTime));
                    int alpha = 255 - ((time * 255)/levelMessageTime);
                    
                    g.setColor(new Color (255, 0, 0, alpha));
                    g.drawString("Wave " + (level - 1) + " Completed!", 305, 300);    
                }

                damageCastle(g);

                checkLevel(g);

                // clear graphics and flip buffer
                g.dispose();
                strategy.show();

                pause(updateInterval);    
            } else {
                Graphics2D g = (Graphics2D) strategy.getDrawGraphics();       
               
                if (pauseScreen) {                    
                    g.setColor(Color.RED);
                    g.setFont(bigFont);
                    g.drawString("PAUSE", 150, 360); 
                    g.setFont(smallFont);
                    g.drawString("Press any key to continue.", 245, 390);                    
                } else if (dead) {
                    g.drawImage(images[31], 0, 0, null);                 
                } else if (win) {
                    g.drawImage(images[33], 0, 0, null);    
                } else if (level == 4) {      
                    g.drawImage(images[27], 0, 0, null);              
                } else if (level == 7) {
                    g.drawImage(images[28], 0, 0, null);                     
                } else if (level == 10) {
                    g.drawImage(images[29], 0, 0, null);                      
                } else if (level == 13) {
                    g.drawImage(images[30], 0, 0, null);                    
                } else {
                    g.drawImage(images[32], 0, 0, null);    // intro
                }             
                
                g.dispose();
                strategy.show();                   
            }
        } // while
    } // gameLoop

    // pause
    public static void pause(int time) {
        try {
            Thread.currentThread().sleep(time);
        } catch (Exception e) {
        } // catch
    } // wait
    
    public void checkLevel(Graphics2D g) {                
        if (level > 13) {
            win = true;
            gamePause = true;
        }
    }

    public void damageCastle(Graphics2D g) {
        if (100 - (damage / 20) > 0) {
            g.setColor(Color.RED);
            g.fillRect(screenWidth - 115, 10, 100 - (damage / 20), 20);
        } else {
            gamePause = true;
            dead = true;
        }          
    }

    // stop at front of of uvic
    public void moveEnemies(long delta) {
        for (int e = 0; e < enemies.length; e++) {            
            // stop bunny once they get to uvic
            if (enemies[e].x < (schoolWall - enemies[e].w)) {
                enemies[e].x += delta / updateInterval;
            } else {
                damage++;
            }
        }
    }

    public void drawEnemies(Graphics2D g) {
        for (int e = 0; e < enemies.length; e++) {
            g.drawImage(images[enemies[e].currentImage], enemies[e].x, enemies[e].y, null);

            enemies[e].currentImage++;

            if ((enemies[e].currentImage - enemies[e].firstImage) >= enemies[e].imageCount) {
                enemies[e].currentImage = enemies[e].firstImage;
            }
        }
    }

    // using wave figure out how many enemies to add
    public void addWave(long currentTime) {
        //check if level is over
        if ((enemies.length == 0) && (wave == 0)) {
            level++;
            
            levelCompleteTime = System.currentTimeMillis();            
            
            // number of bunnies per wave
            wave = level * 2;
            if (level == 4) {      
                gamePause = true; 
            } else if (level == 7) {
                gamePause = true;
            } else if (level == 10) {
                gamePause = true;
            } else if (level == 13) {
                wave = 51;               // only one boss bunny
                gamePause = true;   
            }
        } 

        if (wave == 0 || level > 13) {
            return;
        }

        if ((lastSpawn + spawnDelay) < currentTime) {
            addOneEnemy();
            lastSpawn = currentTime;
            wave--;
        }

    }

    public void addOneEnemy() {
        Enemy[] tempEnemies = new Enemy[enemies.length + 1];

        for (int e = 0; e < enemies.length; e++) {
            tempEnemies[e] = enemies[e];
        }
        
        int randomBunny;
        
        if ((level == 13) && (wave == 21)) { 
            randomBunny = 12;
            bunnyW = images[25].getWidth();
            bunnyH = images[25].getHeight();           
        } else if (level == 13) {
            // small bunnies on boss level
            bunnyW = images[1].getWidth();
            bunnyH = images[1].getHeight();             
            randomBunny = (int)(Math.random() * 12);
        } else {
            randomBunny = (int)(Math.random() * level);
        }
        
        tempEnemies[enemies.length] = new Enemy(-bunnyW, (((int) (Math.random() * (367 - bunnyH))) + 355), bunnyW, bunnyH, bunnyImageCount, firstBunnyImage[randomBunny]);
            
        // boss bunny
        if ((level == 13) && (wave == 21)) {            
            tempEnemies[enemies.length].health = 50;
        }
        
        enemies = tempEnemies;
    }

    public void killEnemy(int deadEnemy) {       
        kills++;

        if (enemies.length == 0) {
            return;
        }

        Enemy[] tempEnemies = new Enemy[enemies.length - 1];

        for (int e = 0; e < deadEnemy; e++) {
            tempEnemies[e] = enemies[e];
        }

        for (int e = (deadEnemy + 1); e < enemies.length; e++) {
            tempEnemies[e - 1] = enemies[e];
        }
        
        while (usingEnemies) {
            pause(1);    
        }
        
        usingEnemies = true;
        enemies = tempEnemies;
        usingEnemies = false;               
    }

    /*
     * inner class KeyInputHandler handles keyboard input from the user
     */
    private class KeyInputHandler extends KeyAdapter {
        public void keyTyped(KeyEvent e) {        
            if (gamePause && (level != 1)) {
                levelCompleteTime = System.currentTimeMillis();   
            }
            
            if (dead || win) {
                System.exit(0);
            }
                      
            gamePause = false;
            pauseScreen = false;
            
            // if escape is pressed, end game            
            if (e.getKeyChar() == 27) {
                System.exit(0);
            } // if escape pressed
            
            if (e.getKeyChar() == 'p') {
                gamePause = true;
                pauseScreen = true;
            } // if escape pressed            
            
        } // keyTyped
    } // class KeyInputHandler

    public void mousePressed(MouseEvent me) {
//       saySomething("Mouse pressed; # of clicks: " + e.getClickCount(), e);

        if (dead) {
            return;
        }
        
        if(pauseScreen) {
            return;
        }
        
        int checkX = me.getX();
        int checkY = me.getY();

        for (int e = 0; e < enemies.length; e++) {
            if ((checkX > enemies[e].x) && (checkX < (enemies[e].x + enemies[e].w))) {
                if ((checkY > enemies[e].y) && (checkY < (enemies[e].y + enemies[e].h))) {
                    //sounds[0].play();
                    if (clip2.isRunning())
                        clip2.stop(); 
                    clip2.setFramePosition(0);
                    clip2.start();
                    if (--enemies[e].health == 0) {
                        killEnemy(e);
                    }                  
                }
            } else {
                // debug
                //System.out.println("CheckX: " + checkX + "// x: " + enemies[e].x + "// w: " + enemies[e].w + "// CheckY: " + checkY + "// y: " + enemies[e].y + "// h: " + enemies[e].h);
            }
        }           
    } // mousePressed

    public void mouseReleased(MouseEvent e) {
//       saySomething("Mouse released; # of clicks: " + e.getClickCount(), e);
    }

    public void mouseEntered(MouseEvent e) {
//       saySomething("Mouse entered", e);
    }

    public void mouseExited(MouseEvent e) {
//       saySomething("Mouse exited", e);
    }

    public void mouseClicked(MouseEvent me) {
//       saySomething("Mouse clicked (# of clicks: " + e.getClickCount() + ")", e);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] argv) {
        new DefendUvic(); // instantiate this object
    }
}
