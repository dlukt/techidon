package de.icod.techidon.ui.viewholders;

import android.content.Context;
import android.view.ViewGroup;

import de.icod.techidon.R;
import de.icod.techidon.model.viewmodel.ListItem;

@SuppressWarnings("deprecation")

public class SimpleListItemViewHolder extends ListItemViewHolder<ListItem<?>>{
	public SimpleListItemViewHolder(Context context, ViewGroup parent){
		super(context, R.layout.item_generic_list, parent);
	}
}
