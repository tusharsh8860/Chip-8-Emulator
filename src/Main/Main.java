package Main;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Main{
    
    public static void main(String[] args) {
        //select a rom file
        JFileChooser fileChooser = new JFileChooser("roms");
        fileChooser.setDialogTitle("Choose a Rom");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(null);
        //if wrong file exit
        if(result != fileChooser.APPROVE_OPTION)
        {
            System.out.println("Not Selected");
            System.exit(0);
        }
        //else store the file 
        File romFile = fileChooser.getSelectedFile();

        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        window.setTitle("Chip 8 emulator");

        //objects
        Chip8 chip = new Chip8();
        chip.loadRoms(romFile.getAbsolutePath());// load the rom path here
        EmulatorPanel ePanel = new EmulatorPanel(chip);

        window.add(ePanel);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

    }
}
