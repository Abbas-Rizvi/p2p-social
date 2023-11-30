package network;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.time.ZoneId;
import java.util.Date;

public class NTPTimeService implements Serializable {

    private static final long serialVersionUID = 123456789L;

    public Date getNTPDate() {

        // time server
        String[] hosts = new String[] {
                "pool.ntp.org",
                "time-a-g.nist.gov" };

        // create NTP UDP client`
        NTPUDPClient client = new NTPUDPClient();

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

    public String formatedTime(long time) {

        Date date = new Date(time);
        return date.toString();

    }

}
