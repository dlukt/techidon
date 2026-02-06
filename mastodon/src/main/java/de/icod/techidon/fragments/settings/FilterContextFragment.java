package de.icod.techidon.fragments.settings;

import android.os.Bundle;

import de.icod.techidon.R;
import de.icod.techidon.model.FilterContext;
import de.icod.techidon.model.viewmodel.CheckableListItem;
import de.icod.techidon.model.viewmodel.ListItem;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

import me.grishka.appkit.fragments.OnBackPressedListener;

@SuppressWarnings({"deprecation", "unchecked"})

public class FilterContextFragment extends BaseSettingsFragment<FilterContext> implements OnBackPressedListener{
	private static final String STATE_CONTEXT="state_context";

	private EnumSet<FilterContext> context;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		resetDataOnRestore(savedInstanceState);
		setTitle(R.string.settings_filter_context);
		if(savedInstanceState!=null && savedInstanceState.containsKey(STATE_CONTEXT)){
			context=(EnumSet<FilterContext>) savedInstanceState.getSerializable(STATE_CONTEXT);
		}
		if(context==null){
			context=(EnumSet<FilterContext>) getArguments().getSerializable("context");
		}
		if(context==null){
			context=EnumSet.noneOf(FilterContext.class);
		}
		onDataLoaded(Arrays.stream(FilterContext.values()).map(c->{
			CheckableListItem<FilterContext> item=new CheckableListItem<>(c.getDisplayNameRes(), 0, CheckableListItem.Style.CHECKBOX, context.contains(c), this::toggleCheckableItem);
			item.parentObject=c;
			item.isEnabled=true;
			return item;
		}).collect(Collectors.toList()));
	}

	@Override
	protected void doLoadData(int offset, int count){}

	@Override
	public boolean onBackPressed(){
		context=collectCurrentContext();
		Bundle args=new Bundle();
		args.putSerializable("context", context);
		setResult(true, args);
		return false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putSerializable(STATE_CONTEXT, collectCurrentContext());
	}

	private EnumSet<FilterContext> collectCurrentContext(){
		EnumSet<FilterContext> selected=EnumSet.noneOf(FilterContext.class);
		for(ListItem<FilterContext> item:data){
			if(((CheckableListItem<FilterContext>) item).checked)
				selected.add(item.parentObject);
		}
		return selected;
	}
}
