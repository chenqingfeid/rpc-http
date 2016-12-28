package com.sdklite.rpc.http;

import java.util.List;
import java.util.Map;

import com.sdklite.gson.GsonDeserializer;
import com.sdklite.io.annotation.Deserialization;
import com.sdklite.rpc.RpcException;
import com.sdklite.rpc.RpcService;
import com.sdklite.rpc.annotation.Path;
import com.sdklite.rpc.annotation.PathParameter;
import com.sdklite.rpc.http.annotation.Get;

public interface GitHubService extends RpcService {

    @Get
    @Path("/users/{username}/repos")
    @Deserialization(GsonDeserializer.class)
    public List<Map<String, Object>> getRepositories(@PathParameter("username") final String username) throws RpcException;

}
