package app.kiti.com.kitiadmin;

import android.app.Application;
import android.content.Context;

/**
 * Created by Ankit on 4/21/2018.
 */

public class KitiAdminMains extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
    }

    public static Context getContext() {
        return context;
    }

}
