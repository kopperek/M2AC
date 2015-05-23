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

package app.android.kopper.m2ac.providers;

public abstract class AbstractFreemapSlovakiaMapSource extends AbstractHttpMapSource {

    @Override
    protected String getTileAddress(int x,int y,int zoom) {
        return "http://tiles.freemap.sk/"+getTypeName()+"/"+zoom+"/"+x+"/"+y;
    }

	protected abstract String getTypeName();
    protected abstract String getVisibleTypeName();

	@Override
	public int getMaxZoom() {
		return(16);
	}

    @Override
    public int getMinZoom() {
        return 6;
    }

    @Override
	public String getVisibleName() {
		return "Freemap Slovakia-" + getVisibleTypeName();
	}


    @Override
    public String getName() {
        return "FreemapSlovakia-" + getTypeName();
    }
}
