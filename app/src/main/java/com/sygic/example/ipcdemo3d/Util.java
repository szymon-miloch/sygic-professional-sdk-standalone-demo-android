package com.sygic.example.ipcdemo3d;

import java.io.File;
import java.io.FileFilter;

class Util {
    
    private static final String DRIVE_DIR = "Drive";
    private static final String CWD_PATH = "/" + DRIVE_DIR + "/Android";

    private Util() {
    }
    
    public static boolean fileExists(String strFile) {
        File file = new File(strFile);
        if ( file.exists() )
            return  true;
    
        return false;
    }

    // zisti cestu k SD karte
    public static String[] getSDCardPath(final String[] path) {
        String[] strRet = new String[4];
        strRet[0] = path[0];
        strRet[1] = path[1];
        
        File f = android.os.Environment.getExternalStorageDirectory();
        if ( f != null && f.isDirectory() ) {
            strRet[2] = f.getAbsolutePath();
            strRet[3] = "/";
        } else {
            strRet[2] = "/";
            strRet[3] = null;
        }
        return strRet;
    }
    
    // vrati cestu k danemu adresaru - ide max 1 level do hlbky sd karty
    /**
     * Returns the path to Drive root directory (usually /sdcard )
     * @return path to Drive root directory
     */
    public static String getDriveRoot(final String[] path)
    {
        String[] strSDPath = getSDCardPath(path);
        
        for (String string : strSDPath) {
            if ( string == null )
                continue;
            
            File sdDir = new File( string );
            FileFilter flSDFilter = new FileFilter() {
                public boolean accept(File f) {
                    if ( f.isDirectory() && (f.getName().compareTo(DRIVE_DIR) == 0) 
                            && fileExists(f.getParent()+CWD_PATH) )
                        return true;
                    return false;
                }
            };
            
            String s = searchForDriveRoot(sdDir, flSDFilter, 1);
            if ( s != null ) {
                return s;
            }
        }
        
        return null;
    }
    
    private static String searchForDriveRoot(File file, final FileFilter filter, int count) {
        if ( count < 5 ) {
            if ( file.isDirectory() ) {
                File[] entries = file.listFiles(filter);
                if ( entries != null && entries .length != 0 ) {
                    return entries[0].getParent();
                } else {
                    entries = file.listFiles();
                    if ( entries != null && entries .length != 0 ) {
                        int i = 1+count;
                        for ( File entry:entries ) {
                            String s = searchForDriveRoot(entry, filter, i);
                            if ( s != null ) {
                                return s;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
