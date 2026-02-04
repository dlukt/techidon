package de.icod.techidon.ui.viewholders;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.TextView;

import de.icod.techidon.R;
import de.icod.techidon.model.Instance;
import de.icod.techidon.ui.text.HtmlParser;

import me.grishka.appkit.utils.BindableViewHolder;

@SuppressWarnings("deprecation")

public class InstanceRuleViewHolder extends BindableViewHolder<Instance.Rule>{
	private final TextView text, number;
	private int position;

	public InstanceRuleViewHolder(ViewGroup parent){
		super(parent.getContext(), R.layout.item_server_rule, parent);
		text=findViewById(R.id.text);
		number=findViewById(R.id.number);
	}

	public void setPosition(int position){
		this.position=position;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public void onBind(Instance.Rule item){
		if(item.parsedText==null){
			item.parsedText=HtmlParser.parseLinks(item.text);
		}
		text.setText(item.parsedText);
		number.setText(String.format("%d", position+1));
	}
}
