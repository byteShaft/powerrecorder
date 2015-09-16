package com.byteshaft.powerrecorder.utils;


import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.byteshaft.powerrecorder.AppGlobals;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.util.Properties;

public class SftpHelpers {

    private static final String SFTPUSER = "orangebox";
    private static final String SFTPPASS = "orangebox";
    private static final String SFTPHOST = "orange-1508-box.marceldev.fr";
    private static final int SFTPPORT = 22;
    public static ChannelSftp mChannelSftp;

    private String getOutputFilePath() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        return root + "/test.m4a";

    }

    public static boolean upload(String path) {
        JSch jSch = new JSch();
        Session session = null;
        try {
            session = jSch.getSession(SFTPUSER, SFTPHOST, SFTPPORT);
            session.setPassword(SFTPPASS);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(10000);
            session.connect();
            Log.i("APP", "Host connected.");
            Channel channel = session.openChannel("sftp");
            channel.connect();
            mChannelSftp = (ChannelSftp) channel;
            mChannelSftp.cd("uploads");
            File toUpload = new File(path);
            mChannelSftp.put(toUpload.getAbsolutePath(), toUpload.getName());
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        }
        mChannelSftp.exit();
        session.disconnect();
        return true;
    }
}
