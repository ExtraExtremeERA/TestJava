import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Optional;

//Just another temporary experimenting class...
class VarHandleTest {
    int v = 152;
    byte[] data = new byte[]{99,0,0,0,0,0,0,1,99,99,99,99,99,99,99,-127};
    byte[] data1 = new byte[]{-8,0,-2,0,2,0,-2,0,10,12,5,1};
    byte[] data2 = new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,127,1};
    private static final VarHandle VH_ARR_EL;
    private static final VarHandle VH_LONG;
    static {
        try {
            VH_ARR_EL = MethodHandles.arrayElementVarHandle(byte[].class);
            VH_LONG = MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.nativeOrder());
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Test
    void vh() {
        //System.out.println(varHandlesShort());
        trace(data2, 0);
        //System.out.println(a());
    }

    public void trace(byte[] bb, int idx) {
        System.out.println("iters: " + bb.length / 8);
        for (int i = 0; i < bb.length / 8; i++) {
            System.out.println(Long.toBinaryString((long) VH_LONG.get(bb, idx)));
            System.out.println((~(long) VH_LONG.get(bb, idx) | 0x4040404040404040L |
                    0x2020202020202020L | 0x1010101010101010L | 0x808080808080808L | 0x404040404040404L | 0x202020202020202L |
                    0x101010101010101L) );
            //System.out.println();
        }
    }

    public boolean varHandlesLong() {
        int li;
        for(li = 0; li < data.length / 8; li++) {
            if (((long) VH_LONG.get(data, li) & 0x8080808080808080L) != 0) return false;
        }
        int bi = li * 8;
        for(; bi < data.length; bi++) {
            if(data[bi] < 0) return false;
        }
        return data[bi - 1] < 0;
    }

    public int a() {
        int a = 0;
        for (int i = 0; i < data1.length / 8; i++) {
            if ((~(long) VH_LONG.get(data1, i) | 0x4040404040404040L |
                    0x2020202020202020L | 0x1010101010101010L | 0x808080808080808L | 0x404040404040404L | 0x202020202020202L |
                    0x101010101010101L) != 0xFFFFFFFFFFFFFFFFL) break;
            a++;
        }
        System.out.println(a);
        final int b = a * 8;
        System.out.println(b);
        int c = 0;
        for (int i = 0; i < data1.length - b - 1; i++) {
            if (data1[data1.length - b] != 0) break;
            c++;
        }
        return b + c;

    }
}
