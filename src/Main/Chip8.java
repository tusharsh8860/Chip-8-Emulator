package Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Chip8 {

    //chip 8 variables
    byte memory[] = new byte[4096]; //0x0 to 0x1FF are reserved for internal work
    int stack [] = new int[16]; //hold the return address for subroutines
    byte V [] = new byte[16];   //registers
    int sp =0;          // stack pointer
    int pc = 0x200;     // first instruction address
    int indexReg;           // tracks the index of register
    byte delayTimer =0;     //decrements to 60hz when >0 for gamelogic and spite animations
    byte soundTimer =0;     // same for sound
    int opcode;         //16 bit opcode which is the instruction from game rom


    Display display = new Display();
    Keypad keypad = new Keypad();


    public Chip8()
    {   //fontset to display predefined fonts
        byte fontset []= {(byte)0xF0, (byte)0x90, (byte)0x90, (byte)0x90, (byte)0xF0, // 0
    (byte)0x20, (byte)0x60, (byte)0x20, (byte)0x20, (byte)0x70, // 1
    (byte)0xF0, (byte)0x10, (byte)0xF0, (byte)0x80, (byte)0xF0, // 2
    (byte)0xF0, (byte)0x10, (byte)0xF0, (byte)0x10, (byte)0xF0, // 3
    (byte)0x90, (byte)0x90, (byte)0xF0, (byte)0x10, (byte)0x10, // 4
    (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x10, (byte)0xF0, // 5
    (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x90, (byte)0xF0, // 6
    (byte)0xF0, (byte)0x10, (byte)0x20, (byte)0x40, (byte)0x40, // 7
    (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x90, (byte)0xF0, // 8
    (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x10, (byte)0xF0, // 9
    (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x90, (byte)0x90, // A
    (byte)0xE0, (byte)0x90, (byte)0xE0, (byte)0x90, (byte)0xE0, // B
    (byte)0xF0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0xF0, // C
    (byte)0xE0, (byte)0x90, (byte)0x90, (byte)0x90, (byte)0xE0, // D
    (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x80, (byte)0xF0, // E
    (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x80, (byte)0x80  // F
    };
        //load fonset into memory
        for(int i =0; i< fontset.length; i++)
        {
            memory[0x050+i] = fontset[i];
        }
    }
    public void emulateCycle() 
    {
        //making 16bit op code using 0xFF to make it unsigned as there is no opetion in java
        opcode = ((memory[pc] & 0xFF) << 8) | (memory[pc+1] & 0xFF);
        int x;
        int y;
        int nn;
        switch (opcode & 0xF000) {
            case 0x000:
                if(opcode == 0x00E0)    // clear screen
                {
                    display.clear();
                    pc += 2;
                }
                else if(opcode == 0x00EE)   //return from subroutine
                {
                    sp--;
                    pc = stack[sp];
                }
                else if((opcode & 0x00F0) == 0x00C0) //scroll down
                {
                    int n = opcode & 0x000F;
                    display.scrollDown(n);
                }
                break;

            case 0x1000:    // jump to address
                pc = opcode & 0x0FFF;
                break;

            case 0x2000:    // call subroutines

                stack[sp] = pc+2;   //store the address of next instruction 
                sp++;
                pc = opcode & 0x0FFF;//move to subroutine
                break;
            
            case 0x3000:    //check if V[x] == NN value
                x = (opcode & 0x0F00) >> 8; //to get register value 0x0F00
                nn = (opcode & 0x00FF); //to get last digit 0x00FF

                if((V[x] & 0xFF) == nn)
                {
                    pc += 4;    // skip next instruction if equal
                }
                else
                {
                    pc +=2; // if not then move to next instruction
                }
                break;
            
            case 0x4000:    //skip if V[x] != nn
                x = (opcode & 0x0F00) >>8;
                nn = opcode & 0x00FF;
                if((V[x] &0xFF) != nn)
                {
                    pc += 4;
                }
                else
                {
                    pc += 2;
                }
                break;

            case 0x5000:    //skip if V[x] == V[y]
                if((opcode & 0x000F) != 0)
                {
                    break;
                }
                x = (opcode & 0x0F00)>>8;
                y = (opcode & 0x00F0) >>4;
                if((V[x] & 0xFF)==(V[y]& 0xFF))
                {
                    pc += 4;
                }
                else
                {
                    pc += 2;
                }
                break;

            case 0x6000:    //set V[x] == NN
                x = (opcode & 0x0F00) >>8;
                V[x] = (byte)(opcode & 0x00FF);
                pc += 2;
                break;

            case 0x7000:    //add NN to V[x]
                x = (opcode & 0x0F00) >> 8;
                V[x] += (byte)(opcode & 0x00FF);
                pc +=2;
                break;

            case 0x8000: //arithmetic operations
                x = (opcode & 0x0F00) >> 8;
                y = (opcode & 0x00F0) >> 4;
                
                switch (opcode & 0x000F) 
                {
                    case 0x0:   //make them equal
                        V[x] = V[y];
                        break;
                    
                    case 0x1: //perfrom OR 
                        V[x] |= V[y];
                        break;
                    
                    case 0x2: //perform AND
                        V[x] &= V[y];
                        break;
                    
                    case 0x3: //perform XOR
                        V[x] ^= V[y];
                        break;
                    
                    case 0x4:   //add V[x] with V[y] with carry
                        int sum = (V[x] & 0xFF) + (V[y] & 0xFF);
                        if(sum>255)
                        {
                            V[0xF] = (byte)1;
                        }
                        else
                        {
                            V[0xF] =(byte)0;
                        }
                        V[x] = (byte)sum;
                        break;
                    case 0x5: //V[x] - V[y] and set V[f] true if V[x]>= V[y]
                        if((V[x]& 0xFF)>= (V[y] & 0xFF))
                        {
                            V[0xF] = 1;
                        }
                        else
                        {
                            V[0xF] = 0;
                        }
                        V[x] = (byte) ((V[x]& 0xFF) - (V[y] & 0xFF));
                        break;

                    case 0x6: // do V[x] >>1 and V[f] = V[x] LSB (least significant bit) before shift
                        V[0xF] = (byte)(V[x] & 0x1);    // 0x1 bit mask to extract LSB
                        V[x] = (byte)((V[x] & 0xFF) >> 1);
                        break;

                    case 0x7: // V[y] - V[x] and set V[f] true if V[y]>= V[x]
                        if((V[y]& 0xFF) >= (V[x]& 0xFF))
                        {
                            V[0xF] = 1;
                        }
                        else
                        {
                            V[0xF] = 0;
                        }
                        V[x] = (byte)((V[y]& 0xFF)-(V[x]&0xFF));
                        break;

                    case 0xE:   //V[f] MSB (most significant bit)before V[x]<<1
                        V[0xF] = (byte)((V[x]& 0xFF)>>7); //0x80 mask to extract MSB
                        V[x] = (byte)((V[x] & 0xFF)<<1);
                        break;
        
                    default:
                        break;
                }
                pc += 2;
                break;

            case 0x9000:    //skip if V[x] != V[y]
                if((opcode & 0x000F) != 0)
                {
                    break;
                }
                x = (opcode & 0x0F00) >> 8;
                y = (opcode & 0x00F0) >>4;
                if((V[x]& 0xFF) != (V[y]&0xFF))
                {
                    pc += 4;
                }
                else
                {
                    pc += 2;
                }
                break;
                
            case 0xE000://input instructions
                x = (opcode & 0x0F00) >>8;
                int key = V[x] & 0xFF;
                switch (opcode & 0x00FF) 
                {
                    case 0x9E://skip next if V[x] is pressed
                        if(keypad.isKeyPressed(key))
                        {
                            pc += 4;
                        }
                        else
                        {
                            pc += 2;
                        }
                        break;
                    
                    case 0xA1: //skip next if not pressed
                        if(!keypad.isKeyPressed(key))
                        {
                            pc += 4;
                        }
                        else
                        {
                            pc += 2;
                        }
                        break;
                    
                    default:
                        break;
                }
                break;

            case 0xF000: //timers
                x = (opcode & 0x0F00) >> 8;
                switch (opcode & 0x00FF) 
                {
                    case 0x07: // V[x] = delay timer
                        V[x] = delayTimer;
                        break;
                    case 0x15:  // delayTimer = V[x]
                        delayTimer = V[x];
                        break;
                    case 0x18:  ///soundTimer = V[x]
                        soundTimer =V[x];
                        break;
                    case 0x29: // set i to location of sprite for digit in Vx
                        indexReg = (V[x] & 0xFF)*5; //5byte per sprite
                        break;
                    case 0x33: //store BCD of V[x] in memory at i i+1 and i+2
                        int val = V[x] & 0xFF;                     
                        memory[indexReg] = (byte)(val/100);         //if V[x] = 123
                        memory[indexReg +1] = (byte)((val/10)%10);  // then memory[i] = 1; memory[2] = 2;
                        memory[indexReg+2] = (byte)(val%10);        //memory[3] = 3;
                        break;
                    case 0x0A: //wait for key press
                        boolean keyPressed = false;
                        for(int i =0; i<16; i++)
                        {
                            if(keypad.isKeyPressed(i))
                            {
                                V[x] = (byte)i; //store key for executtion if pressed
                                keyPressed = true;
                                break;
                            }
                        }
                        if(!keyPressed)
                        {
                            return;//run again if not pressed
                        }
                        break;
                    case 0x55:  // Store V0 to Vx in memory starting at I
                        for(int i =0; i<=x; i++)
                        {
                            memory[indexReg+i] = V[i];
                        }
                        break;
                    case 0x65:  // Load V0 to Vx from memory starting at I
                        for(int i =0; i<=x; i++)
                        {
                            V[i] = memory[indexReg+i];
                        }
                        break;
                    case 0x1E: //indexReg += V[x]
                        indexReg += (V[x] & 0xFF);
                        break;
                    
                    default:
                        break;
                }
                pc += 2;
                break;   
            
            case 0xA000:    // set indexReg to NNN (address)
                indexReg = (opcode & 0x0FFF);
                pc += 2;
                break;
            
            case 0xB000:    //jump to NNN +V0
                pc = (opcode &0x0FFF) + (V[0] &0xFF);
                break;

            case 0xC000:    //random number logic
                x = (opcode & 0x0F00) >> 8;
                int rand = (int)(Math.random()*256);
                V[x] = (byte)(rand &(opcode & 0x00FF));
                pc += 2;
                break;

            case 0xD000:    //0xDXYH drawing
                x = (opcode & 0x0F00) >> 8;
                y = (opcode & 0x00F0) >> 4;

                int height = (opcode & 0x000F); 
                int posX = V[x] & 0xFF; // make it unsigned to keep the real value
                int posY = V[y] & 0xFF; // keep real value by & with unsigned mask

                V[0xF] = 0;
                for(int row = 0; row<height; row++)
                {   
                    int spriteByte = memory[indexReg + row] & 0xFF;//in case 0xF029 we set index to sprite byte
                    for(int col = 0; col<8; col++)
                    {
                        int spritePixel = (spriteByte >> (7-col)) & 1;
                        if(spritePixel == 1)
                        {
                            boolean erased = display.xorPixel(posX + col, posY + row, true);
                            if(erased)
                            {
                                V[0xF] = 1;
                            }
                        }
                    }
                }
                pc += 2;
                break;

            
            default:
                break;
        }

    }
    public void loadRoms(String filename)
    {
        try 
        {
            File file = new File(filename);
            FileInputStream fis = new FileInputStream(file);
            byte buffer[] = new byte[(int) file.length()];

            fis.read(buffer); //read and write into buffer
            fis.close();

            //load rom into memory
            for(int i =0; i<buffer.length; i++)
            {
                memory[0x200 + i] = buffer[i];
            }
            System.out.println("rom loaded: " + filename + " bytes: " + buffer.length);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    // for reset the chip8 when loading roms while running
    // or for restarting the emulator
    // public void reset()
    // {
    //     //resta all varibales
    //     memory = new byte[4096];
    //     stack = new int[16];
    //     V= new byte[16];
    //     sp = 0;
    //     pc = 0x200;
    //     indexReg = 0;
    //     delayTimer =0;
    //     soundTimer =0;
    //     opcode =0;
    //     display.clear();

    //     byte fontset []= {(byte)0xF0, (byte)0x90, (byte)0x90, (byte)0x90, (byte)0xF0, // 0
    // (byte)0x20, (byte)0x60, (byte)0x20, (byte)0x20, (byte)0x70, // 1
    // (byte)0xF0, (byte)0x10, (byte)0xF0, (byte)0x80, (byte)0xF0, // 2
    // (byte)0xF0, (byte)0x10, (byte)0xF0, (byte)0x10, (byte)0xF0, // 3
    // (byte)0x90, (byte)0x90, (byte)0xF0, (byte)0x10, (byte)0x10, // 4
    // (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x10, (byte)0xF0, // 5
    // (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x90, (byte)0xF0, // 6
    // (byte)0xF0, (byte)0x10, (byte)0x20, (byte)0x40, (byte)0x40, // 7
    // (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x90, (byte)0xF0, // 8
    // (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x10, (byte)0xF0, // 9
    // (byte)0xF0, (byte)0x90, (byte)0xF0, (byte)0x90, (byte)0x90, // A
    // (byte)0xE0, (byte)0x90, (byte)0xE0, (byte)0x90, (byte)0xE0, // B
    // (byte)0xF0, (byte)0x80, (byte)0x80, (byte)0x80, (byte)0xF0, // C
    // (byte)0xE0, (byte)0x90, (byte)0x90, (byte)0x90, (byte)0xE0, // D
    // (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x80, (byte)0xF0, // E
    // (byte)0xF0, (byte)0x80, (byte)0xF0, (byte)0x80, (byte)0x80  // F
    // };
    //     for(int i =0; i< fontset.length; i++)
    //     {
    //         memory[0x050+i] = fontset[i];
    //     }
    // }
}
