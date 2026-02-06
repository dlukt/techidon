package de.icod.techidon.fragments;

import android.content.Context;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import de.icod.techidon.R;
import de.icod.techidon.api.requests.timelines.GetPublicTimeline;
import de.icod.techidon.api.session.AccountSessionManager;
import de.icod.techidon.model.FilterContext;
import de.icod.techidon.model.Status;
import de.icod.techidon.model.TimelineDefinition;
import de.icod.techidon.ui.utils.UiUtils;
import de.icod.techidon.utils.ProvidesAssistContent;

import java.util.List;

import me.grishka.appkit.api.SimpleCallback;

@SuppressWarnings("deprecation")

public class CustomLocalTimelineFragment extends PinnableStatusListFragment implements ProvidesAssistContent.ProvidesWebUri{
    private static final String STATE_MAX_ID="state_max_id";
    //    private String name;
    private String domain;

    private String maxID;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            maxID=savedInstanceState.getString(STATE_MAX_ID);
        }
    }

    @Override
    protected boolean wantsComposeButton() {
        return false;
    }

    @Override
    public void onAttach(Context activity){
        super.onAttach(activity);
        domain=getArguments().getString("domain");
        updateTitle(domain);

        setHasOptionsMenuCompat(true);
    }

    private void updateTitle(String domain) {
        this.domain = domain;
        setTitle(this.domain);
    }

    @Override
    protected void doLoadData(int offset, int count){
        currentRequest=new GetPublicTimeline(true, false, refreshing ? null : maxID, null, count, null, getLocalPrefs().timelineReplyVisibility)
                .setCallback(new SimpleCallback<>(this){
                    @Override
                    public void onSuccess(List<Status> result){
                        if(!result.isEmpty())
                            maxID=result.get(result.size()-1).id;
                        if (getActivity() == null) return;
						AccountSessionManager.get(accountID).filterStatuses(result, FilterContext.PUBLIC);
                        result.stream().forEach(status -> {
                            status.account.acct += "@"+domain;
                            status.mentions.forEach(mention -> mention.id = null);
                            status.isRemote = true;
                        });

                        onDataLoaded(result, !result.isEmpty());
                    }
                })
                .execNoAuth(domain);
    }

    @Override
    protected void onShown(){
        super.onShown();
        if(!getArguments().getBoolean("noAutoLoad") && !loaded && !dataLoading)
            loadData();
    }

    @Override
    public void onCreateAppMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.custom_local_timelines, menu);
        super.onCreateAppMenu(menu, inflater);
        UiUtils.enableOptionsMenuIcons(getContext(), menu, R.id.pin);
    }

    @Override
    protected FilterContext getFilterContext() {
        return FilterContext.PUBLIC;
    }

    @Override
    public Uri getWebUri(Uri.Builder base) {
		return new Uri.Builder()
				.scheme("https")
				.authority(domain)
				.build();
    }

    @Override
    protected TimelineDefinition makeTimelineDefinition() {
        return TimelineDefinition.ofCustomLocalTimeline(domain);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString(STATE_MAX_ID, maxID);
    }
}
