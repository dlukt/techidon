package de.icod.techidon.model;

/**
 * A model object from which {@link de.icod.techidon.ui.displayitems.StatusDisplayItem}s can be generated.
 */
public interface DisplayItemsParent{
	String getID();

	default String getAccountID(){
		return null;
	}
}
