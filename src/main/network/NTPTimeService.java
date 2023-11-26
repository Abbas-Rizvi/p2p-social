package network;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

public class NTPTimeService {

    public static Date getNTPDate() {

        // time server
        String[] hosts = new String[] {
            "pool.ntp.org",
            "time-a-g.nist.gov" };

        // create NTP UDP client`
        NTPUDPClient client = new NTPUDPClient();

        // set time out
        client.setDefaultTimeout(5000);

        for (String host : hosts) {

            try {
                InetAddress hostAddr = InetAddress.getByName(host);
                TimeInfo info = client.getTime(hostAddr);
                Date date = new Date(info.getReturnTime());
                return date;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        client.close();

        return null;

    }

}
