import Rx.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    @RxMethod(info = "inp1+inp2+inp3=", name = "sum")
    public int sum(@RxParam(name = "inp1") int x, @RxParam(name = "inp2") int y, @RxParam(name="inp3")int z) throws RxException {
        return x + y + z;
    }

    @RxMethod(info = "inp1-inp2=", name = "sub alg")
    public int sub(@RxParam(name = "inp1") int x, @RxParam(name = "inp2") int y) throws  RxException {
        return x-y;
    }


    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            RxService<Main> s = new RxService<>(Main.class);
            Main obj = new Main();
            System.out.println(s.Call(obj, scanner.nextLine()));


        } catch (RxAnnotationExpectedException | InvocationTargetException | IllegalAccessException | RxException e) {
            throw new RuntimeException(e);
        }
    }
}