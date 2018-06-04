package team;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author ASD
 */
public final class TeamImageInput {

    private String[] names;
    private int[]    dataStarts;
    private int[]    dataLents;
    private byte[]   data;

    public TeamImageInput(byte ab[]) {
        try {
            ByteArrayInputStream bas = new ByteArrayInputStream(ab);
            DataInputStream ds = new DataInputStream(bas);
            int dataLent = 0;
            int lent = ds.readUnsignedByte();
            names = new String[lent];
            dataStarts = new int[lent];
            dataLents = new int[lent];
            for(int i = 0; i < lent; i++) {
                byte data[] = new byte[ds.readByte()];
                ds.read(data);
                TeamImageData.pack(data);
                names[i] = new String(data);
                dataStarts[i] = dataLent;
                dataLents[i] = ds.readUnsignedShort();
                dataLent += dataLents[i];
            }
            data = new byte[dataLent];
            ds.readFully(data);
            TeamImageData.pack(data);
            bas.close();
            ds.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public final byte[] getData(String name) {
        for(int i = 0; i < names.length; i++) {
            if(names[i].compareTo(name) == 0) {
                byte[] ab = new byte[dataLents[i]];
                System.arraycopy(data, dataStarts[i], ab, 0, dataLents[i]);
                return ab;
            }
        }
        System.out.println("File \"" + name + "\" not found!");
        return null;
    }

}
