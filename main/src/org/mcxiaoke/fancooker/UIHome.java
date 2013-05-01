package org.mcxiaoke.fancooker;

import org.mcxiaoke.fancooker.cache.ImageLoader;
import org.mcxiaoke.fancooker.controller.SimpleDialogListener;
import org.mcxiaoke.fancooker.controller.UIController;
import org.mcxiaoke.fancooker.dialog.ConfirmDialog;
import org.mcxiaoke.fancooker.fragments.ConversationListFragment;
import org.mcxiaoke.fancooker.fragments.HomeFragment;
import org.mcxiaoke.fancooker.fragments.ProfileFragment;
import org.mcxiaoke.fancooker.menu.MenuCallback;
import org.mcxiaoke.fancooker.menu.MenuFragment;
import org.mcxiaoke.fancooker.menu.MenuItemResource;
import org.mcxiaoke.fancooker.util.NetworkHelper;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.slidingmenu.lib.SlidingMenu;

/**
 * @author mcxiaoke
 * 
 */
public class UIHome extends UIBaseSupport implements MenuCallback {

	public static final String TAG = UIHome.class.getSimpleName();

	private ViewGroup mContainer;
	private SlidingMenu mSlidingMenu;
	private Fragment mMenuFragment;

	private void log(String message) {
		Log.d(TAG, message);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (AppContext.DEBUG) {
			log("onCreate()");
		}
	}

	@Override
	protected void setActionBar() {
		ActionBar ab = getActionBar();
		ab.setDisplayHomeAsUpEnabled(false);
	}

	@Override
	protected void onMenuHomeClick() {
	}

	@Override
	protected void initialize() {
		ImageLoader.getInstance();
	}

	@Override
	protected void setLayout() {
		setContentView(R.layout.content_frame);
		mContainer = (ViewGroup) findViewById(R.id.content_frame);
		FragmentManager fm = getFragmentManager();

		fm.beginTransaction()
				.replace(R.id.content_frame, HomeFragment.newInstance())
				.commit();

		mSlidingMenu = new SlidingMenu(this);
		mSlidingMenu.setMode(SlidingMenu.LEFT);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		mSlidingMenu.setShadowWidth(20);
		mSlidingMenu.setShadowDrawable(R.drawable.menu_shadow);
		mSlidingMenu.setBehindOffset(90);
		mSlidingMenu.setFadeDegree(0.35f);
		mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		mSlidingMenu.setMenu(R.layout.menu_frame);

		mMenuFragment = MenuFragment.newInstance();
		fm.beginTransaction().replace(R.id.menu_frame, mMenuFragment).commit();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (AppContext.DEBUG) {
			Log.d(TAG, "onResume()");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (AppContext.DEBUG) {
			Log.d(TAG, "onPause()");
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (AppContext.DEBUG) {
			Log.d(TAG, "onStop()");
		}
		if (!NetworkHelper.isWifi(this)) {
			AppContext.getImageLoader().clearQueue();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (AppContext.DEBUG) {
			Log.d(TAG, "onDestroy()");
		}
		AppContext.getImageLoader().shutdown();
	}

	@Override
	protected int getPageType() {
		return PAGE_HOME;
	}

	@Override
	protected boolean isHomeScreen() {
		return true;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	protected int getMenuResourceId() {
		return R.menu.home_menu;
	}

	protected void onHomeLogoClick() {
		mSlidingMenu.toggle();
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_write:
			onMenuWriteClick();
			break;
		case R.id.menu_search:
			onMenuSearchClick();
			break;
		case R.id.menu_logout:
			onMenuLogoutClick();
			break;
		default:
			break;
		}
		return true;
	}

	private void onMenuOptionClick() {
	}

	private void onMenuProfileClick() {
		UIController.showProfile(this, AppContext.getAccount());
	}

	private void onMenuSearchClick() {
		Intent intent = new Intent(this, UISearch.class);
		startActivity(intent);
	}

	private void onMenuAboutClick() {
		UIController.showAbout(this);
	}

	private void onMenuFeedbackClick() {
		String text = getString(R.string.config_feedback_account) + " ("
				+ Build.MODEL + "-" + Build.VERSION.RELEASE + " "
				+ AppContext.versionName + ") ";
		UIController.showWrite(this, text);
	}

	private void onMenuLogoutClick() {
		final ConfirmDialog dialog = new ConfirmDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("确定注销当前登录帐号吗？");
		dialog.setClickListener(new SimpleDialogListener() {

			@Override
			public void onPositiveClick() {
				super.onPositiveClick();
				AppContext.doLogin(mContext);
				finish();
			}
		});
		dialog.show();
	}

	@Override
	public void onClick(View v) {
	}

	private void replaceFramgnt(Fragment fragment) {
		log("fragment=" + fragment);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.content_frame, fragment);
		ft.commit();
		mSlidingMenu.showContent();
	}

	private void showHome() {
		replaceFramgnt(HomeFragment.newInstance());
	}

	private void showProfile() {
		replaceFramgnt(ProfileFragment.newInstance(AppContext.getAccount()));
	}

	private void showMessage() {
		replaceFramgnt(ConversationListFragment.newInstance(false));
	}

	@Override
	public void onMenuItemSelected(int position, MenuItemResource menuItem) {
		log("onMenuItemSelected: " + menuItem);
		int id = menuItem.getId();
		switch (id) {
		case MenuFragment.MENU_ID_HOME:
			showHome();
			break;
		case MenuFragment.MENU_ID_PROFILE:
			showProfile();
			break;
		case MenuFragment.MENU_ID_MESSAGE:
			showMessage();
			break;
		case MenuFragment.MENU_ID_TOPIC:
			UIController.showTopic(this);
			break;
		case MenuFragment.MENU_ID_RECORD:
			UIController.showRecords(this);
			break;
		case MenuFragment.MENU_ID_DIGEST:
			UIController.showFanfouBlog(this);
			break;
		case MenuFragment.MENU_ID_THEME:
			break;
		case MenuFragment.MENU_ID_OPTION:
			UIController.showOption(this);
			break;
		case MenuFragment.MENU_ID_ABOUT:
			UIController.showAbout(this);
			break;
		default:
			break;
		}

	}

}
