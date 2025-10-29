package Main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.HashMap;

public class Keypad implements KeyListener{

    public boolean keys[] = new boolean[16];
    private HashMap <Integer, Integer> keyMap;

    public Keypad()
    {   //keys of chip 8 / controls
        //    CHIP-8 Keypad Layout
        // 1 2 3 C    =>0x1 0x2 0x3 0xC     => 1 2 3 4  
        // 4 5 6 D    =>0x4 0x5 0x6 0xD     => Q W E R  
        // 7 8 9 E    =>0x7 0x8 0x9 0xE     => A S D F  
        // A 0 B F    =>0xA 0x0 0xB 0xF     => Z X C V  
        
        keyMap = new HashMap<>();
        keyMap.put(KeyEvent.VK_1, 0x1);
        keyMap.put(KeyEvent.VK_2, 0x2);
        keyMap.put(KeyEvent.VK_3, 0x3);
        keyMap.put(KeyEvent.VK_Q, 0x4);

        keyMap.put(KeyEvent.VK_W, 0x5);
        keyMap.put(KeyEvent.VK_E, 0x6);
        keyMap.put(KeyEvent.VK_A, 0x7);
        keyMap.put(KeyEvent.VK_S, 0x8);

        keyMap.put(KeyEvent.VK_D, 0x9);
        keyMap.put(KeyEvent.VK_Z, 0xA);
        keyMap.put(KeyEvent.VK_X, 0x0);
        keyMap.put(KeyEvent.VK_C, 0xB);

        keyMap.put(KeyEvent.VK_4, 0xC);
        keyMap.put(KeyEvent.VK_R, 0xD);
        keyMap.put(KeyEvent.VK_F, 0xE);
        keyMap.put(KeyEvent.VK_V, 0xF);
    }
    @Override
    public void keyTyped(KeyEvent e) 
    {
        
    }

    @Override
    public void keyPressed(KeyEvent e) 
    {
        Integer code = keyMap.get(e.getKeyCode());
        if(code != null)
        {
            keys[code] = true;
        }
        System.out.println("Key Pressed: " + KeyEvent.getKeyText(e.getKeyCode()) + " => Chip8 code: " + code);

    }   

    @Override
    public void keyReleased(KeyEvent e) 
    {
        Integer code = keyMap.get(e.getKeyCode());
        
        if(code != null)
        {
            keys[code] = false;
        }
    }

    //checks if the key is pressed or not
    public boolean isKeyPressed(int index)
    {   
        if(index < 0 || index >= keys.length)
        {
            return false;
        }
        return keys[index];
    }

    //reset all keys
    public void reset()
    {
        Arrays.fill(keys, false);
    }
}
