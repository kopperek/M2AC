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

package app.android.kopper.m2ac.util.tb;

import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;

import app.android.kopper.m2ac.util.MercatorPower2MapSpace;
import app.android.kopper.m2ac.util.XY;

public class TbUtil {

    public static String prepareMapString(LatLng p1,LatLng p2,int zoom) {

        MercatorPower2MapSpace mapSpace=MercatorPower2MapSpace.INSTANCE_256;
        int tileSize=mapSpace.getTileSize();

        XY tileXY1=mapSpace.toTileXY(p1,zoom);
        XY tileXY2=mapSpace.toTileXY(p2,zoom);

        long minTileX=Math.min(tileXY1.getX(),tileXY2.getX());
        long maxTileX=Math.max(tileXY1.getX(),tileXY2.getX());

        long minTileY=Math.min(tileXY1.getY(),tileXY2.getY());
        long maxTileY=Math.max(tileXY1.getY(),tileXY2.getY());

        int width=(int)((maxTileX-minTileX+1)*tileSize);
        int height=(int)((maxTileY-minTileY+1)*tileSize);

        double longitudeMin=mapSpace.cXToLon(minTileX*tileSize,zoom);
        double longitudeMax=mapSpace.cXToLon((maxTileX+1)*tileSize-1,zoom);

        double latitudeMax=mapSpace.cYToLat(minTileY*tileSize,zoom);
        double latitudeMin=mapSpace.cYToLat((maxTileY+1)*tileSize-1,zoom);

        StringBuffer sbMap = new StringBuffer();

        sbMap.append("OziExplorer Map Data File Version 2.2\r\n");
        sbMap.append("t_\r\n");
        sbMap.append("t_\r\n");
        sbMap.append("1 ,Map Code,\r\n");
        sbMap.append("WGS 84,WGS 84,   0.0000,   0.0000,WGS 84\r\n");
        sbMap.append("Reserved 1\r\n");
        sbMap.append("Reserved 2\r\n");
        sbMap.append("Magnetic Variation,,,E\r\n");
        sbMap.append("Map Projection,Mercator,PolyCal,No," + "AutoCalOnly,No,BSBUseWPX,No\r\n");

        String latMax = getDegMinFormat(latitudeMax, true);
        String latMin = getDegMinFormat(latitudeMin, true);
        String lonMax = getDegMinFormat(longitudeMax, false);
        String lonMin = getDegMinFormat(longitudeMin, false);

        String pointLine = "Point%02d,xy, %4s, %4s,in, deg, %1s, %1s, grid," + " , , ,N\r\n";

        sbMap.append(String.format(pointLine, 1, 0, 0, latMax, lonMin));
        sbMap.append(String.format(pointLine, 2, width - 1, 0, latMax, lonMax));
        sbMap.append(String.format(pointLine, 3, width - 1, height - 1, latMin, lonMax));
        sbMap.append(String.format(pointLine, 4, 0, height - 1, latMin, lonMin));

        for (int i = 5; i <= 30; i++) {
            String s = String.format(pointLine, i, "", "", "", "");
            sbMap.append(s);
        }
        sbMap.append("Projection Setup,,,,,,,,,,\r\n");
        sbMap.append("Map Feature = MF ; Map Comment = MC     These follow if they exist\r\n");
        sbMap.append("Track File = TF      These follow if they exist\r\n");
        sbMap.append("Moving Map Parameters = MM?    These follow if they exist\r\n");

        sbMap.append("MM0,Yes\r\n");
        sbMap.append("MMPNUM,4\r\n");

        String mmpxLine = "MMPXY, %d, %5d, %5d\r\n";

        sbMap.append(String.format(mmpxLine, 1, 0, 0));
        sbMap.append(String.format(mmpxLine, 2, width - 1, 0));
        sbMap.append(String.format(mmpxLine, 3, width - 1, height - 1));
        sbMap.append(String.format(mmpxLine, 4, 0, height - 1));

        String mpllLine = "MMPLL, %d, %2.6f, %2.6f\r\n";

        sbMap.append(String.format(Locale.ENGLISH, mpllLine, 1, longitudeMin, latitudeMax));
        sbMap.append(String.format(Locale.ENGLISH, mpllLine, 2, longitudeMax, latitudeMax));
        sbMap.append(String.format(Locale.ENGLISH, mpllLine, 3, longitudeMax, latitudeMin));
        sbMap.append(String.format(Locale.ENGLISH, mpllLine, 4, longitudeMin, latitudeMin));

        sbMap.append("MOP,Map Open Position,0,0\r\n");

        // The simple variant for calculating mm1b
        // http://www.trekbuddy.net/forum/viewtopic.php?t=3755&postdays=0&postorder=asc&start=286
        double mm1b = (longitudeMax - longitudeMin) * 111319;
        mm1b *= Math.cos(Math.toRadians((latitudeMax + latitudeMin) / 2.0)) / width;

        sbMap.append(String.format(Locale.ENGLISH, "MM1B, %2.6f\r\n", mm1b));

        sbMap.append("IWH,Map Image Width/Height, " + width + ", " + height + "\r\n");

        return sbMap.toString();
    }

    private static String getDegMinFormat(double coord, boolean isLatitude) {

        boolean neg = (coord < 0.0);
        coord = Math.abs(coord);
        int deg = (int) coord;
        double min = (coord - deg) * 60.0;

        String degMinFormat = "%d, %3.6f, %c";

        char dirC;
        if (isLatitude)
            dirC = (neg ? 'S' : 'N');
        else
            dirC = (neg ? 'W' : 'E');

        return String.format(Locale.ENGLISH, degMinFormat, deg, min, dirC);
    }


}
