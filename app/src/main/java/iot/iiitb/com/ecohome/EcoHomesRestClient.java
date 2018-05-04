package iot.iiitb.com.ecohome;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Er.RajaDS on 18-03-2018.
 */

public class EcoHomesRestClient {
    private static final String BASE_URL = "https://api.thingspeak.com/channels/";
    private static String CHANNEL_ID="454676";
    private static final String READ_URL = "/feeds";
    private static final String LAST_DATA_AGE = "/last_data_age";
    private static final String LAST_ENTRY = "/last";
    private static final String format = ".json";
    private static String API_KEY = "R0IGB1FZGSKWBIET";

/*
READ DATA request format
https://api.thingspeak.com/channels/<channel_id>/feeds.<format>

READ_DATA JSON Response format
{
"channel": {
"id": 9,
"name": "my_house",
"description": "Netduino Plus connected to sensors around the house",
"latitude": "40.44",
"longitude": "-79.9965",
"field1": "Light",
"field2": "Outside Temperature",
"created_at": "2010-12-14T01:20:06Z",
"updated_at": "2018-01-26T13:08:04Z",
"last_entry_id": 13633195
},
"feeds": [
{
"created_at": "2018-01-26T13:07:48Z",
"entry_id": 13633194,
"field1": "150",
"field2": "23.014861995753716"
}
]
}

*/

    private static AsyncHttpClient client = new AsyncHttpClient();
    private static RequestParams params;

    public static void get_feeds(JsonHttpResponseHandler responseHandler) {
        String url = BASE_URL + CHANNEL_ID + READ_URL + format;
        params.add("api_key", API_KEY);
        client.get(url, params, responseHandler);
    }

    public static void get_feeds(int results, JsonHttpResponseHandler responseHandler) {
        if (results > 8000) {
            System.err.print("Limit exceeded . Please reduce the request size");
            return;
        }
        params = new RequestParams();
        params.add("api_key", API_KEY);
        params.add("results", "" + results);
        String url = BASE_URL + CHANNEL_ID + READ_URL + format;
        System.out.println("URL :  "+url);
        client.get(url, params, responseHandler);
    }

    public static void get_last_feed(JsonHttpResponseHandler responseHandler) {
        params = new RequestParams();
        params.add("api_key", API_KEY);
        String url = BASE_URL + CHANNEL_ID + READ_URL + LAST_ENTRY+format;
        System.out.println("URL :  "+url);
        client.get(url, params, responseHandler);
    }

    public static void get_feeds_avg(int minutes, JsonHttpResponseHandler responseHandler) {
        /**
         * valid values for hours=1/6,1/4,0.5,1,4,6,12,24
         */
        params = new RequestParams();
        params.add("api_key", API_KEY);
        params.add("average", "" + minutes);
        String url = BASE_URL + CHANNEL_ID + READ_URL + format;
        client.get(url, params, responseHandler);
    }

    public static void get_last_data_age(JsonHttpResponseHandler responseHandler) {
    /**
    * LAST_DATA_AGE request format => GET https://api.thingspeak.com/channels/12397/feeds/last_data_age.json
    The response is a JSON object with the age of the most recent value,in seconds:

    {
    "last_data_age": "174",
    "last_data_age_units": "s"
    }
    * */
        String url = BASE_URL + CHANNEL_ID + READ_URL + LAST_DATA_AGE + format;
        params = new RequestParams();
        params.add("api_key", API_KEY);
        client.get(url, params, responseHandler);
    }
}
