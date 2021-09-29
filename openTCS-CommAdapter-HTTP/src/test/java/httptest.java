import com.lvsrobot.http.HttpUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class httptest {
    public static void main(String[] args) {
        String vehicleHost = "127.0.0.1";
        Integer vehiclePort = 8080;
        String stateResponseJson = HttpUtil.doGet("http://" + vehicleHost + ":" + vehiclePort.toString() + "/v1/vehicle/status");
        System.out.println(stateResponseJson);
        JSONObject js = new JSONObject(stateResponseJson);
        JSONObject orderRequestJson = new JSONObject();
//        orderRequestJson.put("action", "load");
        JSONArray ja = new JSONArray();
        ja.put("1");
        ja.put(10);
        ja.put(3);
        ja.put("left");
        ja.put("2");
        ja.put(20);
        ja.put(6);
        ja.put("right");

        orderRequestJson.put("path", ja);
        orderRequestJson.put("action", "load");
        System.out.println(orderRequestJson);
        try {

            String orderResponseJson = HttpUtil.doPost("http://" + vehicleHost + ":" + vehiclePort.toString() + "/v1/vehicle/sendpath", orderRequestJson.toString());
            System.out.println(orderResponseJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
