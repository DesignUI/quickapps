package com.yoavst.quickapps;

import org.androidannotations.annotations.sharedpreferences.DefaultBoolean;
import org.androidannotations.annotations.sharedpreferences.SharedPref;

/**
 * Created by Yoav.
 */
@SharedPref(SharedPref.Scope.UNIQUE)
public interface Preferences {
	@DefaultBoolean(false)
	boolean torchForceFloating();
	@DefaultBoolean(true)
	boolean showRepeatingEvents();
	@DefaultBoolean(true)
	boolean showLocation();
	@DefaultBoolean(true)
	boolean notificationShowContent();
    @DefaultBoolean(true)
    boolean launcherIsVertical();
    @DefaultBoolean(false)
    boolean launcherLoadExternalModules();
    @DefaultBoolean(true)
    boolean launcherAutoAddModules();
	@DefaultBoolean(true)
	boolean showBatteryToggle();
	@DefaultBoolean(false)
	boolean calculatorForceFloating();
	@DefaultBoolean(true)
	boolean stopwatchShowMillis();
	@DefaultBoolean(true)
	boolean isCircle();
	String launcherItems();
	String togglesItems();
}
