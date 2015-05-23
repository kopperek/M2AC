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

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Map;
import java.util.Set;

import app.android.kopper.m2ac.util.MercatorPower2MapSpace;
import app.android.kopper.m2ac.util.XY;
import app.android.kopper.m2ac.util.list.MapSourcesListAdapter;
import app.android.kopper.selectmaparea.MapActivity;

public class LayersActivity extends ListActivity {

    public static final String LAYER_NAME="LayerName";
    public static final String MAP_SOURCES="MapSources";

    private MapSourcesListAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layers);

        List<LatLng> positions=(List<LatLng>)getOrCreateIntent().getExtras().get(MapActivity.SELECTED_POSITIONS);

        XY tileXY1=MercatorPower2MapSpace.INSTANCE_256.toTileXY(positions.get(0),MercatorPower2MapSpace.MAX_ZOOM);
        XY tileXY2=MercatorPower2MapSpace.INSTANCE_256.toTileXY(positions.get(1),MercatorPower2MapSpace.MAX_ZOOM);
        long width= Math.abs(tileXY2.getX()-tileXY1.getX())+1;
        long height=Math.abs(tileXY2.getY()-tileXY1.getY())+1;
        int zoom=MercatorPower2MapSpace.MAX_ZOOM;
        for(;;) {
            if(width<128&&height<128)
                break;
            width/=2;
            height/=2;
            zoom--;
        }
        long tiles=width*height;
        myAdapter=new MapSourcesListAdapter(this.getApplicationContext(),zoom);
        Map<String,Set<Integer>> list=(Map<String,Set<Integer>>)getLastNonConfigurationInstance();
        if(list!=null)
            myAdapter.setSelectedElements(list);
        setListAdapter(myAdapter);

        findViewById(R.id.bDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String,Set<Integer>> selectedElements=myAdapter.getSelectedElements();
                if(selectedElements.size()==0) {
                    Toast.makeText(getApplicationContext(),getString(R.string.error_no_map_source),Toast.LENGTH_SHORT).show();
                    return;
                }

                String layerName=((TextView)findViewById(R.id.tLayerName)).getText().toString().trim();
                if(layerName.length()==0) {
                    Toast.makeText(getApplicationContext(),getString(R.string.error_empty_layer_name),Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent=getOrCreateIntent();
                intent.putExtra(LAYER_NAME,layerName);
                intent.putExtra(MAP_SOURCES,(java.io.Serializable)selectedElements);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    public Intent getOrCreateIntent() {
        return getIntent()==null?new Intent():getIntent();
    }

    @Override
    public Object onRetainNonConfigurationInstance() {
        return myAdapter.getSelectedElements();
    }
}
