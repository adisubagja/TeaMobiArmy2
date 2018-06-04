package army2.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author ASD
 */
public class SerArmy {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        System.out.println("Start server!");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Shutdown Server!");
                ServerManager.stop();
            }
        }));
        ServerManager.init();
        ServerManager.start();
    }

    public static byte[] getFile(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            byte[] ab = new byte[fis.available()];
            fis.read(ab, 0, ab.length);
            fis.close();
            return ab;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveFile(String url, byte[] ab) {
        try {
            File f = new File(url);
            if(f.exists())
                f.delete();
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(url);
            fos.write(ab);
            fos.flush();
            fos.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}

