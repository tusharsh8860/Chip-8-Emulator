package Main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import javax.swing.JPanel;

public class EmulatorPanel extends JPanel implements Runnable{
    //screen variables
    public final int screenWidth = 64;
    public final int screenHeight = 32; 

    public final int scale = 10;

    public final int scaleWidth = screenWidth * scale;
    public final int scaleHeight = screenHeight * scale;

    //FPS   and emulation cycle speed (emulator fps)
    public final int FPS =60;
    public int frames =0;
    public int cycleSpeed =10;

    //OBjects
    Chip8 chip;
    Thread gameThread;

    public EmulatorPanel(Chip8 chip)
    {   
        this.chip = chip;
        this.setPreferredSize(new Dimension(scaleWidth, scaleHeight));
        this.setBackground(Color.BLACK);
        this.addKeyListener(chip.keypad);
        this.requestFocusInWindow(true);
        this.setFocusable(true);
        this.requestFocus();
        startGameThread();

    }
    public void startGameThread()
    {   
        requestFocusInWindow();
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() 
    {     
        //GameLoop Variables and Delta Method
        double drawInterval = 1000000000/FPS;
        long lastTime = System.nanoTime();
        long currentTime;
        double delta = 0;
        long fpstimer = 0;

        while(gameThread != null)
        {      
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime)/drawInterval;
            fpstimer += currentTime - lastTime;
            lastTime = currentTime;
            if(delta >= 1)
            {
                delta-- ;
                frames++;
                update();
                repaint();
            }
            if(fpstimer >= 1000000000)
            {
                System.out.println(frames);
                frames =0;
                fpstimer = 0;
            }
            try
            {
                Thread.sleep(1);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            
        }
    }

    public void update()
    {
        for(int i =0; i<cycleSpeed; i++)
        {
            chip.emulateCycle();
        }
        if(chip.delayTimer>0)
        {
            chip.delayTimer--;
        }
        if(chip.soundTimer>0)
        {   
            if(chip.soundTimer ==1)
            {
                Toolkit.getDefaultToolkit().beep();
            }
            chip.soundTimer--;
        }
    }
    @Override
    public void paintComponent(Graphics g)
    {   
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for(int y =0; y<chip.display.pixels.length; y++)
        {
            for(int x =0; x<chip.display.pixels[0].length; x++)
            {
                if(chip.display.pixels[y][x])
                {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(x * scale, y * scale, scale, scale);
                }
            }
        }
        g2.dispose();
    }

}
