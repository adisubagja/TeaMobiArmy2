package team;

import army2.server.SerArmy;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author ASD
 */
public class TeamImageOutput {

    private ArrayList<String> names;
    private ArrayList<String> files;

    public TeamImageOutput() {
        this.names = new ArrayList<>();
        this.files = new ArrayList<>();
    }
    
    public final void addFile(String name, String file) {
        this.names.add(name);
        this.files.add(file);
    }
    
    public final byte[] output() {
        byte[] output;
        try {
            ByteArrayOutputStream bas = new ByteArrayOutputStream();
            DataOutputStream ds = new DataOutputStream(bas);
            ByteArrayOutputStream data = new ByteArrayOutputStream();
            ds.writeByte(names.size());
            for(int i = 0; i < names.size(); i++) {
                String name = names.get(i);
                ds.writeByte(name.length());
                byte[] namePack = name.getBytes();
                TeamImageData.pack(namePack);
                ds.write(namePack, 0, namePack.length);
                byte[] ab = SerArmy.getFile(files.get(i));
                if(ab == null)
                    throw new IOException("File "+name+" not found!");
                ds.writeShort(ab.length);
                data.write(ab, 0, ab.length);
            }
            byte[] dataPack = data.toByteArray();
            TeamImageData.pack(dataPack);
            ds.write(dataPack, 0, dataPack.length);
            output = bas.toByteArray();
            data.close();
            bas.close();
            ds.close();
        } catch(IOException e) {
            output = null;
            e.printStackTrace();
        }
        return output;
    }

}
