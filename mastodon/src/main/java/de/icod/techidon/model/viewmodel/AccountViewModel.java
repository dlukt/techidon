package de.icod.techidon.model.viewmodel;

import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import de.icod.techidon.GlobalUserPreferences;
import de.icod.techidon.api.session.AccountSession;
import de.icod.techidon.api.session.AccountSessionManager;
import de.icod.techidon.model.Account;
import de.icod.techidon.model.AccountField;
import de.icod.techidon.ui.text.HtmlParser;
import de.icod.techidon.ui.text.LinkSpan;
import de.icod.techidon.ui.utils.CustomEmojiHelper;

import java.util.Collections;

import me.grishka.appkit.imageloader.requests.ImageLoaderRequest;
import me.grishka.appkit.imageloader.requests.UrlImageLoaderRequest;
import me.grishka.appkit.utils.V;

public class AccountViewModel{
	public final Account account;
	public final ImageLoaderRequest avaRequest;
	public final CustomEmojiHelper emojiHelper;
	public final CharSequence parsedName, parsedBio;
	public final String verifiedLink;

	public AccountViewModel(Account account, String accountID){
		this.account=account;
		AccountSession session = AccountSessionManager.get(accountID);
		avaRequest=new UrlImageLoaderRequest(
				TextUtils.isEmpty(account.avatar) ? session.getDefaultAvatarUrl() :
						GlobalUserPreferences.playGifs ? account.avatar : account.avatarStatic,
				V.dp(50), V.dp(50));
		emojiHelper=new CustomEmojiHelper();
		if(session.getLocalPreferences().customEmojiInNames)
			parsedName=HtmlParser.parseCustomEmoji(account.getDisplayName(), account.emojis);
		else
			parsedName=account.getDisplayName();
		parsedBio=HtmlParser.parse(account.note, account.emojis, Collections.emptyList(), Collections.emptyList(), accountID, account);
		SpannableStringBuilder ssb=new SpannableStringBuilder(parsedName);
		ssb.append(parsedBio);
		emojiHelper.setText(ssb);
		String verifiedLink=null;
		for(AccountField fld:account.fields){
			if(fld.verifiedAt!=null){
				verifiedLink=HtmlParser.stripAndRemoveInvisibleSpans(fld.value);
				break;
			}
		}
		this.verifiedLink=verifiedLink;
	}

	public AccountViewModel stripLinksFromBio(){
		if(parsedBio instanceof Spannable spannable){
			for(LinkSpan span:spannable.getSpans(0, spannable.length(), LinkSpan.class)){
				spannable.removeSpan(span);
			}
		}
		return this;
	}
}
