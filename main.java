import bgu.spl.mics.Future;

public class Main {
    public static void main(String[] args) {

        Future<String> f= new Thread(()->{
            System.out.println("It's going to be, Legen, wait for it");
            sleep(5000);
            
            return "DARY";
        }).start();

        System.out.println(f.get());
    }
}
