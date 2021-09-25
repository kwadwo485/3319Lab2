import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class client {
    public static void main(String[] args) throws IOException {
        Socket s = new Socket("localhost", 4999);

        //client to server
        PrintWriter pr = new PrintWriter(s.getOutputStream());
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter message to be encrypted: ");
        String message = sc.nextLine();
        pr.println(message);
        pr.flush();

        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        String str = bf.readLine();
        System.out.println("Server response: " +str);
    }
}
