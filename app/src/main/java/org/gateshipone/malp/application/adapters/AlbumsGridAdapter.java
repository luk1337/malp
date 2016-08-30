/*
 * Copyright (C) 2016  Hendrik Borghorst
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.gateshipone.malp.application.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import org.gateshipone.malp.application.listviewitems.ArtistGridViewItem;
import org.gateshipone.malp.application.listviewitems.SimpleListItem;
import org.gateshipone.malp.mpdservice.mpdprotocol.mpdobjects.MPDAlbum;

public class AlbumsGridAdapter extends GenericSectionAdapter<MPDAlbum> {

    private final AbsListView mListView;
    private final Context mContext;

    private boolean mUseList;

    public AlbumsGridAdapter(Context context, AbsListView listView, boolean useList) {
        super();

        mContext = context;
        mListView = listView;

        mUseList = useList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MPDAlbum album = (MPDAlbum)getItem(position);
        String label = album.getName();

        if ( mUseList ) {
            // Check if a view can be recycled
            if (convertView != null) {
                SimpleListItem listItem = (SimpleListItem) convertView;

                // Make sure to reset the layoutParams in case of change (rotation for example)
                listItem.setText(label);
            } else {
                // Create new view if no reusable is available
                convertView = new SimpleListItem(mContext,label);
            }
        } else {
            // Check if a view can be recycled
            if (convertView != null) {
                ArtistGridViewItem gridItem = (ArtistGridViewItem) convertView;

                // Make sure to reset the layoutParams in case of change (rotation for example)
                ViewGroup.LayoutParams layoutParams = gridItem.getLayoutParams();
                layoutParams.height = ((GridView)mListView).getColumnWidth();
                layoutParams.width = ((GridView)mListView).getColumnWidth();
                gridItem.setLayoutParams(layoutParams);
                gridItem.setTitle(label);
            } else {
                // Create new view if no reusable is available
                convertView = new ArtistGridViewItem(mContext, label, null, new android.widget.AbsListView.LayoutParams(((GridView)mListView).getColumnWidth(), ((GridView)mListView).getColumnWidth()));
            }

            // Check if the scroll speed currently is already 0, then start the image task right away.
            if (mScrollSpeed == 0) {
                ((ArtistGridViewItem) convertView).startCoverImageTask();
            }
        }

        return convertView;
    }
}
