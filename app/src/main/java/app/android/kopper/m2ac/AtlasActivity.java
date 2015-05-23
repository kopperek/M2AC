
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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.android.kopper.m2ac.util.Layer;
import app.android.kopper.m2ac.util.StringUtil;
import app.android.kopper.m2ac.util.list.LayersListAdapter;
import app.android.kopper.selectmaparea.MapActivity;

public class AtlasActivity extends ListActivity {

    private static final int NEW_LAYER_REQUEST_CODE=0x01;
    private LayersListAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atlas);

        myAdapter=new LayersListAdapter(this.getApplicationContext());
        List<Layer> layers=(List<Layer>)getLastNonConfigurationInstance();
        if(layers!=null)
            myAdapter.setLayers(layers);
        setListAdapter(myAdapter);

        findViewById(R.id.bAddLayer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newLayerWizardIntent=new Intent(getApplicationContext(),app.android.kopper.selectmaparea.MapActivity.class);
                newLayerWizardIntent.putExtra(MapActivity.class.getCanonicalName()+"-next",LayersActivity.class);
                startActivityForResult(newLayerWizardIntent,NEW_LAYER_REQUEST_CODE);
            }
        });
        findViewById(R.id.bCreateAtlas).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String atlasName=StringUtil.makeIOFrendlyName(((TextView)findViewById(R.id.tAtlasName)).getText().toString().trim());
                if(atlasName.length()==0) {
                    Toast.makeText(getApplicationContext(),getString(R.string.error_empty_atlas_name),Toast.LENGTH_SHORT).show();
                    return;
                }
                //calculate tiles count
                long tilesCount=0;
                for(Layer layer : myAdapter.getLayers())
                    tilesCount+=layer.getTilesCount();
                if(tilesCount==0) {
                    Toast.makeText(getApplicationContext(),getString(R.string.errors_no_tiles),Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(getWindow().getContext())
                        .setTitle(getString(R.string.create))
                        .setMessage(String.format(getString(R.string.create_confirm),tilesCount))
                        .setPositiveButton(R.string.dialog_yes,new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                Intent createActivity=new Intent(getApplicationContext(),CreateActivity.class);
                                createActivity.putExtra(CreateActivity.LAYERS,(Serializable)myAdapter.getLayers());
                                createActivity.putExtra(CreateActivity.ATLAS_NAME,atlasName);
                                startActivity(createActivity);
                                dialog.dismiss();
                            }

                        })
                        .setNegativeButton(R.string.dialog_no,new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK) {
            String layerName=data.getStringExtra(LayersActivity.LAYER_NAME);
            Map<String,Set<Integer>> mapSources=(Map<String,Set<Integer>>)data.getSerializableExtra(LayersActivity.MAP_SOURCES);
            List<LatLng> positions=(List<LatLng>)data.getSerializableExtra(MapActivity.SELECTED_POSITIONS);

            for(String mapSource:mapSources.keySet()) {
                myAdapter.addLayer(new Layer(layerName,mapSource,mapSources.get(mapSource),positions.get(0),positions.get(1)));
            }
        }
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return myAdapter.getLayers();
    }
}
