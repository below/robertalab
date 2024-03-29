package lejos.ev3.startup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.json.JSONObject;

/**
 * handles registration of the token<br>
 * no singleton pattern but use only one instance of this class
 * 
 * @author dpyka
 */
public class RobertaTokenRegister implements Runnable {

    private final URL serverURL;
    private final String token;
    private boolean isTimeOut = false;
    private boolean isRegistered = false;
    private boolean hasError = false;

    private HttpURLConnection httpURLConnection;

    public RobertaTokenRegister(URL serverURL, String token) {
        this.serverURL = serverURL;
        this.token = token;
    }

    public HttpURLConnection getHttpConnection() {
        return this.httpURLConnection;
    }

    public void setTimeOutInfo(boolean bool) {
        this.isTimeOut = bool;
    }

    public boolean getTimeOutInfo() {
        return this.isTimeOut;
    }

    public void setRegisteredInfo(boolean bool) {
        this.isRegistered = bool;
    }

    public boolean getRegisteredInfo() {
        return this.isRegistered;
    }

    public void setErrorInfo(boolean bool) {
        this.hasError = bool;
    }

    public boolean getErrorInfo() {
        return this.hasError;
    }

    /**
     * send token to server, get ok response if registered successfully, else error
     */
    @Override
    public void run() {
        OutputStream os = null;
        BufferedReader br = null;

        try {
            this.httpURLConnection = openConnection(this.serverURL);

            JSONObject requestEntity = new JSONObject();
            requestEntity.put("brickname", RobertaObserver.getBrickName());
            requestEntity.put("token", this.token);

            os = this.httpURLConnection.getOutputStream();
            os.write(requestEntity.toString().getBytes("UTF-8"));

            br = new BufferedReader(new InputStreamReader(this.httpURLConnection.getInputStream()));
            StringBuilder responseStrBuilder = new StringBuilder();
            String responseString;
            while ( (responseString = br.readLine()) != null ) {
                responseStrBuilder.append(responseString);
            }
            JSONObject response = new JSONObject(responseStrBuilder.toString());
            System.out.println(response);

            if ( response.get("response").equals("ok") ) {
                setRegisteredInfo(true);
            } else {
                throw new IOException();
            }
        } catch ( SocketTimeoutException ste ) {
            setTimeOutInfo(true);
            ste.printStackTrace();
        } catch ( IOException e ) {
            setErrorInfo(true);
            System.out.println("force disconnect (registerToken)");
        } finally {
            try {
                if ( os != null ) {
                    os.close();
                }
                if ( br != null ) {
                    br.close();
                }
            } catch ( IOException e ) {
                //
            }
        }
    }

    /**
     * Opens http connection to server. "POST" as request method. Input, output
     * set to "true".
     * 5 minutes readTimeOut, in this time, the token has to be registered with the server
     * 
     * @param url
     *        the robertalab server url or ip+port
     * @return httpURLConnection http connection object to the server
     * @throws IOException
     *         opening a connection failed
     */
    private HttpURLConnection openConnection(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setReadTimeout(300000);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=utf8");
        return httpURLConnection;
    }

}
