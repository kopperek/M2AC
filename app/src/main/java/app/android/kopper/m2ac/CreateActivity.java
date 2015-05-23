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

package app.android.kopper.m2ac;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import app.android.kopper.ioutil.IOUtil;
import app.android.kopper.m2ac.util.Layer;
import app.android.kopper.m2ac.util.LogUtil;
import app.android.kopper.m2ac.util.StringDef;
import app.android.kopper.m2ac.util.download.AtlasDef;
import app.android.kopper.m2ac.util.download.DownloadErrors;
import app.android.kopper.m2ac.util.download.DownloadResult;
import app.android.kopper.m2ac.util.download.DownloadState;
import app.android.kopper.m2ac.util.download.TilesDownloadTask;
import app.android.kopper.m2ac.util.list.DownloadErrorsListAdapter;

public class CreateActivity extends ListActivity {

    public static final String LAYERS="layers";
    public static final String ATLAS_NAME="atlas.name";

    private TilesDownloadTask tilesDownloadTask;
    private DownloadErrorsListAdapter myAdapter;

    public static byte[] FAKE_TILE;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        try {
            InputStream blankStream=getResources().openRawResource(R.raw.fake);
            FAKE_TILE=IOUtil.read(blankStream);
            blankStream.close();
        } catch(IOException e) {
            LogUtil.e(e);
        }


        findViewById(R.id.bCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        findViewById(R.id.bIgnoreAndContinue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilesDownloadTask.inputResult(Boolean.TRUE);
            }
        });
        findViewById(R.id.bTryAgainForAllLayers).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tilesDownloadTask.inputResult(tilesDownloadTask.getLastProgressState().getErrors());
            }
        });

        myAdapter=new DownloadErrorsListAdapter(this);
        setListAdapter(myAdapter);

        tilesDownloadTask=(TilesDownloadTask)getLastNonConfigurationInstance();
        if(tilesDownloadTask==null) {
            publishProgress(new DownloadState());
            List<Layer> layers=(List<Layer>)getIntent().getExtras().get(LAYERS);
            String atlasName=getIntent().getStringExtra(ATLAS_NAME);
            if(atlasName==null)
                atlasName="out";
            tilesDownloadTask=new TilesDownloadTask(this);
            tilesDownloadTask.execute(new AtlasDef(atlasName,layers));
        } else {
            tilesDownloadTask.setCreateActivity(this);
            publishProgress(tilesDownloadTask.getLastProgressState());
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return tilesDownloadTask;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancel();
    }

    private void cancel() {
        getWindow().getDecorView().findViewById(android.R.id.content).setEnabled(false);
        tilesDownloadTask.interrupt(R.string.download_canceled);
    }

    public void publishProgress(DownloadState value) {
        setText(R.id.tTaskName,value.getTaskName());
        setText(R.id.tSubTaskName,value.getSubTaskName());
        setText(R.id.tSubSubTaskName,value.getSubSubTaskName());
        ProgressBar progress=(ProgressBar)findViewById(R.id.pProgress);
        progress.setMax(value.getProgressTotal());
        progress.setProgress(value.getProgressValue());
        progress.setVisibility(value.getProgressTotal()==value.getProgressValue()?View.INVISIBLE:View.VISIBLE);
        List<DownloadErrors> errors=value.getErrors();
        int visibility=errors.size()>0?View.VISIBLE:View.INVISIBLE;
        findViewById(R.id.lList).setVisibility(visibility);
        findViewById(R.id.bTryAgainForAllLayers).setVisibility(visibility);
        View ignoreAndContinueButton=findViewById(R.id.bIgnoreAndContinue);
        ignoreAndContinueButton.setVisibility(visibility);
        ignoreAndContinueButton.setEnabled(value.isWaitForInput());
        findViewById(R.id.bTryAgainForAllLayers).setEnabled(value.isWaitForInput());
        myAdapter.setState(value);
        DownloadResult downloadResult=value.getDownloadResult();
        if(downloadResult!=null)
            downloadFinished(downloadResult);
    }

    private void setText(int id,StringDef value) {
        String string=value==null?"":String.format(getText(value.getResourceId()).toString(),value.getParams());
        ((TextView)findViewById(id)).setText(string);
    }

    private void downloadFinished(DownloadResult downloadResult) {
        try {
            AlertDialog alertDialog;
            alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.setMessage(String.format(getText(downloadResult.getMessageKey()).toString(),downloadResult.getMessageParams()));
            alertDialog.setTitle(getText(R.string.dialog_finished));
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getText(R.string.dialog_ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            alertDialog.show();
        } catch(Exception e) {
            LogUtil.e(e);
        }
    }

    public TilesDownloadTask getTilesDownloadTask() {
        return tilesDownloadTask;
    }
}
