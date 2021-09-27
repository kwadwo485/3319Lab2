import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import static org.apache.commons.codec.binary.Base64.encodeBase64String;

public class client {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException,
            NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Socket s = new Socket("localhost", 4999);

        //client to server
        PrintWriter pr = new PrintWriter(s.getOutputStream());
        //PrintWriter pr2 = new PrintWriter(s.getOutputStream());
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter message to be encrypted: ");
        File hmacKeyFile = new File("C:/Users/Kwadwo/Desktop/hmacKey.txt");
        String hmacKey = "";
        Scanner fileScanner = new Scanner(hmacKeyFile);
        while(fileScanner.hasNext()){
            hmacKey = fileScanner.next();
        }
        String message = sc.nextLine();
        byte[] plainText = message.getBytes();

        //UTF
        OutputStream outputStream = s.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        Mac sha256 = Mac.getInstance("HmacSHA256");
        Cipher cipherObject = Cipher.getInstance("DES");

        SecretKeySpec sks = new SecretKeySpec(hmacKey.getBytes(),"HmacSHA256");
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        sha256.init(sks);
        SecretKey sk = kg.generateKey();

        //using UTF for hmac digest
        String hmacDigest = encodeBase64String(sha256.doFinal(message.getBytes()));
        dataOutputStream.writeUTF(hmacDigest);
        dataOutputStream.flush(); // send the message

        cipherObject.init(Cipher.ENCRYPT_MODE, sk);
        byte[] encodedMessage = cipherObject.doFinal(plainText);

        //for human observer
        System.out.println("Message: " +message);
        System.out.println("HMAC Key: " +hmacKey);
        System.out.println("HMAC digest: " +hmacDigest);
        System.out.println("Cipher text:" + new String(encodedMessage));

        //send cipher text to server
        pr.println(new String(encodedMessage) +"\n");
        pr.flush();

        //get decoded text from server
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        String str = bf.readLine();
        //response from server
        System.out.println("Server: Decoded message - " +str);

        //closing UTF
        dataOutputStream.close(); // close the output stream when we're done.
    }
}
