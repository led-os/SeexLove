package com.chat.seecolove.view.adaper;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import com.chat.seecolove.R;

/**
 */
public abstract class BaseRclvAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FOOTER = Integer.MIN_VALUE;
    private static final int TYPE_ADAPTEE_OFFSET = 2;
    private boolean canLoadMore = false;

    protected List<T> mList;
    public LayoutInflater mInflater;

    public BaseRclvAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_FOOTER) {
            return onCreateFooterViewHolder(parent, viewType);
        }
        return onCreateContentItemViewHolder(parent, viewType - TYPE_ADAPTEE_OFFSET);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getContentItemCount() && holder.getItemViewType() == TYPE_FOOTER) {
            onBindFooterView(holder, position);
        }else {
            onBindContentItemView(holder, position);
        }
    }

    /**
     * LayoutManager 为GrideLayoutManager时，设置Footer占据一整行
     **/
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) manager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return getItemViewType(position) == TYPE_FOOTER
                            ? gridManager.getSpanCount() : 1;
                }
            });
        }
    }

    /**
     * LayoutManager 为StaggeredGridLayoutManager时，设置Footer占据一整行
     **/
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(holder.getLayoutPosition() == getContentItemCount());
        }
    }



    private static class ViewHolderFooter extends RecyclerView.ViewHolder {

        private View footer;
        private RelativeLayout deffooterView;
        private  AnimationDrawable anim;
        public ViewHolderFooter(View v) {
            super(v);
            footer = v.findViewById(R.id.footer);
            deffooterView = (RelativeLayout) v.findViewById(R.id.no_more_dataview);
            ImageView  iv = (ImageView)v. findViewById(R.id.loading_img);
            anim = (AnimationDrawable) iv.getDrawable();
            iv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    anim.start();
                    return true;
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        int itemCount = getContentItemCount();
        if (isCanLoadMore()||isDefView) {
            itemCount += 1;
        }
        return itemCount;
    }

    public void setList(List<T> list) {
        this.mList = list;
        notifyDataSetChanged();
    }
    public List<T>  getList(){
        return mList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getContentItemCount() && (isCanLoadMore()||isDefView)) {
            return TYPE_FOOTER;
        }
        return getContentItemType(position) + TYPE_ADAPTEE_OFFSET;
    }

    /**
     * 设置是否可以加载更多
     *
     * @param canLoadMore true:可以,false:加载完成
     */
    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public RecyclerView.ViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recy_footer, parent, false);
        ViewHolderFooter vh = new ViewHolderFooter(v);
        return vh;
    }

    public void onBindFooterView(RecyclerView.ViewHolder holder, int position) {
        ViewHolderFooter footerHolder = (ViewHolderFooter) holder;
        if (mList.size() > 0) {
            if (isCanLoadMore()) {
                footerHolder.footer.setVisibility(View.VISIBLE);
                footerHolder.deffooterView.setVisibility(View.GONE);
            } else {
                if(isDefView){
                    footerHolder.deffooterView.setVisibility(View.VISIBLE);
                }else{
                    footerHolder.deffooterView.setVisibility(View.GONE);
                }
                footerHolder.footer.setVisibility(View.GONE);
            }
        } else {
            footerHolder.footer.setVisibility(View.GONE);
            footerHolder.deffooterView.setVisibility(View.VISIBLE);
        }
    }


    private boolean isDefView=false;
    public void setdefFootView(boolean isDefViewc){
        isDefView=isDefViewc;
    }



    public abstract RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int viewType);//创建你要的普通item

    public abstract void onBindContentItemView(RecyclerView.ViewHolder holder, int position);//绑定数据

    public int getContentItemCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    public abstract int getContentItemType(int position);//没用到，返回0即可，为了扩展用的
}
