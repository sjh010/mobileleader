package com.mobileleader.edoc.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.mobileleader.image.client.Exception.UserHttpStatus;

public class HttpTest {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		JSONObject jsonObject = new JSONObject();
		
		JSONObject imageStoreDto = new JSONObject();
		
		imageStoreDto.put("mainKey", "testMainKey");
		imageStoreDto.put("branchCode", "12345");
		imageStoreDto.put("branchTitle", "testBranch");
		imageStoreDto.put("employeeId", "12345");
		imageStoreDto.put("employeeName", "소정환");
		imageStoreDto.put("docMappingCount", 2);
		imageStoreDto.put("previousDeviceInfo", "1313123123");
		imageStoreDto.put("insourceId", "CCRSTEST");
		imageStoreDto.put("insourceTitle", "테스트");
		imageStoreDto.put("customerName", "고갱님");
		imageStoreDto.put("taskKey", "testTaskKey");
		imageStoreDto.put("memo", "메모메모");
		
		jsonObject.put("imageStoreDto", imageStoreDto);
		
		JSONArray jsonArray = new JSONArray();
		
		JSONObject imageFileDto = new JSONObject();
		
		imageFileDto.put("docId", "testDocId");
		imageFileDto.put("docTitle", "테스트타이틀");
		imageFileDto.put("docType", "TIF");
		imageFileDto.put("fileName", "test1.jpg");
		imageFileDto.put("pageCnt", 1);
		imageFileDto.put("funnels", "edoc");
		imageFileDto.put("versionInfo", 1);
		
		jsonArray.add(imageFileDto);
		
		jsonObject.put("imageFileDtos", jsonArray);
		
		System.out.println(jsonObject.toString());
		
		MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
		StringBody stringBody1 = new StringBody(jsonObject.toString(), ContentType.APPLICATION_JSON);
		multipartEntityBuilder.addPart("scanInfo", stringBody1);
		
		try {
			File file = new File("C:\\Users\\user\\Documents\\workspace-sts-3.9.8.RELEASE\\EdocDaemon\\tsa\\etc\\TSA_BG.png");
			
			System.out.println(file.getName());
			
			if (file.isFile()) {
				FileBody fileBody = new FileBody(file, ContentType.APPLICATION_OCTET_STREAM, URLEncoder.encode(file.getName(), "UTF-8"));
				multipartEntityBuilder.addPart("TSA_BG.png", fileBody);
			} else {
				System.out.println("Not File");
			}
			
			
		} catch (Exception e) {
			System.out.println("Exception");
		}
				
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse httpRes = null;
		
		String url = "http://pptr-dr.ccrs.or.kr:7104/upload/";
		
		try {
			HttpPost post = new HttpPost(url);
			post.setEntity(multipartEntityBuilder.build());		
			httpRes = httpClient.execute(post);			
			
			int statusCode = httpRes.getStatusLine().getStatusCode();
			if(statusCode != UserHttpStatus.OK.getStatusCode()) {
				//return new ExceptionResponse(UserHttpStatus.getByCode(statusCode)).toString();
				//return setErrorResponse(ClientError.NOT_FOUND);
			}
			
			// server response parsing		
			HttpEntity httpEntity = httpRes.getEntity();
			BufferedReader br = new BufferedReader(new InputStreamReader(httpEntity.getContent(), Charset.forName("UTF-8")));
	 
			String buffer = "";
			StringBuffer result = new StringBuffer();
			while( (buffer = br.readLine()) != null) {
				result.append(buffer).append("\r\n");
			}

			System.out.println(result.toString());
	 
		} catch(Exception e) {
			//ret = new ExceptionResponse(UserHttpStatus.INTERNAL_SERVER_ERROR, e).toString();
			//ret = setErrorResponse(ClientError.INTERNAL_CLIENT_ERROR);
		} finally {
			try {
				httpRes.close();
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		
		
		
		
		
		
	}
		
}
