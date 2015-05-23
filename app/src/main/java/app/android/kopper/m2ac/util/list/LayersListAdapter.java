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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import app.android.kopper.m2ac.R;
import app.android.kopper.m2ac.util.Layer;
import app.android.kopper.m2ac.util.LogUtil;
import app.android.kopper.m2ac.util.StringUtil;

public class LayersListAdapter extends BaseAdapter {

    private final Context context;
    List<Layer> layers=new LinkedList();

    public LayersListAdapter(Context context) {
        this.context=context;
    }

    @Override
    public int getCount() {
        return layers.size();
    }

    @Override
    public Object getItem(int position) {
        return layers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,View convertView,final ViewGroup parent) {
        try {
            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView=inflater.inflate(R.layout.list_layer,parent,false);
            final Layer layer=layers.get(position);
            setText(rowView,R.id.tName,layer.getVisibleName());
            setText(rowView,R.id.tLatLng,layer.getBoundsAsString());
            setText(rowView,R.id.tZooms,layer.getZoomAsString());
            View removeButton=rowView.findViewById(R.id.bRemove);
            removeButton.setTag(position);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Layer ll=layers.get(((Integer)v.getTag()).intValue());
                    new AlertDialog.Builder(parent.getContext())
                            .setTitle(context.getString(R.string.dialog_remove))
                            .setMessage(String.format(context.getString(R.string.dialog_remove_confirm),ll.getVisibleName()))
                            .setPositiveButton(context.getString(R.string.dialog_yes),new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    layers.remove(ll);
                                    notifyDataSetChanged();
                                    dialog.dismiss();
                                }

                            })
                            .setNegativeButton(context.getString(R.string.dialog_no),new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int which) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            });
            View renameButton=rowView.findViewById(R.id.bRename);
            renameButton.setTag(position);
            renameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //todo: message box
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                    builder.setTitle(context.getString(R.string.dialog_rename));
                    final EditText input = new EditText(parent.getContext());
                    final Layer ll=layers.get(((Integer)v.getTag()).intValue());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setText(ll.getVisibleName());
                    builder.setView(input);
                    builder.setPositiveButton(context.getString(R.string.dialog_ok),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            String providedName=StringUtil.makeIOFrendlyName(input.getText().toString());
                            if(providedName!=null&&!providedName.equals(ll.getVisibleName())) {
                                ll.setVisibleName(getUniqueName(providedName));
                                notifyDataSetChanged();
                            }
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton(context.getString(R.string.dialog_cancel),new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });
            return rowView;
        } catch(Exception e) {
            LogUtil.e(e);
        }
        return (null);
    }

    private String getUniqueName(String name) {
        if(!nameExists(name))
            return(name);
        for(int a=1;;a++) {
            String newName=name+"-"+a;
            if(!nameExists(newName))
                return(newName);
        }
    }

    private boolean nameExists(String name) {
        for(Layer ll:layers)
            if(name.equals(ll.getVisibleName()))
                return(true);
        return(false);
    }

    private void setText(View rowView,int fieldId,String value) {
        TextView textView=(TextView)rowView.findViewById(fieldId);
        textView.setText(value);
    }

    public void addLayer(Layer layer) {
        layer.setVisibleName(getUniqueName(layer.getVisibleName()));
        this.layers.add(layer);
        notifyDataSetChanged();
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers=layers;
    }
}
