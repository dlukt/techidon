package de.icod.techidon.ui.photoviewer;

import de.icod.techidon.model.Status;
import de.icod.techidon.ui.displayitems.MediaGridStatusDisplayItem;

public interface PhotoViewerHost{
	void openPhotoViewer(String parentID, Status status, int attachmentIndex, MediaGridStatusDisplayItem.Holder gridHolder);
}
