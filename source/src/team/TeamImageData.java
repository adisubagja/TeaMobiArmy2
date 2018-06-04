package team;

/**
 *
 * @author ASD
 */
public class TeamImageData {

    private static final byte[] packData;

    static {
        packData = new byte[]{78, 103, 117, 121, 101, 110, 86, 97, 110, 77, 105, 110, 104};
    }

    protected static void pack(byte ab[]) {
        int lent = ab.length;
        int packDataLent = packData.length;
        for(int i = 0; i < lent; i++)
            ab[i] = (byte)(ab[i] ^ packData[i % packDataLent]);
    }

}

