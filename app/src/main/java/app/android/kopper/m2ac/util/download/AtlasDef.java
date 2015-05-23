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

import java.util.List;

import app.android.kopper.m2ac.util.Layer;

public class AtlasDef {


    private final String atlasName;
    private final List<Layer> layers;

    public AtlasDef(String atlasName,List<Layer> layers) {
        this.atlasName=atlasName;
        this.layers=layers;
    }

    public String getAtlasName() {
        return atlasName;
    }

    public List<Layer> getLayers() {
        return layers;
    }
}
