package de.icod.techidon.test;

import de.icod.techidon.ui.viewcontrollers.DropdownSubmenuController;
import de.icod.techidon.ui.viewcontrollers.ToolbarDropdownMenuController;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

class TestDropdownSubmenu extends DropdownSubmenuController{
	final CountDownLatch dismissed=new CountDownLatch(1);
	private final CharSequence backItemTitle;

	TestDropdownSubmenu(ToolbarDropdownMenuController dropdownController, CharSequence backItemTitle){
		super(dropdownController);
		this.backItemTitle=backItemTitle;
		items=new ArrayList<>();
	}

	@Override
	protected CharSequence getBackItemTitle(){
		return backItemTitle;
	}

	@Override
	public void onDismiss(){
		dismissed.countDown();
	}
}
