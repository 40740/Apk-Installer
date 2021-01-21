package com.mgtv.apkinstaller.adapter;

import android.view.View;
import com.mgtv.apkinstaller.R;
import com.mgtv.apkinstaller.bean.ApkListBean;


/**
 * @author: xiawei
 * @date: 2021/1/11
 */
public class ApkListAdapter extends BaseRecycleViewAdapter <ApkListBean.AppListBean, BaseViewHolder> {

    public ApkListAdapter() {
        super(R.layout.apk_list_item);
    }

    public void updateItemView(View itemView, boolean hasFocus, int position) {

        View childItemView = itemView.findViewById(R.id.apk_list_item);
        if (hasFocus) {
            childItemView.setBackgroundResource(R.drawable.img_border_orange);
        } else {
            childItemView.setBackgroundResource(R.drawable.img_no_border);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, ApkListBean.AppListBean item) {
       helper.setText(R.id.text_item,item.getApkName());

    }
}
