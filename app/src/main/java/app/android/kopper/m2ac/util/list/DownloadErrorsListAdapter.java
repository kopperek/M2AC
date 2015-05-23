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
import android.widget.TextView;

import java.util.Arrays;

import app.android.kopper.m2ac.CreateActivity;
import app.android.kopper.m2ac.R;
import app.android.kopper.m2ac.util.LogUtil;
import app.android.kopper.m2ac.util.download.DownloadErrors;
import app.android.kopper.m2ac.util.download.DownloadState;


public class DownloadErrorsListAdapter extends BaseAdapter {

    private final CreateActivity createActivity;
    private DownloadState downloadState=new DownloadState();

    public DownloadErrorsListAdapter(CreateActivity createActivity) {
        this.createActivity=createActivity;
    }

    @Override
    public int getCount() {
        return downloadState.getErrors().size();
    }

    @Override
    public Object getItem(int position) {
        return downloadState.getErrors().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        View rowView=null;
        try {
            LayoutInflater inflater=(LayoutInflater)createActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView=inflater.inflate(R.layout.list_download_errors,parent,false);
            DownloadErrors errors=(DownloadErrors)getItem(position);
            setText(rowView,R.id.tLayerName,errors.getLayer().getVisibleName()+"/"+errors.getZoom());
            setText(rowView,R.id.tAddInfo,String.format(createActivity.getString(R.string.download_errors),errors.getErrors().size(),errors.getLayer().getTilesCount(errors.getZoom())));
            View removeButton=rowView.findViewById(R.id.bTryAgain);
            removeButton.setTag(errors);
            removeButton.setEnabled(downloadState.isWaitForInput());
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadErrors errors=(DownloadErrors)v.getTag();
                    createActivity.getTilesDownloadTask().inputResult(Arrays.asList(errors));
                }
            });
        } catch(Exception e) {
            LogUtil.e(e);
        }
        return rowView;
    }

    private void setText(View rowView,int fieldId,String value) {
        TextView textView=(TextView)rowView.findViewById(fieldId);
        textView.setText(value);
    }


    public void setState(DownloadState value) {
        if(!value.getErrors().equals(this.downloadState.getErrors())||!(value.isWaitForInput()==this.downloadState.isWaitForInput())) {
            this.downloadState=value;
            notifyDataSetChanged();
        }
    }
}
