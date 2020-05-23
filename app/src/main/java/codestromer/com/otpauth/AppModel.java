package codestromer.com.otpauth;

public class AppModel {
    private String appName;
    private String desc;

    private AppModel(){}

    private AppModel(String appName,String desc){
        this.appName = appName;
        this.desc = desc;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
