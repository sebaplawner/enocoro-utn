package enocoro;

import java.util.Arrays;

public class Enocoro {
    private static short[] state_a = new short[2];
    private static short[] Sbox = {
            99, 82, 26, 223, 138, 246, 174, 85, 137, 231, 208, 45, 189, 1, 36, 120,
            27, 217, 227, 84, 200, 164, 236, 126, 171, 0, 156, 46, 145, 103, 55, 83,
            78, 107, 108, 17, 178, 192, 130, 253, 57, 69, 254, 155, 52, 215, 167, 8,
            184, 154, 51, 198, 76, 29, 105, 161, 110, 62, 197, 10, 87, 244, 241, 131,
            245, 71, 31, 122, 165, 41, 60, 66, 214, 115, 141, 240, 142, 24, 170, 193,
            32, 191, 230, 147, 81, 14, 247, 152, 221, 186, 106, 5, 72, 35, 109, 212,
            30, 96, 117, 67, 151, 42, 49, 219, 132, 25, 175, 188, 204, 243, 232, 70,
            136, 172, 139, 228, 123, 213, 88, 54, 2, 177, 7, 114, 225, 220, 95, 47,
            93, 229, 209, 12, 38, 153, 181, 111, 224, 74, 59, 222, 162, 104, 146, 23,
            202, 238, 169, 182, 3, 94, 211, 37, 251, 157, 97, 89, 6, 144, 116, 44,
            39, 149, 160, 185, 124, 237, 4, 210, 80, 226, 73, 119, 203, 58, 15, 158,
            112, 22, 92, 239, 33, 179, 159, 13, 166, 201, 34, 148, 250, 75, 216, 101,
            133, 61, 150, 40, 20, 91, 102, 234, 127, 206, 249, 64, 19, 173, 195, 176,
            242, 194, 56, 128, 207, 113, 11, 135, 77, 53, 86, 233, 100, 190, 28, 187,
            183, 48, 196, 43, 255, 98, 65, 168, 21, 140, 18, 199, 121, 143, 90, 252,
            205, 9, 79, 125, 248, 134, 218, 16, 50, 118, 180, 163, 63, 68, 129, 235
    };
    private static final short[] C = {0x66,0xe9,0x4b0,0xd4,0xef,0x8a,0x2c,0x3b,0x88,0x4c};
    private static final short[] S4box = {1, 3, 9, 10, 5, 14, 7, 2, 13, 0, 12, 15, 4, 8, 6, 11};
    private static final short[] state_b = new short[32];


    public static void Init(short[] key, short[] iv){
        for (int i = 0;i<16;i++){
            state_b[i] = key[i];
        }
        for (int i = 0;i<8;i++){
            state_b[i+16] = iv[i];
        }
        for (int i = 0;i<8;i++){
            state_b[i+24] = C[i];
        }
        state_a[0] = C[8];
        state_a[1] = C[9];
        short ctr = 1;
        for (int r = 0;r<96;r++){
            state_b[31] ^= ctr;
            ctr = gfmultby02((short) 2);
            Next();
        }
    }


    private static void ro_func(short[] b, short[] a){
        short[] u = new  short[2];
        u[0] = (short) (a[0]^Sbox[b[2]]);
        u[1] = (short) (a[1]^Sbox[b[7]]);
        u = L_tarnsformation(u);
        state_a[0] = (short) (u[0]^Sbox[b[16]]);
        state_a[1] = (short) (u[1]^Sbox[b[29]]);
    }


    private static void lambda_func(short[] a, short[] b){
        state_b[0] = (short) (b[31]^a[0]);
        state_b[1] = b[0];
        state_b[2] = b[1];
        state_b[3] = (short) (b[2]^b[6]);
        state_b[4] = b[3];
        state_b[5] = b[4];
        state_b[6] = b[5];
        state_b[7] = b[6];
        state_b[8] = (short) (b[7]^b[15]);
        state_b[9] = b[8];
        state_b[10] = b[9];
        state_b[11] = b[10];
        state_b[12] = b[11];
        state_b[13] = b[12];
        state_b[14] = b[13];
        state_b[15] = b[14];
        state_b[16] = b[15];
        state_b[17] = (short) (b[16]^b[28]);
        for (int i = 18;i<32;i++){
            state_b[i] = b[i];
        }
    }


    private static short gfmultby02(short b) {
        b = (short) (b << 1);
        if (b >> 8 == 1) {
            b = (short) (b ^ 0x11d);
        }
        return b;
    }


    private static short[] L_tarnsformation(short[] u){
    short[] v = new short[2];
    v[0] =(short)(u[0]^u[1]);
    v[1] = (short)(u[0]^gfmultby02(u[1]));
    return v;
    }


    private static void Next(){
    short[] a = Arrays.copyOf(state_a,state_a.length);
    short[] b = Arrays.copyOf(state_b,state_b.length);
    ro_func(b,a);
    lambda_func(a,b);
    }


    public static short Out(){
        return state_a[1];
    }



}
