package de.icod.techidon.fragments.account_list;

import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;

import de.icod.techidon.R;
import de.icod.techidon.api.requests.statuses.PleromaGetStatusReactions;
import de.icod.techidon.model.Emoji;
import de.icod.techidon.model.EmojiReaction;
import de.icod.techidon.model.viewmodel.AccountViewModel;
import de.icod.techidon.ui.text.HtmlParser;
import de.icod.techidon.ui.utils.UiUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import me.grishka.appkit.api.ErrorResponse;
import me.grishka.appkit.api.SimpleCallback;

@SuppressWarnings("deprecation")

public class StatusEmojiReactionsListFragment extends BaseAccountListFragment {
    private String id;
    private String emojiName;
    private String url;
    private int count;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        id = getArguments().getString("statusID");
        emojiName = getArguments().getString("emoji");
        url = getArguments().getString("url");
        count = getArguments().getInt("count");

        SpannableStringBuilder title = new SpannableStringBuilder(getResources().getQuantityString(R.plurals.sk_users_reacted_with, count,
				count, url == null ? emojiName : ":"+emojiName+":"));
        if (url != null) {
            Emoji emoji = new Emoji();
            emoji.shortcode = emojiName;
            emoji.url = url;
            HtmlParser.parseCustomEmoji(title, Collections.singletonList(emoji));
        }
        setTitle(title);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (url != null) {
            UiUtils.loadCustomEmojiInTextView(toolbarTitleView);
        }
    }

    @Override
    public void dataLoaded() {
        super.dataLoaded();
        footerProgress.setVisibility(View.GONE);
    }

    @Override
    protected void doLoadData(int offset, int count){
        currentRequest = new PleromaGetStatusReactions(id, emojiName)
                .setCallback(new SimpleCallback<>(StatusEmojiReactionsListFragment.this){
                    @Override
                    public void onSuccess(List<EmojiReaction> result) {
                        if (getActivity() == null)
                            return;

                        List<AccountViewModel> items = result.get(0).accounts.stream()
                                .map(a -> new AccountViewModel(a, accountID))
                                .collect(Collectors.toList());

                        onDataLoaded(items);
                    }

                    @Override
                    public void onError(ErrorResponse error) {
                        super.onError(error);
                    }
                })
                .exec(accountID);
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!loaded && !dataLoading)
            loadData();
    }

    @Override
    public Uri getWebUri(Uri.Builder base) {
        return null;
    }
}
