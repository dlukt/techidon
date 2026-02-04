package de.icod.techidon.ui.displayitems;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.icod.techidon.R;
import de.icod.techidon.fragments.BaseStatusListFragment;
import de.icod.techidon.model.LegacyFilter;
import de.icod.techidon.model.Status;
import de.icod.techidon.ui.OutlineProviders;

import java.util.List;

@SuppressWarnings("deprecation")

public class WarningFilteredStatusDisplayItem extends StatusDisplayItem{
	public boolean loading;
	public List<StatusDisplayItem> filteredItems;
	public LegacyFilter applyingFilter;

	public WarningFilteredStatusDisplayItem(String parentID, BaseStatusListFragment<?> parentFragment, Status status, List<StatusDisplayItem> filteredItems, LegacyFilter applyingFilter){
		super(parentID, parentFragment);
		this.status=status;
		this.filteredItems=filteredItems;
		this.applyingFilter=applyingFilter;
	}

	@Override
	public Type getType(){
		return Type.WARNING;
	}

	public static class Holder extends StatusDisplayItem.Holder<WarningFilteredStatusDisplayItem>{
		public final View warningWrap;
		public final Button showBtn;
		public final TextView text;
		public List<StatusDisplayItem> filteredItems;

		public Holder(Context context, ViewGroup parent){
			super(context, R.layout.display_item_warning, parent);
			warningWrap=findViewById(R.id.warning_wrap);
			showBtn=findViewById(R.id.reveal_btn);
			showBtn.setOnClickListener(i->item.parentFragment.onWarningClick(this));
			itemView.setOnClickListener(v->item.parentFragment.onWarningClick(this));
			text=findViewById(R.id.text);
		}

		@Override
		public void onBind(WarningFilteredStatusDisplayItem item){
			filteredItems=item.filteredItems;
			String title=item.applyingFilter.title;
			text.setText(item.parentFragment.getString(R.string.sk_filtered, title));

			if(item.inset){
				itemView.setClipToOutline(true);
				itemView.setOutlineProvider(OutlineProviders.roundedRect(8));
			}
		}

		@Override
		public void onClick(){}
	}
}
