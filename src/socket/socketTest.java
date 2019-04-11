package socket;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class socketTest {
    public static void main(String[] args) throws IOException {
        try(Socket s = new Socket("baidu.com",80);
            Scanner in = new Scanner(s.getInputStream(),"UTF-8"))
        {
            while (in.hasNextLine()){
                String line = in.nextLine();
                System.out.println(line);
            }
        }
    }
}
