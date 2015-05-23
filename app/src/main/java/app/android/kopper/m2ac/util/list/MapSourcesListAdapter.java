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

package app.android.kopper.m2ac.util.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import app.android.kopper.m2ac.R;
import app.android.kopper.m2ac.providers.FreemapSlovakiaAtlasMapSource;
import app.android.kopper.m2ac.providers.FreemapSlovakiaCycloMapSource;
import app.android.kopper.m2ac.providers.FreemapSlovakiaHikingMapSource;
import app.android.kopper.m2ac.providers.IMapSource;
import app.android.kopper.m2ac.providers.MapQuestMapMapSource;
import app.android.kopper.m2ac.providers.MapQuestSatMapSource;
import app.android.kopper.m2ac.providers.UmpPcPlMapSource;
import app.android.kopper.m2ac.util.ui.FlowLayout;
import app.android.kopper.selectmaparea.util.LogUtil;

public class MapSourcesListAdapter extends BaseAdapter {

    private final Context context;

    private static final IMapSource sources[]= {
          //  new FakeMapSource(),
            new UmpPcPlMapSource(),
            new MapQuestMapMapSource(),
            new MapQuestSatMapSource(),
            new FreemapSlovakiaAtlasMapSource(),
            new FreemapSlovakiaHikingMapSource(),
            new FreemapSlovakiaCycloMapSource()
    };

    public static IMapSource getMapSource(String mapSourceName) {
        for(IMapSource mapSource:sources)
            if(mapSource.getName().equals(mapSourceName))
                return(mapSource);
        return(null);
    }

    private final int maxZoom;

    public static class Def implements Serializable {

        private String provider;
        private int zoom;

        public Def(String provider,int zoom) {
            this.provider=provider;
            this.zoom=zoom;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Def && ((Def)o).provider.equals(provider) && ((Def)o).zoom==zoom;
        }
    }

    private Map<String,Set<Integer>> selectedElements=new HashMap<>();

    public MapSourcesListAdapter(Context context,int maxZoom) {
        this.context=context;
        this.maxZoom=maxZoom;
    }


    @Override
    public int getCount() {
        return sources.length;
    }

    @Override
    public Object getItem(int position) {
        return sources[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        try {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView=inflater.inflate(R.layout.list_map_source,parent,false);
            TextView textView=(TextView)rowView.findViewById(R.id.tName);
            IMapSource mapSource=sources[position];
            textView.setText(mapSource.getVisibleName());

            Set<Integer> selectedZooms=selectedElements.get(mapSource.getName());
            if(selectedZooms==null)
                selectedZooms=new TreeSet<>();

            final FlowLayout zoomsLayout=(FlowLayout)rowView.findViewById(R.id.zoomLayot);
            for(int i=mapSource.getMinZoom();i<=mapSource.getMaxZoom()&&i<=maxZoom;i++) {
                CheckBox t=new CheckBox(context);
                Def def=new Def(mapSource.getName(),i);
                t.setTag(def);
                if(selectedZooms.contains(i))
                    t.setChecked(true);
                t.setText(String.valueOf(i));
                t.setTextColor(textView.getTextColors().getDefaultColor());
                t.setSingleLine(true);
                t.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                        Def def=(Def)buttonView.getTag();
                        if(isChecked) {
                            Set<Integer> zooms=selectedElements.get(def.provider);
                            if(zooms==null) {
                                zooms=new TreeSet<Integer>();
                                selectedElements.put(def.provider,zooms);
                            }
                            zooms.add(def.zoom);
                        }
                        else {
                            Set<Integer> zooms=selectedElements.get(def.provider);
                            if(zooms!=null) {
                                zooms.remove(new Integer(def.zoom));
                                if(zooms.size()==0)
                                    selectedElements.remove(def.provider);
                            }
                        }
                    }
                });
                zoomsLayout.addView(t,new FlowLayout.LayoutParams(2,2));
            }
            zoomsLayout.setVisibility(View.GONE);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoomsLayout.setVisibility(zoomsLayout.getVisibility()==View.GONE?View.VISIBLE:View.GONE);
                }
            });
            return rowView;
        } catch(Exception e) {
            LogUtil.e(e);
        }
        return (null);
    }

    public Map<String,Set<Integer>> getSelectedElements() {
        return selectedElements;
    }

    public void setSelectedElements(Map<String,Set<Integer>> selectedElements) {
        this.selectedElements=selectedElements;
    }
}
