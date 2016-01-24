package pl.edu.pwr.dropbox;

import com.dropbox.core.*;
import com.dropbox.core.json.JsonReader;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.DbxFiles;
import com.dropbox.core.v2.DbxUsers;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Dropbox {
    public static final String APP_INFO_FILE = "key.json";
    private String clientIdentifier;
    DbxAuthInfo authInfo;
    DbxClientV2 dbxClient;

    public Dropbox() throws JsonReader.FileLoadException {
        this.clientIdentifier = "1337";
        String argAuthFile = "AuthUser.auth";
        authInfo = DbxAuthInfo.Reader.readFromFile(argAuthFile);
        String userLocale = Locale.getDefault().toString();
        DbxRequestConfig requestConfig = new DbxRequestConfig(clientIdentifier, userLocale);
        dbxClient = new DbxClientV2(requestConfig, authInfo.accessToken, authInfo.host);
    }

    // check if file clientIdentifier.auth exist if not create one with auth info from dropbox link
    public Dropbox(String clientIdentifier) throws JsonReader.FileLoadException, IOException, DbxException {
        this.clientIdentifier = clientIdentifier;
        String argAuthFile = clientIdentifier + ".auth";
        if (Files.exists(Paths.get(argAuthFile))){
            authInfo = DbxAuthInfo.Reader.readFromFile(argAuthFile);
        }else {
            System.out.println(getAuthUrl());
            BufferedReader buffer=new BufferedReader(new InputStreamReader(System.in));
            String line=buffer.readLine();
            authorize(line);
        }
        String userLocale = Locale.getDefault().toString();
        DbxRequestConfig requestConfig = new DbxRequestConfig(clientIdentifier, userLocale);
        dbxClient = new DbxClientV2(requestConfig, authInfo.accessToken, authInfo.host);
    }

    public String getAuthUrl() throws JsonReader.FileLoadException {
        DbxRequestConfig requestConfig = new DbxRequestConfig(clientIdentifier, Locale.getDefault().toString());
        DbxAppInfo appInfo = DbxAppInfo.Reader.readFromFile(APP_INFO_FILE);
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(requestConfig, appInfo);
        return webAuth.start();
    }

    public void authorize(String webAuthKey)
            throws IOException, JsonReader.FileLoadException, DbxException {
        DbxRequestConfig requestConfig = new DbxRequestConfig(clientIdentifier, Locale.getDefault().toString());
        DbxAppInfo appInfo = DbxAppInfo.Reader.readFromFile(APP_INFO_FILE);
        DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(requestConfig, appInfo);
        DbxAuthFinish authFinish;
        authFinish = webAuth.finish(webAuthKey.trim());
        this.authInfo = new DbxAuthInfo(authFinish.accessToken, appInfo.host);
        saveAuthFile();
    }

    public boolean saveAuthFile() throws IOException {
        DbxAuthInfo.Writer.writeToFile(authInfo, clientIdentifier+".auth");
        return true;
    }

    public String getInfo()
            throws DbxException {
        DbxClientV2 Client = getClient();
        DbxUsers.FullAccount dbxAccountInfo = Client.users.getCurrentAccount();
        return (dbxAccountInfo.toStringMultiline());
    }

    private String getProperPath(String path) {
        path = path.replace("\\", "/");
        if (!path.isEmpty()) {
            if (!path.startsWith("/")) path = "/" + path;
        }
        return path;
    }

    public boolean uploadFile(String filename, String path) throws IOException, DbxException {
        if (Files.isDirectory(Paths.get(filename))) {
            return createFolder(path);
        } else {
            path = getProperPath(path);
            InputStream in = new FileInputStream(filename);
            DbxFiles.FileMetadata metadata = getClient().files.uploadBuilder(path).run(in);
            System.out.println("Uploaded: " + metadata.name);
            return true;
        }
    }

    public boolean deleteFile(String path) throws DbxException {
        path = getProperPath(path);
        getClient().files.delete(path);
        return true;
    }

    public boolean updateFile(String filename, String path) throws DbxException, IOException, ParseException {
        path = getProperPath(path);
        try {
            DbxFiles.Metadata metadata = getClient().files.getMetadata(path);
            String temp = metadata.toStringMultiline();
            temp = temp.substring(temp.lastIndexOf("modified") + 13, temp.lastIndexOf("modified") + 33);
            temp = temp.replace("T", "-");
            SimpleDateFormat dfDb = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
            File file = new File(filename);
            Date dateDb = dfDb.parse(temp);
            Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(dateDb); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
            dateDb = cal.getTime();
            Date d = new Date(file.lastModified());
            if (d.before(dateDb)) {
                return false;
            }
        } catch (DbxException e) {} //no file on dropbox just pass to upload
        return uploadFile(filename, path);
    }


    public boolean createFolder(String path) throws DbxException {
        path = getProperPath(path);
        getClient().files.createFolder(path);
        return true;
    }

    public DbxClientV2 getClient() {
        return dbxClient;
    }

    public ArrayList<DbxFiles.Metadata> getFileList() throws DbxException {
        return getClient().files.listFolder("").entries;
    }


}