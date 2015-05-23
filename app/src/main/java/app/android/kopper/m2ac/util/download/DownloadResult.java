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

public class DownloadResult {

    private final boolean result;
    private final int messageKey;
    private final Object[] messageParams;

    public DownloadResult(boolean result,int messageKey,Object... messageParams) {
        this.result=result;
        this.messageKey=messageKey;
        this.messageParams=messageParams;
    }

    public boolean isResult() {
        return result;
    }

    public int getMessageKey() {
        return messageKey;
    }

    public Object[] getMessageParams() {
        return messageParams;
    }
}
