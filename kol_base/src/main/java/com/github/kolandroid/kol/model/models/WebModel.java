package com.github.kolandroid.kol.model.models;

import com.github.kolandroid.kol.connection.ServerReply;
import com.github.kolandroid.kol.model.Model;
import com.github.kolandroid.kol.request.Request;
import com.github.kolandroid.kol.session.Session;
import com.github.kolandroid.kol.util.Logger;
import com.github.kolandroid.kol.util.Regex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class WebModel extends Model {
    /**
     * Autogenerated by eclipse.
     */
    private static final long serialVersionUID = -1723871679147309734L;

    /**
     * Determine if a URL actually points to KoL
     */
    private static final Regex URL_FIND = new Regex(
            "^https?://www(\\d*).kingdomofloathing.com/(.*)$", 2);

    private static final Regex URL_BASE_FIND = new Regex(
            "^(?:.*/)?([^/?]*)(?:\\?.*)?$", 1);

    /**
     * Regex for replacing static buttons.
     */
    private static final Regex FIND_FORM = new Regex(
            "<form[^>]*>(<input[^>]*type=[\"']?hidden[^>]*>)*<input[^>]*button[^>]*></form>",
            0);
    private static final Regex HIDDEN_INPUT = new Regex(
            "<input[^>]*type=[\"']?hidden[^>]*>", 0);
    private static final Regex GET_NAME = new Regex(
            "name=[\"']?([^\"'> ]*)[\"'> ]", 1);
    private static final Regex GET_VALUE = new Regex(
            "value=[\"']?([^\"'> ]*)[\"'> ]", 1);

    private static final Regex INPUT_BUTTON = new Regex(
            "<input[^>]*button[^>]*>", 0);
    private static final Regex GET_TEXT = new Regex("value=\"([^>]*)\">", 1);

    private static final Regex FORM_ACTION = new Regex("<form([^>]*)action=[\"']?([^\"' >]*)[\"']?([^>]*)>", 2);
    private static final Regex FORM_METHOD = new Regex("<form([^>]*)method=[\"']?([^\"' >]*)[\"']?([^>]*)>", 2);
    private static final Regex FORM_SUBMIT = new Regex("<form([^>]*)onsubmit=[\"']([^\"']*)[\"']([^>]*)>", 2);

    private static final Regex FORM_FINDER = new Regex("<form([^>]*)>", 0);

    private static final Regex SCRIPT_TAG_FIXER = new Regex("(<title>The Kingdom of Loathing</title>)\n<script>\n(<script)");

    private static final Regex TABLE_FIXER = new Regex("(</td>)(.*?)(</td>|</tr>|</table>|<td[^>]*>)");

    private final static Regex BODY_TAG = new Regex("<body[^>]*?>");

    /**
     * Remove code which redirects when no frames are detected.
     */
    private static final Regex FRAME_REDIRECT = new Regex("if\\s*\\(parent\\.frames\\.length\\s*==\\s*0\\)\\s*location.href\\s*=\\s*[\"']?game\\.php[\"']?;", 0);
    private static final Regex HEAD_TAG = new Regex("<head>");
    private static final String jsInjectCode = "" +
            "function customRepairPostForms() {" +
            "   var forms = document.getElementsByTagName('form');" +
            "   for (var i = 0; i < forms.length; i++) {" +
            "       if(forms[i].method && forms[i].method.toUpperCase() == 'POST') {" +
            "           forms[i].method = 'get';" +
            "           if(forms[i].getAttribute('action')) {" +
            "               forms[i].setAttribute('action', 'POST/' + forms[i].getAttribute('action'));" +
            "           } else {" +
            "               forms[i].setAttribute('action', 'POST/' + location.pathname);" +
            "           }" +
            "           console.log('Repaired POST form ' + forms[i].getAttribute('action'));" +
            "       }" +
            "       " +
            "       forms[i].setAttribute('action', forms[i].getAttribute('action').replace('\\?', '~ANDROIDAPP~'));" +
            "   }" +
            "}\n" +
            "function pop_query(caller, title, button, callback, def) { " +
            "   window.querycallback = callback;" +
            "   window.ANDROIDAPP.displayFormNumeric(title, button, \"javascript:window.querycallback(#VAL)\");" +
            "}\n" +
            "function addInputUniqueTags() {" +
            "   var inputs = document.getElementsByTagName('input');" +
            "   count = 0;" +
            "   for (var i = 0; i < inputs.length; i++) {" +
            "      inputs[i].androidUniqueId = count;" +
            "      count += 1;" +
            "      inputs[i].androidInitialValue = inputs[i].value;" +
            "      inputs[i].androidInitialChecked = inputs[i].checked;" +
            "      inputs[i].addEventListener('change', checkInputChanges, false);" +
            "   }" +
            "   var selects = document.getElementsByTagName('select');" +
            "   for (var i = 0; i < selects.length; i++) {" +
            "       selects[i].androidUniqueId = count;" +
            "       count += 1;" +
            "       selects[i].androidInitialValue = selects[i].selectedIndex;" +
            "       selects[i].addEventListener('change', checkInputChanges, false);" +
            "   }" +
            "   var texts = document.getElementsByTagName('textarea');" +
            "   for (var i = 0; i < texts.length; i++) {" +
            "       texts[i].androidUniqueId = count;" +
            "       count += 1;" +
            "       texts[i].androidInitialValue = texts[i].value;" +
            "       texts[i].addEventListener('change', checkInputChanges, false);" +
            "   }" +
            "   if(inputs.length + selects.length + texts.length > 0) {" +
            "       console.log('Adding unique input ids: (' + inputs.length + ' + ' + selects.length + ' + ' + texts.length + ')');" +
            "   }" +
            "   applyInputChanges(window.ANDROIDAPP.getInputChanges())" +
            "}\n" +
            "window.addEventListener('load', addInputUniqueTags);" +
            "window.addEventListener('load', customRepairPostForms);" +
            "function checkInputChanges() {" +
            "   result = {};" +
            "   var inputs = document.getElementsByTagName('input');" +
            "   for (var i = 0; i < inputs.length; i++) {" +
            "      if(inputs[i].androidUniqueId && (true || inputs[i].value != inputs[i].androidInitialValue || inputs[i].checked != inputs[i].androidInitialChecked)) {" +
            "         result[inputs[i].androidUniqueId] = {name: inputs[i].name, value: inputs[i].value, checked: inputs[i].checked};" +
            "      }" +
            "   }" +
            "   var selects = document.getElementsByTagName('select');" +
            "   for (var i = 0; i < selects.length; i++) {" +
            "      if(selects[i].androidUniqueId && (true || selects[i].selectedIndex != selects[i].androidInitialValue)) {" +
            "         result[selects[i].androidUniqueId] = {name: selects[i].name, value: selects[i].selectedIndex, checked: false};" +
            "      }" +
            "   }" +
            "   var texts = document.getElementsByTagName('textarea');" +
            "   for (var i = 0; i < texts.length; i++) {" +
            "      if(texts[i].androidUniqueId && (true || texts[i].value != texts[i].androidInitialValue)) {" +
            "         result[texts[i].androidUniqueId] = {name: texts[i].name, value: texts[i].value, checked: false};" +
            "      }" +
            "   }" +
            "   window.ANDROIDAPP.reportInputChanges(JSON.stringify(result));" +
            "}\n" +
            "function applyInputChanges(cached_values) {" +
            "   cache = JSON.parse(cached_values);" +
            "   var inputs = document.getElementsByTagName('input');" +
            "   for (var i = 0; i < inputs.length; i++) {" +
            "      var id = inputs[i].androidUniqueId;" +
            "      if(id && cache[id] && inputs[i].name == cache[id].name) {" +
            "         inputs[i].value = cache[id].value;" +
            "         inputs[i].checked = cache[id].checked;" +
            "      }" +
            "   }" +
            "   var selects = document.getElementsByTagName('select');" +
            "   for (var i = 0; i < selects.length; i++) {" +
            "      var id = selects[i].androidUniqueId;" +
            "      if(id && cache[id] && selects[i].name == cache[id].name) {" +
            "         selects[i].selectedIndex = cache[id].value;" +
            "      }" +
            "   }" +
            "   var texts = document.getElementsByTagName('textarea');" +
            "   for (var i = 0; i < texts.length; i++) {" +
            "      var id = texts[i].androidUniqueId;" +
            "      if(id && cache[id] && texts[i].name == cache[id].name) {" +
            "         texts[i].value = cache[id].value;" +
            "      }" +
            "   }" +
            "}";

    private static final Regex POPQUERY_SCRIPT = new Regex("<script[^>]*pop_query[^>]*></script>");

    private static final Regex TYPE_EXTRACTION = new Regex("[&?]androiddisplay=([^&]*)", 1);
    private static final Regex TOP_PANE_REFRESH = new Regex("(top|parent).charpane.location(.href)?=[\"']?charpane.php[\"']?;");
    private String url;
    private WebModelType type;
    private String html;

    public WebModel(Session s, ServerReply text, WebModelType type) {
        super(s);

        Logger.log("WebModel", "Redirect url:" + text.redirectLocation);
        Logger.log("WebModel", "Created for " + text.url);

        this.url = text.url;
        this.type = type;
        this.setHTML(text.html.replace("window.devicePixelRatio >= 2", "window.devicePixelRatio < 2"));

        /*
        for(String x : this.getHTML().split("\n")) {
            Logger.log("WebModel", x);
        }
        */
    }

    public WebModel(Session s, ServerReply text) {
        this(s, text, determineType(s, text));
    }

    private static WebModelType determineType(Session session, ServerReply text) {
        String specified_type = TYPE_EXTRACTION.extractSingle(text.url, "unspecified");
        for (WebModelType type : WebModelType.values()) {
            if (specified_type.equals(type.toString()))
                return type;
        }

        if (text.url.contains("desc_item.php")
                || text.url.contains("desc_effect.php")
                || text.url.contains("desc_skill.php")
                || text.url.contains("desc_familiar.php")
                || text.url.contains("doc.php"))
            return WebModelType.SMALL;

        if (text.url.contains("create.php"))
            return WebModelType.EXTERNAL;

        // If there is no session token, the request must be external
        if (session.getCookie("AWSALB", "").equals("")) {
            return WebModelType.EXTERNAL;
        } else {
            return WebModelType.REGULAR;
        }
    }


    private static String prepareHtml(String html, String url, WebModelType type) {
        html = injectJavascript(html, url);
        html = doMiscFixes(html);
        html = fixPaneReferences(html, type);
        return html;
    }

    private static String fixPaneReferences(String html, WebModelType type) {
        html = FRAME_REDIRECT.replaceAll(html, "");
        html = TOP_PANE_REFRESH.replaceAll(html, "window.ANDROIDAPP.refreshStatsPane();");
        html = html.replace("top.mainpane.document", "document");
        html = html.replace("parent.mainpane", "window");

        if(type == WebModelType.RESULTS && !html.contains("ANDROIDAPP.refreshStatsPane()")) {
            // Force a stats pane refresh on results popups
            html = HEAD_TAG.replaceAll(html, "$0 <script>window.ANDROIDAPP.refreshStatsPane();</script>");
        }
        return html;
    }

    private static String doMiscFixes(String html) {
        //create.php? has an extra script tag
        html = SCRIPT_TAG_FIXER.replaceAll(html, "$1$2");

        // Manually fix the width of the body, to fix top-level center tags
        html = html.replace("<body", "<body style=\"display: inline-block;\"");

        // ?
        html = TABLE_FIXER.replaceAll(html, "$1$3$2");

        // Fix the viewport size by inserting a viewport tag
        html = BODY_TAG
                .replaceAll(html,
                        "$0<meta name=\"viewport\" content=\"width=device-width\">");

        // Fix http images
        html = html.replace("http://images.kingdomofloathing.com/", "https://s3.amazonaws.com/images.kingdomofloathing.com/");

        return html;
    }

    private static String injectJavascript(String html, String url) {
        html = HEAD_TAG.replaceAll(html, "$0 <script>" + jsInjectCode + "</script>");

        //pop_query(...) is replaced by an injected function to interact with android
        html = POPQUERY_SCRIPT.replaceAll(html, "");
        html = html.replace("Right-Click to Multi-Buy", "Long-Press to Multi-Buy");
        html = html.replace("Right-Click to Multi-Make", "Long-Press to Multi-Make");

        //Convert ajax post requests into the proper format
        html = html.replace("$.post(", "$.get(\"POST/\" + ");
        return html;
    }

    public String getURL() {
        return this.url;
    }

    public final String getHTML() {
        return this.html;
    }

    private void setHTML(String html) {
        this.html = prepareHtml(html, url, type);
    }

    public void setFixedHTML(String html) {
        this.html = html;
    }

    public <E> E visitType(WebModelTypeVisitor<E> visitor) {
        return type.visit(visitor);
    }

    public boolean makeRequest(String url) {
        if (url == null || url.length() < 1) return false;

        String originalUrl = url;

        Logger.log("WebModel", "Request first started for " + url);
        if (url.contains("totallyrealaction")) {
            System.out.println("Ignoring duplicate form request");
            return true;
        }

        if (url.contains("http://") || url.contains("https://")) {
            url = URL_FIND.extractSingle(url);
            if (url == null) {
                Logger.log("WebModel", "Unable to load url from " + originalUrl);
                return false;
            }
        }

        if (url.charAt(0) == '?') {
            String currentBase = URL_BASE_FIND.extractSingle(this.url, "main.php");
            url = currentBase + url;
        }

        Logger.log("WebModel", "Request started for " + url);
        followUrl(url);
        return true;
    }

    protected void followUrl(String url) {/*
        if(url.contains("adventure.php")) {
            Request req = new SingleRequest(url);
            this.makeRequest(req, new ResponseHandler() {
                @Override
                public void handle(Session session, ServerReply response) {
                    Logger.log("WebModel(Adventure)", response.url + ":" + response.redirectLocation + ":" + response.cookie);
                    makeRequest(new Request(response.redirectLocation));
                }
            });
        } else {*/
        Request req = new Request(url);
        this.makeRequest(req);
        //}
    }

    public InputStream makeBlockingRequest(String url) {
        url = url.replace("https://www.kingdomofloathing.com/", "");
        url = url.replace("http://www.kingdomofloathing.com/", "");
        url = url.replace("www.kingdomofloathing.com/", "");

        Request req = new Request(url);
        ServerReply result = this.makeBlockingRequest(req);

        String html_result;
        if (result == null) {
            Logger.log("WebModel", "[AJAX] Error loading " + url);
            html_result = "";
        } else {
            html_result = prepareHtml(result.html, url, WebModelType.REGULAR);
            Logger.log("WebModel", "[AJAX] Loaded " + url + " : " + html_result);
        }


        try {
            return new ByteArrayInputStream(html_result.getBytes("UTF-8"));
        } catch (IOException e) {
            Logger.log("WebModel", "Unable to encode as UTF-8");
            return new InputStream() {
                @Override
                public int read() throws IOException {
                    return 0;
                }
            };
        }
    }


    /**
     * Custom serialization code to compress the html.
     *
     * @param oos The stream to save this object to
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.writeObject(type);
        oos.writeObject(url);
        GZIPOutputStream gos = new GZIPOutputStream(oos);
        gos.write(html.getBytes());
        gos.close();
    }

    /**
     * Custom deserialization code to decompress the html.
     *
     * @param ois The stream to load this object from
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        type = (WebModelType) ois.readObject();
        url = (String) ois.readObject();

        GZIPInputStream gzipInputStream = new GZIPInputStream(ois);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int value = 0; value != -1; ) {
            value = gzipInputStream.read();
            if (value != -1) {
                baos.write(value);
            }
        }
        gzipInputStream.close();
        baos.close();
        html = new String(baos.toByteArray(), "UTF-8");
    }

    public enum WebModelType {
        REGULAR("regular") {
            public <E> E visit(WebModelTypeVisitor<E> visitor) {
                return visitor.forRegular();
            }
        }, SMALL("small") {
            public <E> E visit(WebModelTypeVisitor<E> visitor) {
                return visitor.forSmall();
            }
        }, RESULTS("results") {
            public <E> E visit(WebModelTypeVisitor<E> visitor) {
                return visitor.forResults();
            }
        }, EXTERNAL("external") {
            @Override
            public <E> E visit(WebModelTypeVisitor<E> visitor) {
                return visitor.forExternal();
            }
        };

        private final String value;

        WebModelType(String value) {
            this.value = value;
        }

        public abstract <E> E visit(WebModelTypeVisitor<E> visitor);

        @Override
        public String toString() {
            return value;
        }
    }

    public interface WebModelTypeVisitor<E> {
        E forRegular();

        E forSmall();

        E forResults();

        E forExternal();
    }
}
