package com.sdklite.rpc.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.sdklite.rpc.RpcException;
import com.sdklite.rpc.RpcServiceFactory;

public class HttpRpcTest {

    private HttpRpcClient client;

    @Before
    public void setup() {
        this.client = new OkHttpRpcClient.Builder(null).build();
    }

    public void requestWithGet() throws RpcException, IOException {
        final HttpRpcRequest request = this.client.newRequestBuilder().get("https://api.github.com/users/johnsonlee/repos").build();
        final HttpRpcResponse response = this.client.newRpc(request).execute();
        Assert.assertEquals(200, response.getStatusCode());
    }

    public void getWithRpc() throws RpcException {
        RpcServiceFactory factory = new RpcServiceFactory(null);
        final GitHubService github = factory.newRpcService(GitHubService.class, "https://api.github.com");
        final List<Map<String, Object>> repos = github.getRepositories("johnsonlee");
        Assert.assertNotNull(repos);
    }

}
