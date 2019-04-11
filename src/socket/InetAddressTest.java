package socket;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressTest {
    public static void main(String[] args){
        if(args.length > 0){
            String host = args[0];
            try {
                InetAddress[] addresses = InetAddress.getAllByName(host);
                for (InetAddress address:addresses){
                    System.out.println(address);
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }else{
            try {
                InetAddress localHostAddress = InetAddress.getLocalHost();
                System.out.println(localHostAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }
}
