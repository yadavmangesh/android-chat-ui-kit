package constant;

public class StringContract {

    public static class IntentStrings {

        public static final String IMAGE_TYPE = "image/*";

        public static final String UID="uid";

        public static final String AVATAR="avatar";

        public static final String STATUS="status";

        public static final String NAME = "name";

        public static final String TYPE = "type";

        public static final String GUID = "guid";

        public static final String tabBar = "tabBar";

        public static final String[] EXTRA_MIME_DOC=new String[]{"text/plane","text/html","application/pdf","application/msword","application/vnd.ms.excel", "application/mspowerpoint","application/zip"};
    }

    public static class Tab {
        public static final String Conversation = "conversations";

        public static final String User = "users";

        public static final String Group = "groups";
    }
    public static class RequestCode{

        public static final int GALLERY=1;

        public static final int CAMERA = 2;

        public static final int FILE = 25;
    }

}
