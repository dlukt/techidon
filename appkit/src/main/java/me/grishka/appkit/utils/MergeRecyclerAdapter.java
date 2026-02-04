package me.grishka.appkit.utils;

import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.recyclerview.widget.RecyclerView;
import me.grishka.appkit.imageloader.ImageLoaderRecyclerAdapter;
import me.grishka.appkit.imageloader.requests.ImageLoaderRequest;

/**
 * A RecyclerView adapter which merges multiple other adapters into a single list.
 *
 * You MUST override getItemViewType() in each of your adapters and make sure the returned values don't intersect across adapters.
 * If they do, bad things™ will happen.
 */
public class MergeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ImageLoaderRecyclerAdapter{

	private ArrayList<RecyclerView.Adapter<? extends RecyclerView.ViewHolder>> adapters=new ArrayList<>();
	private SparseArray<RecyclerView.Adapter<? extends RecyclerView.ViewHolder>> viewTypeMapping=new SparseArray<>();
	private HashMap<RecyclerView.Adapter<? extends RecyclerView.ViewHolder>, InternalDataObserver> observers=new HashMap<>();

	public void addAdapter(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter){
		addAdapter(adapters.size(), adapter);
	}

	public void addAdapter(int index, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter){
		if(adapters.contains(adapter))
			throw new IllegalArgumentException("Adapter "+adapter+" is already added!");
		adapters.add(index, adapter);
		InternalDataObserver observer=new InternalDataObserver(adapter);
		adapter.registerAdapterDataObserver(observer);
		observers.put(adapter, observer);
		notifyDataSetChanged();
	}

	public void removeAdapter(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter){
		if(adapters.remove(adapter)){
			adapter.unregisterAdapterDataObserver(observers.get(adapter));
			observers.remove(adapter);
			notifyDataSetChanged();
		}
	}

	public void removeAdapterAt(int index){
		removeAdapter(adapters.get(index));
	}

	public void removeAllAdapters(){
		for(RecyclerView.Adapter adapter:adapters){
			adapter.unregisterAdapterDataObserver(observers.get(adapter));
			observers.remove(adapter);
		}
		adapters.clear();
		notifyDataSetChanged();
	}

	public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapterAt(int index){
		return adapters.get(index);
	}

	public int getAdapterCount(){
		return adapters.size();
	}

	public int getAdapterPosition(int pos){
		int count=0;
		for(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter:adapters){
			int c=adapter.getItemCount();
			if(pos>=count && pos<count+c){
				return pos-count;
			}
			count+=c;
		}
		return pos;
	}

	public int getPositionForAdapter(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter){
		int pos=0;
		for(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> a:adapters){
			if(a==adapter)
				return pos;
			pos+=a.getItemCount();
		}
		return pos;
	}


	public RecyclerView.Adapter<? extends RecyclerView.ViewHolder> getAdapterForPosition(int pos){
		int count=0;
		for(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter:adapters){
			int c=adapter.getItemCount();
			if(pos>=count && pos<count+c){
				return adapter;
			}
			count+=c;
		}
		return null;
	}

	public int getAdapterIndexForPosition(int pos){
		int count=0;
		int i=0;
		for(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter:adapters){
			int c=adapter.getItemCount();
			if(pos>=count && pos<count+c){
				return i;
			}
			count+=c;
			i++;
		}
		return -1;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return viewTypeMapping.get(viewType).onCreateViewHolder(parent, viewType);
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		bindViewHolder(getAdapterForPosition(position), holder, getAdapterPosition(position));
	}

	@Override
	public int getItemViewType(int position) {
		RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter=getAdapterForPosition(position);
		int viewType=adapter.getItemViewType(getAdapterPosition(position));
		viewTypeMapping.put(viewType, adapter);
		return viewType;
	}

	@Override
	public int getItemCount() {
		int count=0;
		for(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter:adapters){
			count+=adapter.getItemCount();
		}
		return count;
	}

	@Override
	public int getImageCountForItem(int position) {
		RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter=getAdapterForPosition(position);
		if(adapter instanceof ImageLoaderRecyclerAdapter){
			return ((ImageLoaderRecyclerAdapter)adapter).getImageCountForItem(getAdapterPosition(position));
		}
		return 0;
	}

	@Override
	public ImageLoaderRequest getImageRequest(int position, int image) {
		RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter=getAdapterForPosition(position);
		if(adapter instanceof ImageLoaderRecyclerAdapter){
			return ((ImageLoaderRecyclerAdapter)adapter).getImageRequest(getAdapterPosition(position), image);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return getAdapterForPosition(position).getItemId(getAdapterPosition(position));
	}

	@SuppressWarnings("unchecked")
	private static <VH extends RecyclerView.ViewHolder> void bindViewHolder(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter, RecyclerView.ViewHolder holder, int position){
		((RecyclerView.Adapter<VH>) adapter).onBindViewHolder((VH) holder, position);
	}

	@SuppressWarnings("unchecked")
	public static RecyclerView.Adapter<? extends RecyclerView.ViewHolder> asViewHolderAdapter(RecyclerView.Adapter adapter){
		return (RecyclerView.Adapter<? extends RecyclerView.ViewHolder>) adapter;
	}

	private class InternalDataObserver extends RecyclerView.AdapterDataObserver{

		private RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter;

		public InternalDataObserver(RecyclerView.Adapter<? extends RecyclerView.ViewHolder> adapter){
			this.adapter=adapter;
		}

		@Override
		public void onChanged(){
			notifyDataSetChanged();
		}

		@Override
		public void onItemRangeChanged(int positionStart, int itemCount){
			notifyItemRangeChanged(getPositionForAdapter(adapter)+positionStart, itemCount);
		}

		@Override
		public void onItemRangeChanged(int positionStart, int itemCount, Object payload){
			notifyItemRangeChanged(getPositionForAdapter(adapter)+positionStart, itemCount, payload);
		}

		@Override
		public void onItemRangeInserted(int positionStart, int itemCount){
			notifyItemRangeInserted(getPositionForAdapter(adapter)+positionStart, itemCount);
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount){
			notifyItemRangeRemoved(getPositionForAdapter(adapter)+positionStart, itemCount);
		}

		@Override
		public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount){
			if(itemCount!=1) throw new UnsupportedOperationException("Can't move more than one item");
			int offset=getPositionForAdapter(adapter);
			notifyItemMoved(offset+fromPosition, offset+toPosition);
		}
	}
}
