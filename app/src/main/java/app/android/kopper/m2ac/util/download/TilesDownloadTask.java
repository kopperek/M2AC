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

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

import app.android.kopper.ioutil.IOUtil;
import app.android.kopper.m2ac.CreateActivity;
import app.android.kopper.m2ac.R;
import app.android.kopper.m2ac.providers.IMapSource;
import app.android.kopper.m2ac.util.Layer;
import app.android.kopper.m2ac.util.LogUtil;
import app.android.kopper.m2ac.util.MercatorPower2MapSpace;
import app.android.kopper.m2ac.util.StringDef;
import app.android.kopper.m2ac.util.XY;
import app.android.kopper.m2ac.util.list.MapSourcesListAdapter;
import app.android.kopper.m2ac.util.tb.TarArchive;
import app.android.kopper.m2ac.util.tb.TarTmiArchive;
import app.android.kopper.m2ac.util.tb.TbUtil;

public class TilesDownloadTask extends AsyncTask<AtlasDef,DownloadState,DownloadResult> {

    private CreateActivity createActivity;
    private DownloadState downloadState=new DownloadState();
    private DownloadState lastProgressState;
    private Thread threadForUnpark;
    private Integer interruptedMessageKey;
    private Object inputtedResult;

    public TilesDownloadTask(CreateActivity createActivity) {
        this.createActivity=createActivity;
    }

    @Override
    protected DownloadResult doInBackground(AtlasDef... params) {
        downloadState.setTaskName(new StringDef(R.string.progress_init));
        showProgress(downloadState);

        try {
            File m2ACDir=IOUtil.createExternalStorageDirectory("M2AC");
            File tmpDir=IOUtil.createDirectory(m2ACDir,"tmp");
            downloadState.setSubTaskName(new StringDef(R.string.progress_cleaning_tmp_dir));
            showProgress(downloadState);
            clearDir(tmpDir);
            List<Layer> layersToDownload=params[0].getLayers();
            downloadState.setTaskName(new StringDef(R.string.progress_downloading));
            downloadState.setSubTaskName(null);
            downloadState.setSubSubTaskName(null);
            long totTiles=0;
            for(Layer ll:layersToDownload)
                totTiles+=ll.getTilesCount();
            downloadState.setProgressTotal((int)totTiles);
            downloadState.setProgressValue(0);
            showProgress(downloadState);



            for(int i=0;i<layersToDownload.size();i++) {
                Layer l=layersToDownload.get(i);
                File layerDir=IOUtil.createDirectory(tmpDir,l.getVisibleName());
                IMapSource mapSource=MapSourcesListAdapter.getMapSource(l.getMapSource());
                downloadState.setSubTaskName(new StringDef(R.string.progress_sub_layer,l.getVisibleName(),1+i,layersToDownload.size()));
                Iterator<Integer> zoomIterator=l.getZoom().iterator();
                for(int ii=0; zoomIterator.hasNext() ;ii++) {
                    Integer zoom=zoomIterator.next();
                    File zoomDir=IOUtil.createDirectory(layerDir,String.valueOf(zoom));
                    downloadState.setSubSubTaskName(new StringDef(R.string.progress_sub_sub_zoom,zoom,1+ii,l.getZoom().size()));
                    showProgress(downloadState);
                    XY tileXY1=MercatorPower2MapSpace.INSTANCE_256.toTileXY(l.getP1(),zoom);
                    XY tileXY2=MercatorPower2MapSpace.INSTANCE_256.toTileXY(l.getP2(),zoom);
                    long minY=Math.min(tileXY1.getY(),tileXY2.getY());
                    long maxY=Math.max(tileXY1.getY(),tileXY2.getY());
                    long minX=Math.min(tileXY1.getX(),tileXY2.getX());
                    long maxX=Math.max(tileXY1.getX(),tileXY2.getX());
                    LinkedList<DownloadError> errors=new LinkedList<DownloadError>();
                    for(int x=0;x+minX<=maxX;x++)
                        for(int y=0;y+minY<=maxY;y++) {
                            loadTile(mapSource,zoom,zoomDir,x,y,(int)minX+x,(int)minY+y,errors);
                        }
                    if(errors.size()>0) {
                        downloadState.addDownloadErrors(l,zoom,errors);
                        showProgress(downloadState);
                    }
                }
            }
            for(;;) {
                if(downloadState.getErrors().size()>0) {
                    downloadState.setTaskName(new StringDef(R.string.progress_wait));
                    downloadState.setSubSubTaskName(null);
                    downloadState.setSubTaskName(null);
                    downloadState.setWaitForInput(true);
                    showProgress(downloadState);
                    threadForUnpark=Thread.currentThread();
                    LockSupport.park();
                    checkInterruption();

                    downloadState.setWaitForInput(false);
                    showProgress(downloadState);

                    if(inputtedResult instanceof Boolean && ((Boolean)inputtedResult).booleanValue()) { //ignore errors
                        downloadState.setTaskName(new StringDef(R.string.progress_filling_gaps));
                        //todo: progress
                        publishProgress(downloadState);
                        InputStream blankStream=createActivity.getResources().openRawResource(R.raw.blank);
                        byte[] blankTile=IOUtil.read(blankStream);
                        blankStream.close();
                        for(DownloadErrors errors:downloadState.getErrors()) {
                            String layerName=errors.getLayer().getVisibleName();
                            String zoomName=String.valueOf(errors.getZoom());
                            File layerDir=IOUtil.createDirectory(tmpDir,layerName);
                            File zoomDir=IOUtil.createDirectory(layerDir,zoomName);
                            downloadState.setSubTaskName(new StringDef(R.string.progress_sub_layer_zoom,layerName,zoomName));
                            publishProgress(downloadState);
                            for(DownloadError error:errors.getErrors()) {
                                IOUtil.write(String.format("t_%d_%d.png",(error.getX()*256),(error.getY()*256)),zoomDir,blankTile);
                                checkInterruption();
                            }
                        }
                        downloadState.setTaskName(null);
                        downloadState.setSubTaskName(null);
                        downloadState.getErrors().clear();
                        publishProgress(downloadState);
                        continue;
                    }
                    if(inputtedResult instanceof List) {
                        downloadState.setTaskName(new StringDef(R.string.progress_redownloading));
                        List<DownloadErrors> errors=new LinkedList<>((List<DownloadErrors>)inputtedResult);
                        long totErrors=0;
                        for(DownloadErrors ee:errors)
                            totErrors+=ee.getErrors().size();
                        downloadState.setProgressTotal((int)totErrors);
                        downloadState.setProgressValue(0);
                        publishProgress(downloadState);
                        for(DownloadErrors ee:errors) {
                            IMapSource mapSource=MapSourcesListAdapter.getMapSource(ee.getLayer().getMapSource());
                            String layerName=ee.getLayer().getVisibleName();
                            File layerDir=IOUtil.createDirectory(tmpDir,layerName);
                            String zoomName=String.valueOf(ee.getZoom());
                            downloadState.setSubTaskName(new StringDef(R.string.progress_sub_layer_zoom,layerName,zoomName));
                            publishProgress(downloadState);
                            File zoomDir=IOUtil.createDirectory(layerDir,zoomName);
                            LinkedList<DownloadError> newErrors=new LinkedList<>();
                            for(DownloadError e:ee.getErrors())
                                loadTile(mapSource,ee.getZoom(),zoomDir,e.getX(),e.getY(),e.getTileX(),e.getTileY(),newErrors);
                            int errorIndex=downloadState.getErrors().indexOf(ee);
                            if(newErrors.size()==0)
                                downloadState.getErrors().remove(errorIndex);
                            else
                                downloadState.getErrors().get(errorIndex).setErrors(newErrors);
                            publishProgress(downloadState);
                        }
                        downloadState.setTaskName(null);
                        downloadState.setSubTaskName(null);
                        publishProgress(downloadState);
                        continue;
                    }
                }
                else
                    break;
            }
            //we have all necessary tiles. show (combining) time...


            downloadState.setTaskName(new StringDef(R.string.progress_creating_atlas));
            downloadState.setSubTaskName(new StringDef(R.string.progress_cleaning_out_dir));
            downloadState.setSubSubTaskName(null);
            showProgress(downloadState);
            File outDir=IOUtil.createDirectory(m2ACDir,params[0].getAtlasName());
            clearDir(outDir);
            downloadState.setSubTaskName(null);
            downloadState.setProgressTotal((int)totTiles);
            downloadState.setProgressValue(0);
            showProgress(downloadState);


            File atlasArchiveFile=createFile(outDir,"cr.tar");
            TarArchive atlasArchive=new TarArchive(atlasArchiveFile,null);
            for(int i=0;i<layersToDownload.size();i++) {
                Layer l=layersToDownload.get(i);
                String layerName=l.getVisibleName();
                File layerDir=IOUtil.createDirectory(outDir,layerName);
                downloadState.setSubTaskName(new StringDef(R.string.progress_sub_layer,l.getVisibleName(),1+i,layersToDownload.size()));
                Iterator<Integer> zoomIterator=l.getZoom().iterator();
                for(int ii=0; zoomIterator.hasNext() ;ii++) {
                    Integer zoom=zoomIterator.next();
                    String zoomName=String.valueOf(zoom);
                    File zoomDir=IOUtil.createDirectory(layerDir,zoomName);
                    downloadState.setSubSubTaskName(new StringDef(R.string.progress_sub_sub_zoom,zoom,1+ii,l.getZoom().size()));
                    showProgress(downloadState);
                    File tarFile=createFile(zoomDir,zoomName+".tar");
                    TarTmiArchive mapArchive=new TarTmiArchive(tarFile,null);
                    byte[] mapFileContent=TbUtil.prepareMapString(l.getP1(),l.getP2(),zoom).getBytes();
                    mapArchive.writeFileFromData(zoomName+".map",mapFileContent);
                    for(File tileToCopy:new File(new File(tmpDir,layerName),zoomName).listFiles()) {
                        mapArchive.writeFileFromData("set/"+tileToCopy.getName(),IOUtil.readFile(tileToCopy));
                        downloadState.setProgressValue(downloadState.getProgressValue()+1);
                        showProgress(downloadState);
                        checkInterruption();
                    }
                    mapArchive.writeEndofArchive();
                    mapArchive.close();
                    atlasArchive.writeFileFromData(layerName+"/"+zoomName+"/"+zoomName+".map",mapFileContent);
                }
            }
            atlasArchive.writeFileFromData("cr.tba","Atlas 1.0\r\n".getBytes());
            atlasArchive.writeEndofArchive();
            atlasArchive.close();

            downloadState.setTaskName(null);
            downloadState.setSubTaskName(null);
            downloadState.setSubSubTaskName(null);
            showProgress(downloadState);

            return(new DownloadResult(true,R.string.atlas_created,outDir.getAbsolutePath()));
        } catch(Exception e) {
            if(e instanceof InterruptedException)
                return(new DownloadResult(false,((InterruptedException)e).getKey(),((InterruptedException)e).getParams()));
            else
                return(new DownloadResult(false,R.string.download_error,e.getLocalizedMessage()));
        }
    }

    private File createFile(File dir,String fileName) throws IOException {
        File file=new File(dir,fileName);
        if(file.exists())
            file.delete();
        file.createNewFile();
        return file;
    }

    private void loadTile(IMapSource mapSource,Integer zoom,File zoomDir,int x,int y,int tileX,int tileY,LinkedList<DownloadError> errors) throws IOException, InterruptedException {
        byte[] tile=null;
        try {
            tile=mapSource.getTile(tileX,tileY,zoom);
        } catch(IOException e) {
            LogUtil.e(e);
        }
        if(tile!=null) {
            IOUtil.write(String.format("t_%d_%d.png",(x*256),(y*256)),zoomDir,tile);
        } else {
            errors.add(new DownloadError(tileX,tileY,x,y));
        }
        downloadState.setProgressValue(downloadState.getProgressValue()+1);
        showProgress(downloadState);
        checkInterruption();
    }

    @Override
    protected void onPostExecute(DownloadResult downloadResult) {
        super.onPostExecute(downloadResult);
        downloadState.setDownloadResult(downloadResult);
        showProgress(downloadState);
    }

    public void clearDir(File dir) throws InterruptedException {

        downloadState.setSubSubTaskName(new StringDef(R.string.progress_sub_sub_string,dir.getName()));
        showProgress(downloadState);
        checkInterruption();
        for(File ff:dir.listFiles()) {
            if(ff.isDirectory())
                clearDir(ff);
            ff.delete();
        }
    }

    private void checkInterruption() throws InterruptedException {
        if(interruptedMessageKey!=null)
            throw new InterruptedException(interruptedMessageKey);
    }


    public void interrupt(int key) {
        this.interruptedMessageKey=key;
        unpark();
    }


    public void inputResult(Object inputtedResult) {
        this.inputtedResult=inputtedResult;
        unpark();
    }

    private void unpark() {
        if(threadForUnpark!=null) {
            LockSupport.unpark(threadForUnpark);
            threadForUnpark=null;
        }
    }

    private void showProgress(DownloadState downloadState) {
        lastProgressState=downloadState.getCopy();
        publishProgress(lastProgressState);
    }

    @Override
    protected void onProgressUpdate(DownloadState... values) {
        super.onProgressUpdate(values);
        createActivity.publishProgress(values[0]);
    }

    public void setCreateActivity(CreateActivity createActivity) {
        this.createActivity=createActivity;
    }

    public DownloadState getLastProgressState() {
        return lastProgressState;
    }
}
