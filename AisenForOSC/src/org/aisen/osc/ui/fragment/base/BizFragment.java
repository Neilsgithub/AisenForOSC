package org.aisen.osc.ui.fragment.base;

import android.app.Activity;

import com.m.ui.fragment.ABaseFragment;

public class BizFragment extends ABaseFragment {

	@Override
	protected int inflateContentView() {
		return 0;
	}
	
	public static BizFragment getBizFragment(ABaseFragment fragment) {
		if (fragment != null && fragment.getActivity() != null) {
			BizFragment bizFragment = (BizFragment) fragment.getActivity().getFragmentManager().findFragmentByTag("com.m.ui.BizFragment");

			if (bizFragment == null) {
				bizFragment = new BizFragment();
				fragment.getActivity().getFragmentManager().beginTransaction().add(bizFragment, "com.m.ui.BizFragment").commit();
			}

			return bizFragment;
		}
		
		return null;
	}

	public static BizFragment getBizFragment(Activity context) {
		BizFragment bizFragment = (BizFragment) context.getFragmentManager().findFragmentByTag("BizFragment");
		if (bizFragment == null) {
			bizFragment = new BizFragment();
			context.getFragmentManager().beginTransaction().add(bizFragment, "BizFragment").commit();
		}
		return bizFragment;
	}

	
}
