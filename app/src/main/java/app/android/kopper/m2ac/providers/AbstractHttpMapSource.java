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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;

import app.android.kopper.ioutil.IOUtil;

public abstract class AbstractHttpMapSource implements IMapSource {

	@Override
	public byte[] getTile(int x, int y, int zoom) throws IOException {
		String tileAddress = getTileAddress(x,y,zoom);
        byte[] tile=loadTile(tileAddress);
        if(tile!=null&&tile.length>3&&Arrays.equals(Arrays.copyOf(tile,4),new byte[]{(byte)0x89,'P','N','G' }))
            return tile; //png
        if(tile!=null&&tile.length>1&&Arrays.equals(Arrays.copyOf(tile,2),new byte[]{(byte)0xff,(byte)0xd8}))
            return tile; //jpg
        return(null);
	}

	private byte[] loadTile(String tileAddress) throws IOException {

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        InputStream is=new URL(tileAddress).openStream();
        try {
            IOUtil.copy(is,baos);
			return(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(is!=null)
				is.close();
			if(baos!=null)
				baos.close();
		}
		return null;
	}

	protected abstract String getTileAddress(int x, int y, int zoom);
	
	@Override
	public int getMinZoom() {
		return 0;
	}
}
