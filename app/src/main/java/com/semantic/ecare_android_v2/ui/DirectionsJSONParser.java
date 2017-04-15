package com.semantic.ecare_android_v2.ui;

import android.text.Html;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionsJSONParser {


    //StringBuilder to wrap all JSON data needed
    public static StringBuilder traffic_data = new StringBuilder();
    List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String,String>>>() ;
    //Global Arrays

    String distanceGlobal;
    String durationGlobal;
    private String durationValue;
    private String distanceValue;
    private String instructionTrajet;

    JSONArray jRoutes = null;
    JSONArray jLegs = null;
    JSONArray jSteps = null;

    //jSteps --> object --> json primitive
    JSONObject differentSteps = null;
    JSONObject durationObject = null;
    JSONObject distanceObject = null;
    JSONObject jLegsObject= null;
    JSONObject distanceObjectGlobal= null;
    JSONObject durationObjectGlobal= null;


    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<List<HashMap<String,String>>> parse(JSONObject jObject){

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                jLegsObject = jLegs.getJSONObject(0);
                distanceObjectGlobal = jLegsObject.getJSONObject("distance");
                distanceGlobal = distanceObjectGlobal.getString("text");

                durationObjectGlobal = jLegsObject.getJSONObject("duration");
                durationGlobal = durationObjectGlobal.getString("text");

                Log.d("----La distance globale est ",distanceGlobal);
                Log.d("----La durée approximative du trajet est ",durationGlobal);
                traffic_data.append("La distance globale de ce trajet est : "+distanceGlobal).append("\n");
                traffic_data.append("La durée approximative de ce trajet est : "+durationGlobal).append("\n");
                traffic_data.append("\n");

                List path = new ArrayList<HashMap<String, String>>();
                //Log.d("Traversing all routes",path.get(i).toString());

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");
                    Log.d("Traversing all legs ",jSteps.get(j).toString());


                    for(int m = 0; m<jSteps.length();m++){
                        differentSteps = jSteps.getJSONObject(m);
                        durationObject = differentSteps.getJSONObject("duration");
                        durationValue = durationObject.getString("text");
                        distanceObject = differentSteps.getJSONObject("distance");
                        distanceValue = distanceObject.getString("text");
                        instructionTrajet = differentSteps.getString("html_instructions");
                        Log.d("----Le trajet "+m+" est ",instructionTrajet);
                        Log.d("----La durée du trajet "+m+" est ",durationValue);
                        Log.d("----La distance du trajet "+m+" est ",distanceValue);
                        traffic_data.append(Html.fromHtml((m+1)+" : "+instructionTrajet).toString()).append("---");
                        traffic_data.append("Duration: "+durationValue).append("---");
                        traffic_data.append("Distance: "+distanceValue).append("\n");
                    }

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                    Log.d("jRoutes JSON: ",jRoutes.toString());
                    Log.d("jLegs JSON:",jLegs.toString());
                    Log.d("jSteps JSON",jSteps.toString());//Parsing json with jSteps
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }


        return routes;
    }


    /**
     * Method to decode polyline points
     * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
     * */
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
}