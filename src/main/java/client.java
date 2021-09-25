import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;

import static java.util.Base64.*;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

public class client {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Socket s = new Socket("localhost", 4999);

        //client to server
        PrintWriter pr = new PrintWriter(s.getOutputStream());
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter message to be encrypted: ");
        String secret = "admin";
        String message = sc.nextLine();
        Mac sha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec sks = new SecretKeySpec(secret.getBytes(),"HmacSHA256");
        sha256.init(sks);
        
        String hmacMessage = encodeBase64String(sha256.doFinal(message.getBytes()));
        
        pr.println(hmacMessage);
        pr.flush();

        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        String str = bf.readLine();
        //response from server
        System.out.println("Response: " +str);
    }
}
