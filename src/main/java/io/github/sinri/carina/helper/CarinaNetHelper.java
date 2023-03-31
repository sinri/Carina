package io.github.sinri.carina.helper;

import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @since 2.8
 */
public class KeelNetHelper {
    private static final KeelNetHelper instance = new KeelNetHelper();

    private KeelNetHelper() {
    }

    static KeelNetHelper getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        Long x = KeelNetHelper.getInstance().convertIPv4ToNumber("255.255.255.255");
        System.out.println("x=" + x);
        String s = KeelNetHelper.getInstance().convertNumberToIPv4(x);
        System.out.println("s=" + s);
    }

    public Long convertIPv4ToNumber(String ipv4) {
        //Converts a String that represents an IP to an int.
        try {
            InetAddress i = InetAddress.getByName(ipv4);
            //System.out.println(i);
            byte[] address = i.getAddress();
            //System.out.println(address.length);
            var p1 = Byte.toUnsignedLong(address[0]);
            var p2 = Byte.toUnsignedLong(address[1]);
            var p3 = Byte.toUnsignedLong(address[2]);
            var p4 = Byte.toUnsignedLong(address[3]);
            //System.out.println(p1+"."+p2+"."+p3+"."+p4);
            return ((p1 << 24) + (p2 << 16) + (p3 << 8) + p4);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public String convertNumberToIPv4(long number) {
        //This converts an int representation of ip back to String
        try {
            InetAddress i = InetAddress.getByName(String.valueOf(number));
            return i.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public byte[] convertIPv4ToAddressBytes(long ipv4AsLong) {
        return new byte[]{
                (byte) (ipv4AsLong >> 24),
                (byte) ((ipv4AsLong >> 16) & 0xFF),
                (byte) ((ipv4AsLong >> 8) & 0xFF),
                (byte) (ipv4AsLong & 0xFF),
        };
    }

    public byte[] convertIPv4ToAddressBytes(String ipv4) {
        return convertIPv4ToAddressBytes(this.convertIPv4ToNumber(ipv4));
    }

    /**
     * @return like "127.0.0.1"; If the local host name could not be resolved into an address, null.
     * @since 2.9.1
     */
    public String getLocalHostAddress() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            return null;
        }

    }

    /**
     * @return like "SinriMacInLeqee.local"; If the local host name could not be resolved into an address, null.
     * @since 2.9.1
     */
    public String getLocalHostName() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * @return like "localhost"; If the local host name could not be resolved into an address, null.
     * @since 2.9.1
     */
    public String getLocalHostCanonicalName() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            return localHost.getCanonicalHostName();
        } catch (UnknownHostException e) {
            return null;
        }
    }

    /**
     * @return List of Client IP, combined with X-Forwarded-For and remote address.
     * @since 2.9.2
     */
    public List<String> parseWebClientIPChain(RoutingContext ctx) {
        // X-Forwarded-For
        JsonArray clientIPChain = new JsonArray();
        String xForwardedFor = ctx.request().getHeader("X-Forwarded-For");
        if (xForwardedFor != null) {
            String[] split = xForwardedFor.split("[ ,]+");
            for (var item : split) {
                clientIPChain.add(item);
            }
        }
        clientIPChain.add(ctx.request().remoteAddress().hostAddress());

        List<String> list = new ArrayList<>();
        clientIPChain.forEach(item -> {
            list.add(item.toString());
        });
        return list;
    }
}
