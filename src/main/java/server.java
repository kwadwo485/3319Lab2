import javax.crypto.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class server {

    public static void main(String[] args) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException {
        ServerSocket ss = new ServerSocket(4999);
        Socket s = ss.accept();
        System.out.println("Successfully connected to client...\n\n");

        //from client
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);

        //UTF (server client communication 2)
        InputStream inputStream = s.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        String readHmacDigest = dataInputStream.readUTF();
        System.out.println("Received HMAC digest: " +readHmacDigest);

        String str = bf.readLine();

        //For human observer
        System.out.println("Client: Encrypted plain text - " +str);

       Cipher cipherObject = Cipher.getInstance("DES");
       KeyGenerator kg = KeyGenerator.getInstance("DES");
       SecretKey sk = kg.generateKey();
       cipherObject.init(Cipher.DECRYPT_MODE, sk);

        String str2 = "";
        byte[] receivedText = str2.getBytes();
        byte[] decodedMessage = cipherObject.doFinal(receivedText);//main problem: can't decode str
        System.out.println("Decoded message: " +new String(decodedMessage));

        //server back to client
        PrintWriter pr = new PrintWriter(s.getOutputStream());
        pr.println("Server says - " +new String (decodedMessage));
        pr.flush();
    }

}
