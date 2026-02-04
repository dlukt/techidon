package de.icod.techidon.ui.viewholders;

import android.content.Context;
import android.view.ViewGroup;

import de.icod.techidon.R;
import de.icod.techidon.model.viewmodel.CheckableListItem;
import de.icod.techidon.ui.views.CheckableLinearLayout;

@SuppressWarnings("deprecation")

public abstract class CheckableListItemViewHolder extends ListItemViewHolder<CheckableListItem<?>>{
	protected final CheckableLinearLayout checkableLayout;

	public CheckableListItemViewHolder(Context context, ViewGroup parent){
		super(context, R.layout.item_generic_list_checkable, parent);
		checkableLayout=(CheckableLinearLayout) itemView;
	}

	@Override
	public void onBind(CheckableListItem<?> item){
		super.onBind(item);
		checkableLayout.setChecked(item.checked);
	}
}
