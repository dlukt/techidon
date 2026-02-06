package de.icod.techidon.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.icod.techidon.R;
import de.icod.techidon.api.requests.tags.GetFollowedHashtags;
import de.icod.techidon.model.Hashtag;
import de.icod.techidon.model.HeaderPaginationList;
import de.icod.techidon.ui.DividerItemDecoration;
import de.icod.techidon.ui.utils.UiUtils;
import de.icod.techidon.utils.ProvidesAssistContent;

import me.grishka.appkit.api.SimpleCallback;
import me.grishka.appkit.utils.BindableViewHolder;
import me.grishka.appkit.views.UsableRecyclerView;

@SuppressWarnings("deprecation")

public class FollowedHashtagsFragment extends MastodonRecyclerFragment<Hashtag> implements ScrollableToTop, ProvidesAssistContent.ProvidesWebUri {
    private static final String STATE_NEXT_MAX_ID="state_next_max_id";

    private String nextMaxID;
    private String accountID;

    public FollowedHashtagsFragment() {
        super(20);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args=getArguments();
        accountID=args.getString("account");
        setTitle(R.string.sk_hashtags_you_follow);
        if(savedInstanceState!=null){
            nextMaxID=savedInstanceState.getString(STATE_NEXT_MAX_ID);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(STATE_NEXT_MAX_ID, nextMaxID);
    }

    @Override
    protected void onShown(){
        super.onShown();
        if(!getArguments().getBoolean("noAutoLoad") && !loaded && !dataLoading)
            loadData();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list.addItemDecoration(new DividerItemDecoration(getActivity(), R.attr.colorM3OutlineVariant, 0.5f, 56, 16));
    }

    @Override
    protected void doLoadData(int offset, int count){
        currentRequest=new GetFollowedHashtags(offset==0 ? null : nextMaxID, null, count, null)
                .setCallback(new SimpleCallback<>(this){
                    @Override
                    public void onSuccess(HeaderPaginationList<Hashtag> result){
                        if(getActivity()==null) return;
                        if(result.nextPageUri!=null)
                            nextMaxID=result.nextPageUri.getQueryParameter("max_id");
                        else
                            nextMaxID=null;
                        onDataLoaded(result, nextMaxID!=null);
                    }
                })
                .exec(accountID);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        return new HashtagsAdapter();
    }


    @Override
    public void scrollToTop() {
        smoothScrollRecyclerViewToTop(list);
    }

    @Override
    public String getAccountID() {
        return accountID;
    }

    @Override
    public Uri getWebUri(Uri.Builder base) {
        return isInstanceAkkoma() ? null : base.path("/followed_tags").build();
    }

    private class HashtagsAdapter extends RecyclerView.Adapter<HashtagViewHolder>{
        @NonNull
        @Override
        public HashtagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            return new HashtagViewHolder();
        }

        @Override
        public void onBindViewHolder(@NonNull HashtagViewHolder holder, int position) {
            holder.bind(data.get(position));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class HashtagViewHolder extends BindableViewHolder<Hashtag> implements UsableRecyclerView.Clickable{
        private final TextView title;

        public HashtagViewHolder(){
            super(getActivity(), R.layout.item_text, list);
            title=findViewById(R.id.title);
        }

        @Override
        public void onBind(Hashtag item) {
            title.setText(item.name);
            title.setCompoundDrawablesRelativeWithIntrinsicBounds(itemView.getContext().getDrawable(R.drawable.ic_fluent_number_symbol_24_regular), null, null, null);
        }

        @Override
        public void onClick() {
            UiUtils.openHashtagTimeline(getActivity(), accountID, item.name);
        }
    }
}
