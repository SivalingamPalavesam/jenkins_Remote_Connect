package com.jenkins.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class JenkinsController {

	String username = "sivalingam";
	String password = "Sivalingam@#&16";

	@GetMapping("/jenkinspage")
	public String scrape(Model model) throws ClientProtocolException, IOException
	{
		String urlString = "http://localhost:8080/api/json?tree=jobs[name,url]";

		URI uri = URI.create(urlString); // Convert String to URL

		HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

		CredentialsProvider credsProvider = new BasicCredentialsProvider();

		credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
				new UsernamePasswordCredentials(username, password));

		AuthCache authCache = new BasicAuthCache(); // Create AuthCache instance

		BasicScheme basicAuth = new BasicScheme(); // Generate BASIC scheme object and add it to the local auth cache

		authCache.put(host, basicAuth);

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

		HttpGet httpGet = new HttpGet(uri); // Add AuthCache to the execution context

		HttpClientContext localContext = HttpClientContext.create();

		localContext.setAuthCache(authCache);

		HttpResponse response = httpClient.execute(host, httpGet, localContext);

		String xy = EntityUtils.toString((response.getEntity()));

		JSONObject result = new JSONObject(xy); // Convert String to JSON Object

		JSONArray fromJsonArray = result.getJSONArray("jobs");

		List<Object> data = fromJsonArray.toList();

		model.addAttribute("fromJsonArray", data);

		return "job";

	}

	@PostMapping("/build")
	public String build(@RequestParam("test") String test, Model model) throws IOException, InterruptedException 
	{
		String urlStr = "http://localhost:8080/job/" + test + "/build?token=sivalingam";

		URI uri = URI.create(urlStr);

		HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

		CredentialsProvider credsProvider = new BasicCredentialsProvider();

		credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
				new UsernamePasswordCredentials(username, password));

		AuthCache authCache = new BasicAuthCache();

		BasicScheme basicAuth = new BasicScheme();

		authCache.put(host, basicAuth);

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

		HttpGet httpGet = new HttpGet(uri);

		HttpClientContext localContext = HttpClientContext.create();

		localContext.setAuthCache(authCache);

		httpClient.execute(host, httpGet, localContext);

		return "buildStatus";

	}

	@PostMapping("/status")
	public String JenkinsBuildStatus(@RequestParam("buildname") String buildname, Model model)	throws ClientProtocolException, IOException
	{
		String urlString = "http://localhost:8080/job/" + buildname + "/api/json?tree=builds[number,status,id,result]";

		URI uri = URI.create(urlString);

		HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

		CredentialsProvider credsProvider = new BasicCredentialsProvider();

		credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
				new UsernamePasswordCredentials(username, password));

		AuthCache authCache = new BasicAuthCache();

		BasicScheme basicAuth = new BasicScheme();

		authCache.put(host, basicAuth);

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

		HttpGet httpGet = new HttpGet(uri); // Add AuthCache to the execution context

		HttpClientContext localContext = HttpClientContext.create();

		localContext.setAuthCache(authCache);

		HttpResponse response = httpClient.execute(host, httpGet, localContext);

		String xy = EntityUtils.toString((response.getEntity()));

		JSONObject result = new JSONObject(xy);

		JSONArray fromJsonArray1 = result.getJSONArray("builds");

		List<Object> data1 = fromJsonArray1.toList();

		model.addAttribute("fromJsonArray1", data1);

		return "allBuildStatus";
	}

	String id;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GetMapping("/currentBuild")
	public String currentBuild(@RequestParam("currentBuildName") String currentBuildName, Model model)
			throws ClientProtocolException, IOException {
		String urlString = "http://localhost:8080/job/" + currentBuildName + "/lastBuild/api/json";
		URI uri = URI.create(urlString); // Convert String to URL

		HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

		CredentialsProvider credsProvider = new BasicCredentialsProvider();

		credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
				new UsernamePasswordCredentials(username, password));

		AuthCache authCache = new BasicAuthCache();

		BasicScheme basicAuth = new BasicScheme();

		authCache.put(host, basicAuth);

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

		HttpGet httpGet = new HttpGet(uri);

		HttpClientContext localContext = HttpClientContext.create();

		localContext.setAuthCache(authCache);

		HttpResponse response = httpClient.execute(host, httpGet, localContext);

		String xy = EntityUtils.toString((response.getEntity()));

		JSONObject object = new JSONObject(xy);

		String result = object.getString("result");
		id = object.getString("id");
		Map<String, String> val = new HashMap<>();
		val.put("id", id);
		val.put("result", result);

		List obj = new ArrayList<>();
		obj.add(val);
		System.out.println("***************" + obj);
		model.addAttribute("fromJsonArray", obj);

		return "currentBuildStatus";

	}

	@GetMapping("/downloadFile")
	public void downloadFile(@RequestParam("Logfile") String Logfile, HttpServletResponse response)	throws ClientProtocolException, IOException 
	{

		String urlString = "http://localhost:8080/job/" + Logfile + "/" + id + "/consoleText";

		URI uri = URI.create(urlString); // Convert String to URL

		HttpHost host = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());

		CredentialsProvider credsProvider = new BasicCredentialsProvider();

		credsProvider.setCredentials(new AuthScope(uri.getHost(), uri.getPort()),
				new UsernamePasswordCredentials(username, password));

		AuthCache authCache = new BasicAuthCache();

		BasicScheme basicAuth = new BasicScheme();

		authCache.put(host, basicAuth);

		CloseableHttpClient httpClient = HttpClients.custom().setDefaultCredentialsProvider(credsProvider).build();

		HttpGet httpGet = new HttpGet(uri);

		HttpClientContext localContext = HttpClientContext.create();

		localContext.setAuthCache(authCache);

		HttpResponse responseValue = httpClient.execute(host, httpGet, localContext);

		HttpEntity entity = (responseValue.getEntity());

		String responseString = EntityUtils.toString(entity, "UTF-8");

		// file write method
		FileUtils.writeStringToFile(new File("C:/Users/ELCOT/Music/jenkins console/logfile.txt"), responseString,
				Charset.forName("UTF-8"));

		// FileDownloading method
		File file = new File("C:/Users/ELCOT/Music/jenkins console/logfile.txt");

		response.setContentType("application/octet-stream");

		String headerkey = "Content-Disposition";
		String headerValue = "attachment;filename = " + file.getName();

		response.setHeader(headerkey, headerValue);

		ServletOutputStream outputStream = response.getOutputStream();

		BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

		byte[] buffer = new byte[(int) file.length()]; // 1MB buffer size

		int bytesRead = -1;

		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);

		}
		inputStream.close();
		outputStream.close();

	}
}
