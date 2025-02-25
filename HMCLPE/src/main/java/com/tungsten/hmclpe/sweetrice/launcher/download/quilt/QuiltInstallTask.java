package com.tungsten.hmclpe.sweetrice.launcher.download.quilt;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.tungsten.hmclpe.sweetrice.R;
import com.tungsten.hmclpe.sweetrice.launcher.MainActivity;
import com.tungsten.hmclpe.sweetrice.launcher.game.Argument;
import com.tungsten.hmclpe.sweetrice.launcher.game.Artifact;
import com.tungsten.hmclpe.sweetrice.launcher.game.Library;
import com.tungsten.hmclpe.sweetrice.launcher.game.RuledArgument;
import com.tungsten.hmclpe.sweetrice.launcher.game.Version;
import com.tungsten.hmclpe.sweetrice.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.sweetrice.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.sweetrice.launcher.uis.game.download.DownloadUrlSource;
import com.tungsten.hmclpe.sweetrice.utils.gson.JsonUtils;
import com.tungsten.hmclpe.sweetrice.utils.io.DownloadUtil;
import com.tungsten.hmclpe.sweetrice.utils.io.NetworkUtils;
import com.tungsten.hmclpe.sweetrice.utils.platform.Bits;

import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("ALL")
public class QuiltInstallTask extends AsyncTask<QuiltLoaderVersion,Integer, Version> {

    private MainActivity activity;
    private DownloadTaskListAdapter adapter;
    private String mcVersion;
    private InstallQuiltCallback callback;

    private DownloadTaskListBean bean;

    public QuiltInstallTask(MainActivity activity, DownloadTaskListAdapter adapter, String mcVersion, InstallQuiltCallback callback) {
        this.activity = activity;
        this.adapter = adapter;
        this.mcVersion = mcVersion;
        this.callback = callback;

        bean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_game_install_quilt),"","","");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
        if (!isCancelled()) adapter.addDownloadTask(bean);
    }

    @Override
    protected Version doInBackground(QuiltLoaderVersion... quiltLoaderVersions) {
        QuiltLoaderVersion quiltVersion = quiltLoaderVersions[0];
        String patchUrl = "https://meta.quiltmc.org/v3/versions/loader/" + mcVersion + "/" + quiltVersion.version + "/profile/json";
        try {
            String patchStr = NetworkUtils.doGet(NetworkUtils.toURL(patchUrl));
            Gson gson = JsonUtils.defaultGsonBuilder()
                    .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                    .registerTypeAdapter(Bits.class, new Bits.Serializer())
                    .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                    .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                    .create();
            Version patch = gson.fromJson(patchStr,Version.class);
            ArrayList<DownloadTaskListBean> list = new ArrayList<>();
            String head;
            for (Library library : patch.getLibraries()){
                String url;
                if (DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource) == 0) {
                    url = library.getDownload().getUrl();
                }
                else if (DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource) == 1) {
                    head = DownloadUrlSource.getSubUrl(1,DownloadUrlSource.LIBRARIES) + "/";
                    url = head + library.getPath();
                }
                else {
                    head = DownloadUrlSource.getSubUrl(2,DownloadUrlSource.LIBRARIES) + "/";
                    url = head + library.getPath();
                }
                DownloadTaskListBean bean = new DownloadTaskListBean(library.getArtifactFileName(),
                        url,
                        activity.launcherSetting.gameFileDirectory + "/libraries/" + library.getPath(),
                        library.getDownload().getSha1());
                list.add(bean);
            }
            int maxDownloadTask = activity.launcherSetting.maxDownloadTask;
            if (activity.launcherSetting.autoDownloadTaskQuantity) {
                maxDownloadTask = 64;
            }

            DownloadUtil.DownloadMultipleFilesCallback downloadCallback = new DownloadUtil.DownloadMultipleFilesCallback() {
                @Override
                public void onTaskStart(DownloadTaskListBean bean) {
                    if (!isCancelled()) adapter.addDownloadTask(bean);
                }

                @Override
                public void onTaskProgress(DownloadTaskListBean bean) {
                    if (!isCancelled()) adapter.onProgress(bean);
                }

                @Override
                public void onTaskFinish(DownloadTaskListBean bean) {
                    if (!isCancelled()) adapter.onComplete(bean);
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                    if (!isCancelled()) callback.onFailed(e);
                    cancel(true);
                }
            };

            ArrayList<DownloadTaskListBean> failedFiles = DownloadUtil.downloadMultipleFiles(list, maxDownloadTask, this, activity, downloadCallback);
            if (failedFiles.size() == 0) {
                return patch.setId("quilt").setVersion(quiltVersion.version).setPriority(30000);
            }
            else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("The following files failed to download:");
                for (DownloadTaskListBean bean : failedFiles) {
                    stringBuilder.append("\n\n  " + bean.name);
                }
                Exception e = new Exception(stringBuilder.toString());
                e.printStackTrace();
                if (!isCancelled()) callback.onFailed(e);
                cancel(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (!isCancelled()) callback.onFailed(e);
            cancel(true);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Version version) {
        super.onPostExecute(version);
        if (version == null) {
            callback.onFailed(new Exception("Unknown error"));
        }
        else {
            adapter.onComplete(bean);
            callback.onFinish(version);
        }
    }

    public interface InstallQuiltCallback{
        void onStart();
        void onFailed(Exception e);
        void onFinish(Version version);
    }
}
