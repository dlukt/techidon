package de.icod.techidon.ui.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.icod.techidon.GlobalUserPreferences;
import de.icod.techidon.R;
import de.icod.techidon.model.Attachment;
import de.icod.techidon.model.Status;
import de.icod.techidon.ui.displayitems.MediaGridStatusDisplayItem;
import de.icod.techidon.ui.drawables.BlurhashCrossfadeDrawable;
import de.icod.techidon.ui.drawables.PlayIconDrawable;

@SuppressWarnings("deprecation")

public class MediaAttachmentViewController{
	public final View view;
	public final MediaGridStatusDisplayItem.GridItemType type;
	public final ImageView photo;
	public final View altButton, noAltButton, btnsWrap, extraBadge;
	public final TextView duration;
	public final View playButton;
	private BlurhashCrossfadeDrawable crossfadeDrawable=new BlurhashCrossfadeDrawable();
	private final Context context;
	private boolean didClear;
	private Status status;
	private Attachment attachment;

	public MediaAttachmentViewController(Context context, MediaGridStatusDisplayItem.GridItemType type){
		view=context.getSystemService(LayoutInflater.class).inflate(switch(type){
				case PHOTO -> R.layout.display_item_photo;
				case VIDEO -> R.layout.display_item_video;
				case GIFV -> R.layout.display_item_gifv;
			}, null);
		photo=view.findViewById(R.id.photo);
		altButton=view.findViewById(R.id.alt_button);
		noAltButton=view.findViewById(R.id.no_alt_button);
		btnsWrap=view.findViewById(R.id.alt_badges);
		duration=view.findViewById(R.id.duration);
		playButton=view.findViewById(R.id.play_button);
		extraBadge=view.findViewById(R.id.extra_badge);
		this.type=type;
		this.context=context;
		if(playButton!=null){
			// https://developer.android.com/topic/performance/hardware-accel#drawing-support
			if(Build.VERSION.SDK_INT<28)
				playButton.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
			playButton.setBackground(new PlayIconDrawable(context));
		}
	}

	public void bind(Attachment attachment, Status status){
		this.status=status;
		this.attachment=attachment;
		crossfadeDrawable.setSize(attachment.getWidth(), attachment.getHeight());
		crossfadeDrawable.setBlurhashDrawable(attachment.blurhashPlaceholder);
		crossfadeDrawable.setCrossfadeAlpha(0f);
		photo.setImageDrawable(null);
		photo.setImageDrawable(crossfadeDrawable);
		boolean hasAltText = !TextUtils.isEmpty(attachment.description);
		photo.setContentDescription(!hasAltText ? context.getString(R.string.media_no_description) : attachment.description);
		if(btnsWrap!=null){
			boolean showAlt = hasAltText && GlobalUserPreferences.showAltIndicator;
			boolean showNoAlt = !hasAltText && GlobalUserPreferences.showNoAltIndicator;
			altButton.setVisibility(showAlt ? View.VISIBLE : View.GONE);
			noAltButton.setVisibility(showNoAlt ? View.VISIBLE : View.GONE);
			if(showAlt || showNoAlt){
				btnsWrap.setVisibility(View.VISIBLE);
				String desc = context.getString(showAlt ? R.string.alt_text : R.string.media_no_description);
				btnsWrap.setContentDescription(desc);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
					btnsWrap.setTooltipText(desc);
				}
			}else{
				btnsWrap.setVisibility(View.GONE);
			}
		}
		if(type==MediaGridStatusDisplayItem.GridItemType.VIDEO){
			duration.setText(UiUtils.formatMediaDuration((int)attachment.getDuration()));
		}
		didClear=false;
	}

	public void setImage(Drawable drawable){
		crossfadeDrawable.setImageDrawable(drawable);
		if(didClear)
			 crossfadeDrawable.animateAlpha(0f);
		// Make sure the image is not stretched if the server returned wrong dimensions
		if(drawable!=null && (drawable.getIntrinsicWidth()!=attachment.getWidth() || drawable.getIntrinsicHeight()!=attachment.getHeight())){
			photo.setImageDrawable(null);
			photo.setImageDrawable(crossfadeDrawable);
		}
	}

	public void clearImage(){
		crossfadeDrawable.setCrossfadeAlpha(1f);
		crossfadeDrawable.setImageDrawable(null);
		didClear=true;
	}
}
