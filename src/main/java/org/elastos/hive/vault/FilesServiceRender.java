package org.elastos.hive.vault;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import com.fasterxml.jackson.databind.JsonNode;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.elastos.hive.Vault;
import org.elastos.hive.connection.AuthHelper;
import org.elastos.hive.connection.ConnectionManager;
import org.elastos.hive.entity.file.UploadOutputStream;
import org.elastos.hive.exception.FileNotFoundException;
import org.elastos.hive.exception.HiveException;
import org.elastos.hive.network.FilesApi;
import org.elastos.hive.service.FilesService;
import org.elastos.hive.utils.JsonUtil;
import org.elastos.hive.utils.HttpUtil;
import retrofit2.Response;

class FilesServiceRender implements FilesService {

	private AuthHelper authHelper;
	private ConnectionManager connectionManager;
	private FilesApi filesApi;

	public FilesServiceRender(Vault vault) {
		this.authHelper = vault.getAuthHelper();
		this.connectionManager = authHelper.getConnectionManager();
		this.filesApi = connectionManager.getNetworkApi(FilesApi.class);
	}

	@Override
	public <T> CompletableFuture<T> upload(String path, Class<T> resultType) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return uploadImpl(path, resultType);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private <T> T uploadImpl(String path, Class<T> resultType) throws HiveException {
		try {
			HttpURLConnection connection = this.connectionManager.openURLConnection("/files/upload/" + path);
			OutputStream outputStream = connection.getOutputStream();

			if(resultType.isAssignableFrom(OutputStream.class)) {
				UploadOutputStream uploader = new UploadOutputStream(connection, outputStream);
				return resultType.cast(uploader);
			} else if (resultType.isAssignableFrom(OutputStreamWriter.class)) {
				OutputStreamWriter writer = new OutputStreamWriter(outputStream);
				return resultType.cast(writer);
			} else {
				throw new HiveException("Not supported result type");
			}
		} catch (IOException e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public <T> CompletableFuture<T> download(String path, Class<T> resultType) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> downloadImpl(path, resultType));
	}

	private <T> T downloadImpl(String remoteFile, Class<T> resultType) {
		try {
			Response<ResponseBody> response;

			response = filesApi.downloader(remoteFile).execute();
			int code = response.code();
			if(404 == code) {
				throw new FileNotFoundException(FileNotFoundException.EXCEPTION);
			}

			authHelper.checkResponseWithRetry(response);

			if(resultType.isAssignableFrom(Reader.class)) {
				Reader reader = HttpUtil.getToReader(response);
				return resultType.cast(reader);
			}
			if (resultType.isAssignableFrom(InputStream.class)){
				InputStream inputStream = HttpUtil.getInputStream(response);
				return resultType.cast(inputStream);
			}

			throw new HiveException("Not supported result type");
		} catch (HiveException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public CompletableFuture<Boolean> delete(String path) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return deleteImpl(path);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private Boolean deleteImpl(String remoteFile) throws HiveException {
		try {
			Map<String, String> map = new HashMap<>();
			map.put("path", remoteFile);

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = filesApi.deleteFolder(createJsonRequestBody(json)).execute();
			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> move(String source, String target) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return moveImpl(source, target);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private Boolean moveImpl(String source, String dest) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("src_path", source);
			map.put("dst_path", dest);

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = filesApi.move(createJsonRequestBody(json)).execute();
			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<Boolean> copy(String source, String target) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return copyImpl(source, target);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private Boolean copyImpl(String source, String dest) throws HiveException {
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("src_path", source);
			map.put("dst_path", dest);

			String json = JsonUtil.serialize(map);
			Response<ResponseBody> response;

			response = filesApi.copy(createJsonRequestBody(json)).execute();
			authHelper.checkResponseWithRetry(response);
			return true;
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	@Override
	public CompletableFuture<String> hash(String path) {
		return authHelper.checkValid().thenApplyAsync(aVoid -> {
			try {
				return hashImp(path);
			} catch (HiveException e) {
				throw new CompletionException(e);
			}
		});
	}

	private String hashImp(String remoteFile) throws HiveException {
		try {
			Response response = filesApi.hash(remoteFile).execute();
			authHelper.checkResponseWithRetry(response);
			JsonNode ret = HttpUtil.getValue(response, JsonNode.class);
			return ret.get("SHA256").toString();
		} catch (Exception e) {
			throw new HiveException(e.getLocalizedMessage());
		}
	}

	private RequestBody createJsonRequestBody(String json) {
		return RequestBody.create(MediaType.parse("Content-Type, application/json"), json);
	}
}
