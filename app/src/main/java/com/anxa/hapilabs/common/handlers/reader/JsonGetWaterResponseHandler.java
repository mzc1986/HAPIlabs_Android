package com.anxa.hapilabs.common.handlers.reader;

import android.os.Handler;
import android.os.Message;

import com.anxa.hapilabs.models.MessageObj;
import com.anxa.hapilabs.models.Water;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by aprilanxa on 20/07/2017.
 */

public class JsonGetWaterResponseHandler extends JsonDefaultResponseHandler{
    protected Handler handler;

    protected boolean isError = false;

    String OutputData = "";
    JSONObject jsonResponse;
    String strJson;

    Message msg = new Message();

    public JsonGetWaterResponseHandler(Handler handler)
    {
        super(handler);
        this.handler = handler;
    }

    @Override
    public void start(String strJson)
    {
        this.strJson = strJson;
        start();
    }

    @Override
    public void start()
    {
        try {
            /****** inform handler that the jsn processing has started; this is optional *********/
            msg.what = JsonDefaultResponseHandler.START;
            handler.handleMessage(msg);

            /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
            jsonResponse = new JSONObject(strJson);

            /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
            /*******  Returns null otherwise.  *******/
            try {
                JSONObject api_response = jsonResponse.getJSONObject("api_response");

                String requestStatus = api_response.optString("status");

                //for failed request
                if (requestStatus == null || requestStatus.equalsIgnoreCase("Failed")) {
                    //set message obj for the failed request
                    MessageObj messageObj = JsonUtil.getMessageObj(MessageObj.MESSAGE_TYPE.FAILED, jsonResponse);
                    //transfer the message obj to the response handler
                    setResponseObj(messageObj);
                    //set json response handler to completed
                    msg.what = JsonDefaultResponseHandler.COMPLETED;
                    //set the handler for the waiting class
                    handler.handleMessage(msg);
                    return;
                }

                // GET WORKOUT ARRAY
                JSONObject json = jsonResponse.getJSONObject("water");
                Water water = JsonUtil.getWater(json);

                //transfer the message obj to the response handler
                setResponseObj(water);
                setResultMessage(requestStatus);

                //set json response handler to completed
                msg.what = JsonDefaultResponseHandler.COMPLETED;

                //set the handler for the waiting class
                handler.handleMessage(msg);
                return;
            } catch (JSONException e)
            {
                msg.what = JsonDefaultResponseHandler.ERROR;
                handler.handleMessage(msg);
                e.printStackTrace();
            }
        } catch (JSONException e) {
            msg.what = JsonDefaultResponseHandler.ERROR;
            handler.handleMessage(msg);
            e.printStackTrace();
        }
    }

    @Override
    public void start(String strJson, String id)
    {
        // TODO Auto-generated method stub

    }
}
