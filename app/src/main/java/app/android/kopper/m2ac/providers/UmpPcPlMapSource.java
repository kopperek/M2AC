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

public class UmpPcPlMapSource extends AbstractHttpMapSource {

	private static final int MAX_SERVER_NUM = 4;
	private int serverNum=0;

	@Override
	protected String getTileAddress(int x, int y, int zoom) {
		String s = "http://" + serverNum + ".tiles.ump.waw.pl/ump_tiles/" + zoom + "/" + x + "/" + y + ".png";
		serverNum = (serverNum + 1) % MAX_SERVER_NUM;
		return s;
	}
	
	@Override
	public int getMaxZoom() {
		return(19);
	}
	
	@Override
	public String getVisibleName() {
		return "UMP-pcPL";
	}

    @Override
    public String getName() {
        return "UMP-pcPL";
    }
}
