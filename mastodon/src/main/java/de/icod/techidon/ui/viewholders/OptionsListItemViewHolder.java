package de.icod.techidon.ui.viewholders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import de.icod.techidon.R;
import de.icod.techidon.model.viewmodel.ListItemWithOptionsMenu;

@SuppressWarnings("deprecation")

public class OptionsListItemViewHolder extends ListItemViewHolder<ListItemWithOptionsMenu<?>>{
	private final PopupMenu menu;
	private final ImageButton menuBtn;

	public OptionsListItemViewHolder(Context context, ViewGroup parent){
		super(context, R.layout.item_generic_list_options, parent);
		menuBtn=findViewById(R.id.options_btn);
		menu=new PopupMenu(context, menuBtn);
		menuBtn.setOnClickListener(this::onMenuBtnClick);

		menu.setOnMenuItemClickListener(menuItem->{
			item.performItemSelected(menuItem);
			return true;
		});
	}

	private void onMenuBtnClick(View v){
		menu.getMenu().clear();
		item.performConfigureMenu(menu.getMenu());
		menu.show();
	}
}
