package wiki.justreddy.ga.reddyutils.old.dependency;

import java.net.URL;
import java.net.URLClassLoader;

public class CustomClassLoader extends URLClassLoader {


    public CustomClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }
}
