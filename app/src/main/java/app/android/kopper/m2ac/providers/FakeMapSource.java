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

import java.io.IOException;

import app.android.kopper.m2ac.CreateActivity;

public class FakeMapSource implements IMapSource {

    @Override
    public byte[] getTile(int x,int y,int zoom) throws IOException {
        if(Math.random()<.5)
            return CreateActivity.FAKE_TILE;
        else
            return null;
    }

    @Override
    public int getMaxZoom() {
        return 12;
    }

    @Override
    public int getMinZoom() {
        return 0;
    }

    @Override
    public String getName() {
        return "Fake";
    }

    @Override
    public String getVisibleName() {
        return "Fake";
    }
}
