/*
 * Created by kopper on 2015-05-23.
 * (C) Copyright 2015 kopperek@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package app.android.kopper.m2ac.util;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;

import app.android.kopper.m2ac.util.list.MapSourcesListAdapter;

public class Layer implements Serializable {

    private String layerName;
    private String mapSource;
    private Set<Integer> zoom;
    private LatLon p1;
    private LatLon p2;
    private String visibleName;

    public Layer(String layerName,String mapSource,Set<Integer> zoom,LatLng p1,LatLng p2) {
        this.layerName=layerName;
        this.mapSource=mapSource;
        this.zoom=zoom;
        this.p1=new LatLon(p1);
        this.p2=new LatLon(p2);
        setVisibleName(getLayerName()+"-"+MapSourcesListAdapter.getMapSource(getMapSource()).getVisibleName());
    }

    public String getLayerName() {
        return layerName;
    }

    public String getMapSource() {
        return mapSource;
    }

    public Set<Integer> getZoom() {
        return zoom;
    }

    public LatLng getP1() {
        return p1.getLatLng();
    }

    public LatLng getP2() {
        return p2.getLatLng();
    }

    public String getVisibleName() {
        return visibleName;
    }

    public String getBoundsAsString() {
        return getLanLngAsString(p1)+", "+getLanLngAsString(p2);
    }

    private String getLanLngAsString(LatLon latLng) {
        return doubleToString(latLng.getLatitude())+"/"+doubleToString(latLng.getLongitude());
    }

    private String doubleToString(double value) {
        return BigDecimal.valueOf(value).setScale(3,BigDecimal.ROUND_HALF_DOWN).toString();
    }

    public String getZoomAsString() {
        return Arrays.toString(zoom.toArray(new Integer[zoom.size()]));
    }

    public void setVisibleName(String visibleName) {
        this.visibleName=StringUtil.makeIOFrendlyName(visibleName);
    }

    public long getTilesCount() {
        long tilesCount=0;
        for(Integer zoom : getZoom())
            tilesCount+=getTilesCount(zoom);
        return tilesCount;
    }

    public long getTilesCount(Integer zoom) {
        XY tileXY1=MercatorPower2MapSpace.INSTANCE_256.toTileXY(getP1(),zoom);
        XY tileXY2=MercatorPower2MapSpace.INSTANCE_256.toTileXY(getP2(),zoom);
        long width=Math.abs(tileXY2.getX()-tileXY1.getX())+1;
        long height=Math.abs(tileXY2.getY()-tileXY1.getY())+1;
        return width*height;
    }
}
