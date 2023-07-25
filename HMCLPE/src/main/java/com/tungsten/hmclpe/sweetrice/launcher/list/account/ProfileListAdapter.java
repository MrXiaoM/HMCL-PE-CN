package com.tungsten.hmclpe.sweetrice.launcher.list.account;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tungsten.hmclpe.sweetrice.R;
import com.tungsten.hmclpe.sweetrice.auth.Account;
import com.tungsten.hmclpe.sweetrice.auth.AuthenticationException;
import com.tungsten.hmclpe.sweetrice.auth.authlibinjector.AuthlibInjectorProvider;
import com.tungsten.hmclpe.sweetrice.auth.yggdrasil.GameProfile;
import com.tungsten.hmclpe.sweetrice.auth.yggdrasil.YggdrasilService;
import com.tungsten.hmclpe.sweetrice.auth.yggdrasil.YggdrasilSession;
import com.tungsten.hmclpe.sweetrice.launcher.dialogs.account.AddAuthlibInjectorAccountDialog;
import com.tungsten.hmclpe.sweetrice.launcher.dialogs.account.SelectProfileDialog;
import com.tungsten.hmclpe.sweetrice.skin.utils.Avatar;
import com.tungsten.hmclpe.sweetrice.utils.gson.UUIDTypeAdapter;

import java.util.ArrayList;
import java.util.List;

public class ProfileListAdapter extends BaseAdapter {

    private Context context;
    private List<GameProfile> list;
    private ArrayList<Bitmap> bitmaps;
    private AddAuthlibInjectorAccountDialog.OnAuthlibInjectorAccountAddListener onAuthlibInjectorAccountAddListener;
    private SelectProfileDialog dialog;
    private boolean isNide;

    public ProfileListAdapter (Context context, List<GameProfile> list, ArrayList<Bitmap> bitmaps, AddAuthlibInjectorAccountDialog.OnAuthlibInjectorAccountAddListener onAuthlibInjectorAccountAddListener, SelectProfileDialog dialog,boolean isNide) {
        this.context = context;
        this.list = list;
        this.bitmaps = bitmaps;
        this.onAuthlibInjectorAccountAddListener = onAuthlibInjectorAccountAddListener;
        this.dialog = dialog;
        this.isNide = isNide;
    }

    private class ViewHolder{
        LinearLayout item;
        ImageView face;
        ImageView hat;
        TextView name;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_profile,viewGroup,false);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.face = view.findViewById(R.id.skin_face);
            viewHolder.hat = view.findViewById(R.id.skin_hat);
            viewHolder.name = view.findViewById(R.id.name);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)view.getTag();
        }
        GameProfile gameProfile = list.get(i);
        Bitmap skin = bitmaps.get(i);
        viewHolder.name.setText(gameProfile.getName());
        Avatar.setAvatar(Avatar.bitmapToString(skin),viewHolder.face,viewHolder.hat);
        viewHolder.item.setOnClickListener(view1 -> {
            String skinTexture = Avatar.bitmapToString(skin);
            YggdrasilSession refresh;
            try {
                refresh = new YggdrasilService(new AuthlibInjectorProvider(dialog.url)).refresh(dialog.yggdrasilSession.getAccessToken(),dialog.yggdrasilSession.getClientToken(),new GameProfile(gameProfile.getId(),gameProfile.getName()));
            } catch (AuthenticationException e) {
                throw new RuntimeException(e);
            }
            Account account = new Account(isNide ? 5 : 4,
                    dialog.email,
                    dialog.password,
                    "mojang",
                    "0",
                    refresh.getSelectedProfile().getName(),
                    UUIDTypeAdapter.fromUUID(refresh.getSelectedProfile().getId()),
                    refresh.getAccessToken(),
                    refresh.getClientToken(),
                    "",
                    dialog.url,
                    skinTexture);
            onAuthlibInjectorAccountAddListener.onAccountAdd(account);
            dialog.dismiss();
        });
        return view;
    }
}
