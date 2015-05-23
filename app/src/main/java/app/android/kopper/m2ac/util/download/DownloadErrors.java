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

package app.android.kopper.m2ac.util.download;

import java.util.LinkedList;

import app.android.kopper.m2ac.util.Layer;

public class DownloadErrors {

    private final Layer layer;
    private final Integer zoom;
    private LinkedList<DownloadError> errors;

    public DownloadErrors(Layer layer,Integer zoom,LinkedList<DownloadError> errors) {
        this.layer=layer;
        this.zoom=zoom;
        this.errors=errors;
    }

    public Layer getLayer() {
        return layer;
    }

    public Integer getZoom() {
        return zoom;
    }

    public LinkedList<DownloadError> getErrors() {
        return errors;
    }

    @Override
    public boolean equals(Object o) {
        if(this==o) return true;
        if(o==null||getClass()!=o.getClass()) return false;
        DownloadErrors that=(DownloadErrors)o;
        if(layer!=null?!layer.equals(that.layer):that.layer!=null) return false;
        if(zoom!=null?!zoom.equals(that.zoom):that.zoom!=null) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result=layer!=null?layer.hashCode():0;
        result=31*result+(zoom!=null?zoom.hashCode():0);
        return result;
    }

    public void setErrors(LinkedList<DownloadError> errors) {
        this.errors=errors;
    }
}
