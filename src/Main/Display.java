package Main;

public class Display {
    
    public final int screenWidth = 64;
    public final int screenHeight = 32;

    public boolean pixels[][];
    
    //creates the array using constructor
    public Display()
    {
        pixels = new boolean[screenHeight][screenWidth];
    }
    // clears whole display
    public void clear()
    {
        for(int y =0; y<screenHeight; y++)
        {
            for(int x =0; x<screenWidth; x++)
            {
                pixels[y][x] = false;
            }
        }
    }

    //sets the value of pixel without wrapping 
    public void setPixel(int x, int y, boolean value)
    {
        pixels[y][x] = value;
    }
    
    //reads pixel flag
    public boolean getPixel(int x, int y)
    {   
        x = (x + screenWidth) % screenWidth; // wrapped with screenwidth to bypass negative values
        y = (y + screenHeight) % screenHeight; // to bypass negative y values
        return pixels[y][x];
    }

    //reverse the value of pixel
    public boolean xorPixel(int x, int y, boolean value)
    {   
        x = (x + screenWidth) % screenWidth; // wrapped with screenwidth to bypass negative values
        y = (y + screenHeight) % screenHeight; // to bypass negative y values
        pixels[y][x] ^= value;
        return !pixels[y][x];
    }
    public void scrollDown(int n) {
        for(int y =31; y>= n; y--)
        {
            for(int x = 0; x< 64; x++)
            {
                pixels[y][x] = pixels[y-n][x];
            }
        }
        //clear top n lines
        for(int y =0; y<n; y++)
        {
            for(int x =0; x<64; x++)
            {
                pixels[y][x] = false;
            }
        }
    }

}
