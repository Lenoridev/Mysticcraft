package net.kapitencraft.mysticcraft.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class APITools {
    private static List<Character> convertList(int value) {
        int d = value == 27 ? 10 : 0;
        List<Character> list = new ArrayList<>();
        for (int i = d; i < (value + d); i++) {
            list.add(CONVERT.get(i));
        }
        return list;
    }

    private static final List<Character> CONVERT = List.of(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                    'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
                    'U', 'V', 'W', 'X', 'Y', 'Z', ' ', '-', '+', '*', '/',
                    '!', '?', '.', ',', '\'', '\"', '<', '>', '(', ')', '§', '$', '%', '&', '\\', '=', '`', '´');

    private static char[] invertCharArray(char[] ts) {
        char[] temp = Arrays.copyOf(ts, ts.length);
        for (int i = (ts.length - 1); i >= 0; i--) {
            temp[i] = ts[(ts.length - 1) - i];
        }
        return temp;
    }

    public static long compiler(int convert, String in) {
        List<Character> convertList = convertList(convert);
        in = in.toUpperCase();
        char[] chars = invertCharArray(in.toCharArray());
        long ret = 0;
        char ch;
        for (int i = 0; i < chars.length; i++) {
            ch = chars[i];
            if (convertList.contains(ch)) {
                int buffer = convertList.indexOf(ch);
                ret += buffer * Math.pow(convert, i);
            } else {
                System.out.print("Found unknown char '");
                System.out.print(ch);
                System.out.println("', skipping");
            }
        }
        return ret;
    }

    @SuppressWarnings("all")
    public static String decompile(int convert, long in) {
        List<Character> convertList = convertList(convert);
        StringBuilder builder = new StringBuilder();
        int j = 0;
        while (in >= Math.pow(convert, j) * 15) {
            j++;
        }
        while (in > 0) {
            for (int i = 1; i < convert; i++) {
                long pow = (long) Math.pow(convert, j);
                if (pow * i > in) {
                    i--;
                    if (!(builder.isEmpty() && convertList.get(i) == '0')) {
                        builder.append(convertList.get(i));
                    }
                    in -= pow * i;
                    j--;
                    break;
                }
            }
        }
        return builder.toString();
    }

    public static String transfer(int inConvert, int outConvert, String toTransfer) {
        long value = compiler(inConvert, toTransfer);
        return decompile(outConvert, value);
    }

    public static String transfer(String in) {
        String inConvert = in.substring(0, 2);
        String outConvert = in.substring(2, 4);
        try {
            String transfer = transfer(Integer.parseInt(inConvert), Integer.parseInt(outConvert), in.substring(4));
            return outConvert + inConvert + transfer;
        } catch (NumberFormatException e) {
            System.out.println("unable to ");
        }
        return "";
    }
}