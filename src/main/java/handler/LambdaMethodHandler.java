package handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import model.IotButtonParameters;
import model.SonosRequestBody;
import model.SonosResponseBody;

public class LambdaMethodHandler implements RequestHandler<IotButtonParameters, String> {

  public static LambdaLogger logger;
  private String username = "user";
  private String password = "password";

  public String handleRequest(IotButtonParameters input, Context context) {
    logger = context.getLogger();
    logger.log("Connection from: " + input.getName() + " clickType: " + input.getClickType());

    Gson gson = new Gson();

    SonosRequestBody sonosRequestBody = new SonosRequestBody();
    if (input.getClickType().equalsIgnoreCase("SINGLE")) {
      sonosRequestBody.setResources(false);
    } else {
      sonosRequestBody.setResources(true);
    }

    Integer statusCode = null;

    String encoded = Base64.getEncoder()
        .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

    try {
      URL url = new URL("http://nasareth.synology.me:8080/v1/home/resources/all/");
      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setRequestProperty("Content-Type", "application/json");
      httpURLConnection.setRequestProperty("Authorization", "Basic " + encoded);
      httpURLConnection.setDoOutput(true);

      DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
      outputStream.writeBytes(gson.toJson(sonosRequestBody));

      System.out.println(httpURLConnection.getResponseCode());
      statusCode = httpURLConnection.getResponseCode();
    } catch (IOException e) {
      e.printStackTrace();
    }

    SonosResponseBody sonosResponseBody = new SonosResponseBody();
    if (statusCode == 200) {
      sonosResponseBody.setSuccess(true);
      sonosResponseBody.setMessage("Success");
    } else {
      sonosResponseBody.setSuccess(false);
      sonosResponseBody.setMessage("Failed");
    }

    return gson.toJson(sonosResponseBody);
  }


  // LOCAL DEBUG
  public void localTestingMethod() {
    SonosRequestBody sonosRequestBody = new SonosRequestBody();
    sonosRequestBody.setResources(false);
    String encoded = Base64.getEncoder()
        .encodeToString((username + ":" + password).getBytes(StandardCharsets.UTF_8));

    //URL url = new URL("http://nasareth.synology.me:8080/v1/home/resources/all");

    try {
      URL url = new URL("http://localhost:8080/v1/home/resources/all/");
      HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
      httpURLConnection.setRequestMethod("POST");
      httpURLConnection.setDoOutput(true);

      httpURLConnection.setRequestProperty("Content-Type", "application/json");
      httpURLConnection.setRequestProperty("Authorization", "Basic " + encoded);
      System.out.println(new Gson().toJson(sonosRequestBody));

      DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
      outputStream.writeBytes(new Gson().toJson(sonosRequestBody));

      System.out.println(httpURLConnection.getResponseCode());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}


