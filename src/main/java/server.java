import sun.misc.BASE64Decoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class server {

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        ServerSocket ss = new ServerSocket(4999);
        ServerSocket ss2 = new ServerSocket(1025);
        Socket s = ss.accept();
        Socket s2 = ss2.accept();
        System.out.println("Successfully connected to client...\n\n");
        File hmacKeyFile = new File("C:/Users/Kwadwo/Desktop/hmacKey.txt");
        String hmacKey = "";
        Scanner fileScanner = new Scanner(hmacKeyFile);
        while(fileScanner.hasNext()){
            hmacKey = fileScanner.next();
        }
        Mac sha256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec sks = new SecretKeySpec(hmacKey.getBytes(),"HmacSHA256");
        sha256.init(sks);
        //once successful decryption of message: String hmacDigest = encodeBase64String(sha256.doFinal(message.getBytes()));

        //ciphertext from client via buffer reader
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        //hmac digest from client via readUTF (server client communication 2)
        InputStream inputStream = s.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        String readHmacDigest = dataInputStream.readUTF();
        System.out.println("Received HMAC digest: " +readHmacDigest);

        //secret key sk from client via UTF port 1025
        InputStream inputStream2 = s2.getInputStream();
        DataInputStream dataInputStream2 = new DataInputStream(inputStream2);
        String originalMessage = dataInputStream2.readUTF();
        System.out.println("Original message: " +originalMessage);

        //storing buffer reader ciphertext in str
        String str = bf.readLine();
        String str2 = "";
        System.out.println("Client: Encrypted plain text - " +str);

        //DES encryption
       KeyGenerator kg = KeyGenerator.getInstance("DES");
       SecretKey sk = kg.generateKey();
       Cipher cipherObject = Cipher.getInstance("DES");
        cipherObject.init(Cipher.ENCRYPT_MODE, sk);
        byte[] receivedText = originalMessage.getBytes();
        byte[] encryptionConfirmation = cipherObject.doFinal(receivedText);
        cipherObject.init(Cipher.DECRYPT_MODE, sk);
        byte[] decodedMessage = cipherObject.doFinal(encryptionConfirmation);//main problem: can't decode str
        System.out.println("Decoded message: " +new String(decodedMessage));

        //server back to client
        PrintWriter pr = new PrintWriter(s.getOutputStream());
        pr.println("Server says - " +new String (decodedMessage));
        pr.flush();
    }

}

//        BASE64Decoder decoder = new BASE64Decoder();
//        byte[] encodedKey = decoder.decodeBuffer(skFromServer);
//        Key oldKey = new SecretKeySpec(encodedKey,0,encodedKey.length, "DES");
//        System.out.println("Old key: " +oldKey);
