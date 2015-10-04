package com.umbrella.jotiwa.map.area348.binding;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.umbrella.jotiwa.communication.enumeration.area348.MapPart;
import com.umbrella.jotiwa.map.MapItemListManager;
import com.umbrella.jotiwa.map.ItemType;
import com.umbrella.jotiwa.map.area348.HunterMapPartState;
import com.umbrella.jotiwa.map.area348.MapPartState;
import com.umbrella.jotiwa.map.area348.storage.StorageObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by stesi on 25-9-2015.
 */
public class MapBinder extends HashMap<String, MapBindObject> {

    public MapBinder(GoogleMap gMap)
    {
        this.gMap = gMap;
    }

    private GoogleMap gMap;


    public MapBindObject getAssociatedMapBindObject(MapPartState mapPartState)
    {
        return this.get(mapPartState.getAccessor());
    }

    public Marker findMarker(MapPartState mapPartState, int id)
    {
       MapBindObject mapBindObject =  this.getAssociatedMapBindObject(mapPartState);
        ArrayList<Marker> markers = mapBindObject.getMarkers();
        for(int i = 0; i < markers.size(); i++)
        {
            String[] typeCodes = markers.get(i).getTitle().split(";");
            switch(mapPartState.getMapPart())
            {
                case Vossen:
                    if( Integer.parseInt(typeCodes[2]) == id) return markers.get(i);
                    break;
                case Hunters:

                    break;
            }

            /**
             * If the type code is equal to the map part. Reduant, all the marker in this binding object should be of one state.
             * */
            if(typeCodes[0] == mapPartState.getMapPart().getValue())
            {

            }
        }
        return null;
    }

    public void add(MapPartState mapPartState, StorageObject storageObject, MapBinderAddOptions options)
    {
        if(mapPartState.getAccessor() == "hunter") return;
        String accessor = mapPartState.getAccessor();
        check(accessor);
        MapBindObject bindObject = this.get(accessor);

        if(options == MapBinderAddOptions.MAP_BINDER_ADD_OPTIONS_CLEAR)
        {
            bindObject.getMarkers().clear();
            bindObject.getPolylines().clear();
            bindObject.getCircles().clear();
        }

        ArrayList<MarkerOptions> markers = storageObject.getMarkers();
        /**
         * Loop through data and add each marker to the map and the bind object.
         * */
        for(int m = 0; m < markers.size(); m++)
        {
            bindObject.getMarkers().add(gMap.addMarker(markers.get(m)));
        }

        ArrayList<PolylineOptions> polylines = storageObject.getPolylines();
        /**
         * Loop through data and add each polyline to the map and the bind object.
         * */
        for(int l = 0; l < polylines.size(); l++)
        {
            bindObject.getPolylines().add(gMap.addPolyline(polylines.get(l)));
        }

        ArrayList<CircleOptions> circles = storageObject.getCircles();
        /**
         * Loop through data and add each circle to the map and the bind object.
         * */
        for(int c = 0; c < circles.size(); c++)
        {
            bindObject.getCircles().add(gMap.addCircle(circles.get(c)));
        }

    }

    /**
     * Enumeration for add options.
     * */
    public enum MapBinderAddOptions
    {
        /**
         * Clear the old items.
         * */
        MAP_BINDER_ADD_OPTIONS_CLEAR,

        /**
         * Hold the old items.
         * */
        MAP_BINDER_ADD_OPTIONS_HOLD
    }

    private void check(String accessor)
    {
        if(this.get(accessor) == null) this.put(accessor, new MapBindObject());
    }

}