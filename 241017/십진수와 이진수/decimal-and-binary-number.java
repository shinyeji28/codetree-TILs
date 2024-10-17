import java.util.*;
import java.io.*;
import java.math.*;
public class Main {
    public static void main(String[] args) throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BigInteger num = new BigInteger(br.readLine(),2);
        System.out.println(num.multiply(BigInteger.valueOf(17)).toString(2));

    }
}