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
import java.util.List;

import app.android.kopper.m2ac.util.Layer;
import app.android.kopper.m2ac.util.StringDef;

public class DownloadState {

    //NOTE: don't forget about copy
    private StringDef taskName=null;
    private StringDef subTaskName=null;
    private StringDef subSubTaskName=null;
    private int progressValue=0;
    private int progressTotal=0;
    boolean waitForInput=false;
    private DownloadResult downloadResult;
    private List<DownloadErrors> errors=new LinkedList<>();

    public DownloadState() {

    }

    public void setTaskName(StringDef taskName) {
        this.taskName=taskName;
    }

    public void setSubTaskName(StringDef subTaskName) {
        this.subTaskName=subTaskName;
    }

    public void setSubSubTaskName(StringDef subSubTaskName) {
        this.subSubTaskName=subSubTaskName;
    }

    public void setProgressValue(int progressValue) {
        this.progressValue=progressValue;
    }

    public void setProgressTotal(int progressTotal) {
        this.progressTotal=progressTotal;
    }

    public void setWaitForInput(boolean waitForInput) {
        this.waitForInput=waitForInput;
    }

    public StringDef getTaskName() {
        return taskName;
    }

    public StringDef getSubTaskName() {
        return subTaskName;
    }

    public StringDef getSubSubTaskName() {
        return subSubTaskName;
    }

    public int getProgressValue() {
        return progressValue;
    }

    public int getProgressTotal() {
        return progressTotal;
    }

    public boolean isWaitForInput() {
        return waitForInput;
    }

    public List<DownloadErrors> getErrors() {
        return errors;
    }

    public void addDownloadErrors(Layer layer,Integer zoom,LinkedList<DownloadError> errors) {
        this.errors.add(new DownloadErrors(layer,zoom,errors));
    }

    public DownloadState getCopy() {
        DownloadState result=new DownloadState();
        result.taskName=taskName;
        result.subTaskName=subTaskName;
        result.subSubTaskName=subSubTaskName;
        result.progressValue=progressValue;
        result.progressTotal=progressTotal;
        result.waitForInput=waitForInput;
        result.downloadResult=downloadResult;
        result.errors=new LinkedList<>(errors);
        return result;
    }


    public DownloadResult getDownloadResult() {
        return downloadResult;
    }

    public void setDownloadResult(DownloadResult downloadResult) {
        this.downloadResult=downloadResult;
    }
}
